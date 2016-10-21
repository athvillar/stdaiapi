/**
* PartialDerivableFunction.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.base.function.base;


/**
 * 多个自变量可导函数基类
 * @author 韩晴
 *
 */
public abstract class PartialDerivableFunction extends Function {

	protected double[] param;

	public PartialDerivableFunction() {
		super();
	}

	/**
	 * 计算函数值
	 * @param x
	 * x
	 * @return y
	 */
	public abstract double getY(double[] x);

	/**
	 * 计算导数值
	 * @param x
	 * x(vector)
	 * @param i
	 * 编号
	 * @return y'
	 */
	public abstract double getDerivativeX(double[] x, int i);

	public void setParam(double[] param) {
		this.param = param;
	}
}
