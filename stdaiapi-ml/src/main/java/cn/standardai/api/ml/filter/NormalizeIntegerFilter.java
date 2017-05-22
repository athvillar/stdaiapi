package cn.standardai.api.ml.filter;

import cn.standardai.api.ml.exception.FilterException;
import cn.standardai.lib.base.matrix.MatrixException;
import cn.standardai.lib.base.matrix.MatrixUtil;

public class NormalizeIntegerFilter extends MatrixFilter<Integer[][], Double[][]> {

	@Override
	public Double[][] encode(Integer[][] src) throws FilterException {
		double l2;
		try {
			l2 = MatrixUtil.l2Norm(src);
		} catch (MatrixException e) {
			return null;
		}
		if (src == null) return null;
		Double[][] des = new Double[src.length][];
		for (int i = 0; i < des.length; i++) {
			des[i] = new Double[src[i].length];
			for (int j = 0; j < des.length; j++) {
				des[i][j] = 1.0 * src[i][j] / l2;
			}
		}
		return des;
	}

	@Override
	public Integer[][] decode(Double[][] src) throws FilterException {

		if (src == null) return null;

		double maxSrc = 0.0;
		for (int i = 0; i < src.length; i++) {
			for (int j = 0; j < src.length; j++) {
				if (src[i][j] > maxSrc) maxSrc = src[i][j];
			}
		}
		Integer[][] des = new Integer[src.length][];
		for (int i = 0; i < des.length; i++) {
			des[i] = new Integer[src[i].length];
			for (int j = 0; j < des.length; j++) {
				des[i][j] = new Double(src[i][j] * 255 / maxSrc).intValue();
			}
		}
		return des;
	}

	@Override
	public String getDescription() {
		return "将2维Integer数组归一化为2维Double数组，转换后的值在0～1之间。";
	}
}
