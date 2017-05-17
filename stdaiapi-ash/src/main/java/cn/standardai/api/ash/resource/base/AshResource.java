package cn.standardai.api.ash.resource.base;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.bean.AshCommandParams;
import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.command.AshLs;
import cn.standardai.api.ash.command.AshMk;
import cn.standardai.api.ash.command.AshRm;
import cn.standardai.api.ash.command.base.AshResourceCommand;
import cn.standardai.api.ash.command.base.AshCommand.HttpMethod;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.ash.resource.AshModel;
import cn.standardai.api.core.util.HttpUtil;

public abstract class AshResource {

	private String help;

	private String token;

	public AshReply reply;

	public enum Resource {

		model("model");

		String resource;

		private Resource(String resource) {
			this.resource = resource;
		}

		private static final Map<String, Resource> mappings = new HashMap<String, Resource>();

		static {
			for (Resource resource : values()) {
				mappings.put(resource.resource, resource);
			}
		}

		public static Resource resolve(String resource) {
			return (resource != null ? mappings.get(resource) : null);
		}
	}

	public AshResource() {
		this.reply = new AshReply();
	}

	public abstract void mk() throws AshException;

	public abstract void ls() throws AshException;

	public abstract void rm() throws AshException;

	public abstract void parseParam(AshCommandParams params);

	public AshReply help() {
		this.reply.display = this.help;
		return this.reply;
	}

	public static AshResource getInstance(String resourceString) {
		Resource resource = Resource.resolve(resourceString);
		return getInstance(resource);
	}

	public static AshResource getInstance(Resource resource) {
		if (resource == null) return null;
		switch (resource) {
		case model:
			return new AshModel();
		}
		return null;
	}

	public AshReply invoke(Class<? extends AshResourceCommand> cls, AshCommandParams params, String token) throws AshException {
		parseParam(params);
		this.token = token;
		if (cls == AshMk.class) {
			this.mk();
		} else if (cls == AshLs.class) {
			this.ls();
		} else if (cls == AshRm.class) {
			this.rm();
		}
		return this.reply;
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
