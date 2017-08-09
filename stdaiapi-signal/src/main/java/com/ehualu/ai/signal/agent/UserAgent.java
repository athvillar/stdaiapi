package com.ehualu.ai.signal.agent;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.ehualu.ai.signal.exception.BizException;

import cn.standardai.api.core.base.AuthAgent;
import cn.standardai.api.core.exception.AuthException;
import cn.standardai.api.core.util.CryptUtil;
import cn.standardai.api.dao.UserDao;
import cn.standardai.api.dao.bean.User;

public class UserAgent extends AuthAgent {

	public JSONObject init() throws AuthException {
		if (!userId.equals(this.userId)) throw new AuthException("没有权限");
		JSONObject result = new JSONObject();
		UserDao dao = daoHandler.getMySQLMapper(UserDao.class);
		List<User> info = dao.selectById(userId);
		if (info != null && info.size() != 0) {
			JSONObject user = new JSONObject();
			user.put("userId", info.get(0).getUserId());
			user.put("email", info.get(0).getEmail());
			user.put("registTime", info.get(0).getRegistTime());
			user.put("supportMoney", info.get(0).getSupportMoney());
			user.put("remainMoney", info.get(0).getRemainMoney());
			user.put("remainPixel", info.get(0).getRemainPixel());
			user.put("lastLoginTime", info.get(0).getLastLoginTime());
			result.put("user", user);
		}
		return result;
	}
}
