package cn.standardai.lib.algorithm.cnn;

import cn.standardai.lib.base.function.base.DerivableFunction;

public class Layer {

	protected Integer width;

	protected Integer height;

	protected Integer depth;

	public enum LayerType { input, conv, relu, pool, fc };

	protected Double[][][] data;

	protected Double[][][] error;

	protected DerivableFunction activateFunction;

	public static LayerType parseType(String type) {
		if (type == null) return null;
		switch (type) {
		case "input":
		case "INPUT":
			return LayerType.input;
		case "conv":
		case "CONV":
			return LayerType.conv;
		case "relu":
		case "RELU":
			return LayerType.relu;
		case "pool":
		case "POOL":
			return LayerType.pool;
		case "fc":
		case "FC":
			return LayerType.fc;
		default:
			return null;
		}
	}

	public void exec(Double[][][] data) {
		return;
	}

	public void format(Layer prevLayer) throws CnnException {
		return;
	}

	public void calcError() {
		return;
	}

	public void calcError(Layer nextLayer) {
		return;
	}
}
