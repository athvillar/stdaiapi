package cn.standardai.api.ash.command;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.bean.Context;
import cn.standardai.api.core.util.HttpUtil;

public class AshMk extends AshCommand {

	public AshMk(String token) {
		super(token);
	}

	@Override
	public String exec(String[] params) {

		if (params.length != 5) {
			return this.help("参数个数错误");
		}

		JSONObject queryParams = new JSONObject();
		queryParams.put("password", params[3]);
		queryParams.put("email", params[4]);
		String s = HttpUtil.post(Context.getProp().getUrl().getBiz() + "/user/" + params[2], queryParams.toJSONString(), null);
		JSONObject j = JSONObject.parseObject(s);
		if (!"success".equals(j.getString("result"))) {
			return j.getString("message");
		}

		queryParams = new JSONObject();
		queryParams.put("userId", params[2]);
		queryParams.put("password", params[3]);
		s = HttpUtil.post(Context.getProp().getUrl().getBiz() + "/token", queryParams.toJSONString(), null);
		j = JSONObject.parseObject(s);
		if (!"success".equals(j.getString("result"))) {
			return j.getString("message");
		}
		this.token = j.getString("token");
		return "注册成功，欢迎光临！";
	}

	@Override
	public String help() {
		return "mk命令格式：mk [资源]";
	}

	@Override
	public String man() {
		return "mk命令用于创建资源\n"
				+ "用法：\n"
				+ "\tmk [资源]\n"
				+ "创建用户: \n"
				+ "\tmk user";
	}
}
