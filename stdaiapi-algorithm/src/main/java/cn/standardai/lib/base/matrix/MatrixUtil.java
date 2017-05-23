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

	public static double l2Norm(Double[][] m) throws MatrixException {

		if (m == null) throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);
		double sum = 0.0;
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m[i].length; j++) {
				sum += m[i][j] * m[i][j];
			}
		}
		return Math.sqrt(sum);
	}

	public static double l2Norm(Integer[][] m) throws MatrixException {

		if (m == null) throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);
		double sum = 0.0;
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m[i].length; j++) {
				sum += m[i][j] * m[i][j];
			}
		}
		return Math.sqrt(sum);
	}

	public static double l1Norm(Double[] m) throws MatrixException {

		if (m == null) throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);
		double sum = 0.0;
		for (int i = 0; i < m.length; i++) {
			sum += Math.abs(m[i]);
		}
		return sum;
	}

	public static double l1Norm(Integer[] m) throws MatrixException {

		if (m == null) throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);
		double sum = 0.0;
		for (int i = 0; i < m.length; i++) {
			sum += Math.abs(m[i]);
		}
		return sum;
	}

	public static double sum(Double[] m) throws MatrixException {

		if (m == null) throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);
		double sum = 0.0;
		for (int i = 0; i < m.length; i++) {
			sum += m[i];
		}
		return sum;
	}

	public static double sum(Double[][] m) throws MatrixException {

		if (m == null) throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);
		double sum = 0.0;
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m[i].length; j++) {
				sum += m[i][j];
			}
		}
		return sum;
	}

	public static double sum(Double[][][] m) throws MatrixException {

		if (m == null) throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);
		double sum = 0.0;
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m[i].length; j++) {
				for (int k = 0; k < m[i].length; k++) {
					sum += m[i][j][k];
				}
			}
		}
		return sum;
	}

	public static double sumAbs(Double[][][] m) throws MatrixException {

		if (m == null) throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);
		double sum = 0.0;
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m[i].length; j++) {
				for (int k = 0; k < m[i].length; k++) {
					sum += Math.abs(m[i][j][k]);
				}
			}
		}
		return sum;
	}

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

	public static Double[] plus(Double[]... m) throws MatrixException {

		if (m == null || m[0] == null) throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);
		int len = m[0].length;
		for (Double[] m1 : m) {
			if (m1 == null) throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);
			if (m1.length != len) throw new MatrixException(MatrixException.ERRMSG.LENTH_DISMATCH);
		}

		Double[] result = create(len, 0);
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < m.length; j++) {
				result[i] += m[j][i];
			}
		}

		return result;
	}

	public static Double[] minus(Double[] m1, Double[] m2) throws MatrixException {

		if (m1 == null || m2 == null) throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);
		if (m1.length != m2.length) throw new MatrixException(MatrixException.ERRMSG.LENTH_DISMATCH);

		Double[] result = new Double[m1.length];
		for (int i = 0; i < m1.length; i++) {
			result[i] = m1[i] - m2[i];
		}

		return result;
	}

	public static Double[][] minus(Double[][] m1, Double[][] m2) throws MatrixException {

		if (m1 == null || m2 == null) throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);
		if (m1.length != m2.length || m1[0].length != m2[0].length) throw new MatrixException(MatrixException.ERRMSG.LENTH_DISMATCH);

		Double[][] result = new Double[m1.length][m1[0].length];
		for (int i = 0; i < m1.length; i++) {
			for (int j = 0; j < m1[0].length; j++) {
				result[i][j] = m1[i][j] - m2[i][j];
			}
		}

		return result;
	}

	public static Double[][] multiply(Double[][] m1, Double[][] m2) throws MatrixException {

		if (m1 == null || m2 == null) throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);
		if (m1[0].length != m2.length) throw new MatrixException(MatrixException.ERRMSG.LENTH_DISMATCH);

		// multiply
		Double[][] result = create(m1.length, m2[0].length, 0);
		for (int i = 0; i < m1.length; i++) {
			for (int j = 0; j < m2[0].length; j++) {
				for (int k = 0; k < m1[0].length; k++) {
					result[i][j] += (m1[i][k] * m2[k][j]);
				}
			}
		}

		return result;
	}

	public static Double[] multiply(Double[] m1, Double[][] m2) throws MatrixException {

		if (m1 == null || m2 == null) throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);
		if (m1.length != m2.length) throw new MatrixException(MatrixException.ERRMSG.LENTH_DISMATCH);

		// multiply
		Double[] result = create(m2[0].length, 0);
		for (int i = 0; i < m1.length; i++) {
			for (int j = 0; j < m2[i].length; j++) {
				result[j] += m1[i] * m2[i][j];
			}
		}

		return result;
	}

	public static Double[][] multiplyTC(Double[] m1, Double[] m2) throws MatrixException {

		if (m1 == null || m2 == null) throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);

		// multiply
		Double[][] result = new Double[m1.length][m2.length];
		for (int i = 0; i < m1.length; i++) {
			for (int j = 0; j < m2.length; j++) {
				result[i][j] = m1[i] * m2[j];
			}
		}

		return result;
	}

	public static Double[] multiplyCT(Double[] m1, Double[][] m2) throws MatrixException {

		if (m1 == null || m2 == null) throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);
		if (m1.length != m2[0].length) throw new MatrixException(MatrixException.ERRMSG.LENTH_DISMATCH);

		// multiply
		Double[] result = create(m2.length, 0);
		for (int i = 0; i < m1.length; i++) {
			for (int j = 0; j < m2.length; j++) {
				result[j] += (m1[i] * m2[j][i]);
			}
		}

		return result;
	}

	public static Double multiplyCT(Double[] m1, Double[] m2) throws MatrixException {

		if (m1 == null || m2 == null) throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);
		if (m1.length != m2.length) throw new MatrixException(MatrixException.ERRMSG.LENTH_DISMATCH);

		// multiply
		Double result = 0.0;
		for (int i = 0; i < m1.length; i++) {
			result += (m1[i] * m2[i]);
		}

		return result;
	}

	public static Double[] elementMultiply(Double[] ... m) throws MatrixException {

		if (m == null || m[0] == null) throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);
		int len = m[0].length;
		for (Double[] m1 : m) {
			if (m1 == null) throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);
			if (m1.length != len) throw new MatrixException(MatrixException.ERRMSG.LENTH_DISMATCH);
		}

		Double[] result = create(len, 1.0);
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < m.length; j++) {
				result[i] *= m[j][i];
			}
		}

		return result;
	}

	public static Double[][] elementMultiply(Double[][] ... m) throws MatrixException {

		if (m == null || m[0] == null || m[0][0] == null) throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);
		int lenM = m[0].length;
		int lenN = m[0][0].length;
		for (Double[][] m1 : m) {
			if (m1 == null) throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);
			if (m1.length != lenM) throw new MatrixException(MatrixException.ERRMSG.LENTH_DISMATCH);
			for (Double[] m11 : m1) {
				if (m11 == null) throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);
				if (m11.length != lenN) throw new MatrixException(MatrixException.ERRMSG.LENTH_DISMATCH);
			}
		}

		Double[][] result = create(lenM, lenN, 1.0);
		for (int i = 0; i < lenM; i++) {
			for (int j = 0; j < lenN; j++) {
				for (int k = 0; k < m.length; k++) {
					result[i][j] *= m[k][i][j];
				}
			}
		}

		return result;
	}

	public static Double[][] devide(Double[][] m, double devider) throws MatrixException {

		if (m == null) throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);
		if (devider == 0) throw new MatrixException(MatrixException.ERRMSG.ZERO_DEVIDE);

		Double[][] result = new Double[m.length][m[0].length];
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m[0].length; j++) {
				result[i][j] = m[i][j] / devider;
			}
		}

		return result;
	}

	public static Double[][] devide(Integer[][] m, int devider) throws MatrixException {

		if (m == null) throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);
		if (devider == 0) throw new MatrixException(MatrixException.ERRMSG.ZERO_DEVIDE);

		Double[][] result = new Double[m.length][m[0].length];
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m[0].length; j++) {
				result[i][j] = 1.0 * m[i][j] / devider;
			}
		}

		return result;
	}

	public static Double[] devide(Double[] m, double devider) throws MatrixException {

		if (m == null) throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);
		if (devider == 0) throw new MatrixException(MatrixException.ERRMSG.ZERO_DEVIDE);

		Double[] result = new Double[m.length];
		for (int i = 0; i < m.length; i++) {
			result[i] = m[i] / devider;
		}

		return result;
	}

	public static Double[][] multiply(Double[][] m, double muliplier) throws MatrixException {

		if (m == null) throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);

		Double[][] result = new Double[m.length][m[0].length];
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m[0].length; j++) {
				result[i][j] = m[i][j] * muliplier;
			}
		}

		return result;
	}

	public static Double[] multiply(Double[] m, double muliplier) throws MatrixException {

		if (m == null) throw new MatrixException(MatrixException.ERRMSG.NULL_ELEMENT);

		Double[] result = new Double[m.length];
		for (int i = 0; i < m.length; i++) {
			result[i] = m[i] * muliplier;
		}

		return result;
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

	public static Object[][] transpose(Object[][] m1) throws MatrixException {

		if (m1 == null) return null;

		Object[][] result = new Object[m1[0].length][m1.length];
		for (int i = 0; i < m1.length; i++) {
			for (int j = 0; j < m1[0].length; j++) {
				result[j][i] = m1[i][j];
			}
		}

		return result;
	}

	public static Double[] concatenate(Double[] m1, Double[] m2) throws MatrixException {

		if (m1 == null) return m2;
		if (m2 == null) return m1;

		Double[] result = new Double[m1.length + m2.length];
		for (int i = 0; i < m1.length; i++) {
			result[i] = m1[i];
		}
		for (int i = 0; i < m2.length; i++) {
			result[m1.length + i] = m2[i];
		}

		return result;
	}

	public static Double[] create(int m, double value) {
		Double[] result = new Double[m];
		for (int i = 0; i < m; i++) result[i] = value;
		return result;
	}

	public static Double[][] create(int m, int n, double value) {
		Double[][] result = new Double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				result[i][j] = value;
			}
		}
		return result;
	}

	public static Double[] random(int m, double min, double max) {
		Double[] result = new Double[m];
		for (int i = 0; i < m; i++) result[i] = Math.random() * (max - min) + min;
		return result;
	}

	public static Double[][] random(int m, int n, double min, double max) {
		Double[][] result = new Double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				result[i][j] = Math.random() * (max - min) + min;
			}
		}
		return result;
	}

	public static void print(Double[] v) {
		System.out.print("[ ");
		for (int i = 0; i < v.length; i++) {
			System.out.print(v[i]);
			if (i != v.length - 1) {
				System.out.print(", ");
			}
		}
		System.out.println(" ]");
	}

	public static void print(Double[][] m) {
		System.out.print("[ ");
		for (int i = 0; i < m.length; i++) {
			System.out.print("[ ");
			for (int j = 0; j < m[i].length; j++) {
				System.out.print(m[i][j]);
				if (j != m[i].length - 1) {
					System.out.print(", ");
				}
			}
			System.out.print(" ]");
			if (i != m.length - 1) {
				System.out.println(", ");
			} else {
			}
		}
		System.out.println(" ]");
	}

	public static Double[][] subMatrix(Double[][] src, int m, int n, int direction) {
		if (src == null) return null;
		Double[][] des = new Double[m][n];
		if (direction == 1) {
			for (int i = 0; i < m; i++) {
				for (int j = 0; j < n; j++) {
					des[i][j] = src[i][j];
				}
			}
		} else {
			for (int i = 0; i < m; i++) {
				for (int j = 0; j < n; j++) {
					des[i][j] = src[src.length - m + i - 1][j];
				}
			}
		}
		return des;
	}
}
