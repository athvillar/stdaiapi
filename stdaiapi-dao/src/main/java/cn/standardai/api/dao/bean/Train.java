package cn.standardai.api.dao.bean;

import java.util.Date;

public class Train {

	private String trainId;

	private String modelId;

	private Integer epochDataCnt;

	private Integer epochCnt;

	private Date startTime;

	private Date endTime;

	private Long totalSecond;

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

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Long getTotalSecond() {
		return totalSecond;
	}

	public void setTotalSecond(Long totalSecond) {
		this.totalSecond = totalSecond;
	}
}
