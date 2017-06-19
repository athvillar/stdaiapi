package cn.standardai.api.ml.agent;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.util.HttpUtil;
import cn.standardai.api.ml.exception.MLException;

public abstract class HttpHandler {

	public enum HttpMethod { GET, POST, PUT, DELETE }

	public String userId;

	public String token;

	public static JSONObject http(HttpMethod method, String url, Map<String, String> params, JSONObject body, String token) throws MLException {
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
			throw new MLException(result.getString("message"));
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

	protected String fillWithSpace(String s, int n) {
		StringBuilder sb = new StringBuilder();
		if (s != null) sb.append(s);
		for (int i = s == null ? 0 : s.length(); i < n; i++) {
			sb.append("  ");
		}
		return sb.toString();
	}
}
