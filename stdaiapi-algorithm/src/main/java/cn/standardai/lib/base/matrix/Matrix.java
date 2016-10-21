/**
* Matrix.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.base.matrix;

/**
 * 矩阵类
 * @author 韩晴
 *
 */
public class Matrix {

	private double[][] element;

	private int m;

	public int getM() {
		return m;
	}

	public int getN() {
		return n;
	}

	public double[][] getElement() {
		return element;
	}

	public void setElement(double[][] element) {
		this.element = element;
	}

	private int n;

	public Matrix() {
	}

	public Matrix(int m, int n) {
		this.m = m;
		this.n = n;
		this.element = new double[m][n];
	}

	public Matrix(double[][] element) {
		this.element = element;
		this.m = element.length;
		if (m == 0) {
			n = 0;
		} else {
			n = element[0].length;
		}
	}

	public Matrix plus(Matrix matrix) throws MatrixException {
		// check
		if (this.element == null || matrix.element == null) {
			throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);
		}
		if (this.m != matrix.m || this.n != matrix.n) {
			throw new MatrixException(MatrixException.ERRMSG.LENTH_DISMATCH);
		}

		// plus
		Matrix resultMatrix = new Matrix(this.m, this.n);
		for (int i = 0; i < element.length; i++) {
			for (int j = 0; j < element[i].length; j++) {
				resultMatrix.element[i][j] = this.element[i][j] + matrix.element[i][j];
			}
		}

		return resultMatrix;
	}

	public Matrix multiply(Matrix matrix) throws MatrixException {
		// check
		if (this.element == null || matrix.element == null) {
			throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);
		}
		if (this.n != matrix.m) {
			throw new MatrixException(MatrixException.ERRMSG.LENTH_DISMATCH);
		}

		// multiply
		Matrix resultMatrix = new Matrix(this.m, matrix.n);
		for (int i = 0; i < this.m; i++) {
			for (int j = 0; j < matrix.n; j++) {
				for (int k = 0; k < this.n; k++) {
					resultMatrix.element[i][j] += (this.element[i][k] * matrix.element[k][j]);
				}
			}
		}

		return resultMatrix;
	}

	public Matrix devide(double devider) throws MatrixException {
		// check
		if (this.element == null) {
			throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);
		}
		if (devider == 0) {
			throw new MatrixException(MatrixException.ERRMSG.ZERO_DEVIDE);
		}

		// devide
		Matrix resultMatrix = new Matrix(this.element);
		for (int i = 0; i < this.m; i++) {
			for (int j = 0; j < this.n; j++) {
				resultMatrix.element[i][j] /= devider;
			}
		}

		return resultMatrix;
	}

	public Matrix transpose() throws MatrixException {
		// check
		if (this.element == null) {
			throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);
		}

		// transpose
		Matrix resultMatrix = new Matrix(this.n, this.m);
		for (int i = 0; i < this.m; i++) {
			for (int j = 0; j < this.n; j++) {
				resultMatrix.element[j][i] = this.element[i][j];
			}
		}

		return resultMatrix;
	}

	public Matrix inverse() throws MatrixException {
		// check
		if (this.element == null) {
			throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);
		}
		if (this.m != this.n) {
			throw new MatrixException(MatrixException.ERRMSG.LENTH_DISMATCH);
		}

		// transpose
		Determinant determinant = new Determinant(this);
		if (determinant.getValue() == 0) {
			throw new MatrixException(MatrixException.ERRMSG.ZERO_DEVIDE);
		}
		Matrix adjoint = new Matrix(this.m, this.n);
		for (int i = 0; i < this.m; i++) {
			for (int j = 0; j < this.n; j++) {
				if ((i + j) % 2 == 0) {
					adjoint.element[i][j] = determinant.cofactor(i, j).getValue();
				} else {
					adjoint.element[i][j] = (-1) * determinant.cofactor(i, j).getValue();
				}
			}
		}
		adjoint = adjoint.transpose();

		return adjoint.devide(determinant.getValue());
	}
}
