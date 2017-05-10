package cn.standardai.api.dao.bean;

import java.util.Date;

public class User {

	private String userId;

	private String password;

	private String email;

	private Date registTime;

	private Date lastLoginTime;

	private Character status;

	private Double supportMoney;

	private Double remainMoney;

	private Integer remainPixel;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getRegistTime() {
		return registTime;
	}

	public void setRegistTime(Date registTime) {
		this.registTime = registTime;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public Character getStatus() {
		return status;
	}

	public void setStatus(Character status) {
		this.status = status;
	}

	public Double getSupportMoney() {
		return supportMoney;
	}

	public void setSupportMoney(Double supportMoney) {
		this.supportMoney = supportMoney;
	}

	public Double getRemainMoney() {
		return remainMoney;
	}

	public void setRemainMoney(Double remainMoney) {
		this.remainMoney = remainMoney;
	}

	public Integer getRemainPixel() {
		return remainPixel;
	}

	public void setRemainPixel(Integer remainPixel) {
		this.remainPixel = remainPixel;
	}
}
