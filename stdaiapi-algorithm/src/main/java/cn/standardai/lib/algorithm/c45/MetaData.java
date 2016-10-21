/**
* Attribute.java
* Copyright 2015 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.c45;

import java.util.ArrayList;
import java.util.List;

/**
 * 属性
 * @author 韩晴
 *
 */
public class MetaData {

	// 属性
	private List<Attribute> attributes;

	// 目标属性
	private Attribute target;

	/**
	 * constructor
	 */
	public MetaData(String[] attributesName, String[] attributesProperty) {
		this.attributes = new ArrayList<Attribute>();
		for (int i = 0; i < attributesName.length; i++) {
			switch (attributesProperty[i].charAt(0)) {
			case 'X':
				this.target = new Attribute(attributesName[i]);
				break;
			default:
				this.attributes.add(new Attribute(attributesName[i], new Classifier(Classifier.char2CLASS(attributesProperty[i].charAt(0)))));
			}
		}
	}

	/**
	 * constructor
	 */
	public MetaData(MetaData metaData) {
		this.attributes = new ArrayList<Attribute>();
		this.attributes.addAll(metaData.getAttributes());
		this.target = new Attribute(metaData.getTarget());
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	public Attribute getTarget() {
		return target;
	}

	public void setTarget(Attribute target) {
		this.target = target;
	}
}
