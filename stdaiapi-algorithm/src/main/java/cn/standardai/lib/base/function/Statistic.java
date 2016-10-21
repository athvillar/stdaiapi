/**
* Statistic.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.base.function;

/**
 * 统计相关函数
 * @author 韩晴
 *
 */
public class Statistic {

	/**
	 * 计算数组最大值
	 * @param input
	 * 输入数组
	 * @return 最大值
	 */
	public static Integer max(int[] input) {
		if (input == null || input.length == 0) {
			return null;
		}
		int max = input[0];
		for (int i = 0; i < input.length; i++) {
			if (input[i] > max) {
				max = input[i];
			}
		}
		return max;
	}

	/**
	 * 计算数组最大值索引
	 * @param input
	 * 输入数组
	 * @return 最大值
	 */
	public static Integer maxIndex(double[] input) {
		if (input == null || input.length == 0) {
			return null;
		}
		double max = input[0];
		int maxIndex = 0;
		for (int i = 0; i < input.length; i++) {
			if (input[i] > max) {
				max = input[i];
				maxIndex = i;
			}
		}
		return maxIndex;
	}

	/**
	 * 计算数组平均值
	 * @param input
	 * 输入数组
	 * @return 平均值
	 */
	public static double avg(int[] input) {
		if (input == null || input.length == 0) {
			return 0;
		}
		double sum = 0;
		for (int i = 0; i < input.length; i++) {
			sum += input[i];
		}
		return sum / input.length;
	}

	/**
	 * 计算数组和
	 * @param input
	 * 输入数组
	 * @return 和
	 */
	public static double sum(double[] input) {
		if (input == null || input.length == 0) {
			return 0;
		}
		int max = 0;
		for (int i = 0; i < input.length; i++) {
			max += input[i];
		}
		return max;
	}
}
