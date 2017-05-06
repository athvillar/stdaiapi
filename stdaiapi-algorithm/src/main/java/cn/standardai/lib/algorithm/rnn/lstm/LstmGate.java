package cn.standardai.lib.algorithm.rnn.lstm;

public class LstmGate {

	private Double[][] w;

	private Double[] b;

	public LstmGate(Integer r, Integer c) {
		this.w = new Double[r][c];
		this.b = new Double[c];
	}
}
