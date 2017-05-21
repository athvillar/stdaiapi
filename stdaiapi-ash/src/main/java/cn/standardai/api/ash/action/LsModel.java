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

	@Override
	public AshReply exec() throws AshException {

		JSONObject j = comm.http(HttpMethod.GET, Context.getProp().getUrl().getMl() + "/dnn", null, null);

		JSONArray models = j.getJSONArray("models");
		if (models == null || models.size() == 0) this.reply.display = "没有记录";

		String result;
		if (this.param.has('l')) {
			result = "modelName\t\t\t\t\t\t\tupdateTime\t\t";
		} else {
			result = "modelName";
		}
		for (int i = 0; i < models.size(); i++) {
			result += "\n" + models.getJSONObject(i).getString("modelName") + "\t";
			if (this.param.has('l')) result += DateUtil.format(models.getJSONObject(i).getDate("updateTime"), DateUtil.YYYY__MM__DD__HH__MM__SS);
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
