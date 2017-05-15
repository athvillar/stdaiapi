/**
* Normalizer.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.base.function;

/**
 * 归一化函数
 * @author 韩晴
 *
 */
public class Normalizer {

	/**
	 * 归一化
	 * @param input
	 * 输入数组
	 * @return 归一化后结果
	 */
	public static double[] toProbability(double[] input) {
		double[] output = new double[input.length];
		double total = 0;
		for (int i = 0; i < input.length; i++) {
			total += input[i];
		}
		if (total == 0) {
			return output;
		}
		for (int i = 0; i < input.length; i++) {
			output[i] = input[i] / total;
		}
		return output;
	}

	/**
	 * 归一化
	 * @param input
	 * 输入数组
	 * @return 归一化后结果
	 */
	public static Double[] toProbability(Double[] input) {
		Double[] output = new Double[input.length];
		double total = 0;
		for (int i = 0; i < input.length; i++) {
			total += input[i];
		}
		if (total == 0) {
			return output;
		}
		for (int i = 0; i < input.length; i++) {
			output[i] = input[i] / total;
		}
		return output;
	}
}
