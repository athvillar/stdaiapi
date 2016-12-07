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
public class MatrixUtil {

	public static Double[][] plus(Double[][] m1, Double[][] m2) throws MatrixException {

		if (m1 == null || m2 == null) throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);
		if (m1.length != m2.length || m1[0].length != m2[0].length) throw new MatrixException(MatrixException.ERRMSG.LENTH_DISMATCH);

		Double[][] result = new Double[m1.length][m1[0].length];
		for (int i = 0; i < m1.length; i++) {
			for (int j = 0; j < m1[0].length; j++) {
				result[i][j] = m1[i][j] + m2[i][j];
			}
		}

		return result;
	}

	public static Double[] plus(Double[] m1, Double[] m2) throws MatrixException {

		if (m1 == null || m2 == null) throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);
		if (m1.length != m2.length) throw new MatrixException(MatrixException.ERRMSG.LENTH_DISMATCH);

		Double[] result = new Double[m1.length];
		for (int i = 0; i < m1.length; i++) {
			result[i] = m1[i] + m2[i];
		}

		return result;
	}

	public static Double[][] multiply(Double[][] m1, Double[][] m2) throws MatrixException {

		if (m1 == null || m2 == null) throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);
		if (m1[0].length != m2.length) throw new MatrixException(MatrixException.ERRMSG.LENTH_DISMATCH);

		// multiply
		Double[][] result = new Double[m1.length][m2[0].length];
		for (int i = 0; i < m1.length; i++) {
			for (int j = 0; j < m2[0].length; j++) {
				for (int k = 0; k < m1[0].length; k++) {
					result[i][j] += (m1[i][k] * m2[k][j]);
				}
			}
		}

		return result;
	}

	public static Double[] multiply(Double[][] m1, Double[] m2) throws MatrixException {

		if (m1 == null || m2 == null) throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);
		if (m1[0].length != m2.length) throw new MatrixException(MatrixException.ERRMSG.LENTH_DISMATCH);

		// multiply
		Double[] result = new Double[m1.length];
		for (int i = 0; i < m1.length; i++) {
			for (int k = 0; k < m1[0].length; k++) {
				result[i] += (m1[i][k] * m2[k]);
			}
		}

		return result;
	}

	public static Double[][] devide(Double[][] m1, double devider) throws MatrixException {

		if (m1 == null) throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);
		if (devider == 0) throw new MatrixException(MatrixException.ERRMSG.ZERO_DEVIDE);

		for (int i = 0; i < m1.length; i++) {
			for (int j = 0; j < m1[0].length; j++) {
				m1[i][j] /= devider;
			}
		}

		return m1;
	}

	public static Double[][] transpose(Double[][] m1) throws MatrixException {

		if (m1 == null) return null;

		Double[][] result = new Double[m1[0].length][m1.length];
		for (int i = 0; i < m1.length; i++) {
			for (int j = 0; j < m1[0].length; j++) {
				result[j][i] = m1[i][j];
			}
		}

		return result;
	}
}
