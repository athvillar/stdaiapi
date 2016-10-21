/**
* Order.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.base.function;

/**
 * 排序方法
 * @author 韩晴
 *
 */
public class Order {

	// 排序方法，升序，降序
	public static enum OrderMethod {ASC, DESC};

	/**
	 * 排序数组，返回排序后的索引
	 * @param input
	 * 输入数组
	 * @param order
	 * 排序方式 ASC 升序 DESC 降序
	 * @return 排序后的索引
	 */
	public static int[] getOrderedIndex(int[] input, OrderMethod order) {

		// 初始化索引数组
		int[] inputCopy = new int[input.length];
		int[] orderedIndex = new int[input.length];
		for (int i = 0; i < input.length; i++) {
			inputCopy[i] = input[i];
			orderedIndex[i] = i;
		}

		if (order == OrderMethod.ASC) {
			int temp;
			for (int i = 0; i < inputCopy.length; i++) {
				for (int j = inputCopy.length - 2; j >= 0; j--) {
					if (inputCopy[j] > inputCopy[j + 1]) {
						temp = inputCopy[j];
						inputCopy[j] = inputCopy[j + 1];
						inputCopy[j + 1] = temp;
						temp = orderedIndex[j];
						orderedIndex[j] = orderedIndex[j + 1];
						orderedIndex[j + 1] = temp;
					}
				}
			}
		} else {
			int temp;
			for (int i = 0; i < inputCopy.length; i++) {
				for (int j = inputCopy.length - 2; j >= 0; j--) {
					if (inputCopy[j] < inputCopy[j + 1]) {
						temp = inputCopy[j];
						inputCopy[j] = inputCopy[j + 1];
						inputCopy[j + 1] = temp;
						temp = orderedIndex[j];
						orderedIndex[j] = orderedIndex[j + 1];
						orderedIndex[j + 1] = temp;
					}
				}
			}
		}

		return orderedIndex;
	}
}
