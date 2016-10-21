/**
* TestKNN.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.test;

import java.util.ArrayList;
import java.util.List;

import cn.standardai.lib.algorithm.knn.IntegerNode;
import cn.standardai.lib.algorithm.knn.KNN;
import cn.standardai.lib.algorithm.knn.KNNNode;

public class TestKNN {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		trainBigOrSamll();
		trainDotsArea();
	}
	
	public static void trainBigOrSamll () {

		List<Integer> feature = null;
		IntegerNode node = null;
		KNN knn = new KNN();
		ArrayList<KNNNode<?, ?>> nodeList = new ArrayList<KNNNode<?, ?>>();

		feature = new ArrayList<Integer>();
		feature.add(90);
		node = new IntegerNode(feature, "BIG");
		nodeList.add(node);

		feature = new ArrayList<Integer>();
		feature.add(20);
		node = new IntegerNode(feature, "SMALL");
		nodeList.add(node);

		feature = new ArrayList<Integer>();
		feature.add(16);
		node = new IntegerNode(feature, "SMALL");
		nodeList.add(node);

		feature = new ArrayList<Integer>();
		feature.add(100);
		node = new IntegerNode(feature, "BIG");
		nodeList.add(node);

		feature = new ArrayList<Integer>();
		feature.add(80);
		node = new IntegerNode(feature, "BIG");
		nodeList.add(node);

		knn.setNodes(nodeList);

		feature = new ArrayList<Integer>();
		feature.add(1);
		node = new IntegerNode(feature);
		node = (IntegerNode)knn.sort(node);

		System.out.println(node.getCategory());
	}

	public static void trainDotsArea() {

		List<Integer> feature = null;
		String category = null;
		IntegerNode node = null;
		KNN knn = new KNN();
		ArrayList<KNNNode<?, ?>> nodeList = new ArrayList<KNNNode<?, ?>>();
/*
		feature = new ArrayList<Integer>();
		feature.add(2);
		feature.add(3);
		category = "YELLOW";
		node = new IntegerNode(feature, category);
		nodeList.add(node);
*/
		feature = new ArrayList<Integer>();
		feature.add(2);
		feature.add(4);
		category = "YELLOW";
		node = new IntegerNode(feature, category);
		nodeList.add(node);

		feature = new ArrayList<Integer>();
		feature.add(2);
		feature.add(5);
		category = "YELLOW";
		node = new IntegerNode(feature, category);
		nodeList.add(node);
/*
		feature = new ArrayList<Integer>();
		feature.add(3);
		feature.add(3);
		category = "YELLOW";
		node = new IntegerNode(feature, category);
		nodeList.add(node);
*/
		feature = new ArrayList<Integer>();
		feature.add(3);
		feature.add(4);
		category = "YELLOW";
		node = new IntegerNode(feature, category);
		nodeList.add(node);

		feature = new ArrayList<Integer>();
		feature.add(4);
		feature.add(3);
		category = "YELLOW";
		node = new IntegerNode(feature, category);
		nodeList.add(node);

		feature = new ArrayList<Integer>();
		feature.add(4);
		feature.add(4);
		category = "YELLOW";
		node = new IntegerNode(feature, category);
		nodeList.add(node);

		feature = new ArrayList<Integer>();
		feature.add(4);
		feature.add(5);
		category = "YELLOW";
		node = new IntegerNode(feature, category);
		nodeList.add(node);

		feature = new ArrayList<Integer>();
		feature.add(3);
		feature.add(16);
		category = "RED";
		node = new IntegerNode(feature, category);
		nodeList.add(node);

		feature = new ArrayList<Integer>();
		feature.add(4);
		feature.add(17);
		category = "RED";
		node = new IntegerNode(feature, category);
		nodeList.add(node);

		feature = new ArrayList<Integer>();
		feature.add(3);
		feature.add(17);
		category = "RED";
		node = new IntegerNode(feature, category);
		nodeList.add(node);

		feature = new ArrayList<Integer>();
		feature.add(4);
		feature.add(16);
		category = "RED";
		node = new IntegerNode(feature, category);
		nodeList.add(node);

		feature = new ArrayList<Integer>();
		feature.add(5);
		feature.add(18);
		category = "RED";
		node = new IntegerNode(feature, category);
		nodeList.add(node);

		feature = new ArrayList<Integer>();
		feature.add(3);
		feature.add(15);
		category = "RED";
		node = new IntegerNode(feature, category);
		nodeList.add(node);

		feature = new ArrayList<Integer>();
		feature.add(4);
		feature.add(15);
		category = "RED";
		node = new IntegerNode(feature, category);
		nodeList.add(node);

		feature = new ArrayList<Integer>();
		feature.add(9);
		feature.add(7);
		category = "BLUE";
		node = new IntegerNode(feature, category);
		nodeList.add(node);

		feature = new ArrayList<Integer>();
		feature.add(9);
		feature.add(8);
		category = "BLUE";
		node = new IntegerNode(feature, category);
		nodeList.add(node);

		feature = new ArrayList<Integer>();
		feature.add(9);
		feature.add(9);
		category = "BLUE";
		node = new IntegerNode(feature, category);
		nodeList.add(node);

		feature = new ArrayList<Integer>();
		feature.add(9);
		feature.add(11);
		category = "BLUE";
		node = new IntegerNode(feature, category);
		nodeList.add(node);

		knn.setNodes(nodeList);

		feature = new ArrayList<Integer>();
		feature.add(4);
		feature.add(11);
		node = new IntegerNode(feature);
		node = (IntegerNode)knn.sort(node);

		System.out.println(node.getCategory());
	}

}
