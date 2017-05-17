package cn.standardai.api.ash.command;

import cn.standardai.api.ash.command.base.AshResourceCommand;

public class AshRm extends AshResourceCommand {

	protected final String help = "rm命令格式：rm [资源ID]";

	protected final String man = "rm命令用于删除指定资源ID的资源\n"
				+ "用法：\n"
				+ "\trm [资源ID]";
}
