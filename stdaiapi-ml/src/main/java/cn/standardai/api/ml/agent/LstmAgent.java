package cn.standardai.api.ml.agent;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.base.AuthAgent;
import cn.standardai.api.dao.bean.Data;
import cn.standardai.api.ml.bean.DnnModel;
import cn.standardai.api.ml.daohandler.DataHandler;
import cn.standardai.api.ml.daohandler.ModelHandler;
import cn.standardai.api.ml.exception.MLException;
import cn.standardai.api.ml.run.ModelGhost;
import cn.standardai.lib.algorithm.common.DataUtil;
import cn.standardai.lib.algorithm.rnn.lstm.Lstm;

public class LstmAgent extends AuthAgent {

	private ModelHandler mh = new ModelHandler(daoHandler);

	private DataHandler dh = new DataHandler(daoHandler);

	/*
	 * {
	 *   "name": "testlstm",
	 *   "structure": {
	 *     "layerSize":8,
	 *     "inputSize":7,
	 *     "outputSize":7
	 *   }
	 * }
	 */
	public JSONObject create(JSONObject request) throws MLException {

		JSONObject structure = request.getJSONObject("structure");
		if (structure == null) throw new MLException("缺少表达式");

		Integer layerSize = structure.getInteger("layerSize");
		Integer inputSize = structure.getInteger("inputSize");
		Integer outputSize = structure.getInteger("outputSize");
		if (layerSize == null || inputSize == null || outputSize == null) throw new MLException("缺少必要的参数");

		return mh.createModel(userId, request.getString("name"), "LSTM", structure.toJSONString());
	}

	/*
	 * {
	 *   "parent": "xxx",
	 *   "new": true,
	 *   "dic": {
	 *     "dicId": xxx,
	 *     "dicName": xxx
	 *   }
	 *   "train": {
	 *     "dth": 1,
	 *     "learningRate": 0.0125,
	 *     "dLearningRate": 0.99,
	 *     "maxLearningRate": 0.0125,
	 *     "gainThreshold": 0.95,
	 *     "watchEpoch": 5,
	 *     "epoch": 1000,
	 *     "datasetName": "sampleArticle",
	 *     "datasetUsage": "SGLWD"
	 *   }
	 *   "test":{
	 *     "hint": "a",
	 *     "length": 100
	 *   }
	 * }
	 */
	public JSONObject process(String id, JSONObject request) throws MLException {

		String parentModelId = request.getString("parent");
		DnnModel model = mh.findModel(userId, id, id, parentModelId);

		Lstm lstm = createLstm(model);
		JSONObject result = new JSONObject();

		JSONObject train = request.getJSONObject("train");
		JSONObject test = request.getJSONObject("test");
		if (train != null) {
			// 训练网络
			ModelGhost mg = new ModelGhost();
			mg.loadModel(lstm);
			mg.loadParam("dth", train.getDouble("dth"));
			mg.loadParam("learningRate", train.getDouble("learningRate"));
			mg.loadParam("dLearningRate", train.getDouble("dLearningRate"));
			mg.loadParam("maxLearningRate", train.getDouble("maxLearningRate"));
			mg.loadParam("gainThreshold", train.getDouble("gainThreshold"));
			mg.loadParam("watchEpoch", train.getInteger("watchEpoch"));
			mg.loadParam("epoch", train.getInteger("epoch"));

			List<Data> dataList = dh.getData(dh.getDataset(userId, train));
			if (dataList == null || dataList.size() == 0) {
				throw new MLException("找不到数据(datasetName:" + train.getJSONObject("datasetName") +")");
			}
			switch (train.getString("datasetUsage")) {
			case "SGLWD":
				char[] dic = DataUtil.String2CharDic(dataList.get(0).getData());
				String yWords = dataList.get(0).getData().substring(1) + dataList.get(0).getData().charAt(0);
				Double[][] xs = DataUtil.getX(dataList.get(0).getData(), dic);
				Integer[] ys = DataUtil.getY(yWords, dic);
				mg.loadData(xs, ys);
				break;
			default:
				throw new MLException("不支持的数据使用方式(datasetUsage:" + train.getString("datasetUsage") + ")");
			}

			Boolean newFlag = request.getBoolean("new");
			mh.upgradeModel2Training(model, newFlag);

			// 训练
			mg.invoke();
		} else if (test != null) {
			String hint = test.getString("hint");
			Double[][] predictXs = getX("I", dic);
			Integer[] predictYs = lstm.predict(predictXs, 100);
			for (int i = 0; i < predictYs.length; i++) {
				System.out.print(dic[predictYs[i]]);
			}
		}

		return result;
	}

	private Lstm createLstm(DnnModel model) {

		Lstm lstm;
		if (model.getStructure() == null) {
			// 无模型，新建模型
			JSONObject structure = JSONObject.parseObject(model.getScript());
			Integer layerSize = structure.getInteger("layerSize");
			Integer inputSize = structure.getInteger("inputSize");
			Integer outputSize = structure.getInteger("outputSize");
			lstm = new Lstm(layerSize, inputSize, outputSize);
		} else {
			// 有模型，使用最新模型继续训练
			lstm = Lstm.getInstance(model.getStructure());
		}

		return lstm;
	}

	public JSONObject status(String id) {
		// TODO Auto-generated method stub
		return null;
	}
}
