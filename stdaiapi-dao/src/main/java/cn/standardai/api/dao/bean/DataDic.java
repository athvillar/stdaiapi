package cn.standardai.api.dao.bean;

public class DataDic {

	private String dataDicId;

	private String dataDicName;

	private String description;

	private String userId;

	private Character sharePolicy;

	public String getDataDicId() {
		return dataDicId;
	}

	public void setDataDicId(String dataDicId) {
		this.dataDicId = dataDicId;
	}

	public String getDataDicName() {
		return dataDicName;
	}

	public void setDataDicName(String dataDicName) {
		this.dataDicName = dataDicName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Character getSharePolicy() {
		return sharePolicy;
	}

	public void setSharePolicy(Character sharePolicy) {
		this.sharePolicy = sharePolicy;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
