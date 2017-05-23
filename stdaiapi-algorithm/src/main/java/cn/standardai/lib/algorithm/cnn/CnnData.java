package cn.standardai.lib.algorithm.cnn;

import java.util.ArrayList;
import java.util.List;

import cn.standardai.lib.algorithm.base.DnnData;

public class CnnData extends DnnData {

	public Integer[][][] x;

	public Integer[] y;

	public CnnData(Integer[][][] x, Integer[] y) {
		this.x = x;
		this.y = y;
	}
}
