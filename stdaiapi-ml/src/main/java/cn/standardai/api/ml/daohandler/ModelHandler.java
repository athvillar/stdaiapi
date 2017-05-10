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
import cn.standardai.api.ml.bean.DnnModel;
import cn.standardai.api.ml.bean.DnnModel.Status;
import cn.standardai.api.ml.exception.MLException;

public class ModelHandler {

	private DaoHandler daoHandler;

	public ModelHandler(DaoHandler daoHandler) {
		this.daoHandler = daoHandler;
	}

	public JSONObject createModel(String userId, String name, String type, String script) throws MLException {

		JSONObject result = new JSONObject();
		ModelTemplateDao dao = daoHandler.getMySQLMapper(ModelTemplateDao.class);
		if (name != null) {
			ModelTemplate modelTemolate = dao.selectByKey(name, userId);
			if (modelTemolate != null) {
				ModelTemplate param = new ModelTemplate();
				param.setModelTemplateName(name);
				param.setScript(script);
				param.setUserId(userId);
				param.setCreateTime(new Date());
				dao.updateByKey(param);
				result.put("id", modelTemolate.getModelTemplateId());
				result.put("name", modelTemolate.getModelTemplateName());
				result.put("time", DateUtil.format(param.getCreateTime(), DateUtil.YYYY_MM_DD_HH_MM_SS));
			} else {
				ModelTemplate param = new ModelTemplate();
				param.setModelTemplateId(MathUtil.random(17));
				param.setModelTemplateName(name == null ? param.getModelTemplateId() : name);
				param.setType(type);
				param.setScript(script);
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
			param.setModelTemplateName(name == null ? param.getModelTemplateId() : name);
			param.setType(type);
			param.setScript(script);
			param.setUserId(userId);
			param.setCreateTime(new Date());
			dao.insert(param);
			result.put("id", param.getModelTemplateId());
			result.put("name", param.getModelTemplateName());
			result.put("time", DateUtil.format(param.getCreateTime(), DateUtil.YYYY_MM_DD_HH_MM_SS));
		}

		return result;
	}

	public DnnModel findModel(String userId, String modelTemplateId, String modelTemplateName, String modelId) throws MLException {

		ModelTemplateDao modelTemplateDao = daoHandler.getMySQLMapper(ModelTemplateDao.class);
		ModelTemplate modelTemplate = modelTemplateDao.selectById(modelTemplateId);
		if (modelTemplate == null) {
			modelTemplate = modelTemplateDao.selectByKey(modelTemplateName, userId);
			if (modelTemplate == null) throw new MLException("模型不存在");
		}

		DnnModel model = new DnnModel();
		model.setModelTemplateId(modelTemplate.getModelTemplateId());
		model.setScript(modelTemplate.getScript());
		model.setUserId(userId);

		ModelDao modelDao = daoHandler.getMySQLMapper(ModelDao.class);
		if (modelId == null) {
			List<Model> models = modelDao.selectByModelTemplateId(model.getModelTemplateId());
			if (models != null && models.size() != 0) {
				model.setModelId(models.get(0).getModelId());
				model.setParentModelId(models.get(0).getParentModelId());
				model.setStructure(models.get(0).getStructure());
			}
		} else {
			List<Model> models = modelDao.selectByIdModelTemplateId(modelId, model.getModelTemplateId());
			if (models != null && models.size() != 0) {
				model.setModelId(models.get(0).getModelId());
				model.setParentModelId(models.get(0).getParentModelId());
				model.setStructure(models.get(0).getStructure());
			}
		}

		return model;
	}

	public void upgradeModel2Training(DnnModel dnnModel, Boolean newFlag) {
		ModelDao modelDao = daoHandler.getMySQLMapper(ModelDao.class);
		if (newFlag) {
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
		} else {
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
			} else {
				// 更新旧模型
				Model model = new Model();
				model.setModelId(model.getModelId());
				model.setStatus(Status.Training.status);
				model.setUpdateTime(new Date());
				modelDao.updateStatusById(model);
			}
		}
	}

	public void updateModelById(Model model) {
		ModelDao modelDao = daoHandler.getMySQLMapper(ModelDao.class);
		modelDao.updateStatusById(model);
	}
}
