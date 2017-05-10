package cn.standardai.api.ml.run;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import cn.standardai.api.dao.base.DaoHandler;
import cn.standardai.api.dao.bean.Model;
import cn.standardai.api.ml.bean.DnnModel.Status;
import cn.standardai.api.ml.daohandler.ModelHandler;
import cn.standardai.lib.algorithm.base.Dnn;
import cn.standardai.lib.algorithm.exception.DnnException;
import cn.standardai.lib.algorithm.exception.UsageException;
import cn.standardai.lib.algorithm.rnn.lstm.Lstm;

public class ModelGhost implements Runnable {

	private DaoHandler daoHandler = new DaoHandler(true);

	private Dnn model;

	private Object trainX;

	private Object trainY;

	private Map<String, Object> modelContext;

	public ModelGhost() {
		this.modelContext = new HashMap<String, Object>();
	}

	public void invoke() {

		this.model.addIndicator("loss");
		this.model.addIndicator("correctRate");
		Executor exec = Executors.newSingleThreadExecutor();
		exec.execute(this);

		return;
	}

	public void loadModel(Dnn model) {
		this.model = model;
	}

	public void loadData(Object trainX, Object trainY) {
		this.trainX = trainX;
		this.trainY = trainY;
	}

	public void loadParam(String key, Object value) {
		this.modelContext.put(key, value);
	}

	@Override
	public void run() {

		if (this.model instanceof Lstm) {
			int watchEpoch = (int)modelContext.get("watchEpoch");
			((Lstm)this.model).setParam(
					(double)modelContext.get("dth"),
					(double)modelContext.get("learningRate"),
					(double)modelContext.get("maxLearningRate"),
					(double)modelContext.get("dth"),
					(double)modelContext.get("gainThreshold"),
					watchEpoch,
					(int)modelContext.get("epoch"));
			ModelRunner mr = new ModelRunner(this.model, this.trainX, this.trainY);

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

			ModelHandler mh = new ModelHandler(daoHandler);
			Model model = new Model();
			model.setModelId(modelContext.get("modelId").toString());
			model.setStatus(Status.Normal.status);
			mh.updateModelById(model);
		}

		done();
	}

	private class ModelRunner implements Runnable {

		private Dnn model;

		private Object trainX;

		private Object trainY;

		public ModelRunner(Dnn model, Object trainX, Object trainY) {
			this.model = model;
			this.trainX = trainX;
			this.trainY = trainY;
		}

		@Override
		public void run() {
			if (this.model instanceof Lstm) {
				try {
					((Lstm)this.model).train((Double[][])trainX, (Integer[])trainY);
				} catch (DnnException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void done() {
		daoHandler.releaseSession();
	}
}
