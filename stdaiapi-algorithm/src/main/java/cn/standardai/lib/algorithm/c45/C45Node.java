/**
* C45Node.java
* Copyright 2015 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.c45;

import java.util.Map;
import java.util.Map.Entry;

import cn.standardai.lib.algorithm.common.StringUtil;

/**
 * C4.5节点
 * @author 韩晴
 *
 */
public class C45Node {

	// 父节点
	private C45Node parent;

	// 子节点
	private Map<Object, C45Node> children;

	// 属性
	private Attribute attribute;

	// 分类
	private String classification;

	/**
	 * constructor
	 */
	public C45Node() {
	}

	public String toString() {
		return toString(1);
	}

	public String toString(int layer) {
		StringBuffer sb = new StringBuffer();
		sb.append(StringUtil.makeDuplicateString('|', layer));
		sb.append("C45Node\n");
		sb.append(StringUtil.makeDuplicateString('|', layer));
		sb.append("attribute").append(attribute == null ? ":null\n" : attribute.toString(layer));
		sb.append(StringUtil.makeDuplicateString('|', layer));
		sb.append("classification:").append(classification == null ? "null" : classification.toString()).append("\n\n");
		if (children != null) {
			for (Entry<Object, C45Node> entry : children.entrySet()) {
				sb.append(entry.getKey() + ":");
				sb.append(entry.getValue().toString(layer + 1));
			}
		}

		return sb.toString();
	}

	public C45Node getParent() {
		return parent;
	}

	public void setParent(C45Node parent) {
		this.parent = parent;
	}

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	public Map<Object, C45Node> getChildren() {
		return children;
	}

	public void setChildren(Map<Object, C45Node> children) {
		this.children = children;
	}
}
