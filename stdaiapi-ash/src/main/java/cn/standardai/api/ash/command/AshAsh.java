package cn.standardai.api.ash.command;

import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.command.base.AshCommonCommand;

public class AshAsh extends AshCommonCommand {

	public AshAsh() {
		//setParamRules(null, null, 1, null);
	}

	@Override
	public void invoke() {

	}

	@Override
	public AshReply help() {
		//this.reply.display = "help命令格式：help [名词]";
		return this.reply;
	}

	@Override
	public AshReply man() {
		/*
		this.reply.display = "help命令用于显示帮助信息\n"
				+ "用法\n"
				+ "\thelp [名词]\n"
				+ "参数\n"
				+ "\t名词:\t显示该名词解释";
				*/
		return this.reply;
	}

	@Override
	public String[][] getDialog() {
		return null;
	}
}
