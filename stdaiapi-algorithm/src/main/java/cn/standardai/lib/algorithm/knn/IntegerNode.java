/**
* IntegerNode.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.knn;

import java.util.List;


public class IntegerNode extends KNNNode<Integer, Object> {

	public IntegerNode() {
		super();
	}

	public IntegerNode(List<Integer> feature) {
		super(feature);
	}

	public IntegerNode(List<Integer> feature, Object category) {
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
			result += Math.sqrt((getFeature().get(i) - (Integer)feature.get(i)) * (getFeature().get(i) - (Integer)feature.get(i)));
		}
		return result;
	}
}
