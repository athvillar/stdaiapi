package cn.standardai.api.ml.agent;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.base.AuthAgent;
import cn.standardai.api.core.exception.AuthException;
import cn.standardai.api.dao.bean.Data;
import cn.standardai.api.dao.bean.Dataset;
import cn.standardai.api.dao.bean.ModelTemplate;
import cn.standardai.api.ml.bean.DnnAlgorithm;
import cn.standardai.api.ml.bean.DnnDataSetting;
import cn.standardai.api.ml.bean.DnnModelSetting;
import cn.standardai.api.ml.bean.DnnTrainSetting;
import cn.standardai.api.ml.daohandler.DataHandler;
import cn.standardai.api.ml.daohandler.ModelHandler;
import cn.standardai.api.ml.exception.JSONFormatException;
import cn.standardai.api.ml.exception.MLException;
import cn.standardai.api.ml.filter.DataFilter;
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

		JSONObject structure = request.getJSONObject("structure");
		if (structure == null) throw new JSONFormatException("缺少模型结构(structure)");
		Cnn cnn = null;
		try {
			switch (algorithm) {
			case cnn:
				cnn = Cnn.getInstance(structure);
				break;
			case lstm:
				DeepLstm.getInstance(structure);
				break;
			}
		} catch (DnnException e) {
			throw new MLException("模型创建脚本执行失败", e);
		}
		setDataSetting(dataSetting, dataset, algorithm, cnn);

		return mh.createModel(userId, modelTemplateName, algorithm, dataSetting, structure.toJSONString());
	}

	/*
	 * {
	 *   "name": "testlstm",
	 *   "algorithm": "LSTM/CNN",
	 *   "data": { ... },
	 *   "structure": { ... }
	 * }
	 */
	public JSONObject predict(String userId, String modelTemplateName, JSONObject request) throws MLException, DnnException {

		String modelId = request.getString("modelId");
		DnnModelSetting ms;
		if (modelId == null) {
			ms = mh.findLastestModel(userId, modelTemplateName);
		} else {
			ms = mh.findModel(userId, modelTemplateName, modelId);
		}
		if (ms == null) throw new MLException("找不到模型(" + userId + "/" + modelTemplateName + (modelId == null ? "" : "/" + modelId) + ")");
		Dnn<?> dnn = createModel(ms);

		JSONObject data = request.getJSONObject("data");
		if (data == null) throw new JSONFormatException("缺少数据(data)");
		DnnDataSetting ds = DnnDataSetting.parse(data);
		Dataset dataset = dh.getDataset(this.userId, ds.getDatasetId(), ds.getDatasetName());
		if (dataset == null) throw new JSONFormatException("找不到指定的数据集(datasetId=" + ds.getDatasetId() + ", datasetName=" + ds.getDatasetName() + ")");

		if (ds.getDatasetId() == null || "".equals(ds.getDatasetId())) {
			ds.setDatasetId(ms.getDataSetting().getDatasetId());
		}
		if (ds.getxColumn() == null || "".equals(ds.getxColumn())) {
			ds.setxColumn(ms.getDataSetting().getxColumn());
		}
		if (ds.getxFilter() == null || "".equals(ds.getxFilter())) {
			ds.setxFilter(ms.getDataSetting().getxFilter());
		}
		if (ds.getyFilter() == null || "".equals(ds.getyFilter())) {
			ds.setyFilter(ms.getDataSetting().getyFilter());
		}

		DataHandler dh = new DataHandler(this.daoHandler);
		List<Data> rawData = dh.getData(dataset);
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

		JSONObject result = new JSONObject();
		switch (ms.getAlgorithm()) {
		case cnn:
			Integer[][] ys1 = new Integer[rawData.size()][];
			for (int i = 0; i < rawData.size(); i++) {
				Integer[][][] x = DataFilter.encode(ds.getData(rawData.get(i), ds.getxColumn()), xFilters);
				ys1[i] = ((Cnn)dnn).predictY(x);
			}
			result.put("y", I22J(ys1));
			break;
		case lstm:
			JSONObject lstmJ = request.getJSONObject("lstm");
			Integer terminator = null;
			Integer steps = null;
			if (lstmJ != null) {
				terminator = lstmJ.getInteger("terminator");
				steps = lstmJ.getInteger("steps");
			}
			Integer[][] ys2 = new Integer[rawData.size()][];
			for (int i = 0; i < rawData.size(); i++) {
				Double[][] x = DataFilter.encode(ds.getData(rawData.get(i), ds.getxColumn()), xFilters);
				ys2[i] = ((DeepLstm)dnn).predictY(x, terminator, steps);
			}
			result.put("value", I22J(ys2));
			break;
		default:
			break;
		}

		return result;
	}

	private JSONArray I22J(Integer[][] ys) {
		JSONArray arrJ2 = new JSONArray();
		for (int i = 0; i < ys.length; i++) {
			JSONArray arrJ1 = new JSONArray();
			for (int j = 0; j < ys[i].length; j++) {
				arrJ1.add(ys[i][j]);
			}
			arrJ2.add(arrJ1);
		}
		return arrJ2;
	}

	private void setDataSetting(DnnDataSetting dataSetting, Dataset dataset, DnnAlgorithm algorithm, Cnn cnn) {

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
					if (cnn.layers.get(0).depth == 3) {
						dataSetting.setxFilter("RGBImageFilter");
					} else {
						dataSetting.setxFilter("GrayImageFilter|ExpInteger3D");
					}
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
			switch (algorithm) {
			case cnn:
				dataSetting.setyFilter("SequenceIntegerFilter|SprInteger1D(" + cnn.layers.get(cnn.layers.size() - 1).depth + ")");
				break;
			case lstm:
				dataSetting.setyFilter("SmartSplitFilter");
				break;
			}
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
		List<ModelTemplate> modelTemplates = mh.findModelTemplates(userId);
		JSONObject result = new JSONObject();
		JSONArray modelsJ = new JSONArray();
		for (int i = 0; i < modelTemplates.size(); i++) {
			JSONObject modelJ = (JSONObject) JSON.toJSON(modelTemplates.get(i));
			modelsJ.add(modelJ);
		}
		result.put("models", modelsJ);
		return result;
	}

	public JSONObject delete(String userId, String modelTemplateName) throws MLException, AuthException {
		if (!userId.equals(this.userId)) throw new AuthException("没有权限");
		if (mh.deleteModelTemplate(modelTemplateName, userId) == 0) throw new MLException("模型不存在");
		return new JSONObject();
	}
}
