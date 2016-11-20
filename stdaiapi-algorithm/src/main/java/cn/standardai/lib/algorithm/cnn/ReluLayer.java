package cn.standardai.lib.algorithm.cnn;

import cn.standardai.lib.base.function.activate.Self;

public class ReluLayer extends Layer {

	private String function;

	public ReluLayer(String function) {
		this.function = function;
		this.activateFunction = new Self();
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
	}

	@Override
	public void exec(Double[][][] data) {
		if ("max".equals(this.function)) {
			for (int i = 0; i < this.width; i++) {
				for (int j = 0; j < this.height; j++) {
					for (int k = 0; k < this.depth; k++) {
						this.data[i][j][k] = data[i][j][k] > 0 ? data[i][j][k] : 0;
					}
				}
			}
		} else {
			this.data = data.clone();
		}
	}
}
