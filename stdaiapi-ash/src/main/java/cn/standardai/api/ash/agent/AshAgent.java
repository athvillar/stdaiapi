package cn.standardai.api.ash.agent;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.command.base.AshCommand;
import cn.standardai.api.ash.command.base.AshResourceRelatedCommand;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.ash.exception.DialogException;
import cn.standardai.api.ash.exception.HttpException;
import cn.standardai.api.ash.exception.ParamException;
import cn.standardai.api.ash.resource.base.AshResource.Resource;
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

		AshCommand ashCommand = AshCommand.getInstance(commands[0]);
		if (ashCommand == null) {
			result.put("message", "无此命令，或者此命令正在开发中，如需帮助，请输入“help”");
			return result;
		}

		int paramStartIndex = 1;
		if (ashCommand instanceof AshResourceRelatedCommand) {
			Resource resource = null;
			if (commands.length >= 2) {
				resource = Resource.resolve(commands[1]);
			}
			if (resource == null) {
				resource = Resource.resolve(request.getString("resource"));
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
			if (params[pIndex] == null) {
				params[pIndex] = commands[i];
			} else {
				params[pIndex] += " " + commands[i];
			}
			if (commands[i].startsWith("\"")) open = true;
			if (commands[i].endsWith("\"")) open = false;
			if (open) {
				continue;
			} else {
				pIndex++;
			}
		}
		AshReply reply;
		try {
			reply = ashCommand.exec(params, this.userId, this.getToken());
			result.put("display", reply.display);
			//result.put("message", reply.message);
			result.put("hidden", reply.hidden);
		} catch (ParamException e) {
			result.put("message", e.getMessage());
			result.put("display", ashCommand.help().display);
			//result.put("hidden", reply.hidden);
		} catch (HttpException e) {
			result.put("message", e.getMessage());
			//result.put("display", "如需帮助，请使用msg命令联系管理员");
			//result.put("hidden", reply.hidden);
		} catch (AshException e) {
			result.put("message", e.getMessage());
			result.put("display", "如需帮助，请使用msg命令联系管理员");
			//result.put("hidden", reply.hidden);
		}

		return result;
	}

	public JSONObject dialog(JSONObject request) throws AshException {

		JSONObject dialog = request.getJSONObject("dialog");
		if (dialog == null) throw new DialogException("参数错误");

		String dialogId = dialog.getString("id");
		JSONArray answersJ = dialog.getJSONArray("answers");
		String question = AshDialog.getQuestion(dialogId, answersJ.size());
		if (question == null) {
			String[] answers = new String[answersJ.size()];
			for (int i = 0; i < answers.length; i++) {
				answers[i] = answersJ.getString(i);
			}
			String command = request.getString("command");
			String resource = request.getString("resource");
			return AshDialog.finish(command, resource, answers, this.getToken());
		} else {
			JSONObject result = new JSONObject();
			result.put("display", question);
			return result;
		}
	}
}
