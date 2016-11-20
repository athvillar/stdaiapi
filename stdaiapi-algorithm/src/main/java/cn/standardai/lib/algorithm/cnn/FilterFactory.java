package cn.standardai.lib.algorithm.cnn;

public class FilterFactory extends Layer {

	protected Integer width;

	protected Integer height;

	public FilterFactory(Integer width, Integer height) {
		this.width = width;
		this.height = height;
	}

	public Filter getInstance(Integer depth) {
		return new Filter(this.width, this.height, depth);
	}
}
