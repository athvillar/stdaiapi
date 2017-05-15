package cn.standardai.api.ash.command;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.bean.Context;
import cn.standardai.api.core.util.HttpUtil;

public class AshLs extends AshCommand {

	public AshLs(String token) {
		super(token);
	}

	@Override
	public String exec(String[] params) {

		if (params.length > 2) {
			return this.help();
		}
		if (params.length == 2 && !params[1].equals("-l")) {
			return this.help();
		}

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("token", this.token);
		String s = HttpUtil.get(Context.getProp().getUrl().getMl() + "/lstm");
		JSONObject j = JSONObject.parseObject(s);

		if (!"success".equals(j.getString("result"))) {
			return j.getString("message");
		}
		JSONArray models = j.getJSONArray("models");
		if (models == null) return "没有记录\n";

		String result;
		if (params.length == 1) {
			result = "modelId\n";
		} else {
			result = "modelId\t\t\tupdateTime\t\t\n";
		}
		for (int i = 0; i < models.size(); i++) {
			result += models.getJSONObject(i).getString("modelId") + "\t";
			if (params.length == 1) result += models.getJSONObject(i).getString("updateTime");
			result += "\n";
		}
		result += "共" + models.size() + "条记录\n";

		return result;
	}

	@Override
	public String help() {
		return "ls命令格式：ls [-l]\n";
	}

	@Override
	public String man() {
		return "ls命令用于显示当前资源类别下的所有资源\n"
				+ "用法：\n"
				+ "\tls -参数\n"
				+ "参数：\n"
				+ "\t-l: 显示详细信息\n";
	}
}
