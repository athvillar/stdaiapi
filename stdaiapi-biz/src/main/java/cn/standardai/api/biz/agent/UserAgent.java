package cn.standardai.api.biz.agent;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.biz.exception.BizException;
import cn.standardai.api.core.base.AuthAgent;
import cn.standardai.api.core.util.CryptUtil;
import cn.standardai.api.dao.UserDao;
import cn.standardai.api.dao.bean.User;

public class UserAgent extends AuthAgent {

	public JSONObject getById(String userId) {
		JSONObject result = new JSONObject();
		UserDao dao = daoHandler.getMySQLMapper(UserDao.class);
		List<User> info = dao.selectById(userId);
		if (info != null && info.size() != 0) {
			result.put("userId", info.get(0).getUserId());
		}
		return result;
	}

	public JSONObject upgradeById(String userId, JSONObject request) throws UnsupportedEncodingException {

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
			// 用户存在，更新
			if (request.getString("oldPassword") != null && request.getString("newPassword") != null) {
				// 更新密码
				param.setPassword(CryptUtil.encodeBase64(CryptUtil.encryptMD5(request.getString("oldPassword").getBytes())));
				if (dao.selectCountByAuth(param) == 0) {
					// 旧密码错误，返回
					result.put("result", "failure");
					result.put("message", "wrong username or password");
				} else {
					param.setPassword(CryptUtil.encodeBase64(CryptUtil.encryptMD5(request.getString("newPassword").getBytes())));
					dao.updatePassword(param);
					result.put("result", "success");
				}
			}
			if (request.getString("email") != null) {
				// 更新个人信息
				param.setEmail(request.getString("email"));
				dao.updatePersonalInfo(param);
				result.put("result", "success");
			}
		}

		return result;
	}

	public void removeById(String userId) throws BizException {
		if (!userId.equals(this.userId)) throw new BizException("没有权限");
		UserDao dao = daoHandler.getMySQLMapper(UserDao.class);
		dao.deleteById(userId);
	}
}
