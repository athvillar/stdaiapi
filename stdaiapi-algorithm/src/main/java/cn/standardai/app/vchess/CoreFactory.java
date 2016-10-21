/**
* CoreFactory.java
* Copyright 2015 standardai Co.ltd.
*/
package cn.standardai.app.vchess;

import cn.standardai.lib.algorithm.ann.BPNetwork;
import cn.standardai.lib.base.function.activate.Sigmoid;
import cn.standardai.lib.base.function.cost.CrossEntropy;

/**
 * 遗传单元
 * @author 韩晴
 *
 */
public class CoreFactory {

	/**
	 * 获得新神经网络，根据不同的输入类型，获得不同结构的神经网络
	 */
	public static BPNetwork getInstance() {
		int[] neuronNumber = {361, 15, 361};
		return new BPNetwork(neuronNumber, 0.5, -0.5, 0.5, new Sigmoid(1), new CrossEntropy());
	}
}
