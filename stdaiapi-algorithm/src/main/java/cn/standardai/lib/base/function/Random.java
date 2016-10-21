/**
* Random.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.base.function;

/**
 * 随机函数应用
 * @author 韩晴
 *
 */
public class Random {

	/**
	 * 在序列中按百分比随机选出索引
	 * @param totalIndex
	 * 序列长度
	 * @param percentage
	 * 百分比
	 * @return 选出的索引
	 */
	public static int[] randomIndex(int totalIndex, double percentage) {

		int integerPart = (int)Math.floor(totalIndex * percentage);
		double decimalPart = totalIndex * percentage - integerPart;

		// 初始化返回数组
		int[] result;
		if (Math.random() < decimalPart) {
			result = new int[integerPart + 1];
		} else {
			result = new int[integerPart];
		}

		// 确定随机索引
		for (int i = 0; i < result.length; i++) {
			result[i] = (int)Math.floor(Math.random() * totalIndex);
		}

		return result;
	}
}
