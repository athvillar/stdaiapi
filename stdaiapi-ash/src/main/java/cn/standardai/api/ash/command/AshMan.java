package cn.standardai.api.ash.command;

import cn.standardai.api.ash.base.AshCommand;
import cn.standardai.api.ash.base.AshCommonCommand;
import cn.standardai.api.ash.exception.AshException;

public class AshMan extends AshCommonCommand {

	public AshMan() {
		setParamRules(null, null, 1, 1);
	}

	@Override
	public void invoke() throws AshException {

		AshCommand command = AshCommand.getInstance(param.get(1));
		if (command == null) {
			this.reply.display = "没有找到" + param.get(1);
		}
		this.reply.display = command.man();
	}

	@Override
	public String help() {
		return "man [命令名]";
	}

	@Override
	public String man() {
		return "man命令用于显示帮助信息\n"
			+ "语法\n"
			+ "\tman [命令名]";
	}

	@Override
	public void readParam() throws AshException {
		return;
	}
}
