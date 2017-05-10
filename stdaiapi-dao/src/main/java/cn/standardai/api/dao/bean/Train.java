package cn.standardai.api.dao.bean;

import java.util.Date;

public class Train {

	private String trainId;

	private String modelId;

	private String datasetId;

	private Integer epochDataCnt;

	private Integer epochCnt;

	private Date createTime;

	private Date updateTime;

	private Integer totalSecond;

	public String getTrainId() {
		return trainId;
	}

	public void setTrainId(String trainId) {
		this.trainId = trainId;
	}

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

	public Integer getEpochDataCnt() {
		return epochDataCnt;
	}

	public void setEpochDataCnt(Integer epochDataCnt) {
		this.epochDataCnt = epochDataCnt;
	}

	public Integer getEpochCnt() {
		return epochCnt;
	}

	public void setEpochCnt(Integer epochCnt) {
		this.epochCnt = epochCnt;
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

	public Integer getTotalSecond() {
		return totalSecond;
	}

	public void setTotalSecond(Integer totalSecond) {
		this.totalSecond = totalSecond;
	}
}
