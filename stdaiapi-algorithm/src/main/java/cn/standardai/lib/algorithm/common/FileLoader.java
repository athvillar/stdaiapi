/**
* FileLoader.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.common;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.standardai.lib.algorithm.c45.MetaData;
import cn.standardai.lib.algorithm.knn.IntegerNode;
import cn.standardai.lib.algorithm.knn.KNN;
import cn.standardai.lib.algorithm.knn.KNNNode;

public class FileLoader {

	public static void load() throws IOException {

		IntegerNode node = null;
		KNN knn = new KNN();
		ArrayList<KNNNode<?, ?>> nodeList = new ArrayList<KNNNode<?, ?>>();

		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\work\\standardai\\testdata\\knn.txt"), "UTF-8"));

		// 读取数据
		String line;
		List<ArrayList<Integer>> targetList = new ArrayList<ArrayList<Integer>>();
		while ((line = br.readLine()) != null) {
			String[] dataInfo = line.split(",");
			ArrayList<Integer> feature = new ArrayList<Integer>();
			for (int i = 0; i < dataInfo.length - 1; i++) {
				feature.add(Integer.parseInt(dataInfo[i]));
			}
			if ("?".equals(dataInfo[dataInfo.length - 1])) {
				targetList.add(feature);
				continue;
			}
			String category = dataInfo[dataInfo.length - 1];
			node = new IntegerNode(feature, category);
			nodeList.add(node);
		}

		knn.setNodes(nodeList);
		knn.setK(7);

		for (ArrayList<Integer> target : targetList) {
			node = new IntegerNode(target);
			node = (IntegerNode)knn.sort(node);
			for (int i = 0; i < node.getFeature().size(); i++) {
				System.out.print(node.getFeature().get(i) + ",");
			}
			System.out.println(": " + node.getCategory());
		}

		if (br != null) {
			br.close();
		}
	}
}
