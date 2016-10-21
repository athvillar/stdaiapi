/**
* TestAnn.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.test;

import cn.standardai.lib.algorithm.ann.BPNetwork;
import cn.standardai.lib.base.function.Dual;
import cn.standardai.lib.base.function.activate.Sigmoid;
import cn.standardai.lib.base.function.base.DerivableFunction;
import cn.standardai.lib.base.function.base.PartialDerivableFunction;
import cn.standardai.lib.base.function.cost.CrossEntropy;
import cn.standardai.lib.base.model.Hedgecopter;

/**
 * 神经网络测试类--Hedgecopter
 * @author 韩晴
 *
 */
public class TestHedgecopter {

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
		int[] num = {1, 2, 1};
		
		DerivableFunction af = new Sigmoid(0.5);
		PartialDerivableFunction cf = new CrossEntropy();
		BPNetwork bpnet = new BPNetwork(num, 0.2, -0.5, 0.5, af, cf);

		// 训练
		Hedgecopter hedgecopter = new Hedgecopter(bpnet, 5);
		while (!hedgecopter.crash()) {
			hedgecopter.next();
		}
		bpnet.train(input, expectation);

		// 预测
		double[] input2 = {0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0};
		predict = bpnet.predict(input2);
		
		// 输出预测
		Dual dual = new Dual();
		for (int i = 0; i < num[num.length - 1]; i++) {
			System.out.println(predict[i]);
			//System.out.println(dual.getY(predict[i]));
		}
	}
}
