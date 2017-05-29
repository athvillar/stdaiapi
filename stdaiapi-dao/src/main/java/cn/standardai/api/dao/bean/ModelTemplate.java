package cn.standardai.api.dao.bean;

import java.util.Date;

public class ModelTemplate {

	private String modelTemplateId;

	private String modelTemplateName;

	private String userId;

	private String algorithm;

	private String script;

	private String datasetId;

	private String xColumn;

	private String xFilter;

	private String yColumn;

	private String yFilter;

	private Character sharePolicy;

	private Date createTime;

	public String getDatasetId() {
		return datasetId;
	}

	public void setDatasetId(String datasetId) {
		this.datasetId = datasetId;
	}

	public String getxColumn() {
		return xColumn;
	}

	public void setxColumn(String xColumn) {
		this.xColumn = xColumn;
	}

	public String getxFilter() {
		return xFilter;
	}

	public void setxFilter(String xFilter) {
		this.xFilter = xFilter;
	}

	public String getyColumn() {
		return yColumn;
	}

	public void setyColumn(String yColumn) {
		this.yColumn = yColumn;
	}

	public String getyFilter() {
		return yFilter;
	}

	public void setyFilter(String yFilter) {
		this.yFilter = yFilter;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getModelTemplateId() {
		return modelTemplateId;
	}

	public void setModelTemplateId(String modelTemplateId) {
		this.modelTemplateId = modelTemplateId;
	}

	public String getModelTemplateName() {
		return modelTemplateName;
	}

	public void setModelTemplateName(String modelTemplateName) {
		this.modelTemplateName = modelTemplateName;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public Character getSharePolicy() {
		return sharePolicy;
	}

	public void setSharePolicy(Character sharePolicy) {
		this.sharePolicy = sharePolicy;
	}
}
