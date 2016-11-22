package cn.standardai.lib.algorithm.cnn;

public class FilterFactory {

	protected Integer width;

	protected Integer height;

	public FilterFactory(Integer width, Integer height) {
		this.width = width;
		this.height = height;
	}

	public Filter getInstance(Integer depth, Integer divider) {
		return new Filter(this.width, this.height, depth, divider);
	}
}
