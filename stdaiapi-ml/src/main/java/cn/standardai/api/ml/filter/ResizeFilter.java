package cn.standardai.api.ml.filter;

import cn.standardai.api.ml.exception.FilterException;
import cn.standardai.lib.base.matrix.MatrixUtil;

public class ResizeFilter extends DimensionFilter<Integer[][], Integer[][]> {

	public static void main(String[] args) throws FilterException {
		Integer[][] a = new Integer[][] {{1,2,3,4,5},{3,4,5,6,7},{5,6,7,8,9},{3,4,5,6,7},{5,6,7,8,9}};
		ResizeFilter r = new ResizeFilter();
		r.setParam(0, "2");
		r.setParam(1, "2");
		Integer[][] b = r.encode(a);
		MatrixUtil.print(b);
	}

	@Override
	public Integer[][] encode(Integer[][] src) throws FilterException {

		if (src == null) return null;
		Integer width = Integer.parseInt(params.get(0));
		Integer height = Integer.parseInt(params.get(1));
		Integer[][] dst = new Integer[width][height];

		for (int i = 0; i < dst.length; i++) {
			for (int j = 0; j < dst[i].length; j++) {
				Integer srcX = null;
				Integer srcY = null;
				if (width > src.length) {
					if (i >= (width - src.length) / 2 && (i < width - (width - src.length - 1) / 2 - 1)) {
						srcX = i - (width - src.length) / 2;
					}
				} else {
					srcX = i + (src.length - width) / 2;
				}
				if (height > src[0].length) {
					if (j >= (height - src[0].length) / 2 && (j < height - (height - src[0].length - 1) / 2 - 1)) {
						srcY = j - (height - src[0].length) / 2;
					}
				} else {
					srcY = j + (src[0].length - height) / 2;
				}
				if (srcX == null || srcY == null) {
					dst[i][j] = 0;
				} else {
					dst[i][j] = src[srcX][srcY];
				}
			}
		}

		return dst;
	}

	@Override
	public Integer[][] decode(Integer[][] src) throws FilterException {
		return null;
	}

	@Override
	public String getDescription() {
		return "Resize数组大小。";
	}
}
