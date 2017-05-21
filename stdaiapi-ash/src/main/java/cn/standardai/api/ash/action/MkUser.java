package cn.standardai.api.ash.action;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.base.Action;
import cn.standardai.api.ash.base.AshCommand.HttpMethod;
import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.core.bean.Context;

public class MkUser extends Action {

	private String userId;

	private String password;

	private String email;

	@Override
	public AshReply exec() throws AshException {
		JSONObject body = new JSONObject();
		body.put("password", password);
		body.put("email", email);
		comm.http(HttpMethod.POST, Context.getProp().getUrl().getBiz() + "/user/" + userId, null, body);

		body = new JSONObject();
		body.put("userId", userId);
		body.put("password", password);
		JSONObject j = comm.http(HttpMethod.POST, Context.getProp().getUrl().getBiz() + "/token", null, body);

		this.reply.hidden = j.getString("token");
		this.reply.display = "注册成功，欢迎光临！";
		return this.reply;
	}

	@Override
	public void readParam() throws AshException {
		this.userId = this.param.get(1);
		this.password = this.param.get(2);
		this.email = this.param.get(3);
	}
}
