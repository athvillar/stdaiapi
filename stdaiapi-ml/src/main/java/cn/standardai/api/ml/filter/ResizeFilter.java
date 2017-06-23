package cn.standardai.api.ml.filter;

import cn.standardai.api.ml.exception.FilterException;

public class ResizeFilter extends DimensionFilter<Integer[][], Integer[][]> {

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
					dst[i][j] = 255;
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
