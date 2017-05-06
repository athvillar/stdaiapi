package cn.standardai.api.ml.agent;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.util.DateUtil;
import cn.standardai.api.core.util.MathUtil;
import cn.standardai.api.dao.ModelTemplateDao;
import cn.standardai.api.dao.base.DaoHandler;
import cn.standardai.api.dao.bean.Model;
import cn.standardai.api.dao.bean.ModelTemplate;
import cn.standardai.api.ml.exception.MLException;
import cn.standardai.lib.algorithm.cnn.CNN;

public class ModelAgent {

	private DaoHandler daoHandler;

	public ModelAgent(DaoHandler daoHandler) {
		this.daoHandler = daoHandler;
	}

	public JSONObject create(String userId, String name, String type, String structure) throws MLException {

		JSONObject result = new JSONObject();
		ModelTemplateDao dao = daoHandler.getMySQLMapper(ModelTemplateDao.class);
		if (name != null) {
			ModelTemplate modelTemolate = dao.selectByKey(name, userId);
			if (modelTemolate != null) {
				ModelTemplate param = new ModelTemplate();
				param.setModelTemplateName(name);
				param.setScript(structure);
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
				param.setType("LSTM");
				param.setScript(structure);
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
			param.setType("CNN");
			param.setScript(structure);
			param.setUserId(userId);
			param.setCreateTime(new Date());
			dao.insert(param);
			result.put("id", param.getModelTemplateId());
			result.put("name", param.getModelTemplateName());
			result.put("time", DateUtil.format(param.getCreateTime(), DateUtil.YYYY_MM_DD_HH_MM_SS));
		}

		return result;
	}

	/**
	 * TODO
	public JSONObject update(String datasetId, String label, Lstm lstm) throws MLException {

		Model newModel = new Model();
		String modelId = MathUtil.random(31);
		newModel.setModelId(modelId);
		newModel.setModelTemplateId(modelTemplate.getModelTemplateId());
		newModel.setParentModelId(parentModelId);
		parentModelId = newModel.getModelId();
		newModel.setLabel(label);
		newModel.setDatasetId(dataset.getDatasetId());
		newModel.setStructure(CNN.getBytes(cnn));
		newModel.setCreateTime(new Date());
		modelDao.insert(newModel);
	}
	 */

	public JSONObject status(String userId, String id) {
		// TODO Auto-generated method stub
		return null;
	}
}
