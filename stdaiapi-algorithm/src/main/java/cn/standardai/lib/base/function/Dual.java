/**
* Dual.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.base.function;

import cn.standardai.lib.base.function.base.Function;

/**
 * 二值函数
 * @author 韩晴
 *
 */
public class Dual extends Function {

	double y1;
	
	double y2;
	
	double middleValue;

	public Dual() {
		super();
		y1 = 1;
		y2 = 0;
		middleValue = 0.5;
	}

	public Dual(double y1, double y2, double middleValue) {
		super();
		this.y1 = y1;
		this.y2 = y2;
		this.middleValue = middleValue;
	}

	/**
	 * 计算函数值
	 * @param x
	 * x
	 * @return y
	 */
	public double getY(double x) {
		if (x >= this.middleValue) {
			return y1;
		} else {
			return y2;
		}
	}
}
