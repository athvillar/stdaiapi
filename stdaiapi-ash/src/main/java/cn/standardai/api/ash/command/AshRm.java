package cn.standardai.api.ash.command;

import cn.standardai.api.ash.base.AshResourceRelatedCommand;

public class AshRm extends AshResourceRelatedCommand {

	@Override
	public String help() {
		return "rm [资源类别] [资源名]";
	}

	@Override
	public String man() {
		return "rm命令用于删除指定资源\n"
				+ "语法\n"
				+ "\trm [资源类别] [资源名]\n"
				+ "用例\n"
				+ "\trm model testmodel, 删除模型名为testmodel的模型。";
	}
}
