package cn.standardai.api.ash.command;

public class AshMan extends AshCommand {

	public AshMan(String token) {
		super(token);
	}

	@Override
	public String exec(String[] params) {

		if (params.length != 2) {
			return this.help("参数个数不正确");
		}

		AshCommand command = AshCommand.getInstance(params[1], this.token);
		if (command == null) {
			return "没有找到" + params[1];
		}

		return command.man();
	}

	@Override
	public String help() {
		return "man命令格式：man [命令名]";
	}

	@Override
	public String man() {
		return "man命令用于显示帮助信息\n"
				+ "用法\n"
				+ "\tman [命令名]";
	}
}
