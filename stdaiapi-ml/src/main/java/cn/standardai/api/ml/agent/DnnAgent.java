package cn.standardai.api.ml.agent;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.base.AuthAgent;
import cn.standardai.api.core.exception.AuthException;
import cn.standardai.api.dao.bean.Dataset;
import cn.standardai.api.dao.bean.Model;
import cn.standardai.api.ml.bean.DnnAlgorithm;
import cn.standardai.api.ml.bean.DnnDataSetting;
import cn.standardai.api.ml.bean.DnnModelSetting;
import cn.standardai.api.ml.bean.DnnTrainSetting;
import cn.standardai.api.ml.daohandler.DataHandler;
import cn.standardai.api.ml.daohandler.ModelHandler;
import cn.standardai.api.ml.exception.JSONFormatException;
import cn.standardai.api.ml.exception.MLException;
import cn.standardai.api.ml.run.ModelGhost;
import cn.standardai.lib.algorithm.base.Dnn;
import cn.standardai.lib.algorithm.cnn.Cnn;
import cn.standardai.lib.algorithm.exception.DnnException;
import cn.standardai.lib.algorithm.rnn.lstm.DeepLstm;

public class DnnAgent extends AuthAgent {

	private ModelHandler mh = new ModelHandler(daoHandler);

	private DataHandler dh = new DataHandler(daoHandler);

	/*
	 * {
	 *   "name": "testlstm",
	 *   "algorithm": "LSTM/CNN",
	 *   "data": { ... },
	 *   "structure": { ... }
	 * }
	 */
	public JSONObject create(JSONObject request) throws MLException {

		String modelTemplateName = request.getString("name");
		if (modelTemplateName == null || "".equals(modelTemplateName)) throw new JSONFormatException("缺少模型名(name)");

		DnnAlgorithm algorithm = DnnAlgorithm.resolve(request.getString("algorithm"));
		if (algorithm == null) throw new JSONFormatException("缺少算法(algorithm=" + algorithm + ")");

		JSONObject data = request.getJSONObject("data");
		if (data == null) throw new JSONFormatException("缺少数据(data)");
		DnnDataSetting dataSetting = DnnDataSetting.parse(data);
		Dataset dataset = dh.getDataset(this.userId, dataSetting.getDatasetId(), dataSetting.getDatasetName());
		if (dataset == null) throw new JSONFormatException("找不到指定的数据集(datasetId=" + dataSetting.getDatasetId() + ", datasetName=" + dataSetting.getDatasetName() + ")");

		setDataSetting(dataSetting, dataset, algorithm);

		JSONObject structure = request.getJSONObject("structure");
		if (structure == null) throw new JSONFormatException("缺少模型结构(structure)");

		return mh.createModel(userId, modelTemplateName, algorithm, dataSetting, structure.toJSONString());
	}

	private void setDataSetting(DnnDataSetting dataSetting, Dataset dataset, DnnAlgorithm algorithm) {

		// 如果用户没有输入的话，根据数据集和算法选择filter和column
		dataSetting.setDatasetId(dataset.getDatasetId());
		if (dataSetting.getxColumn() == null || "".equals(dataSetting.getxColumn())) {
			switch (dataset.getType().toLowerCase()) {
			case "file":
				dataSetting.setxColumn("table.data.ref");
				break;
			default:
				dataSetting.setxColumn("table.data.x");
				break;
			}
		}
		if (dataSetting.getyColumn() == null || "".equals(dataSetting.getyColumn())) {
			dataSetting.setyColumn("table.data.y");
		}
		if (dataSetting.getxFilter() == null || "".equals(dataSetting.getxFilter())) {
			switch (algorithm) {
			case cnn:
				switch (dataset.getFormat().toLowerCase()) {
				case "json":
					dataSetting.setxFilter("Json3");
					break;
				case "jpg":
				case "bmp":
				case "png":
					dataSetting.setxFilter("RGBImageFilter|NormalizeIntegerFilter");
					break;
				default:
					dataSetting.setxFilter("Default3");
					break;
				}
				break;
			case lstm:
				switch (dataset.getFormat().toLowerCase()) {
				case "json":
					dataSetting.setxFilter("Json2");
					break;
				case "jpg":
				case "bmp":
				case "png":
					dataSetting.setxFilter("GrayImageFilter|NormalizeIntegerFilter");
					break;
				default:
					dataSetting.setxFilter("Default2");
					break;
				}
				break;
			default:
				break;
			}
		}
		if (dataSetting.getyFilter() == null || "".equals(dataSetting.getyFilter())) {
			dataSetting.setyFilter("SequenceIntegerFilter");
		}
	}

