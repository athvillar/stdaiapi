package cn.standardai.api.ash.action;

import cn.standardai.api.ash.command.base.AshCommand.HttpMethod;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.core.bean.Context;

public class RmUser extends Action {

	@Override
	public void exec() throws AshException {
		comm.http(HttpMethod.DELETE, Context.getProp().getUrl().getBiz() + "/user/" + comm.userId, null, null);
		comm.reply.display = "用户(" + comm.userId + ")已删除";
	}

	@Override
	public void setParam() throws AshException {
		return;
	}
}
