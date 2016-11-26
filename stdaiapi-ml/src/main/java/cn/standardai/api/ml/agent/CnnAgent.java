package cn.standardai.api.ml.agent;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.util.DateUtil;
import cn.standardai.api.core.util.MathUtil;
import cn.standardai.api.dao.DataDao;
import cn.standardai.api.dao.DatasetDao;
import cn.standardai.api.dao.ModelDao;
import cn.standardai.api.dao.ModelTemplateDao;
import cn.standardai.api.dao.base.DaoHandler;
import cn.standardai.api.dao.bean.Data;
import cn.standardai.api.dao.bean.Dataset;
import cn.standardai.api.dao.bean.Model;
import cn.standardai.api.dao.bean.ModelTemplate;
import cn.standardai.api.ml.exception.MLException;
import cn.standardai.lib.algorithm.cnn.CNN;
import cn.standardai.lib.algorithm.cnn.CnnException;
import cn.standardai.lib.algorithm.exception.StorageException;

public class CnnAgent {

	private DaoHandler daoHandler = new DaoHandler(true);

	public JSONObject create(String userId, JSONObject request) throws MLException {

		JSONObject structure = request.getJSONObject("structure");
		if (structure == null) throw new MLException("缺少表达式");

		try {
			CNN.getInstance(request);
		} catch (CnnException e) {
			throw new MLException("卷积神经网络创建失败", e);
		}

		JSONObject result = new JSONObject();
		ModelTemplateDao dao = daoHandler.getMySQLMapper(ModelTemplateDao.class);
		if (request.getString("name") != null) {
			ModelTemplate modelTemolate = dao.selectByKey(request.getString("name"), userId);
			if (modelTemolate != null) {
				ModelTemplate param = new ModelTemplate();
				param.setModelTemplateName(request.getString("name"));
				param.setScript(structure.toJSONString());
				param.setUserId(userId);
				param.setCreateTime(new Date());
				dao.updateByKey(param);
				result.put("id", modelTemolate.getModelTemplateId());
				result.put("name", modelTemolate.getModelTemplateName());
				result.put("time", DateUtil.format(param.getCreateTime(), DateUtil.YYYY_MM_DD_HH_MM_SS));
			} else {
				ModelTemplate param = new ModelTemplate();
				param.setModelTemplateId(MathUtil.random(17));
				param.setModelTemplateName(request.getString("name") == null ? param.getModelTemplateId() : request.getString("name"));
				param.setType("CNN");
				param.setScript(structure.toJSONString());
				param.setUserId(userId);
				param.setCreateTime(new Date());
				dao.insert(param);
				result.put("id", param.getModelTemplateId());
				result.put("name", param.getModelTemplateName());
				result.put("time", DateUtil.format(param.getCreateTime(), DateUtil.YYYY_MM_DD_HH_MM_SS));
			}
		} else {
			ModelTemplate param = new ModelTemplate();
			param.setModelTemplateId(MathUtil.random(17));
			param.setModelTemplateName(request.getString("name") == null ? param.getModelTemplateId() : request.getString("name"));
			param.setType("CNN");
			param.setScript(structure.toJSONString());
			param.setUserId(userId);
			param.setCreateTime(new Date());
			dao.insert(param);
			result.put("id", param.getModelTemplateId());
			result.put("name", param.getModelTemplateName());
			result.put("time", DateUtil.format(param.getCreateTime(), DateUtil.YYYY_MM_DD_HH_MM_SS));
		}

		return result;
	}