	/*
	 * {
	 *   "parent": "xxx",
	 *   "new": true,
	 *   "train": {
	 *     "dth":1,
	 *     "learningRate":0.07,
	 *     "epoch":8000,
	 *     "trainSecond": 3600,
	 *     "batchSize": 100,
	 *     "watchEpoch":1,
	 *     "testLossIncreaseTolerance":3
	 *   }
	 * }
	 */
	public JSONObject train(String userId, String modelTemplateName, JSONObject request) throws MLException, DnnException, AuthException {

		if (!userId.equals(this.userId)) throw new AuthException("没有权限");

		String parentModelId = request.getString("parentModelId");
		boolean isNew = request.getBooleanValue("new");
		JSONObject train = request.getJSONObject("train");
		if (train == null) throw new JSONFormatException("缺少训练参数(train)");
		DnnTrainSetting ts = DnnTrainSetting.parse(train);

		DnnModelSetting modelSetting;
		if (parentModelId == null) {
			modelSetting = mh.findLastestModel(userId, modelTemplateName);
			if (modelSetting == null) throw new MLException("模型不存在");
		} else {
			modelSetting = mh.findModel(userId, modelTemplateName, parentModelId);
			if (modelSetting == null) throw new MLException("模型不存在");
			modelSetting.setParentModelId(parentModelId);
		}
		Dnn<?> dnn = createModel(modelSetting);

		ModelGhost mg = new ModelGhost();
		mg.loadModel(dnn);
		mg.loadParam("trainSetting", ts);
		mg.loadParam("modelSetting", modelSetting);

		mh.upgradeModel2Training(modelSetting, isNew);
		mg.invoke();

		JSONObject result = new JSONObject();
		result.put("modelId", modelSetting.getModelId());
		return result;
	}

	private Dnn<?> createModel(DnnModelSetting model) throws DnnException {

		switch (model.getAlgorithm()) {
		case cnn:
			Cnn cnn;
			if (model.getStructure() == null) {
				// 无模型，新建模型
				JSONObject structure = JSONObject.parseObject(model.getScript());
				cnn = Cnn.getInstance(structure);
			} else {
				// 有模型，使用最新模型继续训练
				cnn = Cnn.getInstance(model.getStructure());
			}
			return cnn;
		case lstm:
			DeepLstm lstm;
			if (model.getStructure() == null) {
				// 无模型，新建模型
				JSONObject structure = JSONObject.parseObject(model.getScript());
				lstm = DeepLstm.getInstance(structure);
			} else {
				// 有模型，使用最新模型继续训练
				lstm = DeepLstm.getInstance(model.getStructure());
			}
			return lstm;
		default:
			return null;
		}
	}

	public JSONObject status(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	public JSONObject list() {
		List<Model> models = mh.findModels(userId);
		JSONObject result = new JSONObject();
		JSONArray jsonModels = new JSONArray();
		for (int i = 0; i < models.size(); i++) {
			JSONObject jsonModel = new JSONObject();
			jsonModel = (JSONObject) JSON.toJSON(models.get(0));
			jsonModels.add(jsonModel);
		}
		result.put("models", jsonModels);
		return result;
	}

	public JSONObject delete(String modelId) throws MLException {
		JSONObject result = new JSONObject();
		if (mh.deleteModel(modelId, userId) == 0) {
			throw new MLException("删除异常");
		}
		return result;
	}
}
