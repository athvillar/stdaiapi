package cn.standardai.api.ash.command;

import cn.standardai.api.ash.command.base.AshResourceCommand;

public class AshMk extends AshResourceCommand {

	protected final String help = "mk命令格式：mk [资源] [参数1, 2, 3...]";

	protected final String man = "mk命令用于创建资源\n"
				+ "用法：\n"
				+ "\tmk [资源] [参数1, 2, 3...]\n"
				+ "例:\n"
				+ "\t创建用户:\tmk user";
}
