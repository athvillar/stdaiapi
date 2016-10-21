package cn.standardai.api.core.util;

public class DBUtil {

	public static String UUID12() {
		return MathUtil.random(12);
	}

	public static String UUID16() {
		return MathUtil.random(16);
	}
}
