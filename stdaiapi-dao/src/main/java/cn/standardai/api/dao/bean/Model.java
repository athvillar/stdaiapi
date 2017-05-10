package cn.standardai.api.dao.bean;

import java.util.Date;

public class Model {

	private String modelId;

	private String modelTemplateId;

	private String userId;

	private Character status;

	private String datasetId;

	private String dataDicId;

	private String parentModelId;

	private byte[] structure;

	private Date createTime;

	private Date updateTime;

	public String getModelId() {
		return modelId;
	}

	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	public String getModelTemplateId() {
		return modelTemplateId;
	}

	public void setModelTemplateId(String modelTemplateId) {
		this.modelTemplateId = modelTemplateId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Character getStatus() {
		return status;
	}

	public void setStatus(Character status) {
		this.status = status;
	}

	public String getParentModelId() {
		return parentModelId;
	}

	public void setParentModelId(String parentModelId) {
		this.parentModelId = parentModelId;
	}

	public byte[] getStructure() {
		return structure;
	}

	public void setStructure(byte[] structure) {
		this.structure = structure;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getDatasetId() {
		return datasetId;
	}

	public void setDatasetId(String datasetId) {
		this.datasetId = datasetId;
	}

	public String getDataDicId() {
		return dataDicId;
	}

	public void setDataDicId(String dataDicId) {
		this.dataDicId = dataDicId;
	}
}
