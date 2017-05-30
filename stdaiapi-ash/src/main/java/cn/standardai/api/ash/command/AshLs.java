package cn.standardai.api.ash.command;

import cn.standardai.api.ash.base.AshResourceRelatedCommand;

public class AshLs extends AshResourceRelatedCommand {

	@Override
	public String help() {
		return "ls [资源类别] [-a] [-l] [-u 用户名]";
	}

	@Override
	public String man() {
		return "ls命令用于显示当前资源类别下的所有资源\n"
				+ "语法\n"
				+ "\tls [资源类别] [-a] [-l]\n"
				+ "参数\n"
				+ "\t-a: 显示所有可用资源，如果省略将只显示自己的资源\n"
				+ "\t-l: 显示资源详细信息\n"
				+ "\t-u 用户名: 显示该用户共享的资源\n"
				+ "用例\n"
				+ "\tls model -l，查看模型详细信息。\n"
				+ "\tls data -al，查看所有人共享的数据详细信息。\n"
				+ "\tls model -u A，查看A用户共享的模型信息。";
	}
}
