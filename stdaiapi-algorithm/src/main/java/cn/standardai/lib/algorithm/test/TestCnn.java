package cn.standardai.lib.algorithm.test;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.lib.algorithm.cnn.Cnn;
import cn.standardai.lib.algorithm.cnn.CnnData;
import cn.standardai.lib.algorithm.cnn.ConvLayer;
import cn.standardai.lib.algorithm.cnn.FCLayer;
import cn.standardai.lib.algorithm.cnn.Filter;
import cn.standardai.lib.algorithm.cnn.Layer;
import cn.standardai.lib.base.function.Statistic;
import cn.standardai.tool.Image2Data;

public class TestCnn {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			//test15Faces();
			test2Faces();
			//test44();
			//testChangeW();
			//testYale();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void test2Faces() throws Exception {

		String param = "" +
		"{" +
		"  \"layers\" : [" +
		"    {\"type\": \"INPUT\", \"width\": 100, \"height\": 100, \"depth\": 1 }," +
		"    {\"type\": \"POOL\", \"method\": \"max\", \"spatial\": 2, \"stride\": 2}," +
		"    {\"type\": \"POOL\", \"method\": \"max\", \"spatial\": 2, \"stride\": 2}," +
		"    {\"type\": \"CONV\", \"depth\": 4, \"stride\": 1, \"padding\":1, \"learningRate\": 0.2, \"aF\": \"sigmoid\"," +
		"      \"filter\": {\"width\":3, \"height\":3}" +
		"    }," +
		//"    {\"type\": \"RELU\", \"function\": \"max\"}," +
		//"    {\"type\": \"POOL\", \"method\": \"max\", \"spatial\": 2, \"stride\": 2}," +
		//"    {\"type\": \"CONV\", \"depth\": 16, \"stride\": 1, \"padding\":1, \"learningRate\": 0.000045, \"aF\": \"sigmoid\"," +
		//"      \"filter\": {\"width\":3, \"height\":3}" +
		//"    }," +
		//"    {\"type\": \"RELU\", \"function\": \"max\"}," +
		//"    {\"type\": \"POOL\", \"method\": \"max\", \"spatial\": 2, \"stride\": 2}," +
		//"    {\"type\": \"FC\", \"depth\": 4, \"learningRate\": 0.02, \"aF\": \"sigmoid\" }" +
		"    {\"type\": \"FC\", \"depth\": 2, \"learningRate\": 0.02, \"aF\": \"sigmoid\" }" +
		"  ]" +
		"}";

		// 创建网络
		Cnn cnn = Cnn.getInstance(JSONObject.parseObject(param));
		int[] trainingsetNums = new int[] {1, 2, 7, 12, 18, 9, 20, 21};
		CnnData[] cnnDatas = new CnnData[trainingsetNums.length];
		for (int i = 0; i < trainingsetNums.length; i++) {
			Integer[][][] data = Image2Data.getGray("/Users/athvillar/Documents/work/yale/s" + trainingsetNums[i] + ".bmp");
			Integer[] target = new Integer[trainingsetNums.length];
			for (int j = 0; j < target.length; j++) {
				if (j == (trainingsetNums[i] - 1) / 11) {
					target[j] = 1;
				} else {
					target[j] = 0;
				}
			}
			CnnData cnnData = new CnnData(data, target);
			cnnDatas[i] = cnnData;
			//cnn.addData(data, target);
		}
		cnn.mountData(cnnDatas);

