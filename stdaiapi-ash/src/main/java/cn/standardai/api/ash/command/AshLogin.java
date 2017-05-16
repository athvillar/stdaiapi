package cn.standardai.api.ash.command;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.bean.Context;
import cn.standardai.api.core.util.HttpUtil;

public class AshLogin extends AshCommand {

	public AshLogin(String token) {
		super(token);
	}

	@Override
	public String exec(String[] params) {

		if (params.length != 3) {
			return this.help("参数个数错误");
		}

		JSONObject queryParams = new JSONObject();
		queryParams.put("userId", params[1]);
		queryParams.put("password", params[2]);
		String s = HttpUtil.post(Context.getProp().getUrl().getBiz() + "/token", queryParams.toJSONString(), null);
		JSONObject j = JSONObject.parseObject(s);
		if (!"success".equals(j.getString("result"))) {
			return j.getString("message");
		}
		this.token = j.getString("token");
		return "登陆成功，欢迎光临！";
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
}
