package cn.standardai.api.ml.filter;

import cn.standardai.api.ml.exception.FilterException;
import cn.standardai.lib.base.function.Statistic;

public class SprInt2Double1D extends DimensionFilter<Integer, Double[]> {

	@Override
	public Double[] encode(Integer src) throws FilterException {
		if (src == null) return null;
		Double[] des = new Double[Integer.parseInt(this.getParam(0).toString())];
		for (int i = 0; i < des.length; i++) {
			if (i == src) {
				des[i] = 1.0;
			} else {
				des[i] = 0.0;
			}
		}
		return des;
	}

	@Override
	public Integer decode(Double[] src) throws FilterException {
		if (src == null) return null;
		return Statistic.maxIndex(src);
	}

	@Override
	public String getDescription() {
		return "将Integer类型的数字扩展为1维Double one hot数组。";
	}
}
