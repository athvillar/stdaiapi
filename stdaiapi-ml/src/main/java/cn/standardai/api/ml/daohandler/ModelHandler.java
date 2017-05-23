package cn.standardai.api.ml.daohandler;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.util.DateUtil;
import cn.standardai.api.core.util.MathUtil;
import cn.standardai.api.dao.ModelDao;
import cn.standardai.api.dao.ModelTemplateDao;
import cn.standardai.api.dao.base.DaoHandler;
import cn.standardai.api.dao.bean.Model;
import cn.standardai.api.dao.bean.ModelTemplate;
import cn.standardai.api.ml.bean.DnnAlgorithm;
import cn.standardai.api.ml.bean.DnnDataSetting;
import cn.standardai.api.ml.bean.DnnModelSetting;
import cn.standardai.api.ml.bean.DnnModelSetting.Status;
import cn.standardai.api.ml.exception.MLException;

public class ModelHandler {

	private DaoHandler daoHandler;

	public ModelHandler(DaoHandler daoHandler) {
		this.daoHandler = daoHandler;
	}

	public JSONObject createModel(String userId, String modelTemplateName, DnnAlgorithm algorithm,
			DnnDataSetting dataSetting, String script) throws MLException {

		JSONObject result = new JSONObject();
		ModelTemplateDao dao = daoHandler.getMySQLMapper(ModelTemplateDao.class);

		// 按照模型名查找模型
		ModelTemplate modelTemolate = dao.selectByKey(modelTemplateName, userId);
		if (modelTemolate != null) {
			// 模型已存在，错误
			throw new MLException("模型已存在(name=" + modelTemplateName + ")");
		} else {
			ModelTemplate param = new ModelTemplate();
			param.setModelTemplateId(MathUtil.random(17));
			param.setModelTemplateName(modelTemplateName);
			param.setUserId(userId);
			param.setAlgorithm(algorithm.name());
			param.setScript(script);
			param.setDatasetId(dataSetting.getDatasetId());
			param.setxColumn(dataSetting.getxColumn());
			param.setxFilter(dataSetting.getxFilter());
			param.setyColumn(dataSetting.getyColumn());
			param.setyFilter(dataSetting.getyFilter());
			param.setCreateTime(new Date());
			dao.insert(param);
			result.put("id", param.getModelTemplateId());
			result.put("name", param.getModelTemplateName());
			result.put("time", DateUtil.format(param.getCreateTime(), DateUtil.YYYY_MM_DD_HH_MM_SS));
		}

		return result;
	}

	public DnnModelSetting findModel(String userId, String modelTemplateName, String modelId) {

		ModelTemplateDao modelTemplateDao = daoHandler.getMySQLMapper(ModelTemplateDao.class);
		ModelTemplate modelTemplate = modelTemplateDao.selectByKey(modelTemplateName, userId);
		if (modelTemplate == null) return null;

		DnnModelSetting ms = new DnnModelSetting();
		DnnDataSetting ds = new DnnDataSetting();
		ms.setModelTemplateId(modelTemplate.getModelTemplateId());
		ms.setScript(modelTemplate.getScript());
		ms.setUserId(userId);
		ds.setDatasetId(modelTemplate.getDatasetId());
		ds.setxColumn(modelTemplate.getxColumn());
		ds.setxFilter(modelTemplate.getxFilter());
		ds.setyColumn(modelTemplate.getyColumn());
		ds.setyFilter(modelTemplate.getyFilter());
		ms.setDataSetting(ds);

		ModelDao modelDao = daoHandler.getMySQLMapper(ModelDao.class);
		Model model = modelDao.selectByIdModelTemplateId(modelId, ms.getModelTemplateId());
		if (model == null) return null;
		ms.setModelId(model.getModelId());
		ms.setParentModelId(model.getParentModelId());
		ms.setStructure(model.getStructure());

		return ms;
	}

