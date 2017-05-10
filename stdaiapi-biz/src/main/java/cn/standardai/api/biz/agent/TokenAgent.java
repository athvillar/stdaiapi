package cn.standardai.api.biz.agent;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.biz.exception.BizException;
import cn.standardai.api.core.base.AuthAgent;
import cn.standardai.api.core.util.CryptUtil;
import cn.standardai.api.core.util.MathUtil;
import cn.standardai.api.dao.TokenDao;
import cn.standardai.api.dao.UserDao;
import cn.standardai.api.dao.bean.Token;
import cn.standardai.api.dao.bean.User;

public class TokenAgent extends AuthAgent {

	public JSONObject createToken(JSONObject request) throws BizException {

		JSONObject result = new JSONObject();

		String userId = request.getString("userId");
		User param1 = new User();
		param1.setUserId(userId);
		try {
			param1.setPassword(CryptUtil.encodeBase64(CryptUtil.encryptMD5(request.getString("password").getBytes())));
		} catch (UnsupportedEncodingException e) {
			throw new BizException("加密失败");
		}
		UserDao dao1 = daoHandler.getMySQLMapper(UserDao.class);
		if (dao1.selectCountByAuth(param1) == 0) throw new BizException("认证失败");

		Token param2 = new Token();
		param2.setUserId(userId);
		String token = MathUtil.random(17);
		param2.setToken(token);
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, 3);
		param2.setExpireTime(c.getTime());

		TokenDao dao2 = daoHandler.getMySQLMapper(TokenDao.class);
		dao2.deleteByUserId(userId);
		dao2.insert(param2);
		result.put("token", token);

		return result;
	}

	public void removeById(String token) throws BizException {
		TokenDao dao = daoHandler.getMySQLMapper(TokenDao.class);
		String userId = dao.selectUserIdByToken(token);
		if (userId == null || !userId.equals(this.userId)) throw new BizException("没有权限");
		dao.deleteByUserId(userId);
	}
}
