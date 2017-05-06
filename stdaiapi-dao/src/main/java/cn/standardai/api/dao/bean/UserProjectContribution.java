package cn.standardai.api.dao.bean;

public class UserProjectContribution {

	private String contributionId;//主键
	
	private String userId;//用户账号
	
	private String projectId;//项目编号
	
	private String contributionDate;//支持日期
	
	private String contributionMoney;//支持金额
	
	private String obtainContent;//回报内容
	
	private String obtainNumber;//回报数量
	
	private String obtainState;//回报状态

	public String getContributionId() {
		return contributionId;
	}

	public void setContributionId(String contributionId) {
		this.contributionId = contributionId;
	}

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

	public String getContributionDate() {
		return contributionDate;
	}

	public void setContributionDate(String contributionDate) {
		this.contributionDate = contributionDate;
	}

	public String getContributionMoney() {
		return contributionMoney;
	}

	public void setContributionMoney(String contributionMoney) {
		this.contributionMoney = contributionMoney;
	}

	public String getObtainContent() {
		return obtainContent;
	}

	public void setObtainContent(String obtainContent) {
		this.obtainContent = obtainContent;
	}

	public String getObtainNumber() {
		return obtainNumber;
	}

	public void setObtainNumber(String obtainNumber) {
		this.obtainNumber = obtainNumber;
	}

	public String getObtainState() {
		return obtainState;
	}

	public void setObtainState(String obtainState) {
		this.obtainState = obtainState;
	}
	
}
