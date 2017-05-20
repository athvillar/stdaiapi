package cn.standardai.api.ash.command.base;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.bean.AshCommandParams;
import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.command.AshHelp;
import cn.standardai.api.ash.command.AshHistory;
import cn.standardai.api.ash.command.AshLogin;
import cn.standardai.api.ash.command.AshLogout;
import cn.standardai.api.ash.command.AshLs;
import cn.standardai.api.ash.command.AshMan;
import cn.standardai.api.ash.command.AshMk;
import cn.standardai.api.ash.command.AshMsg;
import cn.standardai.api.ash.command.AshRm;
import cn.standardai.api.ash.command.AshVersion;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.ash.exception.HttpException;
import cn.standardai.api.core.util.HttpUtil;

public abstract class AshCommand {

	public enum Command {

		help("help"), history("history"), ls("ls"), man("man"), rm("rm"),
		msg("msg"), message("message"), login("login"), logout("logout"), mk("mk"), version("version");

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

	public enum HttpMethod { GET, POST, PUT, DELETE }

	public String userId;

	public String token;

	protected AshReply reply;

	public char[] fp = null;

	public char[] vp = null;

	public Integer pNumMax = null;

	public Integer pNumMin = null;

	protected AshCommandParams params;

	public AshCommand() {
		this.reply = new AshReply();
	}

	public AshReply exec(String[] params, String userId, String token) throws AshException {
		this.userId = userId;
		this.token = token;
		this.params = parseParam(params);
		invoke();
		return this.reply;
	}

	public abstract void invoke() throws AshException;

	public abstract AshReply help();

	public abstract AshReply man();

	public static AshCommand getInstance(String commandString) {

		Command command = AshCommand.Command.resolve(commandString);
		if (command == null) return null;
		switch (command) {
		case help:
			return new AshHelp();
		case history:
			return new AshHistory();
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
		case logout:
			return new AshLogout();
		case mk:
			return new AshMk();
		case version:
			return new AshVersion();
		}
		return null;
	}

	protected void setParamRules(char[] fp, char[] vp, Integer pNumMax, Integer pNumMin) {
		// -x
		this.fp = fp;
		// -x x
		this.vp = vp;
		// xx xx xx
		this.pNumMax = pNumMax;
		this.pNumMin = pNumMin;
	}

	private AshCommandParams parseParam(String[] paramStrings) throws AshException {
		return AshCommandParams.parse(paramStrings, fp, vp, pNumMax, pNumMin);
	}

	public JSONObject http(HttpMethod method, String url, Map<String, String> params, JSONObject body) throws HttpException {
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
			throw new HttpException(result.getString("message"));
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
