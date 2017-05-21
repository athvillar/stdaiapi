package cn.standardai.api.ash.command;

import cn.standardai.api.ash.base.AshResourceRelatedCommand;

public class AshLs extends AshResourceRelatedCommand {

	@Override
	public String help() {
		return "ls命令格式：ls [-l]";
	}

	@Override
	public String man() {
		return "ls命令用于显示当前资源类别下的所有资源\n"
				+ "用法：\n"
				+ "\tls -参数\n"
				+ "参数：\n"
				+ "\t-l: 显示详细信息";
	}
}
