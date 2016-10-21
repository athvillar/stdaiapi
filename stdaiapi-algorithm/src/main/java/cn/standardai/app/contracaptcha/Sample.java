/**
* Sample.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.app.contracaptcha;

/**
 * 训练数据
 * @author 韩晴
 *
 */
public class Sample {

	// 输入
	private double[][] input;

	// 期望输出
	private double[][] expectation;

	public double[][] getInput() {
		return input;
	}

	public void setInput(double[][] input) {
		this.input = input;
	}

	public double[][] getExpectation() {
		return expectation;
	}

	public void setExpectation(double[][] expectation) {
		this.expectation = expectation;
	}

}
