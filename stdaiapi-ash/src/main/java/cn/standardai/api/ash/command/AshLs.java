package cn.standardai.api.ash.command;

import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.command.base.AshResourceRelatedCommand;

public class AshLs extends AshResourceRelatedCommand {

	public AshLs() {
		setParamRules(new char[] {'l'}, null, 0, null);
	}

	@Override
	public AshReply help() {
		this.reply.display = "ls命令格式：ls [-l]";
		return this.reply;
	}

	@Override
	public AshReply man() {
		this.reply.display = "ls命令用于显示当前资源类别下的所有资源\n"
				+ "用法：\n"
				+ "\tls -参数\n"
				+ "参数：\n"
				+ "\t-l: 显示详细信息";
		return this.reply;
	}

	@Override
	public String[][] getDialog() {
		return null;
	}
}
