/**
* TestJData.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.standardai.lib.algorithm.rnn.lstm.Lstm;
import cn.standardai.lib.algorithm.rnn.lstm.LstmException;
import cn.standardai.lib.base.matrix.MatrixException;
import cn.standardai.lib.base.matrix.MatrixUtil;
import cn.standardai.tool.CsvParser;

/**
 * TestJData
 * @author 韩晴
 *
 */
public class TestJData {

	private static String basePath = "/Users/athvillar/Documents/JData/";

	private static String rawFile1 = "raw/JData_User.csv";

	private static String rawFile2 = "raw/JData_Product.csv";

	private static String rawFile3 = "raw/JData_Comment.csv";

	private static String rawFile4 = "raw/JData_Action_201602.csv";

	private static String rawFile5 = "raw/JData_Action_201603.csv";

	private static String rawFile6 = "raw/JData_Action_201604.csv";

	public static void main(String[] args)  {
		try {
			process();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void process() throws Exception {

		// 数据预处理
		Map<String, Object> data = preProcessData();

		String[] users = (String[])data.get("users");
		Map<String, Map<String[], Boolean>> resultMap = new HashMap<String, Map<String[], Boolean>>();
		//for (int i = 0; i < users.length; i++) {
		for (int i = 0; i < 2; i++) {
			// 对一个用户进行分析
			String user = users[i].toString();
			Map<String, Object> data41User = (Map<String, Object>)data.get(users[i].toString());
			resultMap.put(users[i], process1User(user, data41User));
		}

		print(resultMap);
	}

	private static Map<String, Object> preProcessData() throws MatrixException {

		// 获得用户列表
		Map<String, Object> data = new HashMap<String, Object>();
		String[] users = new String[] {"200001","200002"};
		//String[][] rawUsers = CsvParser.parse(new String[] {basePath + rawFile1} , null, null, null, new int[] {0}, false, false);
		//String[] users = MatrixUtil.transpose(rawUsers)[0];
		data.put("users", users);

		boolean fromRawFile = true;
		// 预处理用户数据
		for (int i = 0; i < users.length; i++) {
			// 对每个用户，预处理数据
			String user = users[i];
			String[][] rawData;
			if (fromRawFile) {
				// 读原始文件
				//String inFiles[] = { basePath + rawFile4, basePath + rawFile5, basePath + rawFile6 };
				String inFiles[] = { basePath + rawFile6 };
				int[] keyIndice = {0};
				String[] values = { user + ".0" };
				int[] includeIndice = {1,4};
				String outFile = basePath + "user_" + user + ".csv";
				rawData = CsvParser.parse(inFiles, outFile, keyIndice, values, includeIndice, false, false);
			} else {
				String inFile = basePath + "user_" + user + ".csv";
				rawData = CsvParser.parse(new String[] { inFile }, null, null, null, new int[] {0, 1}, true, false);
			}

			// 按商品归类
			Map<String, List<String>> eventMap = new HashMap<String, List<String>>();
			for (int j = 0; j < rawData.length; j++) {
				if (eventMap.containsKey(rawData[j][0])) {
					eventMap.get(rawData[j][0]).add(rawData[j][1]);
				} else {
					List<String> eventList = new ArrayList<String>();
					eventList.add(rawData[j][1]);
					eventMap.put(rawData[j][0], eventList);
				}
			}

			List<Double[]> xs = new ArrayList<Double[]>();
			List<Integer> ys = new ArrayList<Integer>();
			for (Entry<String, List<String>> entry : eventMap.entrySet()) {

				boolean buyed = false;
				boolean firstMove = true;
				for (String eventString : entry.getValue()) {
					Integer event = Integer.parseInt(eventString);
					if (event == 4) {
						buyed = true;
						break;
					} else {
						Double[] x1 = MatrixUtil.create(7, 0.0);
						x1[event] = 1.0;
						xs.add(x1);
						if (firstMove) {
							firstMove = false;
						} else {
							ys.add(event);
						}
					}
				}

				if (buyed) {
					ys.add(4);
				} else {
					ys.add(0);
				}
			}

			Map<String, Object> data41User = new HashMap<String, Object>();
			data41User.put("xs", xs.toArray(new Double[xs.size()][]));
			data41User.put("ys", ys.toArray(new Integer[ys.size()]));

			data.put(user, data41User);
		}

		return data;
	}

	private static Map<String[], Boolean> process1User(String user, Map<String, Object> data) throws Exception {

		// 训练
		Double[][] xs = (Double[][])data.get("xs");
		Integer[] ys = (Integer[])data.get("ys");
		Lstm lstm = new Lstm(30, 7, 7);
		lstm.setParam(1, 0.0001, 1, 1, 1, 10);
		lstm.train(xs, ys, 1000);
		System.out.println("Training finished!");

		// 预测
		predict(lstm, data);

		return null;
	}

	private static void predict(Lstm lstm, Map<String, Object> data) throws LstmException {
		double cRate = 0;
		List<List<Double[]>> testXs = (List<List<Double[]>>)data.get("testXs");
		List<List<Integer>> testYs = (List<List<Integer>>)data.get("testYs");
		for (int i = 0; i < testXs.size(); i++) {
			Integer[] result = lstm.predict(testXs.get(i).toArray(new Double[testXs.get(i).size() - 1][]), 1);
			if (result[0] == testYs.get(i).get(testYs.get(i).size() - 1)) {
				System.out.println("Correct! " + result[0]);
				cRate += 1.0;
			} else {
				System.out.println("Wrong! Output: " + result[0] + ", Expect: " + testYs.get(i).get(testYs.get(i).size() - 1));
			}
		}
		System.out.println("Correct rate: " + cRate / testXs.size() * 100 + "%");
	}

	private static void print(Map<String, Map<String[], Boolean>> resultMap) {
		// TODO Auto-generated method stub
		
	}
}
