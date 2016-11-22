package cn.standardai.lib.algorithm.cnn;

public class ReluLayer extends Layer {

	private String function;

	public ReluLayer(String function) {
		this.function = function;
	}

	@Override
	public void format(Layer prevLayer) throws CnnException {
		if (this.function == null) throw new CnnException("RELU层初始化异常");
		switch (this.function) {
		case "max":
			break;
		default:
			throw new CnnException("RELU层初始化异常");
		}
		this.width = prevLayer.width;
		this.height = prevLayer.height;
		this.depth = prevLayer.depth;
		this.data = new Double[this.width][this.height][this.depth];
		this.error = new Double[this.width][this.height][this.depth];
		this.initError();
	}

	@Override
	public void exec(Layer prev) {
		if ("max".equals(this.function)) {
			for (int i = 0; i < this.width; i++) {
				for (int j = 0; j < this.height; j++) {
					for (int k = 0; k < this.depth; k++) {
						this.data[i][j][k] = prev.data[i][j][k] > 0 ? prev.data[i][j][k] : 0;
					}
				}
			}
		} else {
			this.data = prev.data.clone();
		}
	}

	@Override
	public void calcPrevError(Layer prev) {
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				for (int k = 0; k < this.depth; k++) {
					prev.error[i][j][k] = this.data[i][j][k] > 0 ? this.error[i][j][k] : 0;
				}
			}
		}
	}
}
