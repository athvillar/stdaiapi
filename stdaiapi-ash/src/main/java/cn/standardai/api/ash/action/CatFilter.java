package cn.standardai.api.ash.action;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.base.Action;
import cn.standardai.api.ash.base.AshCommand.HttpMethod;
import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.core.bean.Context;

public class CatFilter extends Action {

	private String filterName;

	public CatFilter() {
		setParamRules(null, null, 1, 1);
	}

	@Override
	public AshReply exec() throws AshException {

		JSONObject j = comm.http(HttpMethod.GET, Context.getProp().getUrl().getMl() + "/filters/" + filterName, null, null);

		String desc = j.getString("description");
		if (desc == null) {
			this.reply.display = "没有找到过滤器";
			return this.reply;
		}

		String result = "过滤器名\t\t" + filterName + "\n";
		result += "描述\t\t\t" + desc;
		this.reply.display = result;
		return this.reply;
	}

	@Override
	public void readParam() throws AshException {
		filterName = param.get(1);
	}
}
