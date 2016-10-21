/**
* Attribute.java
* Copyright 2015 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.c45;

import cn.standardai.lib.algorithm.common.StringUtil;

/**
 * 属性
 * @author 韩晴
 *
 */
public class Attribute {

	// 属性名
	private String name;

	// 分类器
	private Classifier classifier;

	/**
	 * constructor
	 */
	public Attribute(String name) {
		this.name = name;
	}

	/**
	 * constructor
	 */
	public Attribute(String name, Classifier classifier) {
		this.name = name;
		this.classifier = classifier;
	}

	/**
	 * constructor
	 */
	public Attribute(Attribute attribute) {
		this.name = attribute.getName();
		this.classifier = attribute.getClassifier();
	}

	public String toString(int layer) {
		StringBuffer sb = new StringBuffer();
		sb.append(".name:").append(name == null ? "null" : name.toString()).append("\n");
		sb.append(StringUtil.makeDuplicateString('|', layer));
		sb.append(classifier == null ? "classifier:null\n" : classifier.toString(layer));
		return sb.toString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Classifier getClassifier() {
		return classifier;
	}

	public void setClassifier(Classifier classifier) {
		this.classifier = classifier;
	}
}
