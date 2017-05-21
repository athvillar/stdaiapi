package cn.standardai.api.ash.action;

import cn.standardai.api.ash.command.base.AshCommand.HttpMethod;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.core.bean.Context;

public class RmModel extends Action {

	private String modelName;

	@Override
	public void exec() throws AshException {
		comm.http(HttpMethod.DELETE, Context.getProp().getUrl().getMl() + "/dnn/" + comm.userId + "/" + modelName, null, null);
		comm.reply.display = "模型(" + comm.userId + "/" + modelName + ")已删除";
	}

	@Override
	public void setParam() throws AshException {
		return;
	}
}
