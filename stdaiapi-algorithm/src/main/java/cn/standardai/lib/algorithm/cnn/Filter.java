package cn.standardai.lib.algorithm.cnn;

public class Filter {

	// TODO all public
	public Integer width;

	public Integer height;

	public Integer depth;

	public Double w[][][];

	public Double b;

	public Filter(Integer width, Integer height, Integer depth, Integer divider) {
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.w = new Double[width][height][depth];
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				for (int k = 0; k < this.depth; k++) {
					//this.w[i][j][k] = (Math.random() - 0.5) * 4 / this.width / this.height / this.depth;
					this.w[i][j][k] = (Math.random() - 0.5) * 4 / this.width / this.height / this.depth / divider;
					//this.w[i][j][k] = new Double(i);
				}
			}
		}
		//this.b = 2.0;
		//this.b = (Math.random() - 0.5);
		this.b = (Math.random() - 0.5) / 100;
	}
}
