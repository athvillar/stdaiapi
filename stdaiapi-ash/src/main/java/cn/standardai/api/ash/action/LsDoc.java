package cn.standardai.api.ash.action;

import cn.standardai.api.ash.base.Action;
import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.ash.resource.ResDoc;

public class LsDoc extends Action {

	@Override
	public AshReply exec() throws AshException {

		String result = "";
		for (int i = 0; i < ResDoc.doc.length; i++) {
			result += ResDoc.doc[i][0] + "\n";
		}
		result += "共" + ResDoc.doc.length + "条记录";
		this.reply.display = result;
		return this.reply;
	}

	@Override
	public void readParam() throws AshException {
		return;
	}
}
