package cn.standardai.api.ash.agent;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.action.MkUser;
import cn.standardai.api.ash.base.AshCommand;
import cn.standardai.api.ash.base.AshCommonCommand;
import cn.standardai.api.ash.base.AshResource;
import cn.standardai.api.ash.base.AshResourceRelatedCommand;
import cn.standardai.api.ash.base.Executable;
import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.ash.exception.DialogException;
import cn.standardai.api.ash.exception.HttpException;
import cn.standardai.api.ash.exception.ParamException;
import cn.standardai.api.core.base.AuthAgent;
import cn.standardai.api.core.exception.AuthException;

public class AshAgent extends AuthAgent {

	public JSONObject exec(JSONObject request) throws AshException, AuthException {

		JSONObject result = new JSONObject();
		String commandLine = request.getString("ash");
		if (commandLine == null) {
			result.put("message", "缺少命令，如需帮助，请输入“help”");
			return result;
		};
		String[] commands = commandLine.replaceAll("\r", " ").replaceAll("\n", " ").split(" ");
		if (commands == null) {
			result.put("message", "缺少命令，如需帮助，请输入“help”");
			return result;
		};

		AshCommand ashCommand = AshCommand.getInstance(commands[0]);
		if (ashCommand == null) {
			result.put("message", "无此命令，或者此命令正在开发中，如需帮助，请输入“help”");
			return result;
		}

		int paramStartIndex = 1;
		if (ashCommand instanceof AshResourceRelatedCommand) {
			AshResource resource = null;
			if (commands.length >= 2) {
				resource = AshResource.getInstance(commands[1]);
			}
			if (resource == null) {
				resource = AshResource.getInstance(request.getString("resource"));
				if (resource == null) {
					result.put("message", "资源不明，请使用msg命令联系管理员");
					return result;
				}
			} else {
				paramStartIndex = 2;
			}
			((AshResourceRelatedCommand)ashCommand).setResource(resource);
		}

		String[] params = new String[commands.length - paramStartIndex];
		boolean open = false;
		int pIndex = 0;
		for (int i = paramStartIndex; i < commands.length; i++) {
			commands[i] = commands[i].trim();
			if ("".equals(commands[i])) continue;
			if (params[pIndex] == null) {
				params[pIndex] = commands[i];
			} else {
				params[pIndex] += " " + commands[i];
			}
			// TODO
			//if (commands[i].startsWith("\"")) open = true;
			//if (commands[i].endsWith("\"")) open = false;
			if (commands[i].length() == 1 && commands[i].startsWith("'")) {
				open = !open;
			} else {
				if (commands[i].startsWith("'")) open = true;
				if (commands[i].endsWith("'")) open = false;
			}
			if (open) {
				continue;
			} else {
				pIndex++;
			}
		}

		try {
			Executable executor = ashCommand.getExecutor();
			executor.setParam(params);
			ArgsHelper.check(executor);
			if (!(executor instanceof MkUser || executor instanceof AshCommonCommand)) {
				this.checkToken(this.token);
			}
			ashCommand.setUserId(this.userId);
			ashCommand.setToken(this.token);
			executor.setUserId(this.userId);
			executor.setToken(this.token);
			executor.readParam();
			AshReply reply = executor.exec();
			result.put("display", reply.display);
			//result.put("message", reply.message);
			result.put("hidden", reply.hidden);
		} catch (DialogException e) {
			//result.put("message", e.getMessage());
			result.put("display", e.question);
			JSONObject callback = new JSONObject();
			callback.put("command", commandLine + " -" + e.answerField + " ");
			result.put("callback", callback);
		/*
		// TODO 未使用
		} catch (CallbackException e) {
			JSONObject callback = new JSONObject();
			callback.put("url", e.url);
			JSONArray paramsJ = new JSONArray();
			for (int i = 0; i < e.params.length; i++) {
				paramsJ.add(e.params[i]);
			}
			callback.put("files", paramsJ);
			result.put("callback", callback);
		*/
		} catch (ParamException e) {
			result.put("message", e.getMessage());
			result.put("display", ashCommand.help());
		} catch (HttpException e) {
			result.put("message", e.getMessage());
		} catch (AshException e) {
			result.put("message", e.getMessage());
			result.put("display", "如需帮助，请使用msg命令联系管理员");
		}

		return result;
	}
}
