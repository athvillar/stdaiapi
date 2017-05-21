package cn.standardai.api.ash.command;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.base.AshCommonCommand;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.ash.exception.HttpException;
import cn.standardai.api.core.bean.Context;

public class AshLogin extends AshCommonCommand {

	public AshLogin() {
		setParamRules(null, null, 2, 2);
	}

	@Override
	public void invoke() throws HttpException {

		JSONObject body = new JSONObject();
		body.put("userId", this.param.get(1));
		body.put("password", this.param.get(2));
		JSONObject j = http(HttpMethod.POST, Context.getProp().getUrl().getBiz() + "/token", null, body);
		this.reply.hidden = j.getString("token");
		this.reply.display = "登陆成功，欢迎光临！";
	}

	@Override
	public String help() {
		return "login命令格式：login [用户名] [密码]";
	}

	@Override
	public String man() {
		return "login命令用于用户登录\n"
				+ "用法：\n"
				+ "\tlogin [用户名] [密码]";
	}

	@Override
	public void readParam() throws AshException {
		// TODO Auto-generated method stub
		
	}
}
