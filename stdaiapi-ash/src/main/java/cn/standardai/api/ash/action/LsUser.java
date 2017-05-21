package cn.standardai.api.ash.action;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.base.Action;
import cn.standardai.api.ash.base.AshCommand.HttpMethod;
import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.core.bean.Context;
import cn.standardai.api.core.util.DateUtil;

public class LsUser extends Action {

	@Override
	public AshReply exec() throws AshException {
		JSONObject j = comm.http(HttpMethod.GET, Context.getProp().getUrl().getBiz() + "/user/" + comm.userId, null, null);
		JSONObject user = j.getJSONObject("user");
		if (user == null) {
			this.reply.display = "没有用户";
			return this.reply;
		}

		String result;
		if (this.param.has('l')) {
			result = "userId\t\t\t\t\t\t\temail\t\t\t\t账户余额\t\t上次登录时间\t\t";
		} else {
			result = "userId\t\t\t\t\t\t\temail";
		}
		result += "\n" + j.getString("userId") + "\t" + j.getString("email");
		if (this.param.has('l')) {
			result += j.getDouble("remainMoney") + "\t\t";
			result += DateUtil.format(j.getDate("lastLoginTime"), DateUtil.YYYY__MM__DD__HH__MM__SS);
		}

		this.reply.display = result;
		return this.reply;
	}

	@Override
	public void readParam() throws AshException {
		// TODO Auto-generated method stub
		
	}
}