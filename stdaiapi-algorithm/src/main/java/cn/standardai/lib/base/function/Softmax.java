package cn.standardai.lib.base.function;

import cn.standardai.lib.base.function.base.Function;

public class Softmax extends Function {

	public static Double[] getY(Double[] x) {

		if (x == null) return null;

		double sum = 0;
		for (int i = 0; i < x.length; i++) {
			sum += Math.exp(x[i]);
		}
		Double[] result = new Double[x.length];
		for (int i = 0; i < x.length; i++) {
			result[i] = Math.exp(x[i]) / sum;
		}

		return result;
	}
}
