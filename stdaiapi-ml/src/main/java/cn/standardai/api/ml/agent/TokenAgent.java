package cn.standardai.api.ml.agent;

import java.util.Date;
import java.util.List;

import cn.standardai.api.dao.TokenDao;
import cn.standardai.api.dao.base.DaoHandler;
import cn.standardai.api.dao.bean.Token;
import cn.standardai.api.ml.exception.MLException;

public class TokenAgent {

	private DaoHandler daoHandler = new DaoHandler(true);

	public String getUserIdByToken(List<String> list) throws MLException {

		if (list == null || list.size() == 0) throw new MLException("认证失败");

		String token = list.get(0);
		if (token == null) throw new MLException("认证失败");

		Token tokenParam = new Token();
		tokenParam.setToken(token);
		tokenParam.setExpireTime(new Date());
		TokenDao tokenDao = daoHandler.getMySQLMapper(TokenDao.class);
		List<Token> tokenResult = tokenDao.selectByToken(tokenParam);
		if (tokenResult == null || tokenResult.size() == 0) throw new MLException("认证失败");

		return tokenResult.get(0).getUserId();
	}

	public void done() {
		daoHandler.releaseSession();
	}
}
