package cn.standardai.lib.algorithm.cnn;

import java.util.ArrayList;

import com.alibaba.fastjson.JSONArray;

import cn.standardai.lib.base.function.activate.Self;
import cn.standardai.lib.base.function.activate.Sigmoid;

public class FCLayer extends ConvLayer {

	private Integer[] target;

	public FCLayer(Integer depth, Double learningRate) {
		super(depth, 1, 0, learningRate);
	}

	@Override
	public void format(Layer prevLayer) throws CnnException {
		this.kernelWidth = prevLayer.width;
		this.kernelHeight = prevLayer.height;
		this.filterFactory = new FilterFactory(prevLayer.width, prevLayer.height);
		this.filters = new ArrayList<Filter>();
		super.format(prevLayer);
		this.target = new Integer[this.depth];
	}

	@Override
	public void exec(Layer prev) {
		super.exec(prev);
	}

	@Override
	public void calcError() {
		for (int i = 0; i < this.depth; i++) {
			//System.out.print("target:"+this.target[i] + ",data:"+this.data[0][0][i]);
			this.error[0][0][i] = (this.data[0][0][i] - this.target[i]) * this.aF.getDerivativeY(this.data[0][0][i]);
			//System.out.println("error:"+this.error[0][0][i]);
		}
	}

	public void setTarget(JSONArray target) {
		for (int i = 0; i < this.depth; i++) {
			this.target[i] = target.getInteger(i);
		}
	}

	public void setTarget(Integer[] target) {
		for (int i = 0; i < this.depth; i++) {
			this.target[i] = target[i];
		}
	}
}
