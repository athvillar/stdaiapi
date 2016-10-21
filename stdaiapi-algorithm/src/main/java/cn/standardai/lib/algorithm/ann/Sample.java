/**
* Sample.java
* Copyright 2015 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.ann;

/**
 * BP神经网络 训练样例类
 * @author 韩晴
 *
 */
public class Sample {

	private double[] input;

	private double[] expectation;

	public Sample(double[] input, double[] expectation) {
		super();
		this.input = input;
		this.expectation = expectation;
	}

	public double[] getInput() {
		return input;
	}

	public void setInput(double[] input) {
		this.input = input;
	}

	public double[] getExpectation() {
		return expectation;
	}

	public void setExpectation(double[] expectation) {
		this.expectation = expectation;
	}
}	
