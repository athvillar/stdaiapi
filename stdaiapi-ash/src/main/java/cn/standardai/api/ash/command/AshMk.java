package cn.standardai.api.ash.command;

import cn.standardai.api.ash.base.AshResourceRelatedCommand;

public class AshMk extends AshResourceRelatedCommand {

	@Override
	public String help() {
		return "mk命令格式：mk [资源] [参数1, 2, 3...]";
	}

	@Override
	public String man() {
		return "mk命令用于创建资源\n"
				+ "用法：\n"
				+ "\tmk [资源] [参数1, 2, 3...]\n"
				+ "例:\n"
				+ "\t创建用户:\tmk user";
	}
}
