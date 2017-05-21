package cn.standardai.api.ash.command;

import cn.standardai.api.ash.base.AshCommonCommand;
import cn.standardai.api.ash.exception.AshException;

public class AshCd extends AshCommonCommand {

	@Override
	public void invoke() {
		return;
	}

	@Override
	public String help() {
		return "cd命令格式：cd [资源名]";
	}

	@Override
	public String man() {
		return "cd命令用于在资源之间切换，资源相关的命令在操作当前资源时可以在参数中省略资源名"
				+ "例如在model资源目录下，可以通过mk建立model，而不必输入mk model\n"
				+ "用法\n"
				+ "\tcd [资源名]\n";
	}

	@Override
	public void readParam() throws AshException {
		return;
	}
}
