/**
* KMeansNode.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.kmeans;

import java.util.HashMap;
import java.util.List;

/**
 * K平均算法（k-means）Node
 * @author 韩晴
 *
 */
public abstract class KMeansNode<F, C> {

	// 距离度量方法
	public static enum DistanceMeasureMethod {CITYBLOCK, EUCLIDEAN, MINKOWSKI, COSINE, CHEBYCHEV};

	private int index;

	// 特征值
	private List<F> feature;

	// 所属中心点
	private KMeansNode<?, ?> centroid;

	public KMeansNode() {
	}

	public KMeansNode(List<F> feature) {
		this.feature = feature;
	}

	public List<F> getFeature() {
		return feature;
	}

	public void setFeature(List<F> feature) {
		this.feature = feature;
	}

	public KMeansNode<?, ?> getCentroid() {
		return centroid;
	}

	public void setCentroid(KMeansNode<?, ?> kMeansNode) {
		this.centroid = kMeansNode;
	}

	public abstract double getDistance(DistanceMeasureMethod distanceMeasureMethod, KMeansNode<?, ?> targetNode, double param);

	/**
	 * 获得距离最近的一个点
	 * @param targetNodes
	 * 对象点列表
	 * @return
	 * 最近点
	 */
	public HashMap<String, Object> getNearestNode(DistanceMeasureMethod distanceMeasureMethod, List<KMeansNode<?, ?>> targetNodes, double param) {

		// 为最近邻类别计数，保存在counter中
		double minDistance = Double.MAX_VALUE;
		KMeansNode<?, ?> nearestNode = null;
		for (KMeansNode<?, ?> targetNode : targetNodes) {
			double tmp = this.getDistance(distanceMeasureMethod, targetNode, param);
			if (tmp < minDistance) {
				nearestNode = targetNode;
				minDistance = tmp;
			}
		}

		HashMap<String, Object> nearestNodeMap = new HashMap<String, Object>();
		nearestNodeMap.put("node", nearestNode);
		nearestNodeMap.put("distance", minDistance);
		return nearestNodeMap;
	}

	public boolean equals(KMeansNode<?, ?> targetNode) {
		if (this.feature.size() != targetNode.getFeature().size()) {
			return false;
		}
		for (int i = 0; i < this.feature.size(); i++) {
			if (!this.feature.get(i).equals(targetNode.getFeature().get(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 移动至簇中心
	 * @param cluster
	 * 簇点列表
	 */
	public abstract void moveToCenter(List<KMeansNode<?, ?>> cluster);

	/**
	 * 复制节点
	 * @return
	 * 复制后节点
	 */
	public abstract KMeansNode<?, ?> copy();

	/**
	 * 判断节点是否在节点列表中
	 * @param nodes
	 * 节点列表
	 * @return
	 * 判断结果
	 */
	public boolean isIn(List<KMeansNode<?, ?>> nodes) {
		for (int i = 0; i < nodes.size(); i++) {
			if (this.equals(nodes.get(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断节点是否在节点列表中有重复（2个以上）
	 * @param nodes
	 * 节点列表
	 * @return
	 * 判断结果
	 */
	public boolean isDuplicate(List<KMeansNode<?, ?>> nodes) {
		for (int i = 0; i < nodes.size(); i++) {
			if (this != nodes.get(i) && this.equals(nodes.get(i))) {
				return true;
			}
		}
		return false;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
