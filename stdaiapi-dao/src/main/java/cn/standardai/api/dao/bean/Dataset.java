package cn.standardai.api.dao.bean;

import java.util.Date;

public class Dataset {

	private String datasetId;

	private String datasetName;

	private String userId;

	private String format;

	private String keywords;

	private String titles;

	private String dataDicId1;

	private String dataDicId2;

	private String dataDicId3;

	private Character sharePolicy;

	private Date createTime;

	public String getDatasetId() {
		return datasetId;
	}

	public void setDatasetId(String datasetId) {
		this.datasetId = datasetId;
	}

	public String getDatasetName() {
		return datasetName;
	}

	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getTitles() {
		return titles;
	}

	public void setTitles(String titles) {
		this.titles = titles;
	}

	public String getDataDicId1() {
		return dataDicId1;
	}

	public void setDataDicId1(String dataDicId1) {
		this.dataDicId1 = dataDicId1;
	}

	public String getDataDicId2() {
		return dataDicId2;
	}

	public void setDataDicId2(String dataDicId2) {
		this.dataDicId2 = dataDicId2;
	}

	public String getDataDicId3() {
		return dataDicId3;
	}

	public void setDataDicId3(String dataDicId3) {
		this.dataDicId3 = dataDicId3;
	}

	public Character getSharePolicy() {
		return sharePolicy;
	}

	public void setSharePolicy(Character sharePolicy) {
		this.sharePolicy = sharePolicy;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
