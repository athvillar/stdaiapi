package cn.standardai.api.core.base;

import java.util.List;

import org.springframework.http.HttpHeaders;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class BaseService<T extends AuthAgent> {

	private boolean beautify = false;

	protected T agent;

	public enum ReturnType {SUCCESS, FAILURE, WARN};

	public String makeResponse(ReturnType type, String data) {
		String response;
		switch (type) {
		case SUCCESS:
			response = successResponse(data);
			break;
		case FAILURE:
			response = failureResponse(data).toJSONString();
			break;
		case WARN:
			response = warnResponse(data).toJSONString();
			break;
		default:
			response = successResponse(data);
			break;
		}
		if (beautify) {
			return beautifyJson(response);
		} else {
			return response;
		}
	}

	public JSONObject makeResponse(ReturnType type, JSONObject data, String message) {
		JSONObject response;
		switch (type) {
		case SUCCESS:
			response = successResponse(data);
			break;
		case FAILURE:
			response = failureResponse(message);
			break;
		case WARN:
			response = warnResponse(message);
			break;
		default:
			response = successResponse(data);
			break;
		}
		return response;
	}

	public JSONObject makeResponse(ReturnType type, String key, JSON value, String message) {
		JSONObject response;
		switch (type) {
		case SUCCESS:
			response = successResponse(key, value);
			break;
		case FAILURE:
			response = failureResponse(message);
			break;
		case WARN:
			response = warnResponse(message);
			break;
		default:
			response = successResponse(key, value);
			break;
		}
		return response;
	}

	public String successResponse() {
		return "{\"result\":\"success\"}";
	}

	private String successResponse(String data) {
		if (data == null) return successResponse();
		return "{\"result\":\"success\",\"data\":" + data + "}";
	}

	public JSONObject successResponse(JSONObject data) {
		if (data == null) data = new JSONObject();
		data.put("result", "success");
		return data;
	}

	private JSONObject successResponse(String key, JSON value) {
		JSONObject response = new JSONObject();
		response.put("result", "success");
		response.put(key, value);
		return response;
	}

	private JSONObject failureResponse(String message) {
		JSONObject response = new JSONObject();
		response.put("result", "failure");
		response.put("message", message);
		return response;
	}

	private JSONObject warnResponse(String message) {
		JSONObject response = new JSONObject();
		response.put("result", "warn");
		response.put("message", message);
		return response;
	}

	public String beautifyJson(String json) {
		int indence = 2;
		int indenceCnt = 0;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < json.length(); i++) {
			char c = json.charAt(i);
			switch (c) {
			case '[':
				sb.append('\n');
				sb.append(space(indence * indenceCnt));
				sb.append(c);
				indenceCnt++;
				break;
			case '{':
				sb.append('\n');
				sb.append(space(indence * indenceCnt));
				sb.append(c);
				indenceCnt++;
				sb.append('\n');
				sb.append(space(indence * indenceCnt));
				break;
			case ']':
				sb.append('\n');
				indenceCnt--;
				sb.append(space(indence * indenceCnt));
				sb.append(c);
				break;
			case '}':
				sb.append('\n');
				indenceCnt--;
				sb.append(space(indence * indenceCnt));
				sb.append(c);
				break;
			case ',':
				sb.append(c);
				if (json.charAt(i + 1) == '{') break;
				sb.append('\n');
				sb.append(space(indence * indenceCnt));
				break;
			case '\\':
				break;
			default:
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	private String space(int n) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < n; i++) {
			sb.append(' ');
		}
		return sb.toString();
	}

	public boolean isBeautify() {
		return beautify;
	}

	public void setBeautify(boolean beautify) {
		this.beautify = beautify;
	}

	public String getToken(HttpHeaders headers) {
		List<String> l = headers.get("token");
		if (l == null || l.size() == 0) return null;
		return l.get(0);
	}

	public void initAgent(HttpHeaders headers, Class<T> cls) throws InstantiationException, IllegalAccessException {
		this.agent = cls.newInstance();
		this.agent.setUserId(getToken(headers));
	}
}
