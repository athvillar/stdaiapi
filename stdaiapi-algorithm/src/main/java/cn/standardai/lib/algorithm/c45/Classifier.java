/**
* Classifier.java
* Copyright 2015 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.c45;

import cn.standardai.lib.algorithm.common.StringUtil;

/**
 * 分类器
 * @author 韩晴
 *
 */
public class Classifier {

	// 类别枚举
	public static enum TYPE {DISCRETE, CONTINUOUS};

	// 连续值分类返回值
	public static enum CLASS {GT, LT, EQ};

	// 类别
	private TYPE type;

	// 分界点
	private double separator;

	/**
	 * constructor
	 */
	public Classifier(TYPE type) {
		this.type = type;
	}

	/**
	 * constructor
	 */
	public Classifier(TYPE type, double separator) {
		this.type = type;
		this.separator = separator;
	}

	/**
	 * 分类
	 * @return 分类结果
	 */
	public CLASS classify(String input) {
		switch (this.type) {
		case CONTINUOUS:
			if (Double.parseDouble(input) > separator) {
				return CLASS.GT;
			} else {
				return CLASS.LT;
			}
		case DISCRETE:
			return CLASS.EQ;
		default:
			return CLASS.EQ;
		}
	}

	public static TYPE char2CLASS(char c) {
		switch (c) {
		case 'D':
			return TYPE.DISCRETE;
		case 'C':
			return TYPE.CONTINUOUS;
		}
		return null;
	}

	public TYPE getType() {
		return type;
	}

	public double getSeparator() {
		return separator;
	}

	public void setSeparator(double separator) {
		this.separator = separator;
	}

	public String toString(int layer) {
		StringBuffer sb = new StringBuffer();
		sb.append("classifier.type:").append(type == null ? "null" : type.toString()).append("\n");
		sb.append(StringUtil.makeDuplicateString('|', layer));
		sb.append("classifier.separator:").append(separator).append("\n");
		return sb.toString();
	}
}
