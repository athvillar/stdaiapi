/**
* TestAnn.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.test;

import cn.standardai.lib.algorithm.ann.BPNetwork;
import cn.standardai.lib.base.function.Dual;
import cn.standardai.lib.base.function.Softmax;
import cn.standardai.lib.base.function.Statistic;
import cn.standardai.lib.base.function.activate.Sigmoid;
import cn.standardai.lib.base.function.base.DerivableFunction;
import cn.standardai.lib.base.function.base.PartialDerivableFunction;
import cn.standardai.lib.base.function.cost.CrossEntropy;
import cn.standardai.tool.MNISTParser;

/**
 * 神经网络测试类
 * @author 韩晴
 *
 */
public class TestMNIST {

	private static final int width = 28;

	private static final int height = 28;

	private static final String trainingImageFile = "/Users/athvillar/Documents/work/MNIST/t10k-images.idx3-ubyte";

	private static final String trainingLabelFile = "/Users/athvillar/Documents/work/MNIST/t10k-labels.idx1-ubyte";

	private static final String testImageFile = "/Users/athvillar/Documents/work/MNIST/train-images.idx3-ubyte";

	private static final String testLabelFile = "/Users/athvillar/Documents/work/MNIST/train-labels.idx1-ubyte";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			testANN();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void testANN() throws Exception {

		double[][] trainingExpectation = getLabels(trainingLabelFile);
		double[][] trainingInput = getImages(trainingImageFile, trainingExpectation.length);

		// 创建网络
		int[] num = {width * height, 10};
		DerivableFunction af = new Sigmoid(0.5);
		PartialDerivableFunction cf = new CrossEntropy();
		BPNetwork bpnet = new BPNetwork(num, 10, 0.0, 0.0, af, cf, 10, 100);

		int epochCount = 1000;
		for (int i = 0; i < epochCount; i++) {
			// 训练
			bpnet.train(trainingInput, trainingExpectation);
			// 预测
			double[][] testExpectation = getLabels(testLabelFile);
			double[][] testInput = getImages(testImageFile, testExpectation.length);

			int correctCount = 0;
			int totalCount = 1000;
			for (int ii = 0; ii < totalCount; ii++) {
				double[] predict = bpnet.predict(trainingInput[ii]);
				if (Statistic.maxIndex(predict) == Statistic.maxIndex(trainingExpectation[ii])) {
					correctCount++;
				}
			}

			System.out.println("Epoch " + i + ", correct rate:" + 1.0 * correctCount / totalCount);
		}
	}

	private static double[][] getLabels(String filePath) {

		byte[] labels = MNISTParser.getIdx1(filePath);
		int imageNumber = labels.length;

		double[][] expectation = new double[imageNumber][10];
		for (int i = 0; i < imageNumber; i++) {
			for (int j = 0; j < 10; j++) {
				if (labels[i] == j) {
					expectation[i][j] = 1.0;
				} else {
					expectation[i][j] = 0.0;
				}
			}
		}
		return expectation;
	}

	private static double[][] getImages(String filePath, int imageNumber) {

		byte[] pixels = MNISTParser.getIdx3(filePath);
		int imagePixels = width * height;

		double[][] input = new double[imageNumber][imagePixels];
		for (int i = 0; i < imageNumber; i++) {
			for (int j = 0; j < imagePixels; j++) {
				input[i][j] = (double)(pixels[i * imagePixels + j] & 0xFF);
			}
		}

		return input;
	}
}
