package cn.standardai.api.ash.command;

import cn.standardai.api.ash.base.AshResourceRelatedCommand;

public class AshLs extends AshResourceRelatedCommand {

	@Override
	public String help() {
		return "ls [资源类别] [-l]";
	}

	@Override
	public String man() {
		return "ls命令用于显示当前资源类别下的所有资源\n"
				+ "语法\n"
				+ "\tls [资源类别] [-l]\n"
				+ "参数\n"
				+ "\t-l: 显示详细信息\n"
				+ "用例\n"
				+ "\tls model -l，查看模型详细信息。";
	}
}
