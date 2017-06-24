package cn.standardai.api.ml.bean;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DnnModelSetting {

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

	private DnnAlgorithm algorithm;

	private String script;

	private byte[] structure;

	private Date createTime;

	private DnnDataSetting trainDataSetting;

	public String getModelId() {
		return modelId;
	}

	public void setModelId(String modelId) {
		this.modelId = modelId;
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

	public DnnAlgorithm getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(DnnAlgorithm algorithm) {
		this.algorithm = algorithm;
	}

	public DnnDataSetting getTrainDataSetting() {
		return trainDataSetting;
	}

	public void setTrainDataSetting(DnnDataSetting trainDataSetting) {
		this.trainDataSetting = trainDataSetting;
	}
}
