package cn.standardai.api.ash.command;

import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.command.base.AshResourceRelatedCommand;

public class AshCall extends AshResourceRelatedCommand {

	public AshCall() {
		//setParamRules(null, null, null, null);
	}

	@Override
	public AshReply help() {
		this.reply.display = "model命令格式：model [模型名] -t(rain) -p(redict) -r(elease) -s(etting)";
		return this.reply;
	}

	@Override
	public AshReply man() {
		this.reply.display = "model命令用于执行模型资源，包括训练模型，测试模型，部署模型，设置默认参数等操作\n"
				+ "用法：\n"
				+ "\tmodel -t(rain) -p(redict) -r(elease) -s(etting)\n"
				+ "参数：\n"
				+ "\t-t: 训练模型\n"
				+ "\t-p: 测试模型\n"
				+ "\t-r: 部署模型\n"
				+ "\t-s: 设置参数";
		return this.reply;
	}

	@Override
	public String[][] getDialog() {
		return null;
	}
}
