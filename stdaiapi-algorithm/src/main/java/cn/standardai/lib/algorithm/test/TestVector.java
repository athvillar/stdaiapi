/**
* TestMatrix.java
* Copyright 2017 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.test;

import cn.standardai.lib.base.matrix.MatrixException;
import cn.standardai.lib.base.matrix.MatrixUtil;
import cn.standardai.lib.base.matrix.VectorUtil;

public class TestVector {

	public static void main(String[] args) {
		try {
			testSubVector();
		} catch (MatrixException e) {
			e.printStackTrace();
		}
	}

	public static void testSubVector() throws MatrixException {
		Double[] v = {3D,4D,5D,6D,7D};
		Double[] r = VectorUtil.subVector(v, 4);
		// [ 3.0, 4.0, 5.0, 6.0 ]
		MatrixUtil.print(r);
	}
}
