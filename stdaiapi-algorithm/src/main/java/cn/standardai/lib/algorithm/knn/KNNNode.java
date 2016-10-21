/**
* KNNNode.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.knn;

import java.util.List;

/**
 * K最近邻分类算法Node
 * @author 韩晴
 *
 */
public abstract class KNNNode<F, C> {

	// 特征值
	private List<F> feature;

	// 节点分类
	private Object category;

	public KNNNode() {
	}

	public KNNNode(List<F> feature) {
		this.feature = feature;
	}

	public KNNNode(List<F> feature, Object category) {
		this.feature = feature;
		this.category = category;
	}

	public List<F> getFeature() {
		return feature;
	}

	public void setFeature(List<F> feature) {
		this.feature = feature;
	}

	public Object getCategory() {
		return category;
	}

	public void setCategory(Object category) {
		this.category = category;
	}

	public abstract double getDistance(List<?> feature);
}
