package cn.standardai.api.ash.command;

import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.command.base.AshCommonCommand;

public class AshHistory extends AshCommonCommand {

	public AshHistory() {
		setParamRules(null, null, null, 0);
	}

	@Override
	public void invoke() {
		return;
	}

	@Override
	public AshReply help() {
		this.reply.display = "history命令格式：history";
		return this.reply;
	}

	@Override
	public AshReply man() {
		this.reply.display = "history命令用于显示用户在本次登录内的命令输入历史\n"
				+ "用法\n"
				+ "\thistory\n";
		return this.reply;
	}

	@Override
	public String[][] getDialog() {
		return null;
	}
}
