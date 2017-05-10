package cn.standardai.api.dao.bean;

public class UserContribution {

	private String userId;

	private String projectId;

	private String contributeTime;

	private String contributeMoney;

	private String repayItemId;

	private String repayItemNumber;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getContributeTime() {
		return contributeTime;
	}

	public void setContributeTime(String contributeTime) {
		this.contributeTime = contributeTime;
	}

	public String getContributeMoney() {
		return contributeMoney;
	}

	public void setContributeMoney(String contributeMoney) {
		this.contributeMoney = contributeMoney;
	}

	public String getRepayItemId() {
		return repayItemId;
	}

	public void setRepayItemId(String repayItemId) {
		this.repayItemId = repayItemId;
	}

	public String getRepayItemNumber() {
		return repayItemNumber;
	}

	public void setRepayItemNumber(String repayItemNumber) {
		this.repayItemNumber = repayItemNumber;
	}
}
