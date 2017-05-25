package cn.standardai.api.ash.action;

import cn.standardai.api.ash.base.Action;
import cn.standardai.api.ash.base.AshCommand.HttpMethod;
import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.core.bean.Context;

public class RmDic extends Action {

	private String dicName;

	public RmDic() {
		setParamRules(null, null, 1, 1);
	}

	@Override
	public AshReply exec() throws AshException {
		comm.http(HttpMethod.DELETE, Context.getProp().getUrl().getData() + "/dic/" + this.userId + "/" + dicName, null, null);
		this.reply.display = "数据字典(" + comm.userId + "/" + dicName + ")已删除";
		return this.reply;
	}

	@Override
	public void readParam() throws AshException {
		dicName = param.get(1);
	}
}
