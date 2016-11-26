package cn.standardai.api.dao.bean;

import java.util.Date;

public class ModelTemplate {

	private String modelTemplateId;

	private String modelTemplateName;

	private String userId;

	private String type;

	private String script;

	private Date createTime;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
}
