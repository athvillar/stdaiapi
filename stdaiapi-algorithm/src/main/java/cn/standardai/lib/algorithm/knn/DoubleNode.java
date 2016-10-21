/**
* DoubleNode.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.knn;

import java.util.List;


public class DoubleNode extends KNNNode<Double, Object> {

	public DoubleNode() {
		super();
	}

	public DoubleNode(List<Double> feature) {
		super(feature);
	}

	public DoubleNode(List<Double> feature, Object category) {
		super(feature, category);
	}

	/**
	 * 获得节点间几何距离
	 * @param feature
	 * 特征值/坐标
	 * @return
	 * 距离
	 */
	@Override
	public double getDistance(List<?> feature) {

		double result = 0;
		for (int i = 0; i < feature.size(); i++) {
			result += Math.sqrt((getFeature().get(i) - (Double)feature.get(i)) * (getFeature().get(i) - (Double)feature.get(i)));
		}
		return result;
	}
}
