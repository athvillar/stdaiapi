package cn.standardai.api.core.base;

import java.util.Date;
import java.util.List;

import cn.standardai.api.core.exception.AuthException;
import cn.standardai.api.dao.TokenDao;
import cn.standardai.api.dao.base.DaoHandler;
import cn.standardai.api.dao.bean.Token;

public class AuthAgent {

	protected DaoHandler daoHandler = new DaoHandler(true);

	public String userId;

	private String token;

	protected AuthAgent() {
		super();
	}

	public void checkToken(String token) throws AuthException {
		this.userId = getUserIdByToken(token);
		this.token = token;
	}

	public void checkUserIdByToken(String userId, String token) throws AuthException {
		if (userId == null || !userId.equals(getUserIdByToken(token))) throw new AuthException("认证失败");
		this.userId = userId;
		this.token = token;
	}

	public String getUserIdByToken(String token) throws AuthException {

		if (token == null) throw new AuthException("认证失败");

		Token tokenParam = new Token();
		tokenParam.setToken(token);
		tokenParam.setExpireTime(new Date());
		TokenDao tokenDao = daoHandler.getMySQLMapper(TokenDao.class);
		List<Token> tokenResult = tokenDao.selectByToken(tokenParam);
		if (tokenResult == null || tokenResult.size() == 0) throw new AuthException("认证失败");

		return tokenResult.get(0).getUserId();
	}

	public void done() {
		daoHandler.releaseSession();
	}

	public String getToken() {
		return token;
	}
}
