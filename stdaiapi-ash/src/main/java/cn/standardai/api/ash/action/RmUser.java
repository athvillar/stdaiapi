package cn.standardai.api.ash.action;

import cn.standardai.api.ash.base.Action;
import cn.standardai.api.ash.base.AshCommand.HttpMethod;
import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.core.bean.Context;

public class RmUser extends Action {

	private String targetUser;

	public RmUser() {
		setParamRules(null, null, 1, 1);
	}

	@Override
	public AshReply exec() throws AshException {
		comm.http(HttpMethod.DELETE, Context.getProp().getUrl().getBiz() + "/user/" + targetUser, null, null);
		this.reply.display = "用户(" + targetUser + ")已删除";
		return this.reply;
	}

	@Override
	public void readParam() throws AshException {
		targetUser = param.get(1);
	}
}
