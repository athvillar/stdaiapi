package cn.standardai.api.ml.run;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import cn.standardai.api.dao.base.DaoHandler;
import cn.standardai.api.dao.bean.Data;
import cn.standardai.api.dao.bean.Model;
import cn.standardai.api.ml.bean.DnnDataSetting;
import cn.standardai.api.ml.bean.DnnModelSetting;
import cn.standardai.api.ml.bean.DnnModelSetting.Status;
import cn.standardai.api.ml.bean.DnnTrainSetting;
import cn.standardai.api.ml.daohandler.DataHandler;
import cn.standardai.api.ml.daohandler.ModelHandler;
import cn.standardai.api.ml.exception.FilterException;
import cn.standardai.api.ml.filter.DataFilter;
import cn.standardai.lib.algorithm.base.Dnn;
import cn.standardai.lib.algorithm.exception.DnnException;
import cn.standardai.lib.algorithm.exception.UsageException;
import cn.standardai.lib.algorithm.rnn.lstm.DeepLstm;
import cn.standardai.lib.algorithm.rnn.lstm.LstmData;
import cn.standardai.lib.base.matrix.MatrixException;

public class ModelGhost implements Runnable {

	private DaoHandler daoHandler = new DaoHandler(true);

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
			DnnDataSetting ds = ms.getDataSetting();
			DataHandler dh = new DataHandler(this.daoHandler);
			List<Data> rawData = dh.getData(dh.getDataset(ms.getUserId(), ds.getDatasetId(), ds.getDatasetName()));
			DataFilter<?, ?>[] xFilters = DataFilter.parseFilters(ds.getxFilter());
			DataFilter<?, ?>[] yFilters = DataFilter.parseFilters(ds.getyFilter());

			switch (ms.getAlgorithm()) {
			case cnn:
				// TODO
				break;
			case lstm:
				LstmData[] data = new LstmData[rawData.size()];
				for (int i = 0; i < rawData.size(); i++) {
					Double[][] x = DataFilter.encode(ds.getData(rawData.get(i), ds.getxColumn()), xFilters);
					Integer[] y = DataFilter.encode(ds.getData(rawData.get(i), ds.getyColumn()), yFilters);
					data[i] = new LstmData(x, y);
				}
				((Dnn<LstmData>)this.model).mountData(data);
				break;
			}

			ModelRunner mr = new ModelRunner(this.model);
			synchronized (this.model.indicator) {
				Executor exec = Executors.newSingleThreadExecutor();
				exec.execute(mr);
				int epoch = 0;
				while (true) {
					try {
						this.model.indicator.wait();
						if (this.model.containCatalog("final")) {
							break;
						}
						epoch += watchEpoch;
						System.out.println("Epoch " + epoch + ",\tLoss: " + this.model.getValue("loss", epoch));
					} catch (InterruptedException e) {
						e.printStackTrace();
						break;
					} catch (UsageException e) {
						e.printStackTrace();
					}
				}
				System.out.println("finished at epoch " + epoch);
			}

			Model model = new Model();
			model.setModelId(modelContext.get("modelId").toString());
			model.setStructure(Dnn.getBytes(this.model));
			model.setStatus(Status.Normal.status);
			mh.updateModelStructureById(model);

		} catch (FilterException e) {
			e.printStackTrace();
			Model model = new Model();
			model.setModelId(modelContext.get("modelId").toString());
			model.setStatus(Status.Normal.status);
			mh.updateModelStatusById(model);
		} finally {
			done();
		}
	}

	private class ModelRunner implements Runnable {

		private Dnn<?> model;

		public ModelRunner(Dnn<?> model) {
			this.model = model;
		}

		@Override
		public void run() {
			try {
				((DeepLstm)this.model).train();
			} catch (DnnException | MatrixException e) {
				e.printStackTrace();
			}
		}
	}

	public void done() {
		daoHandler.releaseSession();
	}
}
