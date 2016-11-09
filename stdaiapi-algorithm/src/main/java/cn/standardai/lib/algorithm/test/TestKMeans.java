/**
* TestKMeans.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.test;

import java.util.ArrayList;
import java.util.List;

import cn.standardai.lib.algorithm.kmeans.KMeans;
import cn.standardai.lib.algorithm.kmeans.KMeansNode;
import cn.standardai.lib.algorithm.kmeans.NumberNode;

public class TestKMeans {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		sortDotsArea();
	}
	
	public static void sortDotsArea() {

		List<Double> feature = null;
		NumberNode node = null;
		ArrayList<KMeansNode<?, ?>> nodeList = new ArrayList<KMeansNode<?, ?>>();

		feature = new ArrayList<Double>();
		feature.add(2.0);
		feature.add(3.0);
		node = new NumberNode(feature);
		nodeList.add(node);

		feature = new ArrayList<Double>();
		feature.add(2.0);
		feature.add(4.0);
		node = new NumberNode(feature);
		nodeList.add(node);

		feature = new ArrayList<Double>();
		feature.add(2.0);
		feature.add(5.0);
		node = new NumberNode(feature);
		nodeList.add(node);

		feature = new ArrayList<Double>();
		feature.add(3.0);
		feature.add(3.0);
		node = new NumberNode(feature);
		nodeList.add(node);

		feature = new ArrayList<Double>();
		feature.add(3.0);
		feature.add(4.0);
		node = new NumberNode(feature);
		nodeList.add(node);

		feature = new ArrayList<Double>();
		feature.add(4.0);
		feature.add(3.0);
		node = new NumberNode(feature);
		nodeList.add(node);

		feature = new ArrayList<Double>();
		feature.add(4.0);
		feature.add(4.0);
		node = new NumberNode(feature);
		nodeList.add(node);

		feature = new ArrayList<Double>();
		feature.add(4.0);
		feature.add(5.0);
		node = new NumberNode(feature);
		nodeList.add(node);

		feature = new ArrayList<Double>();
		feature.add(3.0);
		feature.add(16.0);
		node = new NumberNode(feature);
		nodeList.add(node);

		feature = new ArrayList<Double>();
		feature.add(4.0);
		feature.add(17.0);
		node = new NumberNode(feature);
		nodeList.add(node);

		feature = new ArrayList<Double>();
		feature.add(3.0);
		feature.add(17.0);
		node = new NumberNode(feature);
		nodeList.add(node);

		feature = new ArrayList<Double>();
		feature.add(4.0);
		feature.add(16.0);
		node = new NumberNode(feature);
		nodeList.add(node);

		feature = new ArrayList<Double>();
		feature.add(5.0);
		feature.add(18.0);
		node = new NumberNode(feature);
		nodeList.add(node);

		feature = new ArrayList<Double>();
		feature.add(9.0);
		feature.add(7.0);
		node = new NumberNode(feature);
		nodeList.add(node);

		feature = new ArrayList<Double>();
		feature.add(9.0);
		feature.add(8.0);
		node = new NumberNode(feature);
		nodeList.add(node);

		feature = new ArrayList<Double>();
		feature.add(9.0);
		feature.add(9.0);
		node = new NumberNode(feature);
		nodeList.add(node);

		feature = new ArrayList<Double>();
		feature.add(9.0);
		feature.add(11.0);
		node = new NumberNode(feature);
		nodeList.add(node);

		//KMeans kmeans = new KMeans(nodeList, 3);
		KMeans kmeans = new KMeans(nodeList, 4);
		//KMeans kmeans = new KMeans(nodeList, 3, KMeans.InitMethod.KMEANSPLUS, KMeans.FinishCondition.MAX_MOVE, 5);
		kmeans.sort();

		for (KMeansNode<?,?> centroid : kmeans.getCentroids()) {
			System.out.println("cluster start:");
			for (KMeansNode<?,?> theNode : kmeans.getClusters().get(centroid)) {
				System.out.print("Point:");
				for (Object theFeature : theNode.getFeature()) {
					System.out.print(theFeature + " ");
				}
				System.out.println();
			}
			System.out.println("cluster end.");
			System.out.println();
		}
	}

	
	public static void sort() {

		List<Double> feature = null;
		NumberNode node = null;
		ArrayList<KMeansNode<?, ?>> nodeList = new ArrayList<KMeansNode<?, ?>>();

		feature = new ArrayList<Double>();
		feature.add(2.0);
		feature.add(3.0);
		node = new NumberNode(feature);
		nodeList.add(node);


		//KMeans kmeans = new KMeans(nodeList, 3);
		KMeans kmeans = new KMeans(nodeList, 3);
		//KMeans kmeans = new KMeans(nodeList, 3, KMeans.InitMethod.KMEANSPLUS, KMeans.FinishCondition.MAX_MOVE, 5);
		kmeans.sort();

		for (KMeansNode<?,?> centroid : kmeans.getCentroids()) {
			System.out.println("cluster start:");
			for (KMeansNode<?,?> theNode : kmeans.getClusters().get(centroid)) {
				System.out.print("Point:");
				for (Object theFeature : theNode.getFeature()) {
					System.out.print(theFeature + " ");
				}
				System.out.println();
			}
			System.out.println("cluster end.");
			System.out.println();
		}
	}
}
