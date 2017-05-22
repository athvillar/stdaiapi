package cn.standardai.api.ash.command;

import cn.standardai.api.ash.base.AshResourceRelatedCommand;

public class AshCat extends AshResourceRelatedCommand {

	@Override
	public String help() {
		return "cat [资源类别] [资源名]";
	}

	@Override
	public String man() {
		return "cat命令用于显示某个资源详细信息\n"
				+ "语法\n"
				+ "\tcat [资源类别] [资源名]\n"
				+ "用例\n"
				+ "\tcat model testmodel，显示testmodel的详细信息。";
	}
}
