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
import cn.standardai.lib.base.function.Statistic;

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

			Dataset dataset = getDataset(userId, train);
			List<Data> trainData = getData(dataset);

			ModelDao modelDao = daoHandler.getMySQLMapper(ModelDao.class);
			String parentModelId = null;
			Integer dataCount = 0;
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
						dataCount = models.get(0).getDataCount();
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
			for (Data data1 : trainData) {
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
			Integer step = train.getInteger("step");
			if (batchSize == null) batchSize = trainData.size();
			if (batchCount == null) batchCount = 1;
			if (step == null) step = batchCount;
			for (int i = 0; i < batchCount; i += step) {
				cnn.train(batchSize, step);
				// 更新DB
				if (label != null) {
					Model newModel = new Model();
					String modelId = MathUtil.random(31);
					result.put("modelId", modelId);
					newModel.setModelId(modelId);
					newModel.setModelTemplateId(modelTemplate.getModelTemplateId());
					newModel.setParentModelId(parentModelId);
					parentModelId = newModel.getModelId();
					newModel.setLabel(label);
					newModel.setDatasetId(dataset.getDatasetId());
					dataCount += batchSize * step;
					newModel.setDataCount(dataCount);
					newModel.setBatchSize(batchSize);
					newModel.setBatchCount(step);
					newModel.setStructure(CNN.getBytes(cnn));
					newModel.setCreateTime(new Date());
					modelDao.insert(newModel);
				}

				JSONObject test = request.getJSONObject("test");
				List<Data> testData = null;
				if (test != null && testData == null) {
					Dataset testDataset = getDataset(userId, test);
					testData = getData(testDataset);
				}
				if (test != null) {
					stepCheck(modelTemplate.getModelTemplateName(), cnn, trainData, testData, i + step);
				}
			}
		}

		JSONObject test = request.getJSONObject("test");
		if (test != null) {

			Dataset dataset = getDataset(userId, test);
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
							// 有模型，使用最新模型
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
			JSONArray dataResultJSONArray = new JSONArray();
			Double[] correctRateWithWeight = new Double[data.size()];
			Double[] correctRate = new Double[data.size()];
			int dataIndex = 0;
			for (Data data1 : data) {
				switch (data1.getType()) {
				case "json":
					JSONArray data1ResultJSONArray = new JSONArray();
					JSONObject data1ResultJSONObject = new JSONObject();
					Double[][][] resultData = cnn.predict(JSONObject.parseObject(data1.getData()));
					if (resultData == null) continue;
					Double max = Double.NEGATIVE_INFINITY;
					Integer maxIndex = -1;
					Double sum = 0.0;
					for (int i = 0; i < resultData[0][0].length; i++) {
						data1ResultJSONArray.add(resultData[0][0][i]);
						sum += resultData[0][0][i];
						if (resultData[0][0][i] > max) {
							max = resultData[0][0][i];
							maxIndex = i;
						}
					}
					JSONArray target = JSONObject.parseObject(data1.getData()).getJSONArray("target");
					Integer correctIndex = 0;
					for (int i = 0; i < target.size(); i++) {
						if (target.getInteger(i) == 1) {
							correctIndex = i;
							break;
						}
					}
					correctRateWithWeight[dataIndex] = resultData[0][0][correctIndex] / sum;
					if (maxIndex == correctIndex) {
						correctRate[dataIndex] = 1.0;
					} else {
						correctRate[dataIndex] = 0.0;
					}
					//data1ResultJSONObject.put("maxIndex", maxIndex);
					//data1ResultJSONObject.put("maxScore", max);
					//data1ResultJSONObject.put("correctIndex", correctIndex);
					//data1ResultJSONObject.put("correctRate", correctRate);
					//data1ResultJSONObject.put("correctRateWithWeight", correctRateWithWeight);
					//data1ResultJSONObject.put("scores", data1ResultJSONArray);
					dataResultJSONArray.add(data1ResultJSONObject);
					break;
				default:
					break;
				}
				dataIndex++;
			}
			result.put("predict", dataResultJSONArray);
			result.put("correctRateTotal", Statistic.avg(correctRate));
			result.put("correctRateWithWeightTotal", Statistic.avg(correctRateWithWeight));
		}

		return result;
	}

	private void stepCheck(String modelTemplateName, CNN cnn, List<Data> trainData, List<Data> testData, Integer count) {

		Double[] trainCorrectRate = null;
		Double[] testCorrectRate = null;
		if (trainData != null) trainCorrectRate = stepCheck(cnn, trainData);
		if (testData != null) testCorrectRate = stepCheck(cnn, testData);

		System.out.println("" + DateUtil.format(new Date(), DateUtil.YYYY__MM__DD__HH__MM__SS) + " " +
				modelTemplateName + " TrCnt: " + count +
				(trainCorrectRate == null ? "" : ("\tTrCR:" + trainCorrectRate[0] + "," + trainCorrectRate[1])) +
				(testCorrectRate == null ? "" : ("\tTsCR:" + testCorrectRate[0] + "," + testCorrectRate[1])));
	}

	private Double[] stepCheck(CNN cnn, List<Data> data) {

		if (data == null) return null;
		Double[] correctRatesWithWeigth = new Double[data.size()];
		Double[] correctRates = new Double[data.size()];
		for (int index = 0; index < data.size(); index++) {
			// 载入数据
			switch (data.get(index).getType()) {
			case "json":
				Double[][][] resultData = cnn.predict(JSONObject.parseObject(data.get(index).getData()));
				if (resultData == null) continue;
				// 输出预测
				Double max = Double.NEGATIVE_INFINITY;
				Double sum = 0.0;
				Integer maxIndex = -1;
				for (int i = 0; i < resultData[0][0].length; i++) {
					sum += resultData[0][0][i];
					if (resultData[0][0][i] > max) {
						max = resultData[0][0][i];
						maxIndex = i;
					}
				}
				JSONArray target = JSONObject.parseObject(data.get(index).getData()).getJSONArray("target");
				Integer correctIndex = 0;
				for (int i = 0; i < target.size(); i++) {
					if (target.getInteger(i) == 1) {
						correctIndex = i;
						break;
					}
				}
				correctRatesWithWeigth[index] = resultData[0][0][correctIndex] / sum;
				if (maxIndex == correctIndex) {
					correctRates[index] = 1.0;
				} else {
					correctRates[index] = 0.0;
				}
				break;
			default:
				break;
			}
		}

		Double[] correctRate = new Double[2];
		correctRate[0] = Statistic.avg(correctRates);
		correctRate[1] = Statistic.avg(correctRatesWithWeigth);
		return correctRate;
	}

	private Dataset getDataset(String userId, JSONObject json) throws MLException {

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

	public void done() {
		daoHandler.releaseSession();
	}

	public JSONObject status(String userId, String id) {
		// TODO Auto-generated method stub
		return null;
	}
}
