package cn.standardai.api.ash.command;

import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.command.base.AshCommonCommand;
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
	public AshReply help() {
		this.reply.display = "logout命令格式：logout";
		return this.reply;
	}

	@Override
	public AshReply man() {
		this.reply.display = "login命令用于用户登出\n"
				+ "用法：\n"
				+ "\tlogout";
		return this.reply;
	}
}
