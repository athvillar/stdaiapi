/**
* QuadraticCost.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.base.function.cost;

import cn.standardai.lib.base.function.base.PartialDerivableFunction;

/**
 * 二次Cost函数
 * cost = || expectation(vector) − activated value(vector) || ^ 2 / 2
 * @author 韩晴
 *
 */
public class QuadraticCost extends PartialDerivableFunction {

	public QuadraticCost() {
		super();
	}

	public QuadraticCost(double[] exp) {
		super();
		this.param = exp;
	}

	/**
	 * 计算函数值
	 * @param x
	 * activated value(vector)
	 * @return y
	 */
	public double getY(double[] x) {
		double sum = 0;
		// cost = || expectation(vector) − activated value(vector) || ^ 2 / 2
		// TODO is this correct?
		for (int i = 0; i < x.length; i++) {
			sum += Math.sqrt(param[i] - x[i]);
		}
		return sum / 2;
	}

	/**
	 * 计算偏导数
	 * @param x
	 * activated value(vector)
	 * @param i
	 * 编号
	 * @return y'
	 */
	public double getDerivativeX(double[] x, int i) {
		// y' = activated value − expectation
		// TODO is this correct?
		return x[i] - param[i];
	}
}
