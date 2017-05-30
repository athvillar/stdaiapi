package cn.standardai.api.ash.command;

import cn.standardai.api.ash.base.AshResourceRelatedCommand;

public class AshSet extends AshResourceRelatedCommand {

	public AshSet() {
		//setParamRules(null, null, null, null);
	}

	@Override
	public String help() {
		return "set命令格式：set [资源] [参数1, 2, 3...]";
	}

	@Override
	public String man() {
		return "set命令用于设置资源\n"
				+ "用法：\n"
				+ "\tset [资源] [参数1, 2, 3...]\n"
				+ "例:\n"
				+ "\t设置数据:\tset data";
	}
}
