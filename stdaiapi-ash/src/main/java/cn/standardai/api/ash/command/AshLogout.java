package cn.standardai.api.ash.command;

import cn.standardai.api.ash.base.AshCommonCommand;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.ash.exception.HttpException;
import cn.standardai.api.core.bean.Context;

public class AshLogout extends AshCommonCommand {

	public AshLogout() {
		setParamRules(null, null, 0, 0);
	}

	@Override
	public void invoke() throws HttpException {
		http(HttpMethod.DELETE, Context.getProp().getUrl().getBiz() + "/token/" + this.token, null, null);
		this.reply.display = "成功退出";
	}

	@Override
	public String help() {
		return "logout命令格式：logout";
	}

	@Override
	public String man() {
		return "login命令用于用户登出\n"
				+ "用法：\n"
				+ "\tlogout";
	}

	@Override
	public void readParam() throws AshException {
		return;
	}
}
