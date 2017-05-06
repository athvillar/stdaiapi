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

	protected AuthAgent() {
		super();
	}

	public void setUserId(String token) {
		this.userId = getUserIdByToken(token);
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
}
