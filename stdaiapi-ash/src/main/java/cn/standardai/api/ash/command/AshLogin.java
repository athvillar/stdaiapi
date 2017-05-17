package cn.standardai.api.ash.command;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.command.base.AshCommonCommand;
import cn.standardai.api.core.bean.Context;
import cn.standardai.api.core.util.HttpUtil;

public class AshLogin extends AshCommonCommand {

	protected final String help = "login命令格式：login [用户名] [密码]";

	protected final String man = "login命令用于用户登录\n"
				+ "用法：\n"
				+ "\tlogin [用户名] [密码]";

	@Override
	public void invoke() {

		JSONObject queryParams = new JSONObject();
		queryParams.put("userId", this.params.get(1));
		queryParams.put("password", this.params.get(2));
		String s = HttpUtil.post(Context.getProp().getUrl().getBiz() + "/token", queryParams.toJSONString(), null);
		JSONObject j = JSONObject.parseObject(s);
		if (!"success".equals(j.getString("result"))) {
			this.reply.message = j.getString("message");
		}
		this.reply.hidden = j.getString("token");
		this.reply.display = "登陆成功，欢迎光临！";
	}
}
