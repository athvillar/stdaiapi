package cn.standardai.api.ash.command;

import cn.standardai.api.ash.base.AshResourceRelatedCommand;

public class AshCat extends AshResourceRelatedCommand {

	public AshCat() {
		//setParamRules(null, null, null, null);
	}

	@Override
	public String help() {
		//this.reply.display = "mk命令格式：mk [资源] [参数1, 2, 3...]";
		return null;
	}

	@Override
	public String man() {
		/*
		this.reply.display = "mk命令用于创建资源\n"
				+ "用法：\n"
				+ "\tmk [资源] [参数1, 2, 3...]\n"
				+ "例:\n"
				+ "\t创建用户:\tmk user";
				*/
		return null;
	}
}
