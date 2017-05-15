package cn.standardai.api.ash.command;

public class AshMan extends AshCommand {

	@Override
	public String exec(String[] params) {

		if (params.length != 2) {
			return this.help();
		}

		AshCommand command = AshCommand.getInstance(params[1]);
		if (command == null) {
			return "没有找到" + params[1] + "\n";
		}

		return command.man();
	}

	@Override
	public String help() {
		return "man命令格式：man [命令名]\n";
	}

	@Override
	public String man() {
		return "man命令用于显示帮助信息\n"
				+ "用法：\n"
				+ "\tman [命令名]\n";
	}
}
