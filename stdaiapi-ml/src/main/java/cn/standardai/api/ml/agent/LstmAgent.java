package cn.standardai.api.ml.agent;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.base.AuthAgent;
import cn.standardai.api.dao.DataDao;
import cn.standardai.api.dao.DatasetDao;
import cn.standardai.api.dao.ModelDao;
import cn.standardai.api.dao.ModelTemplateDao;
import cn.standardai.api.dao.bean.Data;
import cn.standardai.api.dao.bean.Dataset;
import cn.standardai.api.dao.bean.Model;
import cn.standardai.api.dao.bean.ModelTemplate;
import cn.standardai.api.ml.exception.MLException;
import cn.standardai.api.ml.run.ModelGhost;
import cn.standardai.lib.algorithm.common.DataUtil;
import cn.standardai.lib.algorithm.rnn.lstm.Lstm;

public class LstmAgent extends AuthAgent {

	public JSONObject create(JSONObject request) throws MLException {

		JSONObject structure = request.getJSONObject("structure");
		if (structure == null) throw new MLException("缺少表达式");

		/*
		 * {
		 *   "layerSize":8,
		 *   "inputSize":10,
		 *   "outputSize":10
		 * }
		 */
		Integer layerSize = structure.getInteger("layerSize");
		Integer inputSize = structure.getInteger("inputSize");
		Integer outputSize = structure.getInteger("outputSize");
		if (layerSize == null || inputSize == null || outputSize == null) throw new MLException("缺少必要的参数");

		ModelAgent ma = new ModelAgent(daoHandler);
		return ma.create(userId, request.getString("name"), "LSTM", structure.toJSONString());
	}

	public JSONObject process(String id, JSONObject request) throws MLException {

		ModelTemplateDao modelTemplateDao = daoHandler.getMySQLMapper(ModelTemplateDao.class);
		ModelTemplate modelTemplate = modelTemplateDao.selectById(id);
		if (modelTemplate == null) {
			modelTemplate = modelTemplateDao.selectByKey(id, userId);
			if (modelTemplate == null) throw new MLException("模型不存在");
		}

		JSONObject result = new JSONObject();
		Lstm lstm = null;
		String label = request.getString("label");
		JSONObject train = request.getJSONObject("train");
		if (train != null) {

			ModelDao modelDao = daoHandler.getMySQLMapper(ModelDao.class);
			String parentModelId = null;
			Integer dataCount = 0;

			if (label != null) {
				// 有label，找到此label的最近模型
				List<Model> models = modelDao.selectByLabelModelTemplateId(label, modelTemplate.getModelTemplateId());
				if (models == null || models.size() == 0) {
					// 无模型，新建模型
					JSONObject structure = JSONObject.parseObject(modelTemplate.getScript());
					Integer layerSize = structure.getInteger("layerSize");
					Integer inputSize = structure.getInteger("inputSize");
					Integer outputSize = structure.getInteger("outputSize");
					lstm = new Lstm(layerSize, inputSize, outputSize);
				} else {
					// 有模型，使用最新模型继续训练
					//lstm = Lstm.getInstance(models.get(0).getStructure());
					parentModelId = models.get(0).getModelId();
					dataCount = models.get(0).getDataCount();
				}
			} else {
				JSONObject structure = JSONObject.parseObject(modelTemplate.getScript());
				Integer layerSize = structure.getInteger("layerSize");
				Integer inputSize = structure.getInteger("inputSize");
				Integer outputSize = structure.getInteger("outputSize");
				lstm = new Lstm(layerSize, inputSize, outputSize);
			}

			// 训练网络
			/*
			 * {
			 *   "dth":1,
			 *   "learningRate":0.0125,
			 *   "dLearningRate":0.99,
			 *   "maxLearningRate":0.0125,
			 *   "gainThreshold":0.95,
			 *   "watchEpoch":5,
			 *   "epoch":1000,
			 *   "datasetName": "sampleArticle",
			 *   "datasetUsage": "SGLWD"
			 * }
			 */
			ModelGhost mg = new ModelGhost();
			mg.loadModel(lstm);
			mg.loadParam("dth", train.getDouble("dth"));
			mg.loadParam("learningRate", train.getDouble("learningRate"));
			mg.loadParam("dLearningRate", train.getDouble("dLearningRate"));
			mg.loadParam("maxLearningRate", train.getDouble("maxLearningRate"));
			mg.loadParam("gainThreshold", train.getDouble("gainThreshold"));
			mg.loadParam("watchEpoch", train.getInteger("watchEpoch"));
			mg.loadParam("epoch", train.getInteger("epoch"));

			List<Data> dataList = getData(getDataset(train));
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

			mg.invoke();

		}

		return result;
	}


	private Dataset getDataset(JSONObject json) throws MLException {

		Dataset dataset;
		DatasetDao datasetDao = daoHandler.getMySQLMapper(DatasetDao.class);
		String datasetId = json.getString("datasetId");
		if (datasetId != null) {
			dataset = datasetDao.selectById(datasetId);
			if (dataset == null) throw new MLException("dataset不存在");
		} else {
			String datasetName = json.getString("datasetName");
			if (datasetName != null) {
				dataset = datasetDao.selectByKey(datasetName, userId);
				if (dataset == null) throw new MLException("dataset不存在");
			} else {
				throw new MLException("未指定dataset");
			}
		}

		return dataset;
	}

	private List<Data> getData(Dataset dataset) throws MLException {
		DataDao dataDao = daoHandler.getMySQLMapper(DataDao.class);
		List<Data> data = dataDao.selectDataByDatasetId(dataset.getDatasetId());
		if (data == null) throw new MLException("指定dataset无数据");
		return data;
	}

	public JSONObject status(String id) {
		// TODO Auto-generated method stub
		return null;
	}
}
