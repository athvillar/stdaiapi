package cn.standardai.api.ash.command;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.command.base.AshCommonCommand;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.core.bean.Context;
import cn.standardai.api.core.util.DateUtil;

public class AshMsg extends AshCommonCommand {

	protected final String help = "msg(message)命令格式：msg [-a] [-d] [-n] [-u 用户] [内容]";

	protected final String man = "msg(message)命令用于接收或发送消息\n"
				+ "用法：\n"
				+ "\tmsg(message) [-a] [-d] [-n] [-u 用户] [内容]\n"
				+ "参数：\n"
				+ "\t-a:\t在接收消息时，-a为显示所有信息，如没有此选项，默认显示未读消息\n"
				+ "\t\t在发送消息时，-a为向所有人发送广播，此时-u被忽略\n"
				+ "\t-d:\t删除历史消息，消息将被全部删除，且忽略其它参数\n"
				+ "\t-n:\tn为显示消息的最大条数\n"
				+ "\t-u [用户]:\t在接收消息时，-u为显示该用户的消息，默认显示所有用户消息\n"
				+ "\t\t在发送消息时，-u为给该用户发送消息，省略-u默认给系统管理员发送消息";

	@Override
	public void invoke() throws AshException {

		boolean receive = false;
		boolean all = params.has("a");
		boolean delete = params.has("d");
		Integer number = Integer.parseInt(params.get("n"));
		String user = params.get("u");
		String content = params.get(1);
		if ("".equals(content)) receive = true;

		if (delete) {
			Map<String, String> queryParams = new HashMap<String, String>();
			queryParams.put("type", "receive");
			http(HttpMethod.DELETE, Context.getProp().getUrl().getBiz() + "/messages", queryParams, null);
			this.reply.display = "消息已被删除";
			return;
		} else if (receive) {
			Map<String, String> queryParams = new HashMap<String, String>();
			queryParams.put("type", "receive");
			if (user != null) queryParams.put("userId", user);
			if (all) queryParams.put("all", "1");
			JSONObject j = http(HttpMethod.GET, Context.getProp().getUrl().getBiz() + "/messages", queryParams, null);

			JSONArray messages = j.getJSONArray("messages");
			if (messages == null || messages.size() == 0) {
				reply.display = "没有新消息";
				return;
			}

			String result = "共有" + messages.size() + "条消息\n";
			result += "发件人\t\t时间\t\t\t\t\t\t内容";
			for (int i = 0; i < messages.size(); i++) {
				result += "\n" + messages.getJSONObject(i).getString("fromUserId") + "\t";
				result += DateUtil.format(messages.getJSONObject(i).getDate("createTime"), DateUtil.YYYY__MM__DD__HH__MM__SS) + "\t";
				result += messages.getJSONObject(i).getString("content");
			}
			reply.display = result;
			return;
		} else {
			JSONObject body = new JSONObject();
			body.put("content", content);
			if (user != null) {
				body.put("userId", user);
			} else {
				if (!all) {
					body.put("userId", "admin");
				}
			}
			http(HttpMethod.POST, Context.getProp().getUrl().getBiz() + "/messages", null, body);
			reply.display = "发送成功";
			return;
		}
	}
}
