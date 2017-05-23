package cn.standardai.api.data.bean;

import java.util.HashMap;
import java.util.Map;

public enum SharePolicy {

	pPublic('1', "public"), pProtected('2', "protected"), pPrivate('3', "private");

	public Character key;

	public String value;

	private SharePolicy(Character key, String value) {
		this.key = key;
		this.value = value;
	}

	private static final Map<String, Character> mappings1 = new HashMap<String, Character>();

	private static final Map<Character, String> mappings2 = new HashMap<Character, String>();

	static {
		for (SharePolicy type : values()) {
			mappings1.put(type.value, type.key);
			mappings2.put(type.key, type.value);
		}
	}

	public static Character resolve(String key) {
		return (key != null ? mappings1.get(key) : null);
	}

	public static String parse(Character key) {
		if (key == null) return null;
		return (key != null ? mappings2.get(key) : null);
	}
};