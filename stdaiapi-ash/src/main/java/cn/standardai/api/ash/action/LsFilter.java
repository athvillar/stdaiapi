package cn.standardai.api.ash.action;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.base.Action;
import cn.standardai.api.ash.base.AshCommand.HttpMethod;
import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.core.bean.Context;

public class LsFilter extends Action {

	@Override
	public AshReply exec() throws AshException {

		JSONObject j = comm.http(HttpMethod.GET, Context.getProp().getUrl().getMl() + "/filters", null, null);

		JSONArray filters = j.getJSONArray("filters");
		if (filters == null || filters.size() == 0) {
			this.reply.display = "没有过滤器";
			return this.reply;
		}

		String result = "过滤器名\t\t\t\t\t描述";
		for (int i = 0; i < filters.size(); i++) {
			result += "\n" + fillWithSpace(filters.getJSONObject(i).getString("filterName"), 23) + "\t";
			String desc = filters.getJSONObject(i).getString("description");
			int len = desc.length();
			if (len > 36) {
				result += desc.substring(0, 36) + " ...";
			} else {
				result += desc;
			}
		}
		result += "\n共" + filters.size() + "条记录";
		this.reply.display = result;
		return this.reply;
	}

	@Override
	public void readParam() throws AshException {
		return;
	}
}
