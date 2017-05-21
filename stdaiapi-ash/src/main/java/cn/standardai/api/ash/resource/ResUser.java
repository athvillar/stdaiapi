package cn.standardai.api.ash.resource;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.bean.AshCommandParams;
import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.command.base.AshCommand.HttpMethod;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.ash.resource.base.AshResource;
import cn.standardai.api.core.bean.Context;
import cn.standardai.api.core.util.DateUtil;

public class ResUser extends AshResource {

	private String userId;

	private String password;

	private String email;

	@Override
	public void mk() throws AshException {
		JSONObject body = new JSONObject();
		body.put("password", password);
		body.put("email", email);
		http(HttpMethod.POST, Context.getProp().getUrl().getBiz() + "/user/" + userId, null, body);

		body = new JSONObject();
		body.put("userId", userId);
		body.put("password", password);
		JSONObject j = http(HttpMethod.POST, Context.getProp().getUrl().getBiz() + "/token", null, body);

		this.reply.hidden = j.getString("token");
		this.reply.display = "注册成功，欢迎光临！";
	}

	@Override
	public void ls() throws AshException {

		JSONObject j = http(HttpMethod.GET, Context.getProp().getUrl().getBiz() + "/user/" + userId, null, null);
		JSONObject user = j.getJSONObject("user");
		if (user == null) {
			this.reply.display = "没有用户";
			return;
		}

		String result;
		if (params.has('l')) {
			result = "userId\t\t\t\t\t\t\temail\t\t\t\t账户余额\t\t上次登录时间\t\t";
		} else {
			result = "userId\t\t\t\t\t\t\temail";
		}
		result += "\n" + j.getString("userId") + "\t" + j.getString("email");
		if (params.has('l')) {
			result += j.getDouble("remainMoney") + "\t\t";
			result += DateUtil.format(j.getDate("lastLoginTime"), DateUtil.YYYY__MM__DD__HH__MM__SS);
		}

		this.reply.display = result;
	}

	@Override
	public void rm() throws AshException {
		http(HttpMethod.DELETE, Context.getProp().getUrl().getBiz() + "/user/" + userId, null, null);
		this.reply.display = "用户(" + userId + ")已删除";
	}

	@Override
	public void parseParam(AshCommandParams params) {
		this.userId = params.get(1);
		this.password = params.get(2);
		this.email = params.get(3);
	}

	@Override
	public AshReply help() {
		this.reply.display = "user指平台用户。\n"
				+ "使用平台的大多数功能都需要先注册一个用户，使用mk user命令，按照提示步骤可以注册用户，"
				+ "已注册用户第一次打开平台需要使用login命令登录，login命令的格式是\"login [用户名] [密码]\"。"
				+ "登陆之后的用户可以上传数据，或者使用共享数据建立自己的深度学习模型，并对整个模型的生命周期进行管理。"
				+ "关于模型的介绍可以输入\"help model\"，登出请使用logout命令。";
		return this.reply;
	}

	@Override
	public String[] getMkSteps() {
		return new String[] {
				"请输入用户名：",
				"请输入密码：",
				"请输入email：",
		};
	}
}
