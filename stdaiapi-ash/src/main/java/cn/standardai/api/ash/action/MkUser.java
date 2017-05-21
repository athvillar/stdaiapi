package cn.standardai.api.ash.action;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.command.base.AshCommand.HttpMethod;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.core.bean.Context;

public class MkUser extends Action {

	private String userId;

	private String password;

	private String email;

	@Override
	public void exec() throws AshException {
		JSONObject body = new JSONObject();
		body.put("password", password);
		body.put("email", email);
		comm.http(HttpMethod.POST, Context.getProp().getUrl().getBiz() + "/user/" + userId, null, body);

		body = new JSONObject();
		body.put("userId", userId);
		body.put("password", password);
		JSONObject j = comm.http(HttpMethod.POST, Context.getProp().getUrl().getBiz() + "/token", null, body);

		comm.reply.hidden = j.getString("token");
		comm.reply.display = "注册成功，欢迎光临！";
	}

	@Override
	public void setParam() throws AshException {
		this.userId = this.comm.params.get(1);
		this.password = this.comm.params.get(2);
		this.email = this.comm.params.get(3);
	}
}
