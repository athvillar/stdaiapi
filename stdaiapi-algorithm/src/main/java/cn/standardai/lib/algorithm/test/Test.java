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
public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			test1(100000);
			test2(100000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void test1(int n) {
		int totalCnt = 0;
		int hitCnt = 0;
		for (int i = 0; i < n; i++) {
			double d = Math.random();
			if (d <= 0.5) {
				continue;
			}
			totalCnt++;
			d = Math.random();
			if (d > 0.5) {
				hitCnt++;
			}
		}
		System.out.println("Total:" + n);
		System.out.println("Test:" + totalCnt);
		System.out.println("Hit:" + hitCnt);
	}
	
	public static void test2(int n) {
		int totalCnt = 0;
		int hitCnt = 0;
		for (int i = 0; i < n; i++) {
			double d1 = Math.random();
			double d2 = Math.random();
			if (d1 <= 0.5 && d2 <= 0.5) {
				continue;
			}
			totalCnt++;
			if (d1 > 0.5 && d2 > 0.5) {
				hitCnt++;
			}
		}
		System.out.println("Total:" + n);
		System.out.println("Test:" + totalCnt);
		System.out.println("Hit:" + hitCnt);
	}
}
