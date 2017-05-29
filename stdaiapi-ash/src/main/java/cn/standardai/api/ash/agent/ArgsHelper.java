package cn.standardai.api.ash.agent;

import java.util.HashMap;
import java.util.Map;

import cn.standardai.api.ash.base.Executable;
import cn.standardai.api.ash.exception.DialogException;

public class ArgsHelper {

	private static Map<Class<? extends Executable>, String[][][]> dialogs = new HashMap<Class<? extends Executable>, String[][][]>();

	public static void check(Executable executor) throws DialogException {

		if (!dialogs.containsKey(executor.getClass())) return;

		String[][] dialog = dialogs.get(executor.getClass())[executor.getDialogIndex()];
		for (int i = 0; i < dialog.length; i++) {
			if (executor.getParam().getString(dialog[i][0]) == null) {
				throw new DialogException("缺少参数" + dialog[i][0], dialog[i][1], dialog[i][0]);
			}
		}
	}

	public static void regist(Class<? extends Executable> cls, String[][][] dialog) {
		dialogs.put(cls, dialog);
	}
}
