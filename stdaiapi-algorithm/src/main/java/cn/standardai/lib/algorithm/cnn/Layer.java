package cn.standardai.lib.algorithm.cnn;

import cn.standardai.lib.base.function.activate.Self;
import cn.standardai.lib.base.function.activate.Sigmoid;
import cn.standardai.lib.base.function.base.DerivableFunction;

public class Layer {

	public enum LayerType { input, conv, relu, pool, fc };

	// TODO all public
	public Integer width;

	public Integer height;

	public Integer depth;

	public Double[][][] data;

	public Double[][][] error;

	public DerivableFunction aF;

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

	public void exec(Layer prevLayer) {
		return;
	}

	public void format(Layer prevLayer) throws CnnException {
		return;
	}

	public void calcError() {
		return;
	}

	public void calcPrevError(Layer prev) {
		//prev.initError();
		return;
	}

	public void initError() {
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				for (int k = 0; k < this.depth; k++) {
					this.error[i][j][k] = 0.0;
				}
			}
		}
		return;
	}

	public void print() {
		System.out.println(this.getClass());
		printData();
		printError();
		printFilter();
	}

	public void printData() {
		System.out.println(this.getClass());
		System.out.println("data:");
		for (int k = 0; k < this.depth; k++) {
			for (int j = 0; j < this.height; j++) {
				for (int i = 0; i < this.width; i++) {
					System.out.print(data[i][j][k] + "\t|");
				}
				System.out.println();
			}
			System.out.println("---------------------------------------------");
		}
	}

	public void printError() {
		if (this.error == null) return;
		System.out.println(this.getClass());
		System.out.println("error:");
		for (int k = 0; k < this.depth; k++) {
			for (int j = 0; j < this.height; j++) {
				for (int i = 0; i < this.width; i++) {
					System.out.print(error[i][j][k] + "\t|");
				}
				System.out.println();
			}
			System.out.println("---------------------------------------------");
		}
	}

	public void printFilter() {
		System.out.println(this.getClass());
		System.out.println("filters:none");
		return;
	}

	public void upgrade(Layer prev, int batchNum) {
		return;
	}

	public void setAF(String aF) {
		if (aF == null) {
			this.aF = new Self();
		} else if ("sigmoid".equals(aF)) {
			this.aF = new Sigmoid(1);
		} else {
			this.aF = new Self();
		}
	}
}
