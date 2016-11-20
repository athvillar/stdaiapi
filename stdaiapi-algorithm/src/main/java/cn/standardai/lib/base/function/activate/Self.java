package cn.standardai.lib.base.function.activate;

import cn.standardai.lib.base.function.base.DerivableFunction;

public class Self extends DerivableFunction {

	public Self() {
		super();
	}

	/**
	 * 计算函数值
	 * @param x
	 * x
	 * @return y
	 */
	public double getY(double x) {
		// y = x
		return x;
	}

	/**
	 * 用x计算导数值
	 * @param x
	 * x
	 * @return y'
	 */
	public double getDerivativeX(double x) {
		return 1;
	}

	/**
	 * 用y计算导数值
	 * @param y
	 * y
	 * @return y'
	 */
	public double getDerivativeY(double y) {
		return 1;
	}
}
