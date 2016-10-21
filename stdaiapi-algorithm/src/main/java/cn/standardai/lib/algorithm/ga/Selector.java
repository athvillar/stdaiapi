/**
* Selector.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.ga;

/**
 * 选择器
 * @author hanq
 * 
 */
public abstract class Selector {

	// 选择算法
	public static enum ChooseMethod {ROULETTE};

	// 对象个体集合
	protected GAUnit[] units;

	/**
	 * 构造函数
	 * @param chooseMethod
	 * 选择算法
	 */
	public Selector(ChooseMethod chooseMethod) {
		super();
	}

	/**
	 * 设置适应度
	 * @param
	 * 对象个体适应度
	 */
	public abstract void setFitnesses(GAUnit[] units, int[] fitnesses);

	/**
	 * 选择个体
	 * @param num
	 * 选出个数
	 * @return 选中的个体
	 */
	public abstract GAUnit[] next(int num);
}
