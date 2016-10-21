/**
* Jacobi.java
* Copyright 2015 standardai Co.ltd.
*/
package cn.standardai.lib.base.matrix;

/**
 * Jacobi求特征值及特征向量
 * @author 韩晴
 *
 */
public class Jacobi {

	
	public void getEigen(Matrix a, double eps, int nmax) throws MatrixException {
		if (a.getM() != a.getN()) {
			throw new MatrixException(MatrixException.ERRMSG.LENTH_DISMATCH);
		}
		double n = a.getM();

	}
}
