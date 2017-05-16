package cn.standardai.api.ash.command;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.bean.Context;
import cn.standardai.api.core.util.DateUtil;
import cn.standardai.api.core.util.HttpUtil;

public class AshMsg extends AshCommand {

	public AshMsg(String token) {
		super(token);
	}

	@Override
	public String exec(String[] params) {

		boolean receive = false;
		boolean all = false;
		boolean delete = false;
		Integer number = null;
		String user = null;
		String content = "";
		for (int i = 1; i < params.length; i++) {
			switch (params[i]) {
			case "-a":
				all = true;
				break;
			case "-d":
				delete = true;
				break;
			case "-u":
				if (i >= params.length) return this.help("-u后应指定用户名");
				user = params[i + 1];
				break;
			default:
				if (params[i].startsWith("-")) {
					if (allNumbers(params[i].substring(1))) {
						number = Integer.parseInt(params[i].substring(1));
						break;
					}
				}
				if ("-u".equals(params[i - 1])) {
					user = params[i];
					break;
				}
				content += params[i];
				break;
			}
		}
		if ("".equals(content)) receive = true;

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("token", this.token);
		if (delete) {
			Map<String, String> queryParams = new HashMap<String, String>();
			queryParams.put("type", "receive");
			String s = HttpUtil.delete(Context.getProp().getUrl().getBiz() + "/messages", queryParams, headers);
			JSONObject j = JSONObject.parseObject(s);
			if (!"success".equals(j.getString("result"))) {
				return j.getString("message");
			} else {
				return "消息已被删除";
			}
		} else if (receive) {
			Map<String, String> queryParams = new HashMap<String, String>();
			queryParams.put("type", "receive");
			if (user != null) queryParams.put("userId", user);
			if (all) queryParams.put("all", "1");
			String s = HttpUtil.get(Context.getProp().getUrl().getBiz() + "/messages", queryParams, headers);
			JSONObject j = JSONObject.parseObject(s);
			if (!"success".equals(j.getString("result"))) {
				return j.getString("message");
			}
			JSONArray messages = j.getJSONArray("messages");
			if (messages == null) return "没有新消息";

			String result = "共有" + messages.size() + "条消息\n";
			result += "发件人\t\t时间\t\t\t\t\t\t内容";
			for (int i = 0; i < messages.size(); i++) {
				result += "\n" + messages.getJSONObject(i).getString("fromUserId") + "\t";
				result += DateUtil.format(messages.getJSONObject(i).getDate("createTime"), DateUtil.YYYY__MM__DD__HH__MM__SS) + "\t";
				result += messages.getJSONObject(i).getString("content");
			}
			return result;
		} else {
			JSONObject queryParams = new JSONObject();
			queryParams.put("content", content);
			if (user != null) {
				queryParams.put("userId", user);
			} else {
				if (!all) {
					queryParams.put("userId", "admin");
				}
			}
			String s = HttpUtil.post(Context.getProp().getUrl().getBiz() + "/messages", queryParams.toJSONString(), headers);
			JSONObject j = JSONObject.parseObject(s);
			if (!"success".equals(j.getString("result"))) {
				return j.getString("message") + "\n";
			}
			return "发送成功";
		}
	}

	@Override
	public String help() {
		return "msg(message)命令格式：msg [-a] [-d] [-n] [-u 用户] [内容]";
	}

	@Override
	public String man() {
		return "msg(message)命令用于接收或发送消息\n"
				+ "用法：\n"
				+ "\tmsg(message) [-a] [-d] [-n] [-u 用户] [内容]\n"
				+ "参数：\n"
				+ "\t-a: 在接收消息时，-a为显示所有信息，如没有此选项，默认显示未读消息\n"
				+ "\t在发送消息时，-a为向所有人发送广播，此时-u被忽略\n"
				+ "\t-d: 删除历史消息，消息将被全部删除，且忽略其它参数\n"
				+ "\t-n: n未显示消息的最大条数\n"
				+ "\t-u: 在接收消息时，-u为显示该用户的消息，默认显示所有用户消息\n"
				+ "\t在发送消息时，-u为给该用户发送消息，省略-u默认给系统管理员发送消息";
	}

	private boolean allNumbers(String s) {
		for (char c : s.toCharArray()) {
			if (c < '0' || c > '9') return false;
		}
		return true;
	}
}
