/**
* Vector.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.base.matrix;

public class VectorUtil {

	public static Double[] subVector(Double[] v, int len) throws MatrixException {

		Double[] result = new Double[len];
		for (int i = 0; i < len; i++) {
			result[i] = v[i];
		}
		return result;
	}
}
