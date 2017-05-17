package cn.standardai.api.ash.command;

import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.command.base.AshResourceCommand;

public class AshRm extends AshResourceCommand {

	public AshRm() {
		setParamRules(null, null, null, 1);
	}

	@Override
	public AshReply help() {
		this.reply.display = "rm命令格式：rm [资源ID]";
		return this.reply;
	}

	@Override
	public AshReply man() {
		this.reply.display = "rm命令用于删除指定资源ID的资源\n"
				+ "用法：\n"
				+ "\trm [资源ID]";
		return this.reply;
	}
}
