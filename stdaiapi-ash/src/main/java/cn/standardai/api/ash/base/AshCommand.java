package cn.standardai.api.ash.base;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.command.AshAsh;
import cn.standardai.api.ash.command.AshCall;
import cn.standardai.api.ash.command.AshCat;
import cn.standardai.api.ash.command.AshCd;
import cn.standardai.api.ash.command.AshCp;
import cn.standardai.api.ash.command.AshCurl;
import cn.standardai.api.ash.command.AshEcho;
import cn.standardai.api.ash.command.AshFind;
import cn.standardai.api.ash.command.AshHelp;
import cn.standardai.api.ash.command.AshHistory;
import cn.standardai.api.ash.command.AshLogin;
import cn.standardai.api.ash.command.AshLogout;
import cn.standardai.api.ash.command.AshLs;
import cn.standardai.api.ash.command.AshMan;
import cn.standardai.api.ash.command.AshMk;
import cn.standardai.api.ash.command.AshMsg;
import cn.standardai.api.ash.command.AshRm;
import cn.standardai.api.ash.command.AshSet;
import cn.standardai.api.ash.command.AshVersion;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.ash.exception.HttpException;
import cn.standardai.api.core.util.HttpUtil;

public abstract class AshCommand {

	public enum Command {

		ash("ash", AshAsh.class),
		call("call", AshCall.class),
		cat("cat", AshCat.class),
		cd("cd", AshCd.class),
		cp("cp", AshCp.class),
		curl("curl", AshCurl.class),
		echo("echo", AshEcho.class),
		find("find", AshFind.class),
		help("help", AshHelp.class),
		history("history", AshHistory.class),
		login("login", AshLogin.class),
		logout("logout", AshLogout.class),
		ls("ls", AshLs.class),
		man("man", AshMan.class),
		message("message", AshMsg.class),
		mk("mk", AshMk.class),
		msg("msg", AshMsg.class),
		set("set", AshSet.class),
		rm("rm", AshRm.class),
		version("version", AshVersion.class);

		String command;

		Class<? extends AshCommand> cls;

		private Command(String command, Class<? extends AshCommand> cls) {
			this.command = command;
			this.cls = cls;
		}

		private static final Map<String, Class<? extends AshCommand>> mappings = new HashMap<String, Class<? extends AshCommand>>();

		static {
			for (Command command : values()) {
				mappings.put(command.command, command.cls);
			}
		}

		public static Class<? extends AshCommand> resolve(String command) {
			return (command != null ? mappings.get(command) : null);
		}
	}

	public enum HttpMethod { GET, POST, PUT, DELETE }

	public String userId;

	public String token;

	public abstract String help();

	public abstract String man();

	public abstract Executable getExecutor() throws AshException;

	public static AshCommand getInstance(String commandString) throws AshException {
		Class<? extends AshCommand> cls = AshCommand.Command.resolve(commandString);
		if (cls == null) return null;
		try {
			return cls.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			throw new AshException("命令执行错误");
		}
	}

	public JSONObject http(HttpMethod method, String url, Map<String, String> params, JSONObject body) throws HttpException {
		return AshCommand.http(method, url, params, body, this.token);
	}

	public static JSONObject http(HttpMethod method, String url, Map<String, String> params, JSONObject body, String token) throws HttpException {
		Map<String, String> headers = null;
		if (token != null) {
			headers = new HashMap<String, String>();
			headers.put("token", token);
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

	public static JSONObject httpDelete(String url, Map<String, String> headers, Map<String, String> params) {
		String s = HttpUtil.delete(url, params, headers);
		return JSONObject.parseObject(s);
	}

	public static JSONObject httpPost(String url, Map<String, String> headers, JSONObject body) {
		String s = HttpUtil.post(url, body.toJSONString(), headers);
		return JSONObject.parseObject(s);
	}

	public static JSONObject httpPut(String url, Map<String, String> headers, Map<String, String> params, JSONObject body) {
		String s = HttpUtil.put(url, params, body.toJSONString(), headers);
		return JSONObject.parseObject(s);
	}

	public static JSONObject httpGet(String url, Map<String, String> headers, Map<String, String> params) {
		String s = HttpUtil.get(url, params, headers);
		return JSONObject.parseObject(s);
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
