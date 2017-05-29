package cn.standardai.api.ash.action;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.base.Action;
import cn.standardai.api.ash.base.AshCommand.HttpMethod;
import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.core.bean.Context;
import cn.standardai.api.core.util.DateUtil;

public class LsData extends Action {

	private String targetUserId;

	public LsData() {
		setParamRules(new char[] {'a', 'l'}, new String[] {"u"}, null, 0);
	}

	@Override
	public AshReply exec() throws AshException {

		JSONObject j;
		if (param.has('a')) {
			j = comm.http(HttpMethod.GET, Context.getProp().getUrl().getData() + "/data", null, null);
		} else if (targetUserId != null) {
			j = comm.http(HttpMethod.GET, Context.getProp().getUrl().getData() + "/data/" + targetUserId, null, null);
		} else {
			j = comm.http(HttpMethod.GET, Context.getProp().getUrl().getData() + "/data/" + this.userId, null, null);
		}

		JSONArray data = j.getJSONArray("data");
		if (data == null || data.size() == 0) {
			this.reply.display = "没有数据";
			return this.reply;
		}

		String result;
		if (this.param.has('l')) {
			result = "数据名\t\t\t\t类型\t\t关键词\t\t\t格式\t\t\t创建时间\t\t\t\t描述";
		} else {
			result = "数据名\t\t\t\t关键词";
		}
		for (int i = 0; i < data.size(); i++) {
			result += "\n" + fillWithSpace(data.getJSONObject(i).getString("dataName"), 20) + "\t";
			if (this.param.has('l')) result += fillWithSpace(data.getJSONObject(i).getString("type"), 9) + "\t";
			result += fillWithSpace(data.getJSONObject(i).getString("keywords"), 11) + "\t";
			if (this.param.has('l')) result += fillWithSpace(data.getJSONObject(i).getString("format"), 9) + "\t";
			if (this.param.has('l')) result += DateUtil.format(data.getJSONObject(i).getDate("createTime"), DateUtil.YYYY__MM__DD__HH__MM__SS) + "\t";
			if (this.param.has('l')) result += data.getJSONObject(i).getString("description");
		}
		result += "\n共" + data.size() + "条数据";
		this.reply.display = result;

		return this.reply;
	}

	@Override
	public void readParam() throws AshException {
		targetUserId = param.getString("u");
	}
}
