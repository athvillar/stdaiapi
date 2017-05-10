package cn.standardai.api.biz.agent;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.biz.exception.BizException;
import cn.standardai.api.core.base.AuthAgent;
import cn.standardai.api.core.exception.AuthException;
import cn.standardai.api.core.util.CryptUtil;
import cn.standardai.api.core.util.DateUtil;
import cn.standardai.api.dao.UserDao;
import cn.standardai.api.dao.bean.User;

public class UserAgent extends AuthAgent {

	public JSONObject getById(String userId) throws AuthException {
		if (!userId.equals(this.userId)) throw new AuthException("没有权限");
		JSONObject result = new JSONObject();
		UserDao dao = daoHandler.getMySQLMapper(UserDao.class);
		List<User> info = dao.selectById(userId);
		if (info != null && info.size() != 0) {
			JSONObject user = new JSONObject();
			user.put("userId", info.get(0).getUserId());
			user.put("email", info.get(0).getEmail());
			user.put("registTime", DateUtil.format(info.get(0).getRegistTime(), DateUtil.YYYY__MM__DD__HH__MM__SS));
			user.put("supportMoney", info.get(0).getSupportMoney());
			user.put("remainMoney", info.get(0).getRemainMoney());
			user.put("remainPixel", info.get(0).getRemainPixel());
			user.put("lastLoginTime", DateUtil.format(info.get(0).getLastLoginTime(), DateUtil.YYYY__MM__DD__HH__MM__SS));
			result.put("user", user);
		}
		return result;
	}

	public JSONObject create(String userId, JSONObject request) throws UnsupportedEncodingException, BizException {

		JSONObject result = new JSONObject();
		UserDao dao = daoHandler.getMySQLMapper(UserDao.class);
		User param = new User();
		param.setUserId(userId);

		if (dao.selectCountById(userId) == 0) {
			// 用户不存在，注册
			param.setPassword(CryptUtil.encodeBase64(CryptUtil.encryptMD5(request.getString("password").getBytes())));
			param.setEmail(request.getString("email"));
			dao.insert(param);
			result.put("result", "success");
		} else {
			// 用户存在，错误
			throw new BizException("用户已存在");
		}

		return result;
	}

	public JSONObject updateById(String userId, JSONObject request) throws UnsupportedEncodingException, BizException, AuthException {

		if (!userId.equals(this.userId)) throw new AuthException("没有权限");
		JSONObject result = new JSONObject();
		UserDao dao = daoHandler.getMySQLMapper(UserDao.class);
		User param = new User();
		param.setUserId(userId);

		if (dao.selectCountById(userId) == 0) {
			// 用户不存在，错误
			throw new BizException("用户不存在");
		} else {
			// 用户存在，更新
			if (request.getString("oldPassword") != null && request.getString("newPassword") != null) {
				// 更新密码
				param.setPassword(CryptUtil.encodeBase64(CryptUtil.encryptMD5(request.getString("oldPassword").getBytes())));
				if (dao.selectCountByAuth(param) == 0) {
					// 旧密码错误，返回
					result.put("result", "failure");
					result.put("message", "错误的用户名或密码");
				} else {
					param.setPassword(CryptUtil.encodeBase64(CryptUtil.encryptMD5(request.getString("newPassword").getBytes())));
					dao.updatePasswordById(param);
					result.put("result", "success");
				}
			}
			if (request.getString("email") != null) {
				// 更新个人信息
				param.setEmail(request.getString("email"));
				dao.updateById(param);
				result.put("result", "success");
			}
		}

		return result;
	}

	public void removeById(String userId) throws AuthException {
		if (!userId.equals(this.userId)) throw new AuthException("没有权限");
		UserDao dao = daoHandler.getMySQLMapper(UserDao.class);
		dao.deleteById(userId);
	}
}