		int maxTrainingCount = 300;
		int epoch = 20;
		//for (int count = 0; count < maxTrainingCount; count += batchCount) {
		for (int count = 0; count < maxTrainingCount; count++) {
			// 训练
			cnn.setBatchSize(null);
			cnn.setEpoch(epoch);
			cnn.setWatchEpoch(null);
			cnn.train();
			// 预测
			Double[] trainingCorrectRates = new Double[trainingsetNums.length];
			for (int index = 0; index < trainingsetNums.length; index++) {
				Double[][][] predict = cnn.predict(Image2Data.getGray("/Users/athvillar/Documents/work/yale/s" + trainingsetNums[index] + ".bmp"));
				// 输出预测
				Double max = Double.NEGATIVE_INFINITY;
				Integer maxIndex = -1;
				Double sum = 0.0;
				for (int i = 0; i < predict[0][0].length; i++) {
					sum += predict[0][0][i];
					if (predict[0][0][i] > max) {
						max = predict[0][0][i];
						maxIndex = i;
					}
					//System.out.print("|" + i + ":" + predict[0][0][i]);
				}
				trainingCorrectRates[index] = predict[0][0][(trainingsetNums[index] - 1)/ 11] / sum;
			}
			Integer[] testsetNums = new Integer[] {3, 14, 4, 15};
			Double[] testCorrectRates = new Double[testsetNums.length];
			for (int index = 0; index < testsetNums.length; index++) {
				Double[][][] predict = cnn.predict(Image2Data.getGray("/Users/athvillar/Documents/work/yale/s" + testsetNums[index] + ".bmp"));
				// 输出预测
				Double max = Double.NEGATIVE_INFINITY;
				Integer maxIndex = -1;
				Double sum = 0.0;
				for (int i = 0; i < predict[0][0].length; i++) {
					sum += predict[0][0][i];
					if (predict[0][0][i] > max) {
						max = predict[0][0][i];
						maxIndex = i;
					}
					//System.out.print("|" + i + ":" + predict[0][0][i]);
				}
				testCorrectRates[index] = predict[0][0][(testsetNums[index] - 1)/ 11] / sum;
				//System.out.println("Training count: " + (count + batchCount) +
				//		"\tExpect:" + ((trainingsetNums[index] - 1)/ 11) +
				//		"\tActual:" + maxIndex + "(" + max + ")");
			}
			;
			System.out.println("Training count: " + (count) +
					"\tTrCR:" + Statistic.avg(trainingCorrectRates) +
					"\tTsCR:" + Statistic.avg(testCorrectRates));
		}
		System.out.println("FINISH!");
	}

	public static void test15Faces() throws Exception {

		String param = "" +
		"{" +
		"  \"layers\" : [" +
		"    {\"type\": \"INPUT\", \"width\": 100, \"height\": 100, \"depth\": 1 }," +
		"    {\"type\": \"POOL\", \"method\": \"max\", \"spatial\": 2, \"stride\": 2}," +
		//"    {\"type\": \"POOL\", \"method\": \"max\", \"spatial\": 2, \"stride\": 2}," +
		"    {\"type\": \"CONV\", \"depth\": 8, \"stride\": 1, \"padding\":1, \"learningRate\": 0.3, \"aF\": \"sigmoid\"," +
		"      \"filter\": {\"width\":3, \"height\":3}" +
		"    }," +
		//"    {\"type\": \"RELU\", \"function\": \"max\"}," +
		//"    {\"type\": \"POOL\", \"method\": \"max\", \"spatial\": 2, \"stride\": 2}," +
		//"    {\"type\": \"CONV\", \"depth\": 16, \"stride\": 1, \"padding\":1, \"learningRate\": 0.000045, \"aF\": \"sigmoid\"," +
		//"      \"filter\": {\"width\":3, \"height\":3}" +
		//"    }," +
		//"    {\"type\": \"RELU\", \"function\": \"max\"}," +
		//"    {\"type\": \"POOL\", \"method\": \"max\", \"spatial\": 2, \"stride\": 2}," +
		"    {\"type\": \"FC\", \"depth\": 15, \"learningRate\": 0.08, \"aF\": \"sigmoid\" }" +
		"  ]" +
		"}";

		// 创建网络
		Cnn cnn = Cnn.getInstance(JSONObject.parseObject(param));
		int[] trainingTargets = new int[] {1, 3, 5};
		int[] trainingsetNums = new int[trainingTargets.length * 15];
		int[] testsetNums = new int[165 - trainingTargets.length * 15];
		int trCnt = 0;
		int tsCnt = 0;
		for (int i = 1; i <= 165; i++) {
			boolean isTrainingset = false;
			for (int j = 0; j < trainingTargets.length; j++) {
				if (i % 11 == trainingTargets[j]) {
					isTrainingset = true;
					trainingsetNums[trCnt] = i;
					trCnt++;
					break;
				}
			}
			if (!isTrainingset) {
				testsetNums[tsCnt] = i;
				tsCnt++;
			}
		}
		CnnData[] cnnDatas = new CnnData[trainingsetNums.length];
		for (int i = 0; i < trainingsetNums.length; i++) {
			Integer[][][] data = Image2Data.getGray("/Users/athvillar/Documents/work/yale/s" + trainingsetNums[i] + ".bmp");
			Integer[] target = new Integer[trainingsetNums.length];
			for (int j = 0; j < target.length; j++) {
				if (j == (trainingsetNums[i] - 1) / 11) {
					target[j] = 1;
				} else {
					target[j] = 0;
				}
			}
			CnnData cnnData = new CnnData(data, target);
			cnnDatas[i] = cnnData;
			//cnn.addData(data, target);
		}
		cnn.mountData(cnnDatas);

		int maxTrainingCount = 50;
		int batchCount = 10;
		for (int count = 0; count < maxTrainingCount; count++) {
		//for (int count = 0; count < maxTrainingCount; count += batchCount) {
			// 训练
			//cnn.train(cnn.dataCount(), batchCount);
			cnn.setEpoch(batchCount);
			cnn.setBatchSize(null);
			cnn.setWatchEpoch(null);
			cnn.train();
			// 预测
			Double[] trainingCorrectRates = new Double[trainingsetNums.length];
			for (int index = 0; index < trainingsetNums.length; index++) {
				Double[][][] predict = cnn.predict(Image2Data.getGray("/Users/athvillar/Documents/work/yale/s" + trainingsetNums[index] + ".bmp"));
				// 输出预测
				Double max = Double.NEGATIVE_INFINITY;
				Integer maxIndex = -1;
				Double sum = 0.0;
				for (int i = 0; i < predict[0][0].length; i++) {
					sum += predict[0][0][i];
					if (predict[0][0][i] > max) {
						max = predict[0][0][i];
						maxIndex = i;
					}
					//System.out.print("|" + i + ":" + predict[0][0][i]);
				}
				trainingCorrectRates[index] = predict[0][0][(trainingsetNums[index] - 1)/ 11] / sum;
			}
			Double[] testCorrectRates = new Double[testsetNums.length];
			for (int index = 0; index < testsetNums.length; index++) {
				Double[][][] predict = cnn.predict(Image2Data.getGray("/Users/athvillar/Documents/work/yale/s" + testsetNums[index] + ".bmp"));
				// 输出预测
				Double max = Double.NEGATIVE_INFINITY;
				Integer maxIndex = -1;
				Double sum = 0.0;
				for (int i = 0; i < predict[0][0].length; i++) {
					sum += predict[0][0][i];
					if (predict[0][0][i] > max) {
						max = predict[0][0][i];
						maxIndex = i;
					}
					//System.out.print("|" + i + ":" + predict[0][0][i]);
				}
				testCorrectRates[index] = predict[0][0][(testsetNums[index] - 1)/ 11] / sum;
				//System.out.println("Training count: " + (count + batchCount) +
				//		"\tExpect:" + ((trainingsetNums[index] - 1)/ 11) +
				//		"\tActual:" + maxIndex + "(" + max + ")");
			}
			;
			System.out.println("Training count: " + (count + batchCount) +
					"\tTrCR:" + Statistic.avg(trainingCorrectRates) +
					"\tTsCR:" + Statistic.avg(testCorrectRates));
		}
		System.out.println("FINISH!");
	}

	public static void test44() throws Exception {

		String param = "" +
		"{" +
		"  \"layers\" : [" +
		"    {\"type\": \"INPUT\", \"width\": 4, \"height\": 4, \"depth\": 2 }," +
		"    {\"type\": \"CONV\", \"depth\": 6, \"stride\": 1, \"padding\":1, \"learningRate\": 0.2, \"aF\": \"sigmoid\"," +
		"      \"filter\": {\"width\":3, \"height\":3}" +
		"    }," +
		//"    {\"type\": \"RELU\", \"function\": \"max\"}," +
		"    {\"type\": \"POOL\", \"method\": \"max\", \"spatial\": 2, \"stride\": 2}," +
		"    {\"type\": \"FC\", \"depth\": 2, \"learningRate\": 0.2, \"aF\": \"sigmoid\" }" +
		"  ]" +
		"}";

		String data1 = "" +
		"{" +
		"  \"data\": [" +
		"    [" +
		"      [1,1,2,2]," +
		"      [1,1,2,2]," +
		"      [1,1,2,2]," +
		"      [1,1,2,2]" +
		"    ]," +
		"    [" +
		"      [2,2,3,3]," +
		"      [2,2,3,3]," +
		"      [2,2,3,3]," +
		"      [2,2,3,3]" +
		"    ]" +
		"  ]," +
		"  \"target\": [1,0]" +
		"}";
		String data2 = "" +
		"{" +
		"  \"data\": [" +
		"    [" +
		"      [32,3,3,2]," +
		"      [32,3,3,2]," +
		"      [32,3,3,3]," +
		"      [32,3,3,3]" +
		"    ]," +
		"    [" +
		"      [22,2,1,1]," +
		"      [22,2,1,1]," +
		"      [22,2,2,3]," +
		"      [22,2,3,2]" +
		"    ]" +
		"  ]," +
		"  \"target\": [0,1]" +
		"}";
		String data3 = "" +
		"{" +
		"  \"data\": [" +
		"    [" +
		"      [32,3,3,2]," +
		"      [34,3,3,1]," +
		"      [32,5,3,3]," +
		"      [32,3,3,1]" +
		"    ]," +
		"    [" +
		"      [22,2,1,1]," +
		"      [18,2,1,2]," +
		"      [22,3,2,3]," +
		"      [22,2,4,2]" +
		"    ]" +
		"  ]," +
		"  \"target\": [0,1]" +
		"}";

		// 创建网络
		Cnn cnn = Cnn.getInstance(JSONObject.parseObject(param));
		cnn.addData(JSONObject.parseObject(data1));
		cnn.addData(JSONObject.parseObject(data2));
		// 训练
		int maxTrainingCount = 1000;
		int batchCount = 50;
		for (int count = 0; count < maxTrainingCount; count += batchCount) {

			double correctRate = 0;
			double testCorrectRate = 0;
			//cnn.train(cnn.dataCount(), batchCount);
			// 预测
			Double[][][] predict1 = cnn.predict(JSONObject.parseObject(data1));
			// 输出预测
			Double max = Double.NEGATIVE_INFINITY;
			Integer maxIndex = -1;
			for (int i = 0; i < predict1[0][0].length; i++) {
				if (predict1[0][0][i] > max) {
					max = predict1[0][0][i];
					maxIndex = i;
				}
				//System.out.print("|" + i + ":" + predict1[0][0][i]);
			}
			//System.out.println("" + maxIndex + "(" + max + ")\n");
			correctRate = predict1[0][0][0] / (predict1[0][0][0] + predict1[0][0][1]);
			//System.out.println(correctRate);

			Double[][][] predict2 = cnn.predict(JSONObject.parseObject(data2));
			max = Double.NEGATIVE_INFINITY;
			maxIndex = -1;
			for (int i = 0; i < predict2[0][0].length; i++) {
				if (predict2[0][0][i] > max) {
					max = predict2[0][0][i];
					maxIndex = i;
				}
				//System.out.print("|" + i + ":" + predict2[0][0][i]);
			}
			//System.out.println("" + maxIndex + "(" + max + ")\n");
			correctRate = (correctRate + predict2[0][0][1] / (predict2[0][0][0] + predict2[0][0][1])) / 2;
			System.out.print("Current num: " + (count + batchCount) + ", currect rate:" + correctRate);

			Double[][][] predict3 = cnn.predict(JSONObject.parseObject(data3));
			max = Double.NEGATIVE_INFINITY;
			maxIndex = -1;
			for (int i = 0; i < predict3[0][0].length; i++) {
				if (predict3[0][0][i] > max) {
					max = predict3[0][0][i];
					maxIndex = i;
				}
				//System.out.print("|" + i + ":" + predict2[0][0][i]);
			}
			//System.out.println("" + maxIndex + "(" + max + ")\n");
			testCorrectRate = predict3[0][0][1] / (predict3[0][0][0] + predict3[0][0][1]);
			System.out.print(", test currect rate:" + testCorrectRate);
			System.out.println(", final rate:" + (correctRate - testCorrectRate) / correctRate);
		}

		System.out.println("FINISH!");
	}

	public static void testChangeW() throws Exception {

		String param = "" +
		"{" +
		"  \"layers\" : [" +
		"    {\"type\": \"INPUT\", \"width\": 4, \"height\": 4, \"depth\": 2 }," +
		"    {\"type\": \"CONV\", \"depth\": 6, \"stride\": 1, \"padding\":1, \"learningRate\": 0.03, \"aF\": \"sigmoid\"," +
		"      \"filter\": {\"width\":3, \"height\":3}" +
		"    }," +
		"    {\"type\": \"RELU\", \"function\": \"max\"}," +
		"    {\"type\": \"POOL\", \"method\": \"max\", \"spatial\": 2, \"stride\": 2}," +
		"    {\"type\": \"FC\", \"depth\": 2, \"learningRate\": 0.03, \"aF\": \"sigmoid\" }" +
		"  ]" +
		"}";

		String data1 = "" +
		"{" +
		"  \"data\": [" +
		"    [" +
		"      [1,1,2,2]," +
		"      [1,1,2,2]," +
		"      [1,1,2,2]," +
		"      [1,1,2,2]" +
		"    ]," +
		"    [" +
		"      [2,2,3,3]," +
		"      [2,2,3,3]," +
		"      [2,2,3,3]," +
		"      [2,2,3,3]" +
		"    ]" +
		"  ]," +
		"  \"target\": [1,0]" +
		"}";
		String data2 = "" +
		"{" +
		"  \"data\": [" +
		"    [" +
		"      [32,3,3,2]," +
		"      [32,3,3,2]," +
		"      [32,3,3,3]," +
		"      [32,3,3,3]" +
		"    ]," +
		"    [" +
		"      [22,2,1,1]," +
		"      [22,2,1,1]," +
		"      [22,2,2,3]," +
		"      [22,2,3,2]" +
		"    ]" +
		"  ]," +
		"  \"target\": [0,1]" +
		"}";

		// 创建网络
		Cnn cnn = Cnn.getInstance(JSONObject.parseObject(param));
		cnn.addData(JSONObject.parseObject(data1));
		//cnn.addData(JSONObject.parseObject(data2));
		// 训练
		//cnn.train(10, 100);
		cnn.forward();
		cnn.backward();

		// 预测
		Double[][][] temp = cnn.predict(JSONObject.parseObject(data1)).clone();
		Double[][][] predict0 = new Double[temp.length][temp[0].length][temp[0][0].length];
		Double[][][] predict1 = new Double[temp.length][temp[0].length][temp[0][0].length];
		Double[][][] predict2 = new Double[temp.length][temp[0].length][temp[0][0].length];
		for (int i = 0; i < temp.length; i++) {
			for (int j = 0; j < temp[i].length; j++) {
				for (int k = 0; k < temp[i][j].length; k++) {
					predict0[i][j][k] = temp[i][j][k];
				}
			}
		}
		// 改变某个权重
		Double e = 0.00000001;
		Integer testI = 0;
		Integer testJ = 0;
		Integer testK = 0;
		ConvLayer thisLayer = (ConvLayer)cnn.layers.get(cnn.layers.size() - 1);
		Layer prevLayer = cnn.layers.get(cnn.layers.size() - 2);
		Double oldW = thisLayer.filters.get(0).w[testI][testJ][testK];
		thisLayer.filters.get(0).w[testI][testJ][testK] = oldW + e;
		temp = cnn.predict(JSONObject.parseObject(data1)).clone();
		for (int i = 0; i < temp.length; i++) {
			for (int j = 0; j < temp[i].length; j++) {
				for (int k = 0; k < temp[i][j].length; k++) {
					predict1[i][j][k] = temp[i][j][k];
				}
			}
		}
		thisLayer.filters.get(0).w[testI][testJ][testK] = oldW - e;
		temp = cnn.predict(JSONObject.parseObject(data1)).clone();
		for (int i = 0; i < temp.length; i++) {
			for (int j = 0; j < temp[i].length; j++) {
				for (int k = 0; k < temp[i][j].length; k++) {
					predict2[i][j][k] = temp[i][j][k];
				}
			}
		}
		for (int i = 0; i < thisLayer.width; i++) {
			for (int j = 0; j < thisLayer.height; j++) {
				for (int k = 0; k < thisLayer.depth; k++) {
					Double sum = 0.0;
					for (int i2 = 0; i2 < thisLayer.kernelWidth; i2++) {
						for (int j2 = 0; j2 < thisLayer.kernelHeight; j2++) {
							for (int k2 = 0; k2 < thisLayer.filters.get(k).depth; k2++) {
								if (i * thisLayer.stride + i2 < thisLayer.padding) {}
								else if (i * thisLayer.stride + i2 >= thisLayer.padding + prevLayer.width) {}
								else if (j * thisLayer.stride + j2 < thisLayer.padding) {}
								else if (j * thisLayer.stride + j2 >= thisLayer.padding + prevLayer.height) {}
								else {
									if (i2 == testI && j2 == testJ && k2 == testK && k == 0) {
										//System.out.println("i,j,k:[" + i + "][" + j + "][" + k + "]\ti2,j2,k2:[" + i2 + "][" + j2 + "][" + k2 + "]");
										sum += thisLayer.error[i][j][k] * prevLayer.data[i * thisLayer.stride + i2 - thisLayer.padding][j * thisLayer.stride + j2 - thisLayer.padding][k2];
									}
								}
							}
						}
					}
					System.out.println("\npredict1:" + predict1[i][j][k]);
					System.out.println("predict2:" + predict2[i][j][k]);
					System.out.println("dc/dw=" + ((predict2[i][j][k] - predict1[i][j][k]) / e / 2) + ",\ta*error=" + (sum));
				}
			}
		}

		System.out.println("FINISH!");
	}

	public static void test2() throws Exception {

		String param = "" +
		"{" +
		"  \"maxTrainingCount\" : 10," +
		"  \"layers\" : [" +
		"    {\"type\": \"INPUT\", \"width\": 120, \"height\": 120, \"depth\": 3 }," +
		"    {\"type\": \"CONV\", \"depth\": 4, \"stride\": 1, \"padding\":1, \"learningRate\": 0.05," +
		"      \"filter\": {\"width\":3, \"height\":3}" +
		"    }," +
		//"    {\"type\": \"RELU\", \"function\": \"max\"}," +
		//"    {\"type\": \"POOL\", \"method\": \"avg\", \"spatial\": 2, \"stride\": 2}," +
		//"    {\"type\": \"CONV\", \"depth\": 8, \"stride\": 1, \"padding\":1, \"learningRate\": 0.05," +
		//"      \"filter\": {\"width\":3, \"height\":3}" +
		//"    }," +
		//"    {\"type\": \"RELU\", \"function\": \"max\"}," +
		//"    {\"type\": \"POOL\", \"method\": \"max\", \"spatial\": 2, \"stride\": 2}," +
		"    {\"type\": \"FC\", \"depth\": 2, \"learningRate\": 0.01 }" +
		"  ]" +
		"}";

		// 创建网络
		Cnn cnn = Cnn.getInstance(JSONObject.parseObject(param));
		cnn.loadData(Image2Data.getRGB("/Users/athvillar/Downloads/q1.jpg"), new Integer[] {0, 1});
		cnn.loadData(Image2Data.getRGB("/Users/athvillar/Downloads/q2.png"), new Integer[] {0, 1});
		cnn.loadData(Image2Data.getRGB("/Users/athvillar/Downloads/s1.jpg"), new Integer[] {1, 0});
		cnn.loadData(Image2Data.getRGB("/Users/athvillar/Downloads/s2.jpg"), new Integer[] {1, 0});

		// 训练
		//cnn.train(10, 100);

		// 预测
		Double[][][] predict = cnn.predict(Image2Data.getRGB("/Users/athvillar/Downloads/q1.jpg"));
		// 输出预测
		for (int i = 0; i < predict[0][0].length; i++) {
			System.out.print("|" + i + ":" + predict[0][0][i]);
		}
		System.out.println("--" + (predict[0][0][0] > predict[0][0][1] ? "shoe" : "ball"));
		// 预测
		predict = cnn.predict(Image2Data.getRGB("/Users/athvillar/Downloads/s1.jpg"));
		// 输出预测
		for (int i = 0; i < predict[0][0].length; i++) {
			System.out.print("|" + i + ":" + predict[0][0][i]);
		}
		System.out.println("--" + (predict[0][0][0] > predict[0][0][1] ? "shoe" : "ball"));

		System.out.println("FINISH!");
	}

	public static void test3() throws Exception {

		String param = "" +
		"{" +
		"  \"layers\" : [" +
		"    {\"type\": \"INPUT\", \"width\": 120, \"height\": 120, \"depth\": 3 }," +
		"    {\"type\": \"CONV\", \"depth\": 16, \"stride\": 1, \"padding\":1, \"learningRate\": 0.05," +
		"      \"filter\": {\"width\":3, \"height\":3}" +
		"    }," +
		"    {\"type\": \"RELU\", \"function\": \"max\"}," +
		"    {\"type\": \"POOL\", \"method\": \"avg\", \"spatial\": 2, \"stride\": 2}," +
		"    {\"type\": \"CONV\", \"depth\": 8, \"stride\": 1, \"padding\":1, \"learningRate\": 0.05," +
		"      \"filter\": {\"width\":3, \"height\":3}" +
		"    }," +
		"    {\"type\": \"RELU\", \"function\": \"max\"}," +
		"    {\"type\": \"POOL\", \"method\": \"max\", \"spatial\": 2, \"stride\": 2}," +
		"    {\"type\": \"FC\", \"depth\": 4, \"learningRate\": 0.01 }" +
		"  ]" +
		"}";

		// 创建网络
		Cnn cnn = Cnn.getInstance(JSONObject.parseObject(param));
		cnn.loadData(Image2Data.getRGB("/Users/athvillar/Downloads/q1.jpg"), new Integer[] {0, 1, 0, 0});
		//cnn.loadData(Image2Data.getRGB("/Users/athvillar/Downloads/q2.png"), new Integer[] {0, 1});
		//cnn.loadData(Image2Data.getRGB("/Users/athvillar/Downloads/s1.jpg"), new Integer[] {1, 0, 0, 0});
		//cnn.loadData(Image2Data.getRGB("/Users/athvillar/Downloads/s2.jpg"), new Integer[] {0, 1, 0, 0});

		// 训练
		//cnn.train(10, 100);

		double e = 0.001;
		for (int i = 0; i < cnn.layers.size(); i++) {
			if (cnn.layers.get(i) instanceof FCLayer) {
				FCLayer layer = (FCLayer)cnn.layers.get(i);
				for (int j = 0; j < layer.filters.size(); j++) {
					Filter filter = layer.filters.get(j);
					for (int i2 = 0; i2 < filter.width; i2++) {
						for (int j2 = 0; j2 < filter.height; j2++) {
							for (int k2 = 0; k2 < filter.depth; k2++) {
								Double formerW = filter.w[i2][j2][k2];
								filter.w[i2][j2][k2] += e;
								layer.exec(cnn.layers.get(i - 1));
								Double y1 = layer.data[0][0][j];
								filter.w[i2][j2][k2] = formerW - e;
								layer.exec(cnn.layers.get(i - 1));
								Double y2 = layer.data[0][0][j];
								System.out.print("(f(x+e)-f(x-e))/2e=("+y1+"-"+y2+")/(2*"+e+")="+((y1-y2)/2/e));
								System.out.println("\t & error=("+y1+"-"+y2+")/(2*"+e+")="+((y1-y2)/2/e));
							}
						}
					}
				}
			}
		}

		// 预测
		Double[][][] predict = cnn.predict(Image2Data.getRGB("/Users/athvillar/Downloads/s1.jpg"));
		// 输出预测
		for (int i = 0; i < predict[0][0].length; i++) {
			System.out.print("|" + i + ":" + predict[0][0][i]);
		}
		System.out.println("--" + (predict[0][0][0] > predict[0][0][1] ? "黑" : "白"));
		System.out.println("FINISH!");
	}

	public static void testYale() throws Exception {

		String param = "" +
		"{" +
		"  \"layers\" : [" +
		"    {\"type\": \"INPUT\", \"width\": 100, \"height\": 100, \"depth\": 1 }," +
		"    {\"type\": \"CONV\", \"depth\": 16, \"stride\": 1, \"padding\":1, \"learningRate\": 0.000045, \"aF\": \"sigmoid\"," +
		"      \"filter\": {\"width\":3, \"height\":3}" +
		"    }," +
		//"    {\"type\": \"RELU\", \"function\": \"max\"}," +
		"    {\"type\": \"POOL\", \"method\": \"max\", \"spatial\": 2, \"stride\": 2}," +
		"    {\"type\": \"CONV\", \"depth\": 16, \"stride\": 1, \"padding\":1, \"learningRate\": 0.000045, \"aF\": \"sigmoid\"," +
		"      \"filter\": {\"width\":3, \"height\":3}" +
		"    }," +
		//"    {\"type\": \"RELU\", \"function\": \"max\"}," +
		"    {\"type\": \"POOL\", \"method\": \"max\", \"spatial\": 2, \"stride\": 2}," +
		"    {\"type\": \"FC\", \"depth\": 15, \"learningRate\": 0.0000051, \"aF\": \"sigmoid\" }" +
		"  ]" +
		"}";

		// 创建网络
		Cnn cnn = Cnn.getInstance(JSONObject.parseObject(param));
		for (int i = 1; i <= 165; i++) {
			Integer[][][] data = Image2Data.getGray("/Users/athvillar/Documents/book/yale/s" + i + ".bmp");
			Integer[] target = new Integer[15];
			for (int j = 0; j < target.length; j++) {
				if (j == (i - 1)/ 11) {
					target[j] = 1;
				} else {
					target[j] = 0;
				}
			}
			//cnn.addData(data, target);
		}

		// 训练
		//cnn.train(10, 100);

		// 预测
		Integer[] nums = new Integer[] {3, 88, 54, 102, 44, 91, 123};
		for (int index = 0; index < nums.length; index++) {
			Double[][][] predict = cnn.predict(Image2Data.getGray("/Users/athvillar/Documents/book/yale/s" + nums[index] + ".bmp"));
			// 输出预测
			System.out.println("The " + (index + 1) + "th result(s" + nums[index] + ".bmp) details:");
			Double max = Double.NEGATIVE_INFINITY;
			Integer maxIndex = -1;
			for (int i = 0; i < predict[0][0].length; i++) {
				if (predict[0][0][i] > max) {
					max = predict[0][0][i];
					maxIndex = i;
				}
				//System.out.print("|" + i + ":" + predict[0][0][i]);
			}
			System.out.println("");
			System.out.println("Expect:" + ((nums[index] - 1)/ 11) + ", Actual:" + maxIndex + "(" + max + ")");
		}
		System.out.println("FINISH!");
	}
}
