package cn.standardai.api.ash.command;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.bean.Context;
import cn.standardai.api.core.util.HttpUtil;

public class AshRm extends AshCommand {

	public AshRm(String token) {
		super(token);
	}

	@Override
	public String exec(String[] params) {

		if (params.length > 2) {
			return this.help();
		}

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("token", this.token);
		String s = HttpUtil.delete(Context.getProp().getUrl().getMl() + "/lstm/" + params[1], null, headers);
		JSONObject j = JSONObject.parseObject(s);

		if (!"success".equals(j.getString("result"))) {
			return j.getString("message");
		}

		String result = "modelId\n"
						+ params[1];

		return result;
	}

	@Override
	public String help() {
		return "rm命令格式：rm [资源ID]";
	}

	@Override
	public String man() {
		return "rm命令用于删除指定资源ID的资源\n"
				+ "用法：\n"
				+ "\trm [资源ID]";
	}
}
