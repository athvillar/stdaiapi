package cn.standardai.lib.algorithm.rnn.lstm;

import cn.standardai.lib.algorithm.base.DnnData;

public class LstmData extends DnnData {

	public Double[][] x;

	public Double[][] y;

	public Integer[] y1;

	private int yLength;

	public LstmData(Double[][] x, Double[][] y) {
		this.x = x;
		this.y = y;
		this.yLength = y.length;
	}

	public LstmData(Double[][] x, Integer[] y1) {
		this.x = x;
		this.y1 = y1;
		this.yLength = y1.length;
	}

	public int getYLength() {
		return yLength;
	}
}
