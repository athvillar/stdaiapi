package cn.standardai.api.ash.action;

import cn.standardai.api.ash.base.Action;
import cn.standardai.api.ash.base.AshCommand.HttpMethod;
import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.core.bean.Context;

public class RmModel extends Action {

	private String modelName;

	public RmModel() {
		setParamRules(null, null, 1, 1);
	}

	@Override
	public AshReply exec() throws AshException {
		comm.http(HttpMethod.DELETE, Context.getProp().getUrl().getMl() + "/dnn/" + this.userId + "/" + modelName, null, null);
		this.reply.display = "模型(" + comm.userId + "/" + modelName + ")已删除";
		return this.reply;
	}

	@Override
	public void readParam() throws AshException {
		modelName = param.get(1);
	}
}
