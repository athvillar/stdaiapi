package cn.standardai.api.ash.agent;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.base.AshCommand;
import cn.standardai.api.ash.base.Executable;
import cn.standardai.api.ash.bean.AshParam;
import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.ash.exception.DialogException;
import cn.standardai.api.ash.exception.HttpException;
import cn.standardai.api.core.bean.Context;

public class ArgsHelper {

	private static Map<Class<? extends Executable>, String[][]> dialogs = new HashMap<Class<? extends Executable>, String[][]>();
/*
	public static String getQuestion(String dialogId, int size) throws AshException {

		if (dialogId == null) throw new DialogException("缺少必要的参数");
		String[] ss = dialogId.split(":");
		if (ss.length != 2) throw new DialogException("参数错误1");

		AshCommand comm = AshCommand.getInstance(ss[0]);
		if (comm == null) throw new DialogException("参数错误2");
		Integer idx = Integer.parseInt(ss[1]);

		if (idx >= getDialogSize(comm)) throw new DialogException("参数错误3");
		if (size >= getDialogLength(comm, idx)) throw new DialogException("参数错误4");

		return getDialogQuestion(comm, idx, size);
	}

	public static JSONObject finish(String command, String resource, String[] answers, String token) throws HttpException {

		for (String s : answers) {
			command += s;
		}
		JSONObject body = new JSONObject();
		body.put("ash", command);
		body.put("resource", resource);
		return AshCommand.http(AshCommand.HttpMethod.POST, Context.getProp().getUrl().getAsh() + "/ash", null, body, token);
	}

	public static AshReply make(AshCommand comm, AshParam params) {

		AshReply reply = new AshReply();
		reply.display = getDialogQuestion(comm, 0, params.number());
		JSONObject dialogJ = new JSONObject();
		dialogJ.put("id", "mk" + 0);
		JSONArray answersJ = new JSONArray();
		String s = null;
		int i = 1;
		while ((s = params.get(i)) != null) {
			answersJ.add(s);
		}
		dialogJ.put("answers", answersJ);
		reply.hidden = dialogJ.toJSONString();

		return reply;
	}

	public static Integer getDialogSize(AshCommand comm) {
		if (comm.getDialog() == null) return 0;
		return comm.getDialog().length;
	}

	public static int getDialogLength(AshCommand comm, Integer idx) {
		if (comm.getDialog() == null) return 0;
		if (idx >= comm.getDialog().length) return 0;
		return comm.getDialog()[idx].length;
	}

	public static String getDialogQuestion(AshCommand comm, Integer idx, int idx2) {
		if (comm.getDialog() == null) return null;
		if (idx >= comm.getDialog().length) return null;
		if (idx2 >= comm.getDialog()[idx].length) return null;
		return comm.getDialog()[idx][idx2];
	}
*/
	public static void check(Executable executor) throws DialogException {

		if (!dialogs.containsKey(executor.getClass())) return;

		String[][] dialog = dialogs.get(executor.getClass());
		for (int i = 0; i < dialog.length; i++) {
			if (executor.getParam().get(dialog[i][0]) == null) {
				throw new DialogException("缺少参数" + dialog[i][0], dialog[i][1], dialog[i][0]);
			}
		}
	}

	public static void regist(Class<? extends Executable> cls, String[][] dialog) {
		dialogs.put(cls, dialog);
	}
}