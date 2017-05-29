package cn.standardai.api.ash.action;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.base.Action;
import cn.standardai.api.ash.base.AshCommand.HttpMethod;
import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.core.bean.Context;

public class CatDic extends Action {

	private String dicName;

	private String pDicName;

	public CatDic() {
		setParamRules(null, null, 1, 1);
	}

	@Override
	public AshReply exec() throws AshException {

		JSONObject j = comm.http(HttpMethod.GET, Context.getProp().getUrl().getData() + "/dic/" + pDicName, null, null);

		JSONObject dicJ = j.getJSONObject("dic");
		if (dicJ == null) {
			this.reply.display = "没有找到数据字典";
			return this.reply;
		}

		String result = "字典名\t\t" + dicJ.getString("dicName") + "\n";
		result += "描述\t\t\t" + dicJ.getString("description") + "\n";
		result += "共享\t\t\t" + dicJ.getString("sharePolicy") + "\n";
		this.reply.display = result;
		return this.reply;
	}

	@Override
	public void readParam() throws AshException {
		dicName = param.get(1);
		if (dicName != null && dicName.indexOf('/') != -1) {
			pDicName = dicName;
		} else {
			pDicName = this.userId + "/" + dicName;
		}
	}
}
