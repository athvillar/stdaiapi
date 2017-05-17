package cn.standardai.api.ash.agent;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.command.base.AshCommand;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.core.base.AuthAgent;

public class AshAgent extends AuthAgent {

	public JSONObject exec(JSONObject request) {

		JSONObject result = new JSONObject();
		String commandLine = request.getString("ash");
		if (commandLine == null) {
			result.put("message", "缺少命令，如需帮助，请输入“help”");
			return result;
		};
		String[] commands = commandLine.split(" ");
		if (commands == null) {
			result.put("message", "缺少命令，如需帮助，请输入“help”");
			return result;
		};

		String resource = request.getString("resource");
		AshCommand ashCommand = AshCommand.getInstance(commands[0]);
		if (ashCommand == null) {
			result.put("message", "无此命令，或者此命令正在开发中，如需帮助，请输入“help”");
			return result;
		}
		AshReply reply;
		try {
			reply = ashCommand.exec(commandLine, this.getToken());
			result.put("display", reply.display);
			//result.put("message", reply.message);
			result.put("hidden", reply.hidden);
		} catch (AshException e) {
			result.put("display", "系统错误");
			result.put("message", e.getMessage());
			//result.put("hidden", reply.hidden);
		}

		return result;
	}
}
