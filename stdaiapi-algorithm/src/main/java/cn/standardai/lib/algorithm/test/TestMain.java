/**
* TestMain.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.test;

public class TestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for (int i = 1; i < 20; i++) {
			show(6 * (int)Math.pow(2, i - 1));
		}
	}
	
	public static void show(int count) {
		double circle = calctotal(count);
		double rate = circle / Math.PI;
		System.out.println("" + count + "分圆\t\t周长：" + circle + "\t准确率：" + rate);
		
	}
	
	public static double calctotal(int count) {
		return count * calc(count);
	}
	
	public static double calc(int count) {
		int cut = count / 6;
		if (cut == 1) {
			return 0.5;
		}
		double smallhalf;
		double bighalf;
		double halflast;
		double edge;
		
		halflast = calc(count / 2) / 2;
		bighalf = Math.pow(0.5 * 0.5 - halflast * halflast, 0.5);
		smallhalf = 0.5 - bighalf;
		edge = Math.pow(smallhalf * smallhalf + halflast * halflast, 0.5);
		
		return edge;
	}
}
