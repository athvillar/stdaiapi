/**
* C45.java
* Copyright 2015 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.c45;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.standardai.lib.algorithm.c45.Classifier.CLASS;
import cn.standardai.lib.algorithm.c45.Classifier.TYPE;
import cn.standardai.lib.base.function.Statistic;

/**
 * C4.5算法主类
 * @author 韩晴
 *
 */
public class C45 {

	// 元数据
	private MetaData metaData;

	// 数据集
	private List<Map<String, String>> data;

	// 结束条件，叶子节点上最大数据个数
	private int maxLeaveDataNum;

	// 决策树根节点
	private C45Node root;

	/**
	 * constructor
	 */
	public C45(MetaData metaData, List<Map<String, String>> data, int maxLeaveDataNum) {
		this.metaData = metaData;
		this.data = data;
		this.maxLeaveDataNum = maxLeaveDataNum;
	}

	/**
	 * 训练
	 */
	public void run() {
		setRoot(classify(new MetaData(metaData), makeFullIndexes(), new C45Node()));
		return;
	}

	private double getIGRatio4ContinuousAttr(List<Integer> dataIndexes, final Attribute attribute, Attribute target) {

		// 排序 
		Collections.sort(dataIndexes, new Comparator<Integer>() {
			public int compare(Integer index1, Integer index2) {
				return Double.valueOf(data.get(index1).get(attribute.getName())).compareTo(Double.valueOf(data.get(index2).get(attribute.getName())));
			}
		});

		// 寻找切入点
		Map<Double, Double> igMap = new HashMap<Double, Double>();
		for (int i = 0; i < dataIndexes.size() - 1; i++) {
			double value1 = Double.valueOf(data.get(i).get(attribute.getName()));
			double value2 = Double.valueOf(data.get(i + 1).get(attribute.getName()));
			if (value1 == value2 || data.get(i).get(target.getName()).equals(data.get(i + 1).get(target.getName()))) {
				// 若连续两个值相等，或目标属性相同，跳过，不作为分割候选
				continue;
			}
			// 计算增益
			attribute.getClassifier().setSeparator((value1 + value2) / 2);
			igMap.put(attribute.getClassifier().getSeparator(), getIG(dataIndexes, attribute, target));
		}

		// 最大增益的点即是最佳分割点
		Double maxSplitPoint = Statistic.maxValueKey(igMap);
		attribute.getClassifier().setSeparator(maxSplitPoint);

		// 返回最大增益
		return igMap.get(maxSplitPoint) / getEntropy(dataIndexes, attribute);
	}

	/**
	 * 生成全数据索引
	 */
	private List<Integer> makeFullIndexes() {
		List<Integer> dataIndexes = new ArrayList<Integer>();
		for (int i = 0; i < data.size(); i++) {
			dataIndexes.add(i);
		}
		return dataIndexes;
	}

	/**
	 * 递归训练
	 * @param metaData
	 * 元数据
	 * @param dataIndexes
	 * 数据集索引
	 * @param currentNode
	 * 当前节点
	 * @return 新节点
	 */
	private C45Node classify(MetaData metaData, List<Integer> dataIndexes, C45Node currentNode) {

		// 判断是否符合结束条件
		String category = getCategory(metaData, dataIndexes);
		if (category != null) {
			// 成功分类，不再继续划分
			currentNode.setClassification(category);
			return currentNode;
		}

		// 计算每个属性的增益
		Map<Attribute, Double> igRatioMap = new HashMap<Attribute, Double>();
		for (Attribute attribute : metaData.getAttributes()) {
			// 获得该属性增益率（Information Gain Ratio）
			if (attribute.getClassifier().getType() == TYPE.CONTINUOUS) {
				igRatioMap.put(attribute, getIGRatio4ContinuousAttr(dataIndexes, attribute, metaData.getTarget()));
			} else {
				igRatioMap.put(attribute, getIGRatio(dataIndexes, attribute, metaData.getTarget()));
			}
		}

		// 最大增益TODO的属性
		Attribute maxIGAttribute = Statistic.maxValueKey(igRatioMap);
		// 设为节点属性
		currentNode.setAttribute(maxIGAttribute);
		// 将其从新属性列表中删除
		MetaData newMetaData = new MetaData(metaData);
		newMetaData.getAttributes().remove(maxIGAttribute);

		// 创建子数据集索引列表
		Map<Object, List<Integer>> subDataIndexesMap = splitData2Map(dataIndexes, maxIGAttribute);

		// 递归调用
		currentNode.setChildren(new HashMap<Object, C45Node>());
		for (Object subDataIndexesKey : subDataIndexesMap.keySet()) {
			C45Node childNode = new C45Node();
			childNode = classify(newMetaData, subDataIndexesMap.get(subDataIndexesKey), childNode);
			currentNode.getChildren().put(subDataIndexesKey, childNode);
			childNode.setParent(currentNode);
		}

		return currentNode;
	}

	/**
	 * 获得信息熵
	 * @param dataIndexes
	 * 数据集索引
	 * @param target
	 * 目标属性
	 * @return 信息熵
	 */
	private double getEntropy(List<Integer> dataIndexes, Attribute target) {

		Map<String, Integer> targetMap = new HashMap<String, Integer>();

		// 计算每种结果的个数
		for (Integer i : dataIndexes) {
			String targetValue = data.get(i).get(target.getName());
			count2Map(targetMap, targetValue);
		}

		// 计算熵
		double entropy = 0;
		int total = data.size();

		for (Integer num : targetMap.values()) {
			entropy -= (num.doubleValue() / total) * (Math.log(num.doubleValue() / total) / Math.log(2));
		}
		return entropy;
	}

