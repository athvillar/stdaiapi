package cn.standardai.api.ash.command.base;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.bean.AshCommandParams;
import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.command.AshHelp;
import cn.standardai.api.ash.command.AshLogin;
import cn.standardai.api.ash.command.AshLs;
import cn.standardai.api.ash.command.AshMan;
import cn.standardai.api.ash.command.AshMk;
import cn.standardai.api.ash.command.AshMsg;
import cn.standardai.api.ash.command.AshRm;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.core.util.HttpUtil;

public abstract class AshCommand {

	public String token;

	private String help;

	private String man;

	public AshReply reply;

	protected AshCommandParams params;

	public enum HttpMethod { GET, POST, PUT, DELETE }

	public enum Command {

		help("help"), ls("ls"), man("man"), rm("rm"), msg("msg"), message("message"), login("login"), mk("mk");

		String command;

		private Command(String command) {
			this.command = command;
		}

		private static final Map<String, Command> mappings = new HashMap<String, Command>();

		static {
			for (Command command : values()) {
				mappings.put(command.command, command);
			}
		}

		public static Command resolve(String command) {
			return (command != null ? mappings.get(command) : null);
		}
	}

	public AshCommand() {
		this.reply = new AshReply();
	}

	public abstract void invoke() throws AshException;

	public AshReply exec(String ashString, String token) throws AshException {
		this.token = token;
		this.params = parseParam(ashString);
		invoke();
		return this.reply;
	}

	private AshCommandParams parseParam(String ashString) {
		// TODO Auto-generated method stub
		return null;
	}

	public AshReply help() {
		this.reply.display = this.help;
		return this.reply;
	}

	public AshReply man() {
		this.reply.display = this.man;
		return this.reply;
	}

	public AshReply help(String msg) {
		this.reply.display = msg + "\n" + this.help();
		return this.reply;
	}

	public static AshCommand getInstance(String commandString) {
		Command command = AshCommand.Command.resolve(commandString);
		if (command == null) return null;
		switch (command) {
		case help:
			return new AshHelp();
		case ls:
			return new AshLs();
		case man:
			return new AshMan();
		case msg:
			return new AshMsg();
		case message:
			return new AshMsg();
		case rm:
			return new AshRm();
		case login:
			return new AshLogin();
		case mk:
			return new AshMk();
		}
		return null;
	}

	public JSONObject http(HttpMethod method, String url, Map<String, String> params, JSONObject body) throws AshException {
		Map<String, String> headers = null;
		if (this.token != null) {
			headers = new HashMap<String, String>();
			headers.put("token", this.token);
		}
		JSONObject result = null;
		switch (method) {
		case GET:
			result = httpGet(url, headers, params);
			break;
		case POST:
			result = httpPost(url, headers, body);
			break;
		case PUT:
			result = httpPut(url, headers, params, body);
			break;
		case DELETE:
			result = httpDelete(url, headers, params);
			break;
		}
		if (!"success".equals(result.getString("result"))) {
			this.reply.display = "系统错误";
			this.reply.message = result.getString("message");
			throw new AshException("系统错误", result.getString("message"));
		}
		return result;
	}

	private JSONObject httpDelete(String url, Map<String, String> headers, Map<String, String> params) {
		String s = HttpUtil.delete(url, params, headers);
		return JSONObject.parseObject(s);
	}

	private JSONObject httpPost(String url, Map<String, String> headers, JSONObject body) {
		String s = HttpUtil.post(url, body.toJSONString(), headers);
		return JSONObject.parseObject(s);
	}

	private JSONObject httpPut(String url, Map<String, String> headers, Map<String, String> params, JSONObject body) {
		String s = HttpUtil.put(url, params, body.toJSONString(), headers);
		return JSONObject.parseObject(s);
	}

	private JSONObject httpGet(String url, Map<String, String> headers, Map<String, String> params) {
		String s = HttpUtil.get(url, params, headers);
		return JSONObject.parseObject(s);
	}
}
