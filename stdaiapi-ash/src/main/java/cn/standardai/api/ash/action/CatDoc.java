package cn.standardai.api.ash.action;

import cn.standardai.api.ash.base.Action;
import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.ash.resource.ResDoc;

public class CatDoc extends Action {

	private String docName;

	public CatDoc() {
		setParamRules(null, null, 1, 1);
	}

	@Override
	public AshReply exec() throws AshException {

		String result = "";
		for (int i = 0; i < ResDoc.doc.length; i++) {
			if (ResDoc.doc[i][0].equalsIgnoreCase(docName)) {
				result += ResDoc.doc[i][0];
				result += "\n\n" + ResDoc.doc[i][1];
			}
		}
		this.reply.display = result;
		return this.reply;
	}

	@Override
	public void readParam() throws AshException {
		docName = param.get(1);
	}
}
