package cn.standardai.api.core.bean;

public class Context {

	private static PropertyConfig prop;

	public static PropertyConfig getProp() {
		return prop;
	}

	public static void setProp(PropertyConfig prop) {
		Context.prop = prop;
	}
}
