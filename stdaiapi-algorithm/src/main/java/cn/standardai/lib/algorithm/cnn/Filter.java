package cn.standardai.lib.algorithm.cnn;

public class Filter {

	protected Integer width;

	protected Integer height;

	protected Integer depth;

	protected Double w[][][];

	protected Double b;

	public Filter(Integer width, Integer height, Integer depth) {
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.w = new Double[width][height][depth];
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				for (int k = 0; k < this.depth; k++) {
					this.w[i][j][k] = Math.random();
				}
			}
		}
		this.b = Math.random();
	}

	public Integer conv(Integer[][][] data, Integer padding) {
		// TODO Auto-generated method stub
		return null;
	}
}
