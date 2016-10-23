/**
* TestGA.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.test;

import cn.standardai.app.contracaptcha.AnnTrainer;
import cn.standardai.lib.algorithm.ann.BPNetwork;
import cn.standardai.lib.algorithm.exception.AnnException;

public class TestGA {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		testGA();
		//testBP();
	}

	private static void testGA() {
		try {
			AnnTrainer.trainFigure();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void testBP() {
		// 创建网络
		//BPNetwork bpnet = makeBP1();
		//BPNetwork bpnet = makeBP2();
		//BPNetwork bpnet = makeBP3();
		BPNetwork bpnet = makeBP4();
		bpnet.printInitParam();

		// 训练数据
		double[][] trainInput = {
				// set 1
				{0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0},//1
				{0,0,0,0,0,0,1,1,1,0,0,1,0,1,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,1,1,1,0,0,0,0,0,0},//2
				{0,0,1,0,0,0,1,0,1,0,0,0,0,1,0,0,1,1,0,0,0,0,0,1,0,0,0,0,1,0,0,1,1,0,0,0,0,0,0,0},//3
				{0,0,0,0,0,0,1,1,1,0,0,0,0,1,0,0,0,0,1,0,0,1,1,1,0,0,0,0,1,0,0,0,0,1,0,0,1,1,1,0},//3
				{0,0,0,0,0,0,1,0,1,0,0,1,0,1,0,0,1,1,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0},//4
				{0,0,0,0,0,0,1,1,1,0,0,1,0,0,0,0,1,0,0,0,0,1,1,1,0,0,0,0,1,0,0,1,1,1,0,0,0,0,0,0},//5
				{0,0,0,0,0,0,1,1,1,0,0,1,0,0,0,0,1,0,0,0,0,1,1,1,0,0,1,0,1,0,0,1,1,1,0,0,0,0,0,0},//6
				{0,0,0,0,0,0,1,1,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0},//7
				{0,0,0,0,0,0,1,1,1,0,0,1,0,1,0,0,1,0,1,0,0,1,1,1,0,0,1,0,1,0,0,1,1,1,0,0,0,0,0,0},//8
				{0,0,0,0,0,0,1,1,1,0,0,1,0,1,0,0,1,0,1,0,0,1,1,1,0,0,0,0,1,0,0,1,1,1,0,0,0,0,0,0},//9
				{0,0,0,0,0,0,1,1,1,0,0,1,0,1,0,0,1,0,1,0,0,1,0,1,0,0,1,0,1,0,0,1,1,1,0,0,0,0,0,0}//0
		};
		double[][] trainExp = {
				{0,0,0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,0,1,0},
				{0,0,0,0,0,0,0,1,0,0},
				{0,0,0,0,0,0,0,1,0,0},
				{0,0,0,0,0,0,1,0,0,0},
				{0,0,0,0,0,1,0,0,0,0},
				{0,0,0,0,1,0,0,0,0,0},
				{0,0,0,1,0,0,0,0,0,0},
				{0,0,1,0,0,0,0,0,0,0},
				{0,1,0,0,0,0,0,0,0,0},
				{1,0,0,0,0,0,0,0,0,0}
		};

		// 训练
		try {
			bpnet.train(trainInput, trainExp);
		} catch (AnnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 测试数据
		double[][] testInput = {
				// set 2
				{0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0},//1
				{0,0,0,0,0,0,1,1,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,0,1,0,0,0,0,1,1,1,0,0,0,0,0,0},//2
				{0,0,0,0,0,0,0,1,1,0,0,1,0,1,0,0,0,0,1,0,0,0,1,0,0,0,0,0,1,0,0,0,0,1,0,0,1,1,1,0},//3
				{0,0,0,0,0,0,1,0,1,0,0,1,0,1,0,0,1,0,1,0,0,1,1,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0},//4
				{0,0,0,0,0,0,1,1,1,0,0,1,0,0,0,0,1,0,0,0,0,0,1,1,0,0,0,0,1,0,0,0,0,1,0,0,1,1,1,0},//5
				{0,0,0,0,0,0,0,1,1,0,0,1,0,0,0,0,1,0,0,0,0,1,1,1,0,0,1,0,1,0,0,1,0,1,0,0,1,1,1,0},//6
				{0,0,0,0,0,0,1,1,1,0,0,1,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0},//7
				{0,0,0,0,0,0,1,1,1,0,0,1,0,1,0,0,1,1,1,0,0,1,0,1,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0},//8
				{0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,1,0,1,0,0,1,1,1,0,0,0,0,1,0,0,1,1,1,0,0,0,0,0,0},//9
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,1,0,1,0,0,1,0,1,0,0,1,1,1,0,0,0,0,0,0}//0
		};
		double[][] testExp = {
				{0,0,0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,0,1,0},
				{0,0,0,0,0,0,0,1,0,0},
				{0,0,0,0,0,0,1,0,0,0},
				{0,0,0,0,0,1,0,0,0,0},
				{0,0,0,0,1,0,0,0,0,0},
				{0,0,0,1,0,0,0,0,0,0},
				{0,0,1,0,0,0,0,0,0,0},
				{0,1,0,0,0,0,0,0,0,0},
				{1,0,0,0,0,0,0,0,0,0}
		};

		int fitness = 0;
		// 测试
		for (int i = 0; i < testInput.length; i++) {
			int[] predict = bpnet.monoPredict(testInput[i]);
			// 输出预测
			boolean matchFlg = true;
			for (int j = 0; j < testExp[i].length; j++) {
				if (testExp[i][j] != predict[j]) {
					matchFlg = false;
					break;
				}
			}
			if (matchFlg) {
				fitness += 100;
			}
		}
		System.out.println("Fitness : " + fitness);
	}
	
	private static BPNetwork makeBP1() {
		// 根据基因创建网络
		int[] layerNums = {40,19,20,21,25,27,10};
		double eta = 0.26;
		int maxTrain = 6600;
		double minW = -0.37;
		double maxW = 0.4;
		double sigmoidK = 3.1;

		// 建立网络
		//BPNetwork bpnet = new BPNetwork(layerNums, eta, maxTrain, minW, maxW, sigmoidK);

		return null;
	}
	
	private static BPNetwork makeBP2() {
		// 根据基因创建网络
		int[] layerNums = {40,19,10};
		double eta = 0.26;
		int maxTrain = 7200;
		double minW = -0.13;
		double maxW = 0.0;
		double sigmoidK = 2.3;

		// 建立网络
		//BPNetwork bpnet = new BPNetwork(layerNums, eta, maxTrain, minW, maxW, sigmoidK);

		return null;
	}
	
	private static BPNetwork makeBP3() {
		// 根据基因创建网络
		int[] layerNums = {40,28,10};
		double eta = 0.29;
		int maxTrain = 11100;
		double minW = -0.55;
		double maxW = 0.51;
		double sigmoidK = 3.0;

		// 建立网络
		//BPNetwork bpnet = new BPNetwork(layerNums, eta, maxTrain, minW, maxW, sigmoidK);

		return null;
	}
	
	private static BPNetwork makeBP4() {
		// 根据基因创建网络
		int[] layerNums = {40,20,10};
		double eta = 0.05;
		int maxTrain = 10700;
		double minW = -0.07;
		double maxW = 0.27;
		double sigmoidK = 2.8;

		// 建立网络
		//BPNetwork bpnet = new BPNetwork(layerNums, eta, maxTrain, minW, maxW, sigmoidK);

		return null;
	}
}
