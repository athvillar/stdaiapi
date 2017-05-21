package cn.standardai.api.ash.command;

import cn.standardai.api.ash.base.AshResourceRelatedCommand;

public class AshRm extends AshResourceRelatedCommand {

	@Override
	public String help() {
		return "rm命令格式：rm [资源ID]";
	}

	@Override
	public String man() {
		return "rm命令用于删除指定资源ID的资源\n"
				+ "用法：\n"
				+ "\trm [资源ID]";
	}
}
