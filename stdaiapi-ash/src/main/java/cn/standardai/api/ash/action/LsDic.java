package cn.standardai.api.ash.action;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.base.Action;
import cn.standardai.api.ash.base.AshCommand.HttpMethod;
import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.core.bean.Context;

public class LsDic extends Action {

	public LsDic() {
		setParamRules(new char[] {'l'}, null, null, null);
	}

	@Override
	public AshReply exec() throws AshException {

		JSONObject j = comm.http(HttpMethod.GET, Context.getProp().getUrl().getData() + "/dic", null, null);

		JSONArray data = j.getJSONArray("dic");
		if (data == null || data.size() == 0) {
			this.reply.display = "没有数据字典";
			return this.reply;
		}

		String result;
		if (this.param.has('l')) {
			result = "数据字典名\t\t\t描述";
		} else {
			result = "数据字典名";
		}
		for (int i = 0; i < data.size(); i++) {
			result += "\n" + fillWithSpace(data.getJSONObject(i).getString("dicName"), 12) + "\t\t";
			if (this.param.has('l')) {
				result += data.getJSONObject(i).getString("description");
			}
		}
		result += "\n共" + data.size() + "条数据";
		this.reply.display = result;

		return this.reply;
	}

	@Override
	public void readParam() throws AshException {
		return;
	}
}
