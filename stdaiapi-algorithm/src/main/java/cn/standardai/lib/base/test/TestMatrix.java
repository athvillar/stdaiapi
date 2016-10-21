/**
 * TestMatrix.java
 * copyright standardai Co.Ltd.
 */
package cn.standardai.lib.base.test;

import cn.standardai.lib.base.matrix.Determinant;
import cn.standardai.lib.base.matrix.Matrix;
import cn.standardai.lib.base.matrix.MatrixException;

public class TestMatrix {

	public static void main(String[] args) {
		//testPlus();
		//testMultiply();
		//testTranspose();
		//testDetiminant();
		testInverse();
	}

	static void testPlus() {
		double[][] d1 = {{1,1},{2,0}};
		Matrix m1 = new Matrix(d1);

		double[][] d2 = {{1,1},{2,0}};
		Matrix m2 = new Matrix(d2);

		// test plus
		Matrix m12 = null;
		try {
			m12 = m1.plus(m2);
		} catch (MatrixException e) {
			e.printStackTrace();
		}
		printMatrix(m12);
	}

	static void testMultiply() {
		double[][] d1 = {{1,1},{2,0}};
		Matrix m1 = new Matrix(d1);

		double[][] d3 = {{0,2,3},{1,1,2}};
		Matrix m3 = new Matrix(d3);
		
		// test multiply
		Matrix m13 = null;
		try {
			m13 = m1.multiply(m3);
		} catch (MatrixException e) {
			e.printStackTrace();
		}
		printMatrix(m13);
	}

	static void testTranspose() {
		double[][] d3 = {{0,2,3},{1,1,2}};
		Matrix m3 = new Matrix(d3);
		
		// test transpose
		Matrix m3t = null;
		try {
			m3t = m3.transpose();
		} catch (MatrixException e) {
			e.printStackTrace();
		}
		printMatrix(m3t);
	}

	static void testDetiminant() {
		double[][] d4 = {{4,2,3},{3,1,3},{1,1,2}};
		//double[][] d4 = {{1,3},{2,4}};
		Matrix m4 = new Matrix(d4);
		try {
			Determinant de4 = new Determinant(m4);
			printMatrix(de4);
			System.out.println(de4.getValue());
		} catch (MatrixException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	static void testInverse() {
		//double[][] d4 = {{4,2,3},{3,1,3},{1,1,2}};
		double[][] d4 = {{1,2},{3,4}};
		Matrix m4 = new Matrix(d4);
		try {
			printMatrix(m4.inverse());
			printMatrix(m4.multiply(m4.inverse()));
			printMatrix(m4.inverse().multiply(m4));
		} catch (MatrixException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	static void printMatrix(Matrix matrix) {
		for (int i = 0; i < matrix.getM(); i++) {
			for (int j = 0;j < matrix.getN(); j++) {
				System.out.print(matrix.getElement()[i][j]);
				System.out.print(" ");
			}
			System.out.println("");
		}
	}
}
