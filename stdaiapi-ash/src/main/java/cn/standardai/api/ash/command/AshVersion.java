package cn.standardai.api.ash.command;

import cn.standardai.api.ash.base.AshCommonCommand;
import cn.standardai.api.ash.exception.AshException;

public class AshVersion extends AshCommonCommand {

	public AshVersion() {
		setParamRules(null, null, null, null);
	}

	@Override
	public void invoke() {
		this.reply.display = "stdaiapi-v0.1\nash-v0.1";
		return;
	}

	@Override
	public String help() {
		return "version命令格式：version";
	}

	@Override
	public String man() {
		return "version命令用于显示版本信息\n"
				+ "用法\n"
				+ "\tversion";
	}

	@Override
	public void readParam() throws AshException {
		// TODO Auto-generated method stub
		
	}
}
