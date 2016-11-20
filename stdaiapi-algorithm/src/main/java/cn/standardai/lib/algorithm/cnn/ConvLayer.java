package cn.standardai.lib.algorithm.cnn;

import java.util.ArrayList;
import java.util.List;

import cn.standardai.lib.base.function.activate.Sigmoid;

public class ConvLayer extends Layer {

	protected final Integer stride;

	protected final Integer padding;

	protected Integer kernelWidth;

	protected Integer kernelHeight;

	protected FilterFactory filterFactory;

	protected List<Filter> filters;

	public ConvLayer(Integer depth, Integer stride, Integer padding) {
		this.depth = depth;
		this.stride = stride;
		this.padding = padding;
		this.filters = new ArrayList<Filter>();
		this.activateFunction = new Sigmoid(1);
	}

	public ConvLayer(Integer depth, Integer stride, Integer padding, Integer kernelWidth, Integer kernelHeight) {
		this.depth = depth;
		this.stride = stride;
		this.padding = padding;
		this.kernelWidth = kernelWidth;
		this.kernelHeight = kernelHeight;
		this.filterFactory = new FilterFactory(kernelWidth, kernelHeight);
		this.filters = new ArrayList<Filter>();
		this.activateFunction = new Sigmoid(1);
	}

	@Override
	public void format(Layer prevLayer) throws CnnException {
		if ((prevLayer.width - this.filterFactory.width + 2 * this.padding) % this.stride != 0)
			throw new CnnException("CONV层初始化异常");
		if ((prevLayer.height - this.filterFactory.height + 2 * this.padding) % this.stride != 0)
			throw new CnnException("CONV层初始化异常");
		this.width = (prevLayer.width - this.filterFactory.width + 2 * this.padding) / this.stride + 1;
		this.height = (prevLayer.height - this.filterFactory.height + 2 * this.padding) / this.stride + 1;
		for (int i = 0; i < this.depth; i++) {
			Filter filter = filterFactory.getInstance(prevLayer.depth);
			this.filters.add(filter);
		}
		this.data = new Double[this.width][this.height][this.depth];
		this.error = new Double[this.width][this.height][this.depth];
	}

	@Override
	public void exec(Double[][][] data) {
		// http://cs231n.github.io/assets/conv-demo/index.html
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				for (int k = 0; k < this.depth; k++) {
					Double sum = 0.0;
					for (int i2 = 0; i2 < this.kernelWidth; i2++) {
						for (int j2 = 0; j2 < this.kernelHeight; j2++) {
							for (int k2 = 0; k2 < this.filters.get(k).depth; k2++) {
								if (i * this.stride + i2 < this.padding) {}
								else if (i * this.stride + i2 >= this.padding + this.width) {}
								else if (j * this.stride + j2 < this.padding) {}
								else if (j * this.stride + j2 >= this.padding + this.height) {}
								else {
									//System.out.print("i="+i+",j="+j+",k="+k+",i2="+i2+",j2="+j2+",k2="+k2+":");
									//System.out.print("data[" + (i * this.stride + i2 - this.padding) + "][" + (j * this.stride + j2 - this.padding) + "][" + k2 + "]=" + data[i * this.stride + i2 - this.padding][j * this.stride + j2 - this.padding][k2]);
									//System.out.println("*" + this.filters.get(k).w[i2][j2][k2]);
									sum += data[i * this.stride + i2 - this.padding][j * this.stride + j2 - this.padding][k2] * this.filters.get(k).w[i2][j2][k2];
								}
							}
						}
					}
					sum += this.filters.get(k).b;
					this.data[i][j][k] = sum;
				}
			}
		}
	}

	@Override
	public void calcError(Layer nextLayer) {
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				for (int k = 0; k < this.depth; k++) {
					this.error[i][j][k] = 0.0;
				}
			}
		}
		if (nextLayer instanceof PoolLayer) {
			PoolLayer next = (PoolLayer) nextLayer;
			switch (next.method) {
			case "max":
				for (int i = 0; i < nextLayer.width; i++) {
					for (int j = 0; j < nextLayer.height; j++) {
						for (int k = 0; k < nextLayer.depth; k++) {
							for (int  i2 = 0; i2 < next.spatial; i2++) {
								for (int j2 = 0; j2 < next.spatial; j2++) {
									if (this.data[i * next.stride + i2][j * next.stride + j2][k] == nextLayer.data[i][j][k]) {
										this.error[i * next.stride + i2][j * next.stride + j2][k] += 1.0;
									} else {
										this.error[i * next.stride + i2][j * next.stride + j2][k] += 0.0;
									}
								}
							}
						}
					}
				}
				break;
			case "avg":
				for (int i = 0; i < nextLayer.width; i++) {
					for (int j = 0; j < nextLayer.height; j++) {
						for (int k = 0; k < nextLayer.depth; k++) {
							for (int  i2 = 0; i2 < next.spatial; i2++) {
								for (int j2 = 0; j2 < next.spatial; j2++) {
									this.error[i2 * next.stride + i2][j2 * next.stride + j2][k] +=
											nextLayer.error[i][j][k] / next.spatial / next.spatial;
								}
							}
							for (int l = 0; l < next.spatial; l++) {
							}
						}
					}
				}
				break;
			}
		}
	}
}
