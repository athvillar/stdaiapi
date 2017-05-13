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
import cn.standardai.lib.algorithm.rnn.lstm.DeepLstm;
import cn.standardai.lib.algorithm.rnn.lstm.Lstm;
import cn.standardai.lib.algorithm.rnn.lstm.LstmData;
import cn.standardai.lib.base.matrix.MatrixException;

public class ModelGhost implements Runnable {

	private DaoHandler daoHandler = new DaoHandler(true);

	private Dnn<LstmData> model;

	private LstmData[] data;

	private Map<String, Object> modelContext;

	// TODO delete this
	private char[] dic;

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

	public void loadDic(char[] dic) {
		this.dic = dic;
	}

	public void loadModel(Dnn<LstmData> model) {
		this.model = model;
	}

	public void loadData(LstmData[] data) {
		this.data = data;
	}

	public void loadParam(String key, Object value) {
		this.modelContext.put(key, value);
	}

	@Override
	public void run() {

		if (this.model instanceof DeepLstm) {
			Integer watchEpoch = (Integer)modelContext.get("watchEpoch");
			((DeepLstm)this.model).setDth((Double)modelContext.get("dth"));
			((DeepLstm)this.model).setLearningRate((Double)modelContext.get("learningRate"));
			((DeepLstm)this.model).setBatchSize((Integer)modelContext.get("batchSize"));
			((DeepLstm)this.model).setDiverseDataRate((int[])modelContext.get("diverseDataRate"));
			((DeepLstm)this.model).setEpoch((Integer)modelContext.get("epoch"));
			((DeepLstm)this.model).setTestLossIncreaseTolerance((Integer)modelContext.get("testLossIncreaseTolerance"));
			((DeepLstm)this.model).setTrainSecond((Integer)modelContext.get("trainSecond"));
			((DeepLstm)this.model).setWatchEpoch(watchEpoch);
			this.model.mountData(data);

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

			String hint = " ";
			Double[][] predictXs = getX(hint, dic);
			Integer[] result;
			try {
				result = ((DeepLstm)this.model).predict(predictXs);
				for (int i = 0; i < result.length; i++) {
					System.out.print(dic[result[i]]);
				}
				System.out.println("");
			} catch (DnnException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ModelHandler mh = new ModelHandler(daoHandler);
			Model model = new Model();
			model.setModelId(modelContext.get("modelId").toString());
			model.setStructure(Lstm.getBytes((Lstm)this.model));
			model.setStatus(Status.Normal.status);
			mh.updateModelStructureById(model);
		}

		done();
	}

	private class ModelRunner implements Runnable {

		private Dnn<LstmData> model;

		public ModelRunner(Dnn<LstmData> model) {
			this.model = model;
		}

		@Override
		public void run() {
			if (this.model instanceof Lstm) {
				try {
					((DeepLstm)this.model).train();
				} catch (DnnException | MatrixException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void done() {
		daoHandler.releaseSession();
	}

	// TODO delete this
	private static Double[][] getX(String words, char[] dic) {
		char[] c = words.toCharArray();
		return getX(c, dic);
	}

	private static Double[][] getX(char[] words, char[] dic) {
		Double[][] result = new Double[words.length][dic.length];
		for (int i = 0; i < result.length; i++) {
			Double[] result1 = new Double[dic.length];
			for (int j = 0; j < dic.length; j++) {
				if (words[i] == dic[j]) {
					result1[j] = 1.0;
				} else {
					result1[j] = 0.0;
				}
			}
			result[i] = result1;
		}
		return result;
	}
}
