package cn.standardai.api.ash.command;

import cn.standardai.api.ash.base.AshCommonCommand;
import cn.standardai.api.ash.exception.AshException;

public class AshHistory extends AshCommonCommand {

	public AshHistory() {
		setParamRules(null, null, null, 0);
	}

	@Override
	public void invoke() {
		return;
	}

	@Override
	public String help() {
		return "history";
	}

	@Override
	public String man() {
		return "history命令用于显示用户在本次登录内的命令输入历史\n"
				+ "语法\n"
				+ "\thistory\n";
	}

	@Override
	public void readParam() throws AshException {
		// TODO Auto-generated method stub
		
	}
}
