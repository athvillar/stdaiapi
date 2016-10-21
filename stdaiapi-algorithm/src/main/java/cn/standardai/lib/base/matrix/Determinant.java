/**
* Determinant.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.base.matrix;

/**
 * 行列式
 * @author 韩晴
 *
 */
public class Determinant extends Matrix {

	public Determinant(int n) throws MatrixException {
		super(n, n);
	}

	public Determinant(Matrix matrix) throws MatrixException {
		super(matrix.getElement());
		if (matrix.getElement() == null) {
			throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);
		}
		if (matrix.getM() != matrix.getN()) {
			throw new MatrixException(MatrixException.ERRMSG.LENTH_DISMATCH);
		}
	}

	public double getValue() throws MatrixException {
		
		if (this.getN() == 0) {
			return 1;
		}
		double sum = 0;
		for (int i = 0; i < this.getN(); i++) {
			int j = 0;
			if (i % 2 == 0) {
				sum += this.getElement()[i][j] * cofactor(i, j).getValue();
			} else {
				sum -= this.getElement()[i][j] * cofactor(i, j).getValue();
			}
		}

		return sum;
	}

	public Determinant cofactor(int row, int col) throws MatrixException {
		// ȡ����ʽ
		if (this.getN() == 0) {
			return null;
		}
		Determinant cofactor = new Determinant(this.getN() - 1);
		for (int i1 = 0, i2 = 0; i1 < cofactor.getN(); i1++, i2++) {
			if (i2 == row) {
				i2++;
			}
			for (int j1 = 0, j2 = 0; j1 < cofactor.getN(); j1++, j2++) {
				if (j2 == col) {
					j2++;
				}
				cofactor.getElement()[i1][j1] = this.getElement()[i2][j2];
			}
		}

		return cofactor;
	}
}
