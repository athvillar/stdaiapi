package cn.standardai.api.ash.command;

import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.command.base.AshCommonCommand;

public class AshCd extends AshCommonCommand {

	public AshCd() {
		setParamRules(null, null, 1, 1);
	}

	@Override
	public void invoke() {
		return;
	}

	@Override
	public AshReply help() {
		this.reply.display = "cd命令格式：cd [资源名]";
		return this.reply;
	}

	@Override
	public AshReply man() {
		this.reply.display = "cd命令用于在资源之间切换，资源相关的命令在操作当前资源时可以在参数中省略资源名"
				+ "例如在model资源目录下，可以通过mk建立model，而不必输入mk model\n"
				+ "用法\n"
				+ "\tcd [资源名]\n";
		return this.reply;
	}

	@Override
	public String[][] getDialog() {
		return null;
	}
}