	public JSONObject process(String userId, String id, JSONObject request) throws MLException {

		ModelTemplateDao modelTemplateDao = daoHandler.getMySQLMapper(ModelTemplateDao.class);
		ModelTemplate modelTemplate = modelTemplateDao.selectById(id);
		if (modelTemplate == null) {
			modelTemplate = modelTemplateDao.selectByKey(id, userId);
			if (modelTemplate == null) throw new MLException("模型不存在");
		}

		JSONObject result = new JSONObject();
		CNN cnn = null;
		String label = request.getString("label");
		JSONObject train = request.getJSONObject("train");
		if (train != null) {

			Dataset dataset = getDataset(train);
			List<Data> data = getData(dataset);

			ModelDao modelDao = daoHandler.getMySQLMapper(ModelDao.class);
			String parentModelId = null;
			try {
				if (label != null) {
					// 有label，找到此label的最近模型
					List<Model> models = modelDao.selectByLabelModelTemplateId(label, modelTemplate.getModelTemplateId());
					if (models == null || models.size() == 0) {
						// 无模型，新建模型
						cnn = CNN.getInstance(JSONObject.parseObject(modelTemplate.getScript()));
					} else {
						// 有模型，使用最新模型继续训练
						cnn = CNN.getInstance(models.get(0).getStructure());
						parentModelId = models.get(0).getModelId();
					}
				} else {
					cnn = CNN.getInstance(JSONObject.parseObject(modelTemplate.getScript()));
				}
			} catch (CnnException e) {
				throw new MLException("模型创建失败", e);
			} catch (StorageException e) {
				throw new MLException("模型载入失败", e);
			}

			// 载入数据
			for (Data data1 : data) {
				switch (data1.getType()) {
				case "json":
					cnn.addData(JSONObject.parseObject(data1.getData()));
					break;
				default:
					break;
				}
			}

			// 训练网络
			Integer batchSize = train.getInteger("batchSize");
			Integer batchCount = train.getInteger("batchCount");
			if (batchSize == null) batchSize = data.size();
			if (batchCount == null) batchCount = 1;
			cnn.train(batchSize, batchCount);

			// 更新DB
			if (label != null) {
				Model newModel = new Model();
				String modelId = MathUtil.random(31);
				result.put("modelId", modelId);
				newModel.setModelId(modelId);
				newModel.setModelTemplateId(modelTemplate.getModelTemplateId());
				newModel.setParentModelId(parentModelId);
				newModel.setLabel(label);
				newModel.setDatasetId(dataset.getDatasetId());
				newModel.setDataCount(data.size());
				newModel.setBatchSize(batchSize);
				newModel.setBatchCount(batchCount);
				newModel.setStructure(CNN.getBytes(cnn));
				newModel.setCreateTime(new Date());
				modelDao.insert(newModel);
			}
		}

		JSONObject test = request.getJSONObject("test");
		if (test != null) {

			Dataset dataset = getDataset(test);
			List<Data> data = getData(dataset);

			ModelDao modelDao = daoHandler.getMySQLMapper(ModelDao.class);
			try {
				if (cnn == null) {
					if (label != null) {
						// 有label，找到此label的最近模型
						List<Model> models = modelDao.selectByLabelModelTemplateId(label, modelTemplate.getModelTemplateId());
						if (models == null) {
							// 无模型，新建模型
							cnn = CNN.getInstance(JSONObject.parseObject(modelTemplate.getScript()));
						} else {
							// 有模型，使用最新模型继续训练
							cnn = CNN.getInstance(models.get(0).getStructure());
						}
					} else {
						cnn = CNN.getInstance(JSONObject.parseObject(modelTemplate.getScript()));
					}
				}
			} catch (CnnException e) {
				throw new MLException("模型创建失败", e);
			} catch (StorageException e) {
				throw new MLException("模型载入失败", e);
			}

			// 载入数据
			JSONArray resultJSONArray = new JSONArray();
			for (Data data1 : data) {
				switch (data1.getType()) {
				case "json":
					Double[][][] resultData = cnn.predict(JSONObject.parseObject(data1.getData()));
					if (resultData == null) continue;
					for (int i = 0; i < resultData[0][0].length; i++) {
						resultJSONArray.add(resultData[0][0][i]);
					}
					break;
				default:
					break;
				}
			}
			result.put("predict", resultJSONArray);
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
				dataset = datasetDao.selectById(datasetId);
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

	public void done() {
		daoHandler.releaseSession();
	}

	public JSONObject status(String userId, String id) {
		// TODO Auto-generated method stub
		return null;
	}
}
