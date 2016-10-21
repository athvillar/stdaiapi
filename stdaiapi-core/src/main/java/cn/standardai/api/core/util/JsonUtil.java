package cn.standardai.api.core.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class JsonUtil {

	public static String beautiJson(String json) {

		StringBuilder sb = new StringBuilder();
		int indent = 0;
		int INDENT = 2;
		char newLine = '\n';
		for (int i = 0; i < json.length(); i++) {
			switch (json.charAt(i)) {
			case '{':
			case '[':
				sb.append(newLine).append(getSpace(indent));
				sb.append(json.charAt(i));
				indent += INDENT;
				sb.append(newLine).append(getSpace(indent));
				break;
			case '}':
			case ']':
				indent -= INDENT;
				if (i < json.length() - 1 && json.charAt(i + 1) == ',') {
					sb.append(json.charAt(i));
					sb.append(json.charAt(i + 1));
					sb.append(newLine).append(getSpace(indent));
					i++;
				} else {
					sb.append(newLine).append(getSpace(indent));
					sb.append(json.charAt(i));
					sb.append(newLine).append(getSpace(indent));
				}
				break;
			case '\"':
				if (i < json.length() - 1 && json.charAt(i + 1) == ',') {
					sb.append(json.charAt(i));
					sb.append(json.charAt(i + 1));
					sb.append(newLine).append(getSpace(indent));
					i++;
				} else {
					sb.append(json.charAt(i));
				}
				break;
			default:
				sb.append(json.charAt(i));
				break;
			}
		}

		return sb.toString();
	}

	public static Map<String, Object> toMap(JSONObject json) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (Entry<String, Object> entry : json.entrySet()) {
			if (entry.getKey().endsWith("DATE") || entry.getKey().endsWith("TIME")) {
				if (entry.getValue() == null || "".equals(entry.getValue())) continue;
				try {
					map.put(entry.getKey(), new Date(Long.parseLong(entry.getValue().toString())));
				} catch (NumberFormatException e) {
					map.put(entry.getKey(), entry.getValue());
				}
			} else {
				map.put(entry.getKey(), entry.getValue());
			}
		}
		return map;
	}

	public static <T> List<T> toList(JSONArray json, Class<T> t) {
		List<T> list = new ArrayList<T>();
		for (int i = 0; i < json.size(); i++) {
			T rec = json.getObject(i, t);
			list.add(rec);
		}
		return list;
	}

	public static List<Map<String, Object>> toMapList(JSONArray json) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < json.size(); i++) {
			Map<String, Object> map = toMap(json.getJSONObject(i));
			list.add(map);
		}
		return list;
	}

	private static String getSpace(int num){
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < num; i++) {
			sb.append(" ");
		}
		return sb.toString();
	}
}
