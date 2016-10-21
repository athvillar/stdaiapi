/**
* TestC45.java
* Copyright 2015 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.test;

import cn.standardai.lib.algorithm.c45.C45;
import cn.standardai.lib.algorithm.c45.C45Loader;

/**
 * C4.5测试类
 * @author 韩晴
 *
 */
public class TestC45 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			C45 c45 = C45Loader.getInstance("C:\\work\\c45data.txt");
			c45.run();
			System.out.println(c45.getRoot().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
