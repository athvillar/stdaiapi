package cn.standardai.api.ml.run;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import cn.standardai.api.core.util.MathUtil;
import cn.standardai.api.dao.base.DaoHandler;
import cn.standardai.api.dao.bean.Data;
import cn.standardai.api.dao.bean.Model;
import cn.standardai.api.es.exception.ESException;
import cn.standardai.api.es.service.ESService;
import cn.standardai.api.ml.bean.DnnDataSetting;
import cn.standardai.api.ml.bean.DnnModelSetting;
import cn.standardai.api.ml.bean.DnnModelSetting.Status;
import cn.standardai.api.ml.bean.DnnTrainSetting;
import cn.standardai.api.ml.daohandler.DataHandler;
import cn.standardai.api.ml.daohandler.ModelHandler;
import cn.standardai.api.ml.exception.FilterException;
import cn.standardai.api.ml.filter.DataFilter;
import cn.standardai.lib.algorithm.base.Dnn;
import cn.standardai.lib.algorithm.cnn.CnnData;
import cn.standardai.lib.algorithm.exception.DnnException;
import cn.standardai.lib.algorithm.rnn.lstm.DeepLstm;
import cn.standardai.lib.algorithm.rnn.lstm.LstmData;
import cn.standardai.lib.base.matrix.MatrixException;

public class ModelGhost implements Runnable {

	private DaoHandler daoHandler = new DaoHandler(true);

	private String userId;

	private Dnn<?> model;

	private Map<String, Object> modelContext;

	public ModelGhost() {
		this.modelContext = new HashMap<String, Object>();
	}

	public void invoke() {
		this.model.addIndicator("trainLoss");
		this.model.addIndicator("testLoss");
		this.model.addIndicator("verifyLoss");
		Executor exec = Executors.newSingleThreadExecutor();
		exec.execute(this);
		return;
	}

	public void loadModel(Dnn<?> model) {
		this.model = model;
	}

	public void loadParam(String key, Object value) {
		this.modelContext.put(key, value);
	}

	@Override
	public void run() {

		ModelHandler mh = new ModelHandler(daoHandler);
		String modelId = null;
		try {

			DnnTrainSetting ts = (DnnTrainSetting) modelContext.get("trainSetting");
			Integer watchEpoch = ts.getWatchEpoch();
			this.model.setDth(ts.getDth());
			this.model.setLearningRate(ts.getLearningRate());
			this.model.setBatchSize(ts.getBatchSize());
			this.model.setDiverseDataRate(ts.getDiverseDataRate());
			this.model.setEpoch(ts.getEpoch());
			this.model.setTestLossIncreaseTolerance(ts.getTestLossIncreaseTolerance());
			this.model.setTrainSecond(ts.getTrainSecond());
			this.model.setWatchEpoch(watchEpoch);

			DnnModelSetting ms = (DnnModelSetting) modelContext.get("modelSetting");
			this.userId = ms.getUserId();
			modelId = ms.getModelId();
			DnnDataSetting ds = ms.getDataSetting();
			DataHandler dh = new DataHandler(this.daoHandler);
			List<Data> rawData = dh.getData(dh.getDataset(ms.getUserId(), ds.getDatasetId(), ds.getDatasetName()));
			DataFilter<?, ?>[] xFilters = DataFilter.parseFilters(ds.getxFilter());
			DataFilter<?, ?>[] yFilters = DataFilter.parseFilters(ds.getyFilter());
			for (DataFilter<?, ?> f : xFilters) {
				if (f != null && f.needInit()) {
					f.init(this.userId, this.daoHandler);
				}
			}
			for (DataFilter<?, ?> f : yFilters) {
				if (f != null && f.needInit()) {
					f.init(this.userId, this.daoHandler);
				}
			}

			switch (ms.getAlgorithm()) {
			case cnn:
				CnnData[] data1 = new CnnData[rawData.size()];
				for (int i = 0; i < rawData.size(); i++) {
					Integer[][][] x = DataFilter.encode(ds.getData(rawData.get(i), ds.getxColumn()), xFilters);
					Integer[] y = DataFilter.encode(ds.getData(rawData.get(i), ds.getyColumn()), yFilters);
					data1[i] = new CnnData(x, y);
				}
				((Dnn<CnnData>)this.model).mountData(data1);
				break;
			case lstm:
				LstmData[] data2 = new LstmData[rawData.size()];
				for (int i = 0; i < rawData.size(); i++) {
					Double[][] x = DataFilter.encode(ds.getData(rawData.get(i), ds.getxColumn()), xFilters);
					Integer[] y = DataFilter.encode(ds.getData(rawData.get(i), ds.getyColumn()), yFilters);
					data2[i] = new LstmData(x, y);
				}
				((Dnn<LstmData>)this.model).mountData(data2);
				break;
			}

			ModelRunner mr = new ModelRunner(this.model);
			synchronized (this.model.indicator) {
				Executor exec = Executors.newSingleThreadExecutor();
				exec.execute(mr);
				String trainId = MathUtil.random(32);
				int epoch = 0;
				while (true) {
					try {
						this.model.indicator.wait();
						if (this.model.containCatalog("final")) {
							break;
						}
						epoch += watchEpoch;

						List<Map<String, Object>> indicatorData = new ArrayList<Map<String, Object>>();
						indicatorData.add(makeInsertData(trainId, "trainLoss", epoch, this.model.getValue("trainLoss", epoch)));
						indicatorData.add(makeInsertData(trainId, "testLoss", epoch, this.model.getValue("testLoss", epoch)));
						if (indicatorData != null) ESService.insert("indicator", "indicator", indicatorData);
						//System.out.println("Epoch " + epoch + ",\tTrainLoss: " + this.model.getValue("trainLoss", epoch) + ",\tTestLoss: " + this.model.getValue("testLoss", epoch));
					} catch (InterruptedException e) {
						e.printStackTrace();
						break;
					} catch (ESException e) {
						e.printStackTrace();
					}
				}
				System.out.println("finished at epoch " + epoch);
			}

			Model model = new Model();
			model.setModelId(modelId);
			model.setStructure(this.model.getBytes());
			model.setStatus(Status.Normal.status);
			mh.updateModelStructureById(model);

		} catch (FilterException e) {
			e.printStackTrace();
			Model model = new Model();
			model.setModelId(modelId);
			model.setStatus(Status.Normal.status);
			mh.updateModelStatusById(model);
		} finally {
			done();
		}
	}

	private Map<String, Object> makeInsertData(String trainId, String indicator, Integer epoch, Double value) {

		Map<String, Object> dataTemp = new HashMap<String, Object>();

		dataTemp.put("trainId", trainId);
		dataTemp.put("indicator", indicator);
		dataTemp.put("epoch", epoch);
		dataTemp.put("value", value);
		dataTemp.put("time", new Date());

		return dataTemp;
	}

	private class ModelRunner implements Runnable {

		private Dnn<?> model;

		public ModelRunner(Dnn<?> model) {
			this.model = model;
		}

		@Override
		public void run() {
			try {
				this.model.train();
			} catch (DnnException | MatrixException e) {
				e.printStackTrace();
			}
		}
	}

	public void done() {
		daoHandler.releaseSession();
	}

	public String getUserId() {
		return userId;
	}

	public DaoHandler getDaoHandler() {
		return daoHandler;
	}

	public Dnn<?> getModel() {
		return model;
	}
}
