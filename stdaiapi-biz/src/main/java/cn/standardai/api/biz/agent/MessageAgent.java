package cn.standardai.api.biz.agent;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.biz.exception.BizException;
import cn.standardai.api.core.base.AuthAgent;
import cn.standardai.api.core.exception.AuthException;
import cn.standardai.api.core.util.DateUtil;
import cn.standardai.api.core.util.MathUtil;
import cn.standardai.api.dao.MessageDao;
import cn.standardai.api.dao.UserDao;
import cn.standardai.api.dao.bean.Message;
import cn.standardai.api.dao.bean.User;

public class MessageAgent extends AuthAgent {

	public JSONObject get(String type, String userId, String all) throws AuthException {

		JSONObject result = new JSONObject();
		MessageDao dao = daoHandler.getMySQLMapper(MessageDao.class);

		List<Message> msgs;
		if ("1".equals(all)) {
			if ("send".equals(type)) {
				if (userId == null) {
					msgs = dao.selectByFromUserId(this.userId);
				} else {
					msgs = dao.selectByBothUserId(this.userId, userId);
				}
			} else {
				if (userId == null) {
					msgs = dao.selectByToUserId(this.userId);
				} else {
					msgs = dao.selectByBothUserId(userId, this.userId);
				}
			}
		} else {
			if ("send".equals(type)) {
				if (userId == null) {
					msgs = dao.selectUnreadByFromUserId(this.userId);
				} else {
					msgs = dao.selectUnreadByBothUserId(this.userId, userId);
				}
			} else {
				if (userId == null) {
					msgs = dao.selectUnreadByToUserId(this.userId);
				} else {
					msgs = dao.selectUnreadByBothUserId(userId, this.userId);
				}
			}
		}

		if (msgs == null) {
			result.put("size", 0);
			return result;
		}
		JSONArray msgJs = new JSONArray();
		for (int i = 0; i < msgs.size(); i++) {
			JSONObject msgJ = new JSONObject();
			msgJ.put("messageId", msgs.get(i).getMessageId());
			msgJ.put("fromUserId", msgs.get(i).getFromUserId());
			msgJ.put("toUserId", msgs.get(i).getToUserId());
			msgJ.put("isRead", msgs.get(i).getIsRead());
			msgJ.put("content", msgs.get(i).getMessage());
			msgJ.put("createTime", msgs.get(i).getCreateTime());
			msgJs.add(msgJ);
			if (!msgs.get(i).getIsRead()) {
				dao.updateIsReadById(msgs.get(i).getMessageId());
			}
		}
		result.put("messages", msgJs);
		result.put("size", msgs.size());

		return result;
	}

	public JSONObject create(JSONObject request) throws UnsupportedEncodingException, BizException {

		MessageDao messageDao = daoHandler.getMySQLMapper(MessageDao.class);
		UserDao userDao = daoHandler.getMySQLMapper(UserDao.class);

		String toUserId = request.getString("userId");
		if (toUserId == null) {
			List<User> allUser = userDao.select();
			for (int i = 0; i < allUser.size(); i++) {
				Message param = new Message();
				param.setMessageId(MathUtil.random(32));
				param.setFromUserId(this.userId);
				param.setToUserId(allUser.get(i).getUserId());
				param.setMessage(request.getString("content"));
				param.setIsRead(false);
				messageDao.insert(param);
			}
		} else {
			if (userDao.selectCountById(toUserId) == 0) {
				throw new BizException("用户" + toUserId + "不存在");
			}
			Message param = new Message();
			param.setMessageId(MathUtil.random(32));
			param.setFromUserId(this.userId);
			param.setToUserId(toUserId);
			param.setMessage(request.getString("content"));
			param.setIsRead(false);
			messageDao.insert(param);
		}

		JSONObject result = new JSONObject();
		result.put("result", "success");

		return result;
	}

	public void remove(String type) throws AuthException {
		MessageDao messageDao = daoHandler.getMySQLMapper(MessageDao.class);

		if ("send".equals(type)) {
			messageDao.deleteByFromUserId(this.userId);
		} else {
			messageDao.deleteByToUserId(this.userId);
		}
	}
}
