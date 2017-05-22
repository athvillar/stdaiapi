package cn.standardai.api.ash.action;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.base.Action;
import cn.standardai.api.ash.base.AshCommand.HttpMethod;
import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.core.bean.Context;
import cn.standardai.api.core.util.DateUtil;

public class LsUser extends Action {

	public LsUser() {
		setParamRules(new char[] {'l'}, null, null, null);
	}

	@Override
	public AshReply exec() throws AshException {

		JSONObject j = comm.http(HttpMethod.GET, Context.getProp().getUrl().getBiz() + "/user/" + this.userId, null, null);
		JSONObject user = j.getJSONObject("user");
		if (user == null) {
			this.reply.display = "没有用户";
			return this.reply;
		}

		String result;
		if (this.param.has('l')) {
			result = "用户名\t\t\t\t邮箱\t\t\t\t\t账户余额\t\t\t上次登录时间\t\t";
		} else {
			result = "用户名\t\t\t\t邮箱";
		}
		result += "\n" + fillWithSpace(user.getString("userId"), 13) + "\t\t" + fillWithSpace(user.getString("email"), 20) + "\t";
		if (this.param.has('l')) {
			result += fillWithSpace(user.getDouble("remainMoney").toString(), 8) + "\t\t\t";
			result += DateUtil.format(user.getDate("lastLoginTime"), DateUtil.YYYY__MM__DD__HH__MM__SS);
		}

		this.reply.display = result;
		return this.reply;
	}

	@Override
	public void readParam() throws AshException {
		// TODO Auto-generated method stub
		
	}
}
