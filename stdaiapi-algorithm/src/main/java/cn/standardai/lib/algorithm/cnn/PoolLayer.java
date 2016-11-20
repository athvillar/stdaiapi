package cn.standardai.lib.algorithm.cnn;

import cn.standardai.lib.base.function.activate.Self;

public class PoolLayer extends Layer {

	protected final String method;

	protected final Integer spatial;

	protected final Integer stride;

	public PoolLayer(String method, Integer spatial, Integer stride) {
		this.method = method;
		this.spatial = spatial;
		this.stride = stride;
		this.activateFunction = new Self();
	}

	@Override
	public void format(Layer prevLayer) throws CnnException {
		if (this.method == null) throw new CnnException("POOL层初始化异常");
		switch (this.method) {
		case "max":
		case "avg":
			break;
		default:
			throw new CnnException("POOL层初始化异常");
		}
		if ((prevLayer.width - this.spatial) % this.stride != 0)
			throw new CnnException("Pool层初始化异常");
		this.width = (prevLayer.width - this.spatial) / this.stride + 1;
		this.height = (prevLayer.height - this.spatial) / this.stride + 1;
		this.depth = prevLayer.depth;
		this.data = new Double[this.width][this.height][this.depth];
		this.error = new Double[this.width][this.height][this.depth];
	}

	@Override
	public void exec(Double[][][] data) {
		if ("max".equals(this.method)) {
			for (int i = 0; i < this.width; i++) {
				for (int j = 0; j < this.height; j++) {
					for (int k = 0; k < this.depth; k++) {
						this.data[i][j][k] = Double.MIN_VALUE;
						for (int  i2= 0; i2 < this.spatial; i2++) {
							for (int j2 = 0; j2 < this.spatial; j2++) {
								if (data[i * this.stride + i2][j * this.stride + j2][k] > this.data[i][j][k])
									this.data[i][j][k] = data[i * this.stride + i2][j * this.stride + j2][k];
							}
						}
					}
				}
			}
		} else if ("avg".equals(this.method)) {
			for (int i = 0; i < this.width; i++) {
				for (int j = 0; j < this.height; j++) {
					for (int k = 0; k < this.depth; k++) {
						Double sum = 0.0;
						for (int  i2= 0; i2 < this.spatial; i2++) {
							for (int j2 = 0; j2 < this.spatial; j2++) {
								sum += data[i * this.stride + i2][j * this.stride + j2][k];
							}
						}
						this.data[i][j][k] = sum / this.spatial / this.spatial;
					}
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
		if (nextLayer instanceof ConvLayer) {
			ConvLayer next = (ConvLayer)nextLayer;
			for (int i = 0; i < next.width; i++) {
				for (int j = 0; j < next.height; j++) {
					for (int k = 0; k < next.depth; k++) {
						for (int  i2 = 0; i2 < next.kernelWidth; i2++) {
							for (int j2 = 0; j2 < next.kernelHeight; j2++) {
								for (int k2 = 0; k2 < next.filters.get(k).depth; k2++) {
									if (i * next.stride + i2 < next.padding) {}
									else if (i * next.stride + i2 >= next.padding + next.width) {}
									else if (j * next.stride + j2 < next.padding) {}
									else if (j * next.stride + j2 >= next.padding + next.height) {}
									else {
										//System.out.print("i="+i+",j="+j+",k="+k+",i2="+i2+",j2="+j2+",k2="+k2+":");
										//System.out.print("data[" + (i * next.stride + i2 - next.padding) + "][" + (j * next.stride + j2 - next.padding) + "][" + k2 + "]=" +
										//		this.data[i * this.stride + i2 - next.padding][j * this.stride + j2 - next.padding][k2]);
										//System.out.println("*" + next.error[i][j][k]);
										this.error[i * next.stride + i2 - next.padding][j * next.stride + j2 - next.padding][k2] +=
												this.data[i * next.stride + i2 - next.padding][j * next.stride + j2 - next.padding][k2] * next.error[i][j][k] *
												next.activateFunction.getDerivativeY(next.data[i][j][k]);
									}
								}
							}
						}	
					}
				}
			}
		}
	}
}
