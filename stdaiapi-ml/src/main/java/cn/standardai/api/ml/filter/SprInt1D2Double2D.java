package cn.standardai.api.ml.filter;

import cn.standardai.api.ml.exception.FilterException;
import cn.standardai.lib.base.function.Statistic;

public class SprInt1D2Double2D extends DimensionFilter<Integer[], Double[][]> {

	@Override
	public Double[][] encode(Integer[] src) throws FilterException {
		if (src == null) return null;
		Double[][] des = new Double[src.length][];
		for (int i = 0; i < des.length; i++) {
			des[i] = new Double[Integer.parseInt(this.getParam(0).toString())];
			for (int j = 0; j < des[i].length; j++) {
				if (j == src[i]) {
					des[i][j] = 1.0;
				} else {
					des[i][j] = 0.0;
				}
			}
		}
		return des;
	}

	@Override
	public Integer[] decode(Double[][] src) throws FilterException {
		if (src == null) return null;
		Integer[] is = new Integer[src.length];
		for (int i = 0; i < is.length; i++) {
			is[i] = Statistic.maxIndex(src[i]);
		}
		return is;
	}

	@Override
	public String getDescription() {
		return "将Integer数组扩展为2维Double one hot数组。";
	}
}
