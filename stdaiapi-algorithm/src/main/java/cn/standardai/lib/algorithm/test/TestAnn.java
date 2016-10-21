/**
* TestAnn.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.test;

import cn.standardai.lib.algorithm.ann.BPNetwork;
import cn.standardai.lib.base.function.Dual;

/**
 * 神经网络测试类
 * @author 韩晴
 *
 */
public class TestAnn {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			//trainBPNetworkEqual();
			//trainBPNetworkBinary();
			//trainOR();
			//trainPersonality();
			trainFigure();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void trainFigure() throws Exception {

		double[][] input = {
				{0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,1,1,1,0,0,1,0,1,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,1,1,1,0,0,0,0,0,0},
				{0,0,1,0,0,0,1,0,1,0,0,0,0,1,0,0,1,1,0,0,0,0,0,1,0,0,0,0,1,0,0,1,1,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,1,1,1,0,0,0,0,1,0,0,0,0,1,0,0,1,1,1,0,0,0,0,1,0,0,0,0,1,0,0,1,1,1,0}
		};

		double[][] expectation = {
				{0,0,1},
				{0,1,0},
				{1,0,0},
				{1,0,0}
		};

		double[] predict;

		// 创建网络
		int[] num = {40, 20,20,20, 3};
		BPNetwork bpnet = new BPNetwork(num, 0.1, 3000, -0.5, 0.5, 1);

		// 训练
		bpnet.train(input, expectation);

		// 预测
		double[] input2 = {0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0};
		//double[] input2 = {0,0,0,0,0,0,1,1,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,0,0,1,0,0,0,0,1,1,1,0,0,0,0,0,0};
		//double[] input2 = {0,0,0,0,0,0,0,1,1,0,0,1,0,1,0,0,0,0,1,0,0,0,1,0,0,0,0,0,1,0,0,0,0,1,0,0,1,1,1,0};
		predict = bpnet.predict(input2);
		
		// 输出预测
		Dual dual = new Dual();
		for (int i = 0; i < num[num.length - 1]; i++) {
			System.out.println(predict[i]);
			//System.out.println(dual.getY(predict[i]));
		}
	}

	public static void trainPersonality() throws Exception {

		double[][] input = {
				{0,0,0,1,0},
				{0,0,1,0,1},
				{0,1,1,1,0},
				{0,1,0,0,1},
				{1,0,1,0,0},
				{1,1,0,1,1},
				{1,0,0,0,0},
				{0,1,0,1,1}
		};

		double[][] expectation = {
				{0,0,0,1},
				{0,0,1,0},
				{0,1,0,0},
				{0,0,1,0},
				{0,0,1,0},
				{1,0,0,0},
				{0,0,0,1},
				{0,1,0,0}
		};
		
		double[] predict;

		// 创建网络
		int[] num = {5,8,8,8, 4};
		BPNetwork bpnet = new BPNetwork(num, 0.1, 1000, -0.5, 0.5, 2);

		// 训练
		bpnet.train(input, expectation);

		// 预测
		double[] input2 = {0,0,1,1,1};
		predict = bpnet.predict(input2);

		// 输出预测
		Dual dual = new Dual();
		for (int i = 0; i < num[num.length - 1]; i++) {
			System.out.println(predict[i]);
			//System.out.println(dual.getY(predict[i]));
		}
	}

	public static void trainOR() throws Exception {

		double[][] input = {
				{0,1},
				{1,0},
				{1,1},
				{0,0}
		};

		double[][] expectation = {
				{1},
				{1},
				{1},
				{0}
		};
		
		double[] predict;

		// 创建网络
		int[] num = {2, 3, 1};
		BPNetwork bpnet = new BPNetwork(num, 0.1, 1000, -0.5, 0.5, 2);

		// 训练
		bpnet.train(input, expectation);

		// 预测
		double[] input2 = {1,1};
		predict = bpnet.predict(input2);

		// 输出预测
		Dual dual = new Dual();
		for (int i = 0; i < num[num.length - 1]; i++) {
			//System.out.println(predict[i]);
			System.out.println(dual.getY(predict[i]));
		}
	}

	public static void trainBPNetworkEqual() throws Exception {

		double[][] input = {
				{1,1,1,1,1,1,1,1},
				{1,0,0,0,0,0,0,0},
				{0,1,0,0,0,0,0,0},
				{0,0,1,0,0,0,0,0},
				{0,0,0,1,0,0,0,0},
				{0,0,0,0,1,0,0,0},
				{0,0,0,0,0,1,0,0},
				{0,0,0,0,0,0,1,0},
				{0,0,0,0,0,0,0,1}
		};

		double[][] expectation = {
				{1,1,1,1,1,1,1,1},
				{1,0,0,0,0,0,0,0},
				{0,1,0,0,0,0,0,0},
				{0,0,1,0,0,0,0,0},
				{0,0,0,1,0,0,0,0},
				{0,0,0,0,1,0,0,0},
				{0,0,0,0,0,1,0,0},
				{0,0,0,0,0,0,1,0},
				{0,0,0,0,0,0,0,1}
		};
		
		double[] predict;

		// 创建网络
		int[] num = {8, 111, 8};
		BPNetwork bpnet = new BPNetwork(num, 0.1,5000, -0.5, 0.5, 2);

		// 训练
		bpnet.train(input, expectation);

		// 预测
		double[] input2 = {1,0,0,1,0,0,1,1};
		predict = bpnet.predict(input2);

		// 输出预测
		Dual dual = new Dual();
		for (int i = 0; i < num[num.length - 1]; i++) {
			//System.out.println(predict[i]);
			System.out.println(dual.getY(predict[i]));
		}
	}

	public static void trainBPNetworkBinary() throws Exception {

		double[][] input = {
				{1},
				{2},
				{4},
				{8},
				{9},
				{15},
				{16},
				{32},
				{64},
				{128},
				{255}
		};

		double[][] expectation = {
				{1,0,0,0,0,0,0,0},
				{0,1,0,0,0,0,0,0},
				{0,0,1,0,0,0,0,0},
				{0,0,0,1,0,0,0,0},
				{1,0,0,1,0,0,0,0},
				{1,1,1,1,0,0,0,0},
				{0,0,0,0,1,0,0,0},
				{0,0,0,0,0,1,0,0},
				{0,0,0,0,0,0,1,0},
				{0,0,0,0,0,0,0,1},
				{1,1,1,1,1,1,1,1}
		};
		
		double[] predict;

		// 创建网络
		int[] num = {1, 16, 8};
		BPNetwork bpnet = new BPNetwork(num,2,70000,-1, 1, 0.08);

		// 训练
		bpnet.train(input, expectation);

		// 预测
		double[] input2 = {16};
		predict = bpnet.predict(input2);

		// 输出预测
		Dual dual = new Dual();
		for (int i = 0; i < num[num.length - 1]; i++) {
			System.out.println(predict[i]);
			//System.out.println(dual.getY(predict[i]));
		}
	}
}
