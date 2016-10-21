package com.kingdy.parkos.redis.bean;

import java.io.Serializable;
import java.util.Date;

public class Token implements Serializable {

	private static final long serialVersionUID = 8326959009968341877L;

	private String account;

	private String token;

	private Date createTime;

	private Date expireTime;

	private String ip;

	private String tokenType;

	private String authorizedURL;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public String getAuthorizedURL() {
		return authorizedURL;
	}

	public void setAuthorizedURL(String authorizedURL) {
		this.authorizedURL = authorizedURL;
	}
}
