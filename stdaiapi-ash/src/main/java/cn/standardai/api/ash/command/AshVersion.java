package cn.standardai.api.ash.command;

import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.command.base.AshCommonCommand;

public class AshVersion extends AshCommonCommand {

	public AshVersion() {
		setParamRules(null, null, null, null);
	}

	@Override
	public void invoke() {
		this.reply.display = "stdaiapi-v0.1\nash-v0.1";
		return;
	}

	@Override
	public AshReply help() {
		this.reply.display = "version命令格式：version";
		return this.reply;
	}

	@Override
	public AshReply man() {
		this.reply.display = "version命令用于显示版本信息\n"
				+ "用法\n"
				+ "\tversion";
		return this.reply;
	}

	@Override
	public String[][] getDialog() {
		return null;
	}
}
