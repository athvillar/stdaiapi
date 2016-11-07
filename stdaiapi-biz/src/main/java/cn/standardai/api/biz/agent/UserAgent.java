package cn.standardai.api.biz.agent;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.util.CryptUtil;
import cn.standardai.api.dao.UserDao;
import cn.standardai.api.dao.base.DaoHandler;
import cn.standardai.api.dao.bean.User;

public class UserAgent {

	private DaoHandler daoHandler = new DaoHandler();

	public JSONObject getById(String userId) {
		JSONObject result = new JSONObject();
		UserDao dao = daoHandler.getMySQLMapper(UserDao.class);
		List<User> info = dao.selectById(userId);
		if (info != null && info.size() != 0) {
			result.put("userId", info.get(0).getUserId());
		}
		return result;
	}

	public void upgradeById(String userId, JSONObject request) throws UnsupportedEncodingException {
		UserDao dao = daoHandler.getMySQLMapper(UserDao.class);
		User param = new User();
		param.setUserId(userId);
		param.setPassword(CryptUtil.encodeBase64(CryptUtil.encryptMD5(request.getString("password").getBytes())));
		if (dao.selectCoundById(userId) == 0) {
			dao.insert(param);
		} else {
			dao.updateById(param);
		}
	}

	public void removeById(String userId) {
		UserDao dao = daoHandler.getMySQLMapper(UserDao.class);
		dao.deleteById(userId);
	}

	public void done() {
		if (daoHandler != null) daoHandler.releaseSession();
	}
}
