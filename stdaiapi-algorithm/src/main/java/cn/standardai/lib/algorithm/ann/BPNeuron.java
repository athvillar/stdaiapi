/**
* BPNeuron.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.ann;

import cn.standardai.lib.base.function.base.DerivableFunction;

/**
 * BP神经网络 神经元类
 * @author 韩晴
 *
 */
public class BPNeuron {

	/**
	 * 计算节点在特定输入下的输出
	 * @param input
	 * 输入
	 * @return 输出
	 */
	public double getOutput(double[] input, double[] weight, double threshold, DerivableFunction function) {
		// 计算w·x
		double net = 0;
		for (int i = 0; i < input.length; i++) {
			net += weight[i] * input[i];
		}
		return function.getY(net + threshold);
	}
}	
