/**
* Node.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.node;

import java.util.List;

/**
 * Node
 * @author ����
 *
 */
public class Node {

	private List<Double> feature;

	private double category;

	public List<Double> getFeature() {
		return feature;
	}

	public void setFeature(List<Double> feature) {
		this.feature = feature;
	}

	public double getCategory() {
		return category;
	}

	public void setCategory(double category) {
		this.category = category;
	}
}
