/**
* DerivableFunction.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.base.function.base;


/**
 * 可微函数基类
 * @author 韩晴
 *
 */
public abstract class DerivableFunction extends Function {

	public DerivableFunction() {
		super();
	}

	/**
	 * 计算函数值
	 * @param x
	 * x
	 * @return y
	 */
	public abstract double getY(double x);

	/**
	 * 计算导数值
	 * @param x
	 * x
	 * @return y'
	 */
	public abstract double getDerivativeX(double x);

	/**
	 * 计算导数值
	 * @param y
	 * y
	 * @return y'
	 */
	public abstract double getDerivativeY(double y);
}
