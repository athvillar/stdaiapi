package cn.standardai.api.ash.action;

import cn.standardai.api.ash.base.Action;
import cn.standardai.api.ash.base.AshCommand.HttpMethod;
import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.core.bean.Context;

public class RmData extends Action {

	private String dataName;

	public RmData() {
		setParamRules(null, null, 1, 1);
	}

	@Override
	public AshReply exec() throws AshException {
		comm.http(HttpMethod.DELETE, Context.getProp().getUrl().getData() + "/data/" + this.userId + "/" + dataName, null, null);
		this.reply.display = "数据(" + comm.userId + "/" + dataName + ")已删除";
		return this.reply;
	}

	@Override
	public void readParam() throws AshException {
		dataName = param.get(1);
	}
}
