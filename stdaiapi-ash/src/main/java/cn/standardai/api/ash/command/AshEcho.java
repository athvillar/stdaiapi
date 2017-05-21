package cn.standardai.api.ash.command;

import cn.standardai.api.ash.base.AshCommonCommand;
import cn.standardai.api.ash.exception.AshException;

public class AshEcho extends AshCommonCommand {

	public AshEcho() {
		//setParamRules(null, null, 1, null);
	}

	@Override
	public void invoke() {

	}

	@Override
	public String help() {
		//this.reply.display = "help命令格式：help [名词]";
		return null;
	}

	@Override
	public String man() {
		/*
		this.reply.display = "help命令用于显示帮助信息\n"
				+ "用法\n"
				+ "\thelp [名词]\n"
				+ "参数\n"
				+ "\t名词:\t显示该名词解释";
				*/
		return null;
	}

	@Override
	public void readParam() throws AshException {
		// TODO Auto-generated method stub
		
	}
}