	/**
	 * 获得增益TODO
	 * @param dataIndexes
	 * 数据集索引
	 * @param attribute
	 * 目标属性
	 * @param target
	 * 目标属性
	 * @return 增益
	 */
	private double getIGRatio(List<Integer> dataIndexes, Attribute attribute, Attribute target) {
		return getIG(dataIndexes, attribute, target) / getEntropy(dataIndexes, attribute);
	}

	/**
	 * 获得增益
	 * @param dataIndexes
	 * 数据集索引
	 * @param attribute
	 * 目标属性
	 * @param target
	 * 目标属性
	 * @return 增益
	 */
	private double getIG(List<Integer> dataIndexes, Attribute attribute, Attribute target) {

		double ig = getEntropy(dataIndexes, target);

		// 创建子数据集索引列表
		Map<Object, List<Integer>> subDataIndexesMap = splitData2Map(dataIndexes, attribute);

		// 取得Data个数
		int totalDataCount = 0;
		for (Object subDataIndexesKey : subDataIndexesMap.keySet()) {
			totalDataCount += subDataIndexesMap.get(subDataIndexesKey).size();
		}

		// 计算增益
		for (Object subDataIndexesKey : subDataIndexesMap.keySet()) {
			ig -= getEntropy(subDataIndexesMap.get(subDataIndexesKey), target) * subDataIndexesMap.get(subDataIndexesKey).size() / totalDataCount;
		}

		return ig;
	}

	/**
	 * 判断子集是否属于一个类别
	 * @param metaData
	 * 元数据
	 * @param dataIndexes
	 * 数据集索引
	 * @return
	 * 类别
	 */
	private String getCategory(MetaData metaData, List<Integer> dataIndexes) {

		String str = null;

		// 判断当前节点上数据数量
		if (dataIndexes.size() <= maxLeaveDataNum || metaData.getAttributes().size() == 0) {
			// 不足叶子节点的最大数据数量，不再继续划分
			Map<String, Integer> countMap = new HashMap<String, Integer>();
			for (Integer i : dataIndexes) {
				// 计算各个类别的个数
				str = data.get(i).get(metaData.getTarget().getName());
				count2Map(countMap, str);
			}
			// 获得类别个数最多的类别作为分类结果
			return (String)Statistic.maxValueKey(countMap);
		} else {
			// 超过叶子节点最大允许数据数量，判断各数据是否可以划分为同一类
			for (Integer i : dataIndexes) {
				if (str == null) {
					str = data.get(i).get(metaData.getTarget().getName());
					continue;
				} else if (str.equals(data.get(i).get(metaData.getTarget().getName()))) {
					// 没有不同类别，继续判断
					continue;
				} else {
					// 有不同类数据，需要继续划分
					return null;
				}
			}
			// 所有数据均为同一类，不再继续划分
			return str;
		}
	}

	/**
	 * 创建子数据集Map
	 * @param dataIndexes
	 * 数据集索引
	 * @param attribute
	 * 对象属性
	 * @return
	 * 类别
	 */
	private Map<Object, List<Integer>> splitData2Map(List<Integer> dataIndexes, Attribute attribute) {

		// 创建子数据集Map
		Map<Object, List<Integer>> subDataIndexesMap = new HashMap<Object, List<Integer>>();
		for (Integer i : dataIndexes) {
			// 取得每一条记录，并对其分类
			Object key = getClassification(data.get(i), attribute);
			append2Map(subDataIndexesMap, key, i);
		}

		return subDataIndexesMap;
	}

	/**
	 * 取一条记录分类
	 * @param data
	 * 一条训练数据
	 * @param attribute
	 * 属性
	 * @return
	 * 类别
	 */
	private Object getClassification(Map<String, String> data, Attribute attribute) {

		// 对一条记录分类
		String tmpValue = data.get(attribute.getName());
		CLASS classification = attribute.getClassifier().classify(tmpValue);
		switch (classification) {
		case GT:
		case LT:
			// 连续值
			return classification;
		case EQ:
		default:
			// 离散值
			return tmpValue;
		}
	}

	/**
	 * 将数据放入到对应的Map中
	 * @param <K>
	 * @param map
	 * 对象Map
	 * @param key
	 * 键
	 * @param value
	 * 值
	 * @return
	 * Map对象
	 */
	private <K> void append2Map(Map<K, List<Integer>> map, K key, Integer value) {

		// 根据分类，将数据索引放入到对应的Map中
		if (map.containsKey(key)) {
			// 若Map中已有数据，将本条data加入之后
			map.get(key).add(value);
		} else {
			// 若Map中该值对应数据为空，新建List存放本条data
			List<Integer> subDataIndexes = new ArrayList<Integer>();
			subDataIndexes.add(value);
			map.put(key, subDataIndexes);
		}
	}

	/**
	 * 更新Map中的个数
	 * @param <K>
	 * @param map
	 * 对象Map
	 * @param key
	 * 键
	 */
	private <K> void count2Map(Map<K, Integer> map, K key) {

		// 根据分类，更新对应的Map中的个数
		if (map.containsKey(key)) {
			// 若Map中已有数据，+1
			map.put(key, map.get(key) + 1);
		} else {
			// 若Map中该值对应数据为空，put 1
			map.put(key, 1);
		}
	}

	public C45Node getRoot() {
		return root;
	}

	public void setRoot(C45Node root) {
		this.root = root;
	}
}
