package cn.standardai.api.ash.command;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.base.AshCommonCommand;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.core.bean.Context;
import cn.standardai.api.core.util.DateUtil;

public class AshMsg extends AshCommonCommand {

	public AshMsg() {
		setParamRules(new char[] { 'a', 'd' }, new String[] { "u" }, null, 0);
	}

	@Override
	public void invoke() throws AshException {

		boolean receive = false;
		boolean all = param.has('a');
		boolean delete = param.has('d');
		// TODO
		//Integer number = param.getInteger();
		String user = param.get("u");
		String content = param.get(1);
		if (content == null) receive = true;

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
			result += "发件人\t\t\t时间\t\t\t\t\t内容";
			for (int i = 0; i < messages.size(); i++) {
				result += "\n" + fillWithSpace(messages.getJSONObject(i).getString("fromUserId"), 15) + "\t";
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

	@Override
	public String help() {
		return "msg [-a] [-d] [-n] [-u 用户] [内容]";
	}

	@Override
	public String man() {
		return "msg(message)命令用于接收或发送消息\n"
				+ "语法\n"
				+ "\tmsg(message) [-a] [-d] [-n] [-u 用户] [内容]\n"
				+ "参数\n"
				+ "\t-a:\t在接收消息时，-a为显示所有信息，如没有此选项，默认显示未读消息\n"
				+ "\t\t在发送消息时，-a为向所有人发送广播，此时-u被忽略\n"
				+ "\t-d:\t删除历史消息，消息将被全部删除，且忽略其它参数\n"
				+ "\t-n:\tn为显示消息的最大条数\n"
				+ "\t-u [用户]:\t在接收消息时，-u为显示该用户的消息，默认显示所有用户消息\n"
				+ "\t\t在发送消息时，-u为给该用户发送消息，省略-u默认给系统管理员发送消息\n"
				+ "用例\n"
				+ "\tmsg，接受新消息\n"
				+ "\tmsg -a，查看历史消息\n"
				+ "\tmsg -u abc \"hello world\"，给abc用户发消息\n"
				+ "\tmsg something，给管理员发消息\n"
				+ "\tmsg -a something，广播"
				+ "\tmsg -d，删除消息\n";
	}

	@Override
	public void readParam() throws AshException {
		return;
	}
}
