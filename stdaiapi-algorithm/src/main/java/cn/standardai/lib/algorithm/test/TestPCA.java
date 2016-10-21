/**
* TestPCA.java
* Copyright 2015 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.test;

import cn.standardai.lib.base.function.Statistic;
import cn.standardai.lib.base.matrix.Matrix;
import cn.standardai.lib.base.matrix.MatrixException;

/**
 * C4.5测试类
 * @author 韩晴
 *
 */
public class TestPCA {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double[][] d = {{10,15,29},{15,46,13},{23,21,30},{11,9,35},{42,45,11},{9,48,5},{11,21,14},{8,5,15},{11,12,21},{21,20,25}};
		Matrix m = new Matrix(d);
		
		Matrix vm1 = Statistic.getCovariationMatrix(m);
		System.out.println(vm1.toString());
		Matrix vm2;
		try {
			vm2 = Statistic.getCovariationMatrix2(m);
			System.out.println(vm2.toString());
		} catch (MatrixException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
