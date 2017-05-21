package cn.standardai.api.ash.command;

import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.command.base.AshCommand;
import cn.standardai.api.ash.command.base.AshCommonCommand;

public class AshMan extends AshCommonCommand {

	public AshMan() {
		setParamRules(null, null, 1, 1);
	}

	@Override
	public void invoke() {

		AshCommand command = AshCommand.getInstance(params.get(1));
		if (command == null) {
			this.reply.display = "没有找到" + params.get(1);
		}
		this.reply.display = command.man().display;
	}

	@Override
	public AshReply help() {
		this.reply.display = "man命令格式：man [命令名]";
		return this.reply;
	}

	@Override
	public AshReply man() {
		this.reply.display = "man命令用于显示帮助信息\n"
			+ "用法\n"
			+ "\tman [命令名]";
		return this.reply;
	}

	@Override
	public String[][] getDialog() {
		return null;
	}
}
