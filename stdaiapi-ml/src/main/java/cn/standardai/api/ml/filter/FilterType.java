package cn.standardai.api.ml.filter;

import java.util.HashMap;
import java.util.Map;

public enum FilterType {

	integerDicFilter("IntegerDicFilter", IntegerDicFilter.class);

	Class<? extends DataFilter<?, ?>> cls;

	String clsName;

	private FilterType(String clsName, Class<? extends DataFilter<?, ?>> cls) {
		this.clsName = clsName;
		this.cls = cls;
	}

	private static final Map<String, Class<? extends DataFilter<?, ?>>> mappings = new HashMap<String, Class<? extends DataFilter<?, ?>>>();

	static {
		for (FilterType type : values()) {
			mappings.put(type.clsName, type.cls);
		}
	}

	public static Class<? extends DataFilter<?, ?>> resolve(String type) {
		return (type != null ? mappings.get(type) : null);
	}
}