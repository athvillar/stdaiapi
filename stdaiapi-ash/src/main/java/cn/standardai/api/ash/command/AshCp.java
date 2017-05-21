package cn.standardai.api.ash.command;

import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.command.base.AshResourceRelatedCommand;

public class AshCp extends AshResourceRelatedCommand {

	public AshCp() {
		//setParamRules(null, null, null, null);
	}

	@Override
	public AshReply help() {
		//this.reply.display = "mk命令格式：mk [资源] [参数1, 2, 3...]";
		return this.reply;
	}

	@Override
	public AshReply man() {
		/*
		this.reply.display = "mk命令用于创建资源\n"
				+ "用法：\n"
				+ "\tmk [资源] [参数1, 2, 3...]\n"
				+ "例:\n"
				+ "\t创建用户:\tmk user";
				*/
		return this.reply;
	}

	@Override
	public String[][] getDialog() {
		return null;
	}
}
