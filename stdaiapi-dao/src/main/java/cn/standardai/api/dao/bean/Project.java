package cn.standardai.api.dao.bean;

public class Project {
	
	private String projectId;//项目编号
	
	private String projectName;//项目名称
	
	private String costMoney;//所需金额
	
	private String raiseMoney;//已筹金额
	
	private String releaseDate;//发布时间

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

	public String getCostMoney() {
		return costMoney;
	}

	public void setCostMoney(String costMoney) {
		this.costMoney = costMoney;
	}

	public String getRaiseMoney() {
		return raiseMoney;
	}

	public void setRaiseMoney(String raiseMoney) {
		this.raiseMoney = raiseMoney;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}
	
}
