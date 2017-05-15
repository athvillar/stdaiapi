package cn.standardai.api.ash.agent;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.command.AshCommand;
import cn.standardai.api.ash.command.AshHelp;
import cn.standardai.api.ash.command.AshLs;
import cn.standardai.api.ash.command.AshMan;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.core.base.AuthAgent;

public class AshAgent extends AuthAgent {

	public JSONObject exec(JSONObject request) {

		JSONObject result = new JSONObject();
		String commandLine = request.getString("ash");
		if (commandLine == null) {
			result.put("display", new AshHelp().exec(null));
			return result;
		};
		String resource = request.getString("resource");

		String[] commands = commandLine.split(" ");
		switch (AshCommand.Command.resolve(commands[0])) {
		case help:
			result.put("display", new AshHelp().exec(commands[1]));
			break;
		case ls:
			result.put("display", new AshLs().exec(commands[1]));
			break;
		case man:
			result.put("display", new AshMan().exec(commands[1]));
			break;
		}

		return result;
	}
}