	public DnnModelSetting findLastestModel(String userId, String modelTemplateName) {

		ModelTemplateDao modelTemplateDao = daoHandler.getMySQLMapper(ModelTemplateDao.class);
		ModelTemplate modelTemplate = modelTemplateDao.selectByKey(modelTemplateName, userId);
		if (modelTemplate == null) return null;

		DnnModelSetting ms = new DnnModelSetting();
		DnnDataSetting ds = new DnnDataSetting();
		ms.setModelTemplateId(modelTemplate.getModelTemplateId());
		ms.setUserId(userId);
		ms.setAlgorithm(DnnAlgorithm.resolve(modelTemplate.getAlgorithm()));
		ms.setScript(modelTemplate.getScript());
		ds.setDatasetId(modelTemplate.getDatasetId());
		ds.setxColumn(modelTemplate.getxColumn());
		ds.setxFilter(modelTemplate.getxFilter());
		ds.setyColumn(modelTemplate.getyColumn());
		ds.setyFilter(modelTemplate.getyFilter());
		ms.setDataSetting(ds);

		ModelDao modelDao = daoHandler.getMySQLMapper(ModelDao.class);
		Model model = modelDao.selectLatestByModelTemplateId(ms.getModelTemplateId());
		if (model == null) return ms;
		ms.setModelId(model.getModelId());
		ms.setParentModelId(model.getParentModelId());
		ms.setStructure(model.getStructure());

		return ms;
	}

	public void upgradeModel2Training(DnnModelSetting dnnModel, Boolean newFlag) {
		ModelDao modelDao = daoHandler.getMySQLMapper(ModelDao.class);
		if (newFlag) {
			// newFlag == true, 不保留旧模型，所以是update，注意
			if (dnnModel.getModelId() == null) {
				// 新模型
				Model model = new Model();
				model.setModelId(MathUtil.random(31));
				model.setModelTemplateId(dnnModel.getModelTemplateId());
				model.setUserId(dnnModel.getUserId());
				model.setParentModelId(dnnModel.getModelId());
				model.setStatus(Status.Training.status);
				model.setStructure(null);
				model.setCreateTime(new Date());
				model.setUpdateTime(new Date());
				modelDao.insert(model);
				dnnModel.setModelId(model.getModelId());
			} else {
				// 更新旧模型
				Model model = new Model();
				model.setModelId(dnnModel.getModelId());
				model.setStatus(Status.Training.status);
				model.setUpdateTime(new Date());
				modelDao.updateStatusById(model);
			}
		} else {
			// newFlag == false, 保留旧模型，所以是insert，注意
			// 新模型
			Model model = new Model();
			model.setModelId(MathUtil.random(31));
			model.setModelTemplateId(dnnModel.getModelTemplateId());
			model.setUserId(dnnModel.getUserId());
			model.setParentModelId(dnnModel.getModelId());
			model.setStatus(Status.Training.status);
			model.setStructure(null);
			model.setCreateTime(new Date());
			model.setUpdateTime(new Date());
			modelDao.insert(model);
			dnnModel.setModelId(model.getModelId());
		}
	}

	public void updateModelStatusById(Model model) {
		ModelDao modelDao = daoHandler.getMySQLMapper(ModelDao.class);
		modelDao.updateStatusById(model);
	}

	public void updateModelStructureById(Model model) {
		ModelDao modelDao = daoHandler.getMySQLMapper(ModelDao.class);
		modelDao.updateStructureById(model);
	}

	public List<ModelTemplate> findModelTemplates(String userId) {
		ModelTemplateDao modelDao = daoHandler.getMySQLMapper(ModelTemplateDao.class);
		List<ModelTemplate> modelTemplates = modelDao.selectByUserId(userId);
		return modelTemplates;
	}

	public List<Model> findModels(String userId) {
		ModelDao modelDao = daoHandler.getMySQLMapper(ModelDao.class);
		List<Model> models = modelDao.selectByUserId(userId);
		return models;
	}

	public int deleteModelTemplate(String modelTemplateName, String userId) throws MLException {
		ModelTemplateDao modelTemplateDao = daoHandler.getMySQLMapper(ModelTemplateDao.class);
		return modelTemplateDao.deleteByKey(modelTemplateName, userId);
	}
}
