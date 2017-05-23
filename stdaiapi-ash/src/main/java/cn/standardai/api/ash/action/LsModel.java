package cn.standardai.api.ash.action;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.base.Action;
import cn.standardai.api.ash.base.AshCommand.HttpMethod;
import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.core.bean.Context;
import cn.standardai.api.core.util.DateUtil;

public class LsModel extends Action {

	public LsModel() {
		setParamRules(new char[] {'l'}, null, null, null);
	}

	@Override
	public AshReply exec() throws AshException {

		JSONObject j = comm.http(HttpMethod.GET, Context.getProp().getUrl().getMl() + "/dnn", null, null);

		JSONArray models = j.getJSONArray("models");
		if (models == null || models.size() == 0) {
			this.reply.display = "没有模型";
			return this.reply;
		}

		String result;
		if (this.param.has('l')) {
			result = "模型名\t\t算法\t\t创建时间\t\t\t\t结构";
		} else {
			result = "模型名\t\t算法\t\t创建时间";
		}
		for (int i = 0; i < models.size(); i++) {
			result += "\n" + fillWithSpace(models.getJSONObject(i).getString("modelTemplateName"), 11) + "\t"
							+ fillWithSpace(models.getJSONObject(i).getString("algorithm"), 8) + "\t"
							+ DateUtil.format(models.getJSONObject(i).getDate("createTime"), DateUtil.YYYY__MM__DD__HH__MM__SS) + "\t";
			if (this.param.has('l')) result += models.getJSONObject(i).getString("script");
		}
		result += "\n共" + models.size() + "条记录";
		this.reply.display = result;

		return this.reply;
	}

	@Override
	public void readParam() throws AshException {
		// TODO Auto-generated method stub
		
	}
}
