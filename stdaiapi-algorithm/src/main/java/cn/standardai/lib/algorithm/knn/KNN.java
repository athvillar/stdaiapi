/**
* KNN.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.knn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * K最近邻分类算法主类
 * @author 韩晴
 *
 */
public class KNN {

	// 数据点列表
	private List<KNNNode<?, ?>> nodes;

	// K
	private int k = 1;

	// 是否将新点纳入知识库
	private boolean inputFlg = true;

	public List<KNNNode<?, ?>> getNodes() {
		return nodes;
	}

	public void setNodes(List<KNNNode<?, ?>> nodes) {
		this.nodes = nodes;
	}

	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

	/**
	 * constructor
	 */
	public KNN() {
		this.nodes = new ArrayList<KNNNode<?, ?>>();
	}

	/**
	 * constructor
	 * @param nodes
	 * 数据点列表
	 */
	public KNN(List<KNNNode<?, ?>> nodes) {
		this.nodes = nodes;
		this.k = (int)Math.floor(Math.sqrt(nodes.size()));
	}

	/**
	 * constructor
	 * @param nodes
	 * 数据点列表
	 * @param k
	 * K
	 */
	public KNN(List<KNNNode<?, ?>> nodes, int k) {
		this.nodes = nodes;
		this.k = k;
	}

	/**
	 * constructor
	 * @param nodes
	 * 数据点列表
	 * @param inputFlg
	 * 新点入库FLAG
	 */
	public KNN(List<KNNNode<?, ?>> nodes, boolean inputFlg) {
		this.nodes = nodes;
		this.inputFlg = inputFlg;
	}

	/**
	 * constructor
	 * @param nodes
	 * 数据点列表
	 * @param k
	 * K
	 * @param inputFlg
	 * 新点入库FLAG
	 */
	public KNN(List<KNNNode<?, ?>> nodes, int k, boolean inputFlg) {
		this.nodes = nodes;
		this.k = k;
		this.inputFlg = inputFlg;
	}

	/**
	 * 给新节点分类并加入到节点列表中
	 * @param newNode
	 * 新节点
	 * @return
	 * 分好类的新节点
	 */
	public KNNNode<?, ?> sort(KNNNode<?, ?> newNode) {
		// 给节点分类
		newNode.setCategory(findCategory(newNode.getFeature()));
		// 将新节点加入到已知节点列表中
		if (inputFlg) {
			this.nodes.add(newNode);
		}
		// 返回节点
		return newNode;
	}

	/**
	 * 根据特征值分类，并返回分类结果
	 * @param feature
	 * 特征值
	 * @return
	 * 分类结果
	 */
	public Object findCategory(List<?> feature) {
		// 取得所有已知节点到新节点距离
		List<Double> distanceList = new ArrayList<Double>();
		for (KNNNode<?, ?> node : nodes) {
			Double distance = node.getDistance(feature);
			distanceList.add(distance);
		}

		// 确定新节点分类
		List<KNNNode<?, ?>> fakeNodes = nodes;
		return makeDecision(getNearestNeighber(fakeNodes, distanceList, k));
	}

	/**
	 * 根据最近邻确定分类
	 * @param nearestNodes
	 * 最近邻
	 * @return
	 * 分类结果
	 */
	private Object makeDecision(List<KNNNode<?, ?>> nearestNodes) {
		Map<Object, Integer> counter = new HashMap<Object, Integer>();
		List<Object> categoryList = new ArrayList<Object>();

		// 为最近邻类别计数，保存在counter中
		for (KNNNode<?, ?> node : nearestNodes) {
			Integer cnt = counter.get(node.getCategory());
			if (cnt == null) {
				categoryList.add(node.getCategory());
				counter.put(node.getCategory(), 1);
			} else {
				counter.put(node.getCategory(), cnt + 1);
			}
		}

		// 取得数量最多的类别
		Object maxCategory = null;
		Integer maxCount = 0;
		for (Object category : categoryList) {
			if (counter.get(category) > maxCount) {
				maxCount = counter.get(category);
				maxCategory = category;
			}
		}

		return maxCategory;
	}

	/**
	 * 根据距离获得k个最近邻
	 * @param nodes
	 * 全部节点列表
	 * @param distanceList
	 * 距离列表
	 * @param k
	 * K
	 * @return
	 * k个最近邻
	 */
	private List<KNNNode<?, ?>> getNearestNeighber(List<KNNNode<?, ?>> nodes, List<Double> distanceList, int k) {
		int num = nodes.size() < k ? nodes.size() : k;
		for (int i = 0; i < num; i++) {
			for (int j = nodes.size() - 1; j > i; j--) {
				if (distanceList.get(j) < distanceList.get(j - 1)) {
					double tmp = distanceList.get(j);
					distanceList.set(j, distanceList.get(j));
					distanceList.set(j - 1, tmp);
					KNNNode<?, ?> tmpNodes = nodes.get(j);
					nodes.set(j, nodes.get(j));
					nodes.set(j - 1, tmpNodes);
				}
			}
		}

		return nodes.subList(0, num);
	}

	public boolean isInputFlg() {
		return inputFlg;
	}

	public void setInputFlg(boolean inputFlg) {
		this.inputFlg = inputFlg;
	}
}
