package cn.standardai.api.ash.agent;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.command.AshCommand;
import cn.standardai.api.core.base.AuthAgent;

public class AshAgent extends AuthAgent {

	public JSONObject exec(JSONObject request) {

		JSONObject result = new JSONObject();
		String commandLine = request.getString("ash");
		if (commandLine == null) {
			result.put("message", "缺少命令，如需帮助，请输入“help”\n");
			return result;
		};
		String[] commands = commandLine.split(" ");
		if (commands == null) {
			result.put("message", "缺少命令，如需帮助，请输入“help”\n");
			return result;
		};

		String resource = request.getString("resource");
		AshCommand ashCommand = AshCommand.getInstance(commands[0]);
		if (ashCommand == null) {
			result.put("message", "无此命令，或者此命令正在开发中，如需帮助，请输入“help”\n");
			return result;
		}
		result.put("display", ashCommand.exec(commands));

		return result;
	}
}
