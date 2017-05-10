package cn.standardai.api.ml.bean;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DnnModel {

	public enum Status {

		Training('T'), Normal('N');

		public char status;

		private Status(char status) {
			this.status = status;
		}

		private static final Map<Character, Status> mappings = new HashMap<Character, Status>(2);

		static {
			for (Status status : values()) {
				mappings.put(status.status, status);
			}
		}

		public static Status resolve(Character status) {
			return (status != null ? mappings.get(status) : null);
		}
	}

	private String modelId;

	private String modelTemplateId;

	private String parentModelId;

	private String userId;

	private String script;

	private String label;

	private String datasetId;

	private byte[] structure;

	private Date createTime;

	public String getModelId() {
		return modelId;
	}

	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	public String getDatasetId() {
		return datasetId;
	}

	public void setDatasetId(String datasetId) {
		this.datasetId = datasetId;
	}

	public byte[] getStructure() {
		return structure;
	}

	public void setStructure(byte[] structure) {
		this.structure = structure;
	}

	public String getModelTemplateId() {
		return modelTemplateId;
	}

	public void setModelTemplateId(String modelTemplateId) {
		this.modelTemplateId = modelTemplateId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getParentModelId() {
		return parentModelId;
	}

	public void setParentModelId(String parentModelId) {
		this.parentModelId = parentModelId;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
