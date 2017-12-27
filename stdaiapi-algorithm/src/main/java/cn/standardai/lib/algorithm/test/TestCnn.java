package cn.standardai.lib.algorithm.test;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.lib.algorithm.cnn.Cnn;
import cn.standardai.lib.algorithm.cnn.CnnData;
import cn.standardai.lib.algorithm.cnn.CnnException;
import cn.standardai.lib.algorithm.cnn.FCLayer;
import cn.standardai.lib.algorithm.cnn.Filter;
import cn.standardai.lib.algorithm.exception.UsageException;
import cn.standardai.lib.algorithm.kmeans.KMeans;
import cn.standardai.lib.algorithm.kmeans.KMeansNode;
import cn.standardai.lib.algorithm.kmeans.NumberNode;
import cn.standardai.lib.base.function.Statistic;
import cn.standardai.lib.base.matrix.MatrixException;
import cn.standardai.tool.CsvUtil;
import cn.standardai.tool.ImageUtil;

public class TestCnn {

	private static BufferedWriter bufWrite;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			//test15Faces();
			//test2Faces();
			//test44();
			//testChangeW();
			//testYale();
			aiChallenge();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void aiChallenge() throws CnnException, UsageException, MatrixException, IOException {

		String param = "" +
		"{" +
		"  \"layers\" : [" +
		"    {\"type\": \"INPUT\", \"width\": 89, \"height\": 1, \"depth\": 1 }," +
		//"    {\"type\": \"POOL\", \"method\": \"max\", \"spatial\": 2, \"stride\": 2}," +
		//"    {\"type\": \"POOL\", \"method\": \"max\", \"spatial\": 2, \"stride\": 2}," +
		"    {\"type\": \"CONV\", \"depth\": 10, \"stride\": 1, \"padding\":1, \"learningRate\": 0.2, \"aF\": \"sigmoid\"," +
		"      \"filter\": {\"width\":3, \"height\":1}" +
		"    }," +
		//"    {\"type\": \"RELU\", \"function\": \"max\"}," +
		//"    {\"type\": \"POOL\", \"method\": \"max\", \"spatial\": 2, \"stride\": 2}," +
		//"    {\"type\": \"CONV\", \"depth\": 16, \"stride\": 1, \"padding\":1, \"learningRate\": 0.000045, \"aF\": \"sigmoid\"," +
		//"      \"filter\": {\"width\":3, \"height\":3}" +
		//"    }," +
		//"    {\"type\": \"RELU\", \"function\": \"max\"}," +
		//"    {\"type\": \"POOL\", \"method\": \"max\", \"spatial\": 2, \"stride\": 2}," +
		"    {\"type\": \"FC\", \"depth\": 35, \"learningRate\": 0.04, \"aF\": \"sigmoid\" }" +
		"    {\"type\": \"FC\", \"depth\": 2, \"learningRate\": 0.03, \"aF\": \"sigmoid\" }" +
		"  ]" +
		"}";

		String trainFile = "/Users/athvillar/Documents/aichallenger/股票/ai_challenger_stock_train_20170910/data/20170910/ai_challenger_stock_train_20170910/train.csv";
		String testFile = "/Users/athvillar/Documents/aichallenger/股票/ai_challenger_stock_train_20170910/data/20170910/ai_challenger_stock_train_20170910/test.csv";
		//String trainFile = "/Users/athvillar/Documents/aichallenger/股票/ai_challenger_stock_train_20170910/data/20170910/ai_challenger_stock_train_20170910/stock_train_data_20170910.csv";
		//String testFile = "/Users/athvillar/Documents/aichallenger/股票/ai_challenger_stock_test_20170910/data/20170910/ai_challenger_stock_test_20170910/stock_test_data_20170910.csv";
		String outputFile = "/Users/athvillar/Documents/stock/week3/predict.csv";
		String trainFiles[] = { trainFile };
		String[][] trainInput = CsvUtil.parse(trainFiles, null, null, null, null, false, false);

		// 创建网络
		Cnn cnn = Cnn.getInstance(JSONObject.parseObject(param));
		CnnData[] cnnDatas = new CnnData[trainInput.length];

		for (int i = 0; i < trainInput.length; i++) {
			Integer[][][] data = new Integer[89][1][1];
			for (int j = 0; j < data.length - 1; j++) {
				data[j][0][0] = Math.round(Float.parseFloat(trainInput[i][j + 1]) * 100);
			}
			data[88][0][0] = Math.round(Float.parseFloat(trainInput[i][91]) * 100);
			Integer[] target = new Integer[2];
			if (Float.parseFloat(trainInput[i][90]) == 0.0) {
				target[0] = 0;
				target[1] = 1;
			} else {
				target[0] = 1;
				target[1] = 0;
			}
			CnnData cnnData = new CnnData(data, target);
			cnnDatas[i] = cnnData;
			//cnn.addData(data, target);
		}
		cnn.mountData(cnnDatas);

		int maxTrainingCount = 1;
		int epoch = 1;
		//for (int count = 0; count < maxTrainingCount; count += batchCount) {
		for (int count = 0; count < maxTrainingCount; count++) {
			// 训练
			cnn.setBatchSize(1000);
			cnn.setEpoch(epoch);
			cnn.setWatchEpoch(10);
			cnn.train();
			// 预测
			double trainLoss = 0.0;
			for (int index = 0; index < trainInput.length; index++) {
				Integer[][][] data = new Integer[89][1][1];
				for (int j = 0; j < data.length - 1; j++) {
					data[j][0][0] = Math.round(Float.parseFloat(trainInput[index][j + 1]) * 100);
				}
				data[88][0][0] = Math.round(Float.parseFloat(trainInput[index][91]) * 100);
				Double[][][] predict = cnn.predict(data);
				// 输出预测
				if (Float.parseFloat(trainInput[index][90]) == 0.0) {
					trainLoss += Math.log(1 - predict[0][0][0]);
				} else {
					trainLoss += Math.log(predict[0][0][1]);
				}
			}

			FileOutputStream out = new FileOutputStream(outputFile);
			OutputStreamWriter outWriter = new OutputStreamWriter(out, "UTF-8");
			bufWrite = new BufferedWriter(outWriter);
			String testFiles[] = { testFile };
			String[][] testInput = CsvUtil.parse(testFiles, null, null, null, null, false, false);
			double testLoss = 0.0;
			for (int index = 0; index < testInput.length; index++) {
				Integer[][][] data = new Integer[89][1][1];
				for (int j = 0; j < data.length; j++) {
					//for (int j = 0; j < data.length - 1; j++) {
					data[j][0][0] = Math.round(Float.parseFloat(testInput[index][j + 1]) * 100);
				}
				//data[88][0][0] = Math.round(Float.parseFloat(testInput[index][91]) * 100);
				Double[][][] predict = cnn.predict(data);
				// 输出预测
				if (Float.parseFloat(testInput[index][90]) == 0.0) {
					testLoss += Math.log(1 - predict[0][0][0]);
				} else {
					testLoss += Math.log(predict[0][0][1]);
				}
				print(testInput[index][0] + "," + predict[0][0][1]);
			}
			bufWrite.close();
			outWriter.close();
			out.close();
			System.out.println("Train loss: " + (-trainLoss / trainInput.length) + "\tTest loss:" + (-testLoss / testInput.length));
		}

		System.out.println("FINISH!");
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
			Integer[][][] data = ImageUtil.getR("/Users/athvillar/Documents/work/yale/s" + trainingsetNums[i] + ".bmp");
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

		int maxTrainingCount = 1;
		int epoch = 3000;
		//for (int count = 0; count < maxTrainingCount; count += batchCount) {
		for (int count = 0; count < maxTrainingCount; count++) {
			// 训练
			cnn.setBatchSize(3);
			cnn.setEpoch(epoch);
			cnn.setWatchEpoch(10);
			cnn.train();
			// 预测
			Double[] trainingCorrectRates = new Double[trainingsetNums.length];
			for (int index = 0; index < trainingsetNums.length; index++) {
				Double[][][] predict = cnn.predict(ImageUtil.getR("/Users/athvillar/Documents/work/yale/s" + trainingsetNums[index] + ".bmp"));
				// 输出预测
				Double max = Double.NEGATIVE_INFINITY;
				Double sum = 0.0;
				for (int i = 0; i < predict[0][0].length; i++) {
					sum += predict[0][0][i];
					if (predict[0][0][i] > max) {
						max = predict[0][0][i];
					}
					//System.out.print("|" + i + ":" + predict[0][0][i]);
				}
				trainingCorrectRates[index] = predict[0][0][(trainingsetNums[index] - 1)/ 11] / sum;
			}
			Integer[] testsetNums = new Integer[] {3, 14, 4, 15};
			Double[] testCorrectRates = new Double[testsetNums.length];
			for (int index = 0; index < testsetNums.length; index++) {
				Double[][][] predict = cnn.predict(ImageUtil.getR("/Users/athvillar/Documents/work/yale/s" + testsetNums[index] + ".bmp"));
				// 输出预测
				Double max = Double.NEGATIVE_INFINITY;
				Double sum = 0.0;
				for (int i = 0; i < predict[0][0].length; i++) {
					sum += predict[0][0][i];
					if (predict[0][0][i] > max) {
						max = predict[0][0][i];
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
		"    {\"type\": \"POOL\", \"method\": \"max\", \"spatial\": 2, \"stride\": 2}," +
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
			Integer[][][] data = ImageUtil.getR("/Users/athvillar/Documents/work/yale/s" + trainingsetNums[i] + ".bmp");
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

		int maxTrainingCount = 1;
		int epoch = 3000;
		for (int count = 0; count < maxTrainingCount; count++) {
		//for (int count = 0; count < maxTrainingCount; count += batchCount) {
			// 训练
			//cnn.train(cnn.dataCount(), batchCount);
			cnn.setEpoch(epoch);
			cnn.setBatchSize(5);
			cnn.setWatchEpoch(5);
			cnn.train();
			// 预测
			Double[] trainingCorrectRates = new Double[trainingsetNums.length];
			for (int index = 0; index < trainingsetNums.length; index++) {
				Double[][][] predict = cnn.predict(ImageUtil.getR("/Users/athvillar/Documents/work/yale/s" + trainingsetNums[index] + ".bmp"));
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
				Double[][][] predict = cnn.predict(ImageUtil.getR("/Users/athvillar/Documents/work/yale/s" + testsetNums[index] + ".bmp"));
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
		cnn.loadData(ImageUtil.getRGB("/Users/athvillar/Downloads/q1.jpg"), new Integer[] {0, 1});
		cnn.loadData(ImageUtil.getRGB("/Users/athvillar/Downloads/q2.png"), new Integer[] {0, 1});
		cnn.loadData(ImageUtil.getRGB("/Users/athvillar/Downloads/s1.jpg"), new Integer[] {1, 0});
		cnn.loadData(ImageUtil.getRGB("/Users/athvillar/Downloads/s2.jpg"), new Integer[] {1, 0});

		// 训练
		//cnn.train(10, 100);

		// 预测
		Double[][][] predict = cnn.predict(ImageUtil.getRGB("/Users/athvillar/Downloads/q1.jpg"));
		// 输出预测
		for (int i = 0; i < predict[0][0].length; i++) {
			System.out.print("|" + i + ":" + predict[0][0][i]);
		}
		System.out.println("--" + (predict[0][0][0] > predict[0][0][1] ? "shoe" : "ball"));
		// 预测
		predict = cnn.predict(ImageUtil.getRGB("/Users/athvillar/Downloads/s1.jpg"));
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
		cnn.loadData(ImageUtil.getRGB("/Users/athvillar/Downloads/q1.jpg"), new Integer[] {0, 1, 0, 0});
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
		Double[][][] predict = cnn.predict(ImageUtil.getRGB("/Users/athvillar/Downloads/s1.jpg"));
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
			Integer[][][] data = ImageUtil.getR("/Users/athvillar/Documents/book/yale/s" + i + ".bmp");
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
			Double[][][] predict = cnn.predict(ImageUtil.getR("/Users/athvillar/Documents/book/yale/s" + nums[index] + ".bmp"));
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

	private static void print(String s) {
		try {
			bufWrite.write(s);
			bufWrite.newLine();
			//System.out.println(s);
			bufWrite.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
