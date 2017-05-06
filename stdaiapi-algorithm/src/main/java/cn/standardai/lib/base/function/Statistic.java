/**
* Statistic.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.base.function;

import java.util.Map;
import java.util.Map.Entry;

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

	public static Integer max(Integer...args) {
		if (args == null || args.length == 0) {
			return null;
		}
		int max = Integer.MIN_VALUE;
		for (int i = 0; i < args.length; i++) {
			if (args[i] > max) {
				max = args[i];
			}
		}
		return max;
	}

	public static Double max(Double...args) {
		if (args == null || args.length == 0) {
			return null;
		}
		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < args.length; i++) {
			if (args[i] > max) {
				max = args[i];
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

	public static Integer maxIndex(Double[] input) {
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

	public static double avg(Integer...args) {
		if (args == null || args.length == 0) {
			return 0;
		}
		double sum = 0;
		for (int i = 0; i < args.length; i++) {
			sum += args[i];
		}
		return sum / args.length;
	}

	public static double avg(Double...args) {
		if (args == null || args.length == 0) {
			return 0;
		}
		double sum = 0;
		for (int i = 0; i < args.length; i++) {
			sum += args[i];
		}
		return sum / args.length;
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
		double sum = 0;
		for (int i = 0; i < input.length; i++) {
			sum += input[i];
		}
		return sum;
	}

	public static <T> T maxDoubleValueKey(Map<T, Double> igMap) {
		Double maxValue = Double.NEGATIVE_INFINITY;
		T maxKey = null;
		for (Entry<T, Double> entry : igMap.entrySet()) {
			if (entry.getValue() > maxValue) {
				maxKey = entry.getKey();
				maxValue = entry.getValue();
			}
		}
		return maxKey;
	}

	public static <T> T maxIntegerValueKey(Map<T, Integer> igMap) {
		Integer maxValue = Integer.MIN_VALUE;
		T maxKey = null;
		for (Entry<T, Integer> entry : igMap.entrySet()) {
			if (entry.getValue() > maxValue) {
				maxKey = entry.getKey();
				maxValue = entry.getValue();
			}
		}
		return maxKey;
	}
}
