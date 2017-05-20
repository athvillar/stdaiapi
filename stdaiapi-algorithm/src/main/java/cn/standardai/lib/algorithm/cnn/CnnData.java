package cn.standardai.lib.algorithm.cnn;

import cn.standardai.lib.algorithm.base.DnnData;

public class CnnData extends DnnData {

	public Double[][] x;

	public Double[][] y;

	public Integer[] y1;

	private int yLength;

	public CnnData(Double[][] x, Double[][] y) {
		this.x = x;
		this.y = y;
		this.yLength = y.length;
	}

	public CnnData(Double[][] x, Integer[] y1) {
		this.x = x;
		this.y1 = y1;
		this.yLength = y1.length;
	}

	public int getYLength() {
		return yLength;
	}
}
