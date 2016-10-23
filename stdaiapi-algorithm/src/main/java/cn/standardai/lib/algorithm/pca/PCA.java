/**
* PCA.java
* Copyright 2015 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.pca;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cn.standardai.lib.algorithm.kmeans.KMeansNode;

/**
 * K平均算法（k-means）主类
 * @author 韩晴
 *
 */
public class PCA {

	// 初始中心点生成策略
	private static enum InitMethod {RANDOM, KMEANSPLUS};

	// 结束条件
	private static enum FinishCondition {CENTROID_FIX, MAX_MOVE};

	// 数据点列表
	private List<KMeansNode<?, ?>> nodes;

	// 质心节点列表备份
	private List<KMeansNode<?, ?>> lastCentroids = new ArrayList<KMeansNode<?, ?>>();

	public List<KMeansNode<?, ?>> getNodes() {
		return nodes;
	}

	public void setNodes(List<KMeansNode<?, ?>> nodes) {
		this.nodes = nodes;
	}

	public List<KMeansNode<?, ?>> getCentroids() {
		return centroids;
	}

	public void setCentroids(List<KMeansNode<?, ?>> centroids) {
		this.centroids = centroids;
	}

	public Map<KMeansNode<?, ?>, List<KMeansNode<?, ?>>> getClusters() {
		return clusters;
	}

	public void setClusters(Map<KMeansNode<?, ?>, List<KMeansNode<?, ?>>> clusters) {
		this.clusters = clusters;
	}

	// 质心节点列表
	private List<KMeansNode<?, ?>> centroids = new ArrayList<KMeansNode<?, ?>>();

	// 簇列表
	private Map<KMeansNode<?, ?>, List<KMeansNode<?, ?>>> clusters = new HashMap<KMeansNode<?, ?>, List<KMeansNode<?, ?>>>();

	// K
	private int k;

	// 初始中心点生成策略
	private InitMethod initMethod = InitMethod.RANDOM;

	// 结束条件
	private FinishCondition finishCondition = FinishCondition.CENTROID_FIX;

	// 移动次数
	private int moveCnt = 0;

	// 最大移动次数
	private int maxMoveCnt;

	/**
	 * constructor
	 * @param nodes
	 * 数据点列表
	 * @param k
	 * K
	 */
	public PCA(List<KMeansNode<?, ?>> nodes, int k) {
		this.nodes = nodes;
		this.k = k;
	}

	/**
	 * constructor
	 * @param nodes
	 * 数据点列表
	 * @param k
	 * K
	 * @param initMethod
	 * 初始化中心点方法
	 * @param finishCondition
	 * 结束条件
	 * @param maxMoveCnt
	 * 最大尝试次数
	 */
	public PCA(List<KMeansNode<?, ?>> nodes, int k, InitMethod initMethod, FinishCondition finishCondition, Integer maxMoveCnt) {
		this.nodes = nodes;
		this.k = k;
		this.initMethod = initMethod;
		this.finishCondition = finishCondition;
		if (finishCondition == FinishCondition.MAX_MOVE) {
			this.maxMoveCnt = maxMoveCnt;
		}
	}

	/**
	 * 分类主方法
	 */
	public void sort() {
		// 初始化中心点
		init();
		do {
			// 调整中心点
			adjust();
		} while (!canFinish());
	}

	/**
	 * 判断结束条件
	 * @return
	 * 是否结束算法
	 */
	private boolean canFinish() {
		switch (finishCondition) {
		case CENTROID_FIX:
			// 对每一个中心点
			for (KMeansNode<?, ?> centroid : centroids) {
				// 该中心点发生了变化，算法不结束
				if (!centroid.isIn(lastCentroids)) {
					return false;
				}
			}
			return true;
		case MAX_MOVE:
			// 达到最大尝试次数，算法结束
			return (moveCnt >= maxMoveCnt);
		default:
			return false;
		}
	}

	/**
	 * 初始化中心点
	 * @param method
	 * 初始化方法
	 */
	private void init() {
		switch (initMethod) {
		case RANDOM:
			initRandom();
			break;
		case KMEANSPLUS:
			initKeansPlus();
			break;
		}
		
		for (KMeansNode<?, ?> centroid : centroids) {
			clusters.put(centroid, new ArrayList<KMeansNode<?, ?>>());
		}
	}

	/**
	 * 随机初始化中心点
	 */
	private void initRandom() {
		while (centroids.size() < k) {
			Random r = new Random();
			int index = r.nextInt(nodes.size());
			if (nodes.get(index).isIn(centroids)) {
				continue;
			} else {
				centroids.add(nodes.get(index).copy());
			}
		}
	}

	/**
	 * K-Means++方法初始化中心点
	 */
	private void initKeansPlus() {
		// 随机初始化第一个中心点
		Random r = new Random();
		int index = r.nextInt(nodes.size());
		centroids.add(nodes.get(index).copy());

		// 初始化其余点
		while (centroids.size() < k) {
			List<HashMap<String, Object>> nearestNodes = new ArrayList<HashMap<String, Object>>();
			double sumDistance = 0;
			for (KMeansNode<?, ?> node : nodes) {
				// 对每一个点，获取距离其最近的中心点
				HashMap<String, Object> nearestNodeMap = node.getNearestNode(centroids);
				sumDistance += (Double)nearestNodeMap.get("distance");
				nearestNodes.add(nearestNodeMap);
			}

			double targetDistance = r.nextDouble() * sumDistance;
			for (int j = 0; j < nodes.size(); j++) {
				if (((KMeansNode<?, ?>)nodes.get(j)).isIn(centroids)) {
					continue;
				} else {
					targetDistance -= (Double)nearestNodes.get(j).get("distance");
					if (targetDistance <= 0) {
						centroids.add(nodes.get(j).copy());
						break;
					}
				}
			}
		}
	}

	/**
	 * 调整中心点
	 */
	private void adjust() {
		moveCnt++;
		// 清空簇
		for (KMeansNode<?, ?> centroid : centroids) {
			clusters.get(centroid).clear();
		}
		// 清空备份中心点
		lastCentroids.clear();
		// 取得所有已知节点到新节点距离
		for (KMeansNode<?, ?> node : nodes) {
			// 对每一个点，获取距离其最近的中心点
			HashMap<String, Object> nearestNodeMap = node.getNearestNode(centroids);
			// 给该节点指定中心点
			node.setCentroid((KMeansNode<?, ?>)nearestNodeMap.get("node"));
			// 将该节点加入到最近中心点对应的簇中
			clusters.get((KMeansNode<?, ?>)nearestNodeMap.get("node")).add(node);
		}

		// 对每一个中心点
		for (KMeansNode<?, ?> centroid : centroids) {
			// 备份中心点
			lastCentroids.add(centroid.copy());
			// 将该中心点移动至簇中心
			centroid.moveToCenter(clusters.get(centroid));
		}

		// 对每一个中心点
		for (KMeansNode<?, ?> centroid : centroids) {
			// 删除重复中心点
			if (centroid.isDuplicate(centroids)) {
				centroids.remove(centroid);
				break;
			}
		}
	}
}
