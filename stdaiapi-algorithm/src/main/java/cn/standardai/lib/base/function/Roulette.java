/**
* Roulette.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.base.function;

import cn.standardai.lib.base.function.base.Function;

/**
 * 轮盘赌函数
 * @author 韩晴
 *
 */
public class Roulette extends Function {

	double[] probability;
	
	double sum;

	public Roulette() {
		super();
	}

	public Roulette(double[] probability) {
		super();
		this.probability = probability;
		this.sum = Statistic.sum(probability);
	}

	/**
	 * 计算函数值
	 * @param x
	 * x
	 * @return y
	 * @throws FunctionException 
	 */
	public double getY(double x) throws FunctionException {
		if (x < 0 || x >= this.sum) {
			throw new FunctionException(FunctionException.ERRMSG.ILLEGAL_PARAM);
		}

		int i;
		for (i = 0; i < probability.length; i++) {
			if (x < probability[i]) {
				break;
			} else {
				x -= probability[i];
			}
		}
		// 返回被选中的索引
		return i;
	}
}
