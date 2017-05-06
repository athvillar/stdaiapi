package cn.standardai.api.ml.run;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import cn.standardai.api.dao.base.DaoHandler;
import cn.standardai.lib.algorithm.base.DNN;
import cn.standardai.lib.algorithm.exception.DnnException;
import cn.standardai.lib.algorithm.rnn.lstm.Lstm;

public class ModelGhost implements Runnable {

	private DaoHandler daoHandler;

	private DNN model;

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

	public void loadModel(DNN model) {
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

			((Lstm)this.model).setParam(
					(double)modelContext.get("dth"),
					(double)modelContext.get("learningRate"),
					(double)modelContext.get("maxLearningRate"),
					(double)modelContext.get("dth"),
					(double)modelContext.get("gainThreshold"),
					(int)modelContext.get("watchEpoch"),
					(int)modelContext.get("epoch"));
			ModelRunner mr = new ModelRunner(this.model, this.trainX, this.trainY);

			synchronized (this.model.indicator) {
				Executor exec = Executors.newSingleThreadExecutor();
				exec.execute(mr);
				while (true) {
					try {
						this.model.indicator.wait();
					} catch (InterruptedException e) {
						int cnt = this.model.indicator.get("loss").size();
						System.out.println("size:" + cnt);
						if (cnt > 10) {
							break;
						}
					}
				}
			}
		}
	}

	private class ModelRunner implements Runnable {

		private DNN model;

		private Object trainX;

		private Object trainY;

		public ModelRunner (DNN model, Object trainX, Object trainY) {
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
}
