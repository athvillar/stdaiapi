package cn.standardai.api.ash.command;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.command.base.AshResourceCommand;
import cn.standardai.api.core.bean.Context;
import cn.standardai.api.core.util.DateUtil;
import cn.standardai.api.core.util.HttpUtil;

public class AshLs extends AshResourceCommand {

	protected final String help = "ls命令格式：ls [-l]";

	protected final String man = "msg(message)命令用于接收或发送消息\n"
				+ "ls命令用于显示当前资源类别下的所有资源\n"
				+ "用法：\n"
				+ "\tls -参数\n"
				+ "参数：\n"
				+ "\t-l: 显示详细信息";

	@Override
	public void invoke() {

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("token", this.token);
		String s = HttpUtil.get(Context.getProp().getUrl().getMl() + "/lstm", null, headers);
		JSONObject j = JSONObject.parseObject(s);

		if (!"success".equals(j.getString("result"))) {
			this.reply.message = j.getString("message");
			return;
		}
		JSONArray models = j.getJSONArray("models");
		if (models == null) this.reply.display = "没有记录";

		String result;
		if (params.has("l")) {
			result = "modelId\t\t\t\t\t\t\t\t\tupdateTime\t\t";
		} else {
			result = "modelId";
		}
		for (int i = 0; i < models.size(); i++) {
			result += "\n" + models.getJSONObject(i).getString("modelId") + "\t";
			if (params.has("l")) result += DateUtil.format(models.getJSONObject(i).getDate("updateTime"), DateUtil.YYYY__MM__DD__HH__MM__SS);
		}
		result += "\n共" + models.size() + "条记录";
		this.reply.display = result;
	}
}
