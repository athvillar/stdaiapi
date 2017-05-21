package cn.standardai.api.ash.action;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.command.base.AshCommand.HttpMethod;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.core.bean.Context;
import cn.standardai.api.core.util.DateUtil;

public class LsModel extends Action {

	@Override
	public void exec() throws AshException {

		JSONObject j = comm.http(HttpMethod.GET, Context.getProp().getUrl().getMl() + "/dnn", null, null);

		JSONArray models = j.getJSONArray("models");
		if (models == null || models.size() == 0) comm.reply.display = "没有记录";

		String result;
		if (comm.params.has('l')) {
			result = "modelName\t\t\t\t\t\t\tupdateTime\t\t";
		} else {
			result = "modelName";
		}
		for (int i = 0; i < models.size(); i++) {
			result += "\n" + models.getJSONObject(i).getString("modelName") + "\t";
			if (comm.params.has('l')) result += DateUtil.format(models.getJSONObject(i).getDate("updateTime"), DateUtil.YYYY__MM__DD__HH__MM__SS);
		}
		result += "\n共" + models.size() + "条记录";
		comm.reply.display = result;
	}

	@Override
	public void setParam() throws AshException {
		return;
	}
}
