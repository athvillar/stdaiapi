package cn.standardai.api.distribute.noun;

import java.util.HashMap;
import java.util.Map;

public enum Operator {

	plus("plus");

	public String name;

	private Operator(String name) {
		this.name = name;
	}

	private static final Map<String, Operator> map = new HashMap<String, Operator>();

	static {
		for (Operator item : values()) {
			map.put(item.name, item);
		}
	}

	public static Operator resolve(String name) {
		return (name != null ? map.get(name) : null);
	}
}