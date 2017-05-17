package cn.standardai.api.ash.command;

import cn.standardai.api.ash.command.base.AshCommand;
import cn.standardai.api.ash.command.base.AshCommonCommand;

public class AshMan extends AshCommonCommand {

	protected final String help = "man命令格式：man [命令名]";

	protected final String man = "man命令用于显示帮助信息\n"
			+ "用法\n"
			+ "\tman [命令名]";

	@Override
	public void invoke() {

		AshCommand command = AshCommand.getInstance(params.get(1));
		if (command == null) {
			this.reply.display = "没有找到" + params.get(1);
		}

		this.reply.display = command.man().display;
	}
}
