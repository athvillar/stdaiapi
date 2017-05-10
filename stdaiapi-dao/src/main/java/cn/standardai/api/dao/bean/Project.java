package cn.standardai.api.dao.bean;

import java.util.Date;

public class Project {

	private String projectId;

	private String projectName;

	private String description;

	private String costMoney;

	private String supportedMoney;

	private Date releaseTime;

	private Date startTime;

	private Character status;

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCostMoney() {
		return costMoney;
	}

	public void setCostMoney(String costMoney) {
		this.costMoney = costMoney;
	}

	public String getSupportedMoney() {
		return supportedMoney;
	}

	public void setSupportedMoney(String supportedMoney) {
		this.supportedMoney = supportedMoney;
	}

	public Date getReleaseTime() {
		return releaseTime;
	}

	public void setReleaseTime(Date releaseTime) {
		this.releaseTime = releaseTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Character getStatus() {
		return status;
	}

	public void setStatus(Character status) {
		this.status = status;
	}	
}
