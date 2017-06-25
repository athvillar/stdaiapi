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
import cn.standardai.api.dao.bean.Train;
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
			DnnDataSetting dsTrain = ms.getTrainDataSetting();
			DataHandler dh = new DataHandler(this.daoHandler);

			List<Data> rawData1 = dh.getData(dh.getDataset(ms.getUserId(), dsTrain.getDatasetId(), dsTrain.getDatasetName()));
			DataFilter<?, ?>[] xFilters = DataFilter.parseFilters(dsTrain.getxFilter());
			DataFilter<?, ?>[] yFilters = DataFilter.parseFilters(dsTrain.getyFilter());
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

			String testDatasetUser = null;
			String testDatasetName = ts.getTestDatasetName();
			List<Data> rawData2 = null;
			if (testDatasetName != null) {
				int idx = testDatasetName.indexOf('/');
				if (idx != -1 && idx != testDatasetName.length() - 1) {
					testDatasetUser = testDatasetName.substring(0, idx);
					testDatasetName = testDatasetName.substring(idx + 1);
				} else {
					testDatasetUser = ms.getUserId();
				}
				rawData2 = dh.getData(dh.getDataset(testDatasetUser, null, testDatasetName));
			}

			switch (ms.getAlgorithm()) {
			case cnn:
				CnnData[] cnnData1 = new CnnData[rawData1.size()];
				for (int i = 0; i < rawData1.size(); i++) {
					Integer[][][] x = DataFilter.encode(DnnDataSetting.getData(rawData1.get(i), dsTrain.getxColumn()), xFilters);
					Integer[] y = DataFilter.encode(DnnDataSetting.getData(rawData1.get(i), dsTrain.getyColumn()), yFilters);
					cnnData1[i] = new CnnData(x, y);
				}
				((Dnn<CnnData>)this.model).mountData(cnnData1);
				if (rawData2 != null) {
					CnnData[] cnnData2 = new CnnData[rawData2.size()];
					for (int i = 0; i < rawData2.size(); i++) {
						Integer[][][] x = DataFilter.encode(DnnDataSetting.getData(rawData2.get(i), dsTrain.getxColumn()), xFilters);
						Integer[] y = DataFilter.encode(DnnDataSetting.getData(rawData2.get(i), dsTrain.getyColumn()), yFilters);
						cnnData2[i] = new CnnData(x, y);
					}
					((Dnn<CnnData>)this.model).mountTestData(cnnData2);
				}
				break;
			case lstm:
				LstmData[] lstmData1 = new LstmData[rawData1.size()];
				for (int i = 0; i < rawData1.size(); i++) {
					Double[][] x = DataFilter.encode(DnnDataSetting.getData(rawData1.get(i), dsTrain.getxColumn()), xFilters);
					Integer[] y = DataFilter.encode(DnnDataSetting.getData(rawData1.get(i), dsTrain.getyColumn()), yFilters);
					lstmData1[i] = new LstmData(x, y);
				}
				((Dnn<LstmData>)this.model).mountData(lstmData1);
				if (rawData2 != null) {
					LstmData[] lstmData2 = new LstmData[rawData2.size()];
					for (int i = 0; i < rawData2.size(); i++) {
						Double[][] x = DataFilter.encode(DnnDataSetting.getData(rawData2.get(i), dsTrain.getxColumn()), xFilters);
						Integer[] y = DataFilter.encode(DnnDataSetting.getData(rawData2.get(i), dsTrain.getyColumn()), yFilters);
						lstmData2[i] = new LstmData(x, y);
					}
					((Dnn<LstmData>)this.model).mountTestData(lstmData2);
				}
				break;
			}

			Train train = new Train();
			train.setTrainId(MathUtil.random(32));
			train.setStartTime(new Date());
			train.setModelId(modelId);
			train.setEpochDataCnt(rawData1.size());
			mh.insertTrain(train);

			ModelRunner mr = new ModelRunner(this.model);
			int epoch = 0;
			synchronized (this.model.indicator) {
				Executor exec = Executors.newSingleThreadExecutor();
				exec.execute(mr);
				String trainId = train.getTrainId();
				while (true) {
					try {
						this.model.indicator.wait();
						if (this.model.containCatalog("final")) {
							break;
						}
						epoch += watchEpoch;

						//System.out.println("Epoch " + epoch + ",\tTrainLoss: " + this.model.getValue("trainLoss", epoch) + ",\tTestLoss: " + this.model.getValue("testLoss", epoch));
						List<Map<String, Object>> indicatorData = new ArrayList<Map<String, Object>>();
						indicatorData.add(makeInsertData(trainId, "trainLoss", epoch, this.model.getValue("trainLoss", epoch)));
						indicatorData.add(makeInsertData(trainId, "testLoss", epoch, this.model.getValue("testLoss", epoch)));
						if (indicatorData != null) ESService.insert("indicator", "indicator", indicatorData);
					} catch (InterruptedException e) {
						e.printStackTrace();
						break;
					} catch (ESException e) {
						e.printStackTrace();
					}
				}
				System.out.println("finished at epoch " + epoch);
			}

			train.setEndTime(new Date());
			train.setEpochCnt(this.model.getValue("final", 0).intValue());
			train.setTotalSecond((train.getEndTime().getTime() - train.getStartTime().getTime()) / 1000);
			mh.updateTrain(train);

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
