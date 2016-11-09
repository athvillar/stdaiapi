/**
* NumberNode.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.kmeans;

import java.util.ArrayList;
import java.util.List;


public class NumberNode extends KMeansNode<Double, Object> {

	public NumberNode() {
		super();
	}

	public NumberNode(List<Double> feature) {
		super(feature);
	}

	/**
	 * 获得节点间距离
	 * @param targetNode
	 * 目标点
	 * @return
	 * 距离
	 */
	@SuppressWarnings("unchecked")
	@Override
	public double getDistance(DistanceMeasureMethod distanceMeasureMethod, KMeansNode<?, ?> targetNode, double param) {
		switch (distanceMeasureMethod) {
		case CITYBLOCK:
			return getCityBlockDistance((KMeansNode<Double, Object>)targetNode);
		case EUCLIDEAN:
			return getEuclideanDistance((KMeansNode<Double, Object>)targetNode);
		case MINKOWSKI:
			return getMinkowskiDistance((KMeansNode<Double, Object>)targetNode, param);
		case COSINE:
			return getCosineDistance((KMeansNode<Double, Object>)targetNode);
		case CHEBYCHEV:
			return getChebychevDistance((KMeansNode<Double, Object>)targetNode);
		default:
			return 0;
		}
	}

	/**
	 * 获得节点间绝对值距离（cityblock）
	 * @param targetNode
	 * 目标点
	 * @return
	 * 距离
	 */
	public double getCityBlockDistance(KMeansNode<Double, Object> targetNode) {
		return getMinkowskiDistance(targetNode, 1);
	}

	/**
	 * 获得节点间欧几里得距离（euclidean）
	 * @param targetNode
	 * 目标点
	 * @return
	 * 距离
	 */
	public double getEuclideanDistance(KMeansNode<Double, Object> targetNode) {
		return getMinkowskiDistance(targetNode, 2);
	}

	/**
	 * 获得节点间闵可夫斯基距离（minkowski）
	 * @param targetNode
	 * 目标点
	 * @param lamda
	 * lamda
	 * @return
	 * 距离
	 */
	public double getMinkowskiDistance(KMeansNode<Double, Object> targetNode, double lamda) {
		double distance = 0;
		for (int i = 0; i < this.getFeature().size(); i++) {
			distance += Math.pow(Math.abs(this.getFeature().get(i) - targetNode.getFeature().get(i)), lamda);
		}
		return Math.pow(distance, 1 / lamda);
	}

	/**
	 * 获得节点间余弦距离（cosine）
	 * @param targetNode
	 * 目标点
	 * @return
	 * 距离
	 */
	public double getCosineDistance(KMeansNode<Double, Object> targetNode) {
		double distance = 0;
		double a = 0;
		double b = 0;
		for (int i = 0; i < this.getFeature().size(); i++) {
			distance += this.getFeature().get(i) * targetNode.getFeature().get(i);
			a += this.getFeature().get(i) * this.getFeature().get(i);
			b += targetNode.getFeature().get(i) * targetNode.getFeature().get(i);
		}
		distance /= (Math.sqrt(a) * Math.sqrt(b));
		return distance;
	}

	/**
	 * 获得节点间契比雪夫距离（chebychev）
	 * @param targetNode
	 * 目标点
	 * @return
	 * 距离
	 */
	public double getChebychevDistance(KMeansNode<Double, Object> targetNode) {
		double distance = 0;
		double differ = 0;
		for (int i = 0; i < this.getFeature().size(); i++) {
			differ = Math.abs(this.getFeature().get(i) - this.getFeature().get(i));
			distance = differ > distance ? differ : distance;
		}
		return distance;
	}

	/**
	 * 移动至簇中心
	 * @param cluster
	 * 簇点列表
	 */
	@Override
	public void moveToCenter(List<KMeansNode<?, ?>> cluster) {

		Double feature;
		for (int i = 0; i < this.getFeature().size(); i++) {
			feature = 0.0;
			for (KMeansNode<?, ?> node : cluster) {
				feature += (Double)(node.getFeature().get(i));
			}
			feature /= cluster.size();
			this.getFeature().set(i, feature);
		}

		return;
	}

	/**
	 * 复制节点
	 * @return
	 * 复制后节点
	 */
	@Override
	public KMeansNode<?, ?> copy() {
		NumberNode newNode = new NumberNode();
		newNode.setFeature(new ArrayList<Double>());
		for (int i = 0; i < this.getFeature().size(); i++) {
			newNode.getFeature().add(i, Double.valueOf(this.getFeature().get(i)));
		}
		return newNode;
	}
}
