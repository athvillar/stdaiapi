/**
* CrossEntropy.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.base.function.cost;

import cn.standardai.lib.base.function.base.PartialDerivableFunction;

/**
 * CrossEntropy函数
 * cost = - ∑x ∑j (yj * ln(aj) + (1 - yj) * ln(1 - aj)) / n
 * @author 韩晴
 *
 */
public class CrossEntropy extends PartialDerivableFunction {

	public CrossEntropy() {
		super();
	}

	public CrossEntropy(double[] exp) {
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
		// cost = - ∑x ∑j (yj * ln(aj) + (1 - yj) * ln(1 - aj)) / n
		// TODO not correct
		for (int i = 0; i < x.length; i++) {
			sum += (param[i] * Math.log10(x[i]) + (1 - param[i]) * Math.log10(1 - x[i])) ;
		}
		// TODO not correct, this is not ∑x
		return -sum / x.length;
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
		// TODO
		return x[i] - param[i];
	}
}
