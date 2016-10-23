/**
* RouletteSelector.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.ga;

import cn.standardai.lib.base.function.FunctionException;
import cn.standardai.lib.base.function.Roulette;
import cn.standardai.lib.base.function.Statistic;
import cn.standardai.lib.base.function.base.Function;

/**
 * Roulette选择器
 * @author hanq
 * 
 */
public class RouletteSelector extends Selector {

	// 选择函数
	private Function chooseFunction;

	// 适应度之和
	private double sum;

	/**
	 * 构造函数
	 * @param chooseMethod
	 * 选择算法
	 */
	public RouletteSelector() {
		super(ChooseMethod.ROULETTE);
	}

	/**
	 * 设置适应度
	 * @param
	 * 对象个体适应度
	 */
	public void setFitnesses(GAUnit[] units, int[] fitnesses) {
		this.units = units;
		double[] fitnesses2 = new double[fitnesses.length];
		for (int i = 0; i < fitnesses.length; i++) {
			fitnesses2[i] = Math.pow(fitnesses[i], 2);
		}
		chooseFunction = new Roulette(fitnesses2);
		sum = Statistic.sum(fitnesses2);
	}

	/**
	 * 下一个被选中的个体索引
	 * @return 选中的个体索引
	 */
	public int nextIndex() {
		try {
			return (int)((Roulette)chooseFunction).getY(Math.random() * sum);
		} catch (FunctionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 选择个体
	 * @param num
	 * 选出个数
	 * @return 选中的个体
	 */
	public GAUnit[] next(int num) {
		GAUnit[] obj = new GAUnit[num];
		for (int i = 0; i < num; i++) {
			obj[i] = units[nextIndex()];
		}
		return obj;
	}
}
