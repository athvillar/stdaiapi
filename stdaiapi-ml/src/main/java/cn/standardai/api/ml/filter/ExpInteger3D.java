package cn.standardai.api.ml.filter;

import cn.standardai.api.ml.exception.FilterException;

public class ExpInteger3D extends DimensionFilter<Integer[][], Integer[][][]> {

	@Override
	public Integer[][][] encode(Integer[][] src) throws FilterException {
		if (src == null) return null;
		Integer[][][] des = new Integer[src.length][][];
		for (int i = 0; i < des.length; i++) {
			des[i] = new Integer[src[i].length][];
			for (int j = 0; j < des[i].length; j++) {
				des[i][j] = new Integer[] { src[i][j] };
			}
		}
		return des;
	}

	@Override
	public Integer[][] decode(Integer[][][] src) throws FilterException {
		if (src == null || src.length < 1) return null;
		return src[0];
	}

	@Override
	public String getDescription() {
		return "将2维Integer数组扩展为3维Integer数组，数组长度为1。";
	}
}
