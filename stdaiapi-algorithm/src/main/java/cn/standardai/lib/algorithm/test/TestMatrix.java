/**
* TestMatrix.java
* Copyright 2017 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.test;

import cn.standardai.lib.base.matrix.MatrixException;
import cn.standardai.lib.base.matrix.MatrixUtil;

public class TestMatrix {

	public static void main(String[] args) {
		try {
			//testPlus11();
			//testPlus22();
			testMinus11();
			testMinus22();
			//testMultiply22();
			//testMultiply12();
			//testMultiplyTC11();
			//testMultiplyCT12();
			//testMultiplyCT11();
			//testElementMultiply11();
			//testElementMultiply22();
			//testDevide();
			//testTranspose();
			//testConcatenate();
			//testCreate1();
			//testCreate2();
			//testRandom1();
			//testRandom2();
		} catch (MatrixException e) {
			e.printStackTrace();
		}
	}

	public static void testPlus11() throws MatrixException {
		Double[] m1 = {1D,2D};
		Double[] m2 = {3D,5D};
		Double[]m = MatrixUtil.plus(m1, m2);
		// [ 4.0, 7.0 ]
		MatrixUtil.print(m);
	}

	public static void testPlus22() throws MatrixException {
		Double[][] m1 = {{1D,2D},{3D,4D},{5D,6D}};
		Double[][] m2 = {{3D,5D},{7D,9D},{10D,11D}};
		Double[][]m = MatrixUtil.plus(m1, m2);
		// [ [ -2.0, -3.0 ], [ -4.0, -5.0 ], [ -5.0, -5.0 ] ]
		MatrixUtil.print(m);
	}

	public static void testMinus11() throws MatrixException {
		Double[] m1 = {1D,2D,6D};
		Double[] m2 = {10D,8D,3D};
		Double[] m = MatrixUtil.minus(m1, m2);
		// [ -9.0, -6.0, 3.0 ]
		MatrixUtil.print(m);
	}

	public static void testMinus22() throws MatrixException {
		Double[][] m1 = {{1D,2D},{3D,4D},{5D,6D}};
		Double[][] m2 = {{3D,5D},{7D,9D},{10D,11D}};
		Double[][]m = MatrixUtil.minus(m1, m2);
		// [ [ 4.0, 7.0 ], [ 10.0, 13.0 ], [ 15.0, 17.0 ] ]
		MatrixUtil.print(m);
	}

	public static void testMultiply22() throws MatrixException {
		Double[][] m1 = {{1D,2D},{3D,4D},{5D,6D}};
		Double[][] m2 = {{3D,5D,7D,9D},{4D,6D,8D,10D}};
		Double[][]m = MatrixUtil.multiply(m1, m2);
		// [ [ 11.0, 17.0, 23.0, 29.0 ], [ 25.0, 39.0, 53.0, 67.0 ], [ 39.0, 61.0, 83.0, 105.0 ] ]
		MatrixUtil.print(m);
	}

	public static void testMultiply12() throws MatrixException {
		Double[] m1 = {2D, 3D};
		Double[][] m2 = {{3D,5D,8D},{7D,9D,10D}};
		Double[]m = MatrixUtil.multiply(m1, m2);
		// [ 27.0, 37.0, 46.0 ]
		MatrixUtil.print(m);
	}

	public static void testMultiplyTC11() throws MatrixException {
		Double[] m1 = {2D, 3D, 5D};
		Double[] m2 = {4D, 5D};
		Double[][] m = MatrixUtil.multiplyTC(m1, m2);
		// [ [ 8.0, 10.0 ], [ 12.0, 15.0 ], [ 20.0, 25.0 ] ]
		MatrixUtil.print(m);
	}

	public static void testMultiplyCT12() throws MatrixException {
		Double[] m1 = {2D, 3D, 5D};
		Double[][] m2 = {{4D,5D,6D},{10D,20D,30D}};
		Double[] m = MatrixUtil.multiplyCT(m1, m2);
		// [ 53.0, 230.0 ]
		MatrixUtil.print(m);
	}

	public static void testMultiplyCT11() throws MatrixException {
		Double[] m1 = {3D, 5D, 7D};
		Double[] m2 = {4D, 6D, 8D};
		Double m = MatrixUtil.multiplyCT(m1, m2);
		// 98
		System.out.println(m);
	}

	public static void testElementMultiply11() throws MatrixException {
		Double[] m1 = {3D, 5D, 7D};
		Double[] m2 = {4D, 6D, 8D};
		Double[] m = MatrixUtil.elementMultiply(m1, m2);
		// [ 12.0, 30.0, 56.0 ]
		MatrixUtil.print(m);
	}

	public static void testElementMultiply22() throws MatrixException {
		Double[][] m1 = {{2D,3D,7D},{7D,4D,1D}};
		Double[][] m2 = {{4D,5D,6D},{10D,20D,30D}};
		Double[][] m = MatrixUtil.elementMultiply(m1, m2);
		// [ [ 8.0, 15.0, 42.0 ], [ 70.0, 80.0, 30.0 ] ]
		MatrixUtil.print(m);
	}

	public static void testDevide() throws MatrixException {
		Double[][] m1 = {{2D,3D,7D},{7D,4D,1D}};
		Double m2 = 2D;
		Double[][] m = MatrixUtil.devide(m1, m2);
		// [ [ 1.0, 1.5, 3.5 ], [ 3.5, 2.0, 0.5 ] ]
		MatrixUtil.print(m);
	}

	public static void testTranspose() throws MatrixException {
		Double[][] m1 = {{2D,3D,7D},{7D,4D,1D}};
		Double[][] m = MatrixUtil.transpose(m1);
		// [ [ 2.0, 7.0 ], [ 3.0, 4.0 ], [ 7.0, 1.0 ] ]
		MatrixUtil.print(m);
	}

	public static void testConcatenate() throws MatrixException {
		Double[] m1 = {2D,3D,7D};
		Double[] m2 = {1D,2D,5D,6D};
		Double[] m = MatrixUtil.concatenate(m1, m2);
		// [ 2.0, 3.0, 7.0, 1.0, 2.0, 5.0, 6.0 ]
		MatrixUtil.print(m);
	}

	public static void testCreate1() throws MatrixException {
		Double[] m = MatrixUtil.create(3, 1.1D);
		// [ 1.1, 1.1, 1.1 ]
		MatrixUtil.print(m);
	}

	public static void testCreate2() throws MatrixException {
		Double[][] m = MatrixUtil.create(3, 2, 1.2D);
		// [ [ 1.2, 1.2 ], [ 1.2, 1.2 ], [ 1.2, 1.2 ] ]
		MatrixUtil.print(m);
	}

	public static void testRandom1() throws MatrixException {
		Double[] m = MatrixUtil.random(3, 0.1D, 0.2D);
		// [ 0.10813538587582847, 0.18675135940809, 0.19045509096716629 ]
		MatrixUtil.print(m);
	}

	public static void testRandom2() throws MatrixException {
		Double[][] m = MatrixUtil.random(3, 2, 1.0D, 2.0D);
		// [ [ 1.2465037603127422, 1.9468307365355928 ], 
		// [ 1.1518441795787093, 1.0303223459028634 ], 
		// [ 1.9456754534295118, 1.4807375468271358 ] ]
		MatrixUtil.print(m);
	}
}
