/**
* StringUtil.java
* Copyright 2015 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.common;

public class StringUtil {

	public static String makeDuplicateString(char c, int num) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < num; i++) {
			sb.append(c);
		}
		return sb.toString();
	}
}
