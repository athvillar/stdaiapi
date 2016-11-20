package cn.standardai.lib.algorithm.cnn;

import java.util.ArrayList;

import com.alibaba.fastjson.JSONArray;

import cn.standardai.lib.base.function.activate.Sigmoid;

public class FCLayer extends ConvLayer {

	private Integer[] target;

	public FCLayer(Integer depth) {
		super(depth, 1, 0);
	}

	@Override
	public void format(Layer prevLayer) throws CnnException {
		this.kernelWidth = prevLayer.width;
		this.kernelHeight = prevLayer.height;
		this.filterFactory = new FilterFactory(prevLayer.width, prevLayer.height);
		this.filters = new ArrayList<Filter>();
		this.activateFunction = new Sigmoid(1);
		super.format(prevLayer);
		this.target = new Integer[this.depth];
	}

	@Override
	public void exec(Double[][][] data) {
		super.exec(data);
	}

	public void setTarget(JSONArray target) {
		for (int i = 0; i < this.depth; i++) {
			this.target[i] = target.getInteger(i);
		}
	}

	@Override
	public void calcError() {
		for (int i = 0; i < this.depth; i++) {
			this.error[0][0][i] = (this.target[i] - this.data[0][0][i]) * this.activateFunction.getDerivativeY(this.data[0][0][i]);
		}
	}
}
