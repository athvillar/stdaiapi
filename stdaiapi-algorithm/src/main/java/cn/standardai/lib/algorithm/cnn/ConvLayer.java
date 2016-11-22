package cn.standardai.lib.algorithm.cnn;

import java.util.ArrayList;
import java.util.List;

import cn.standardai.lib.base.function.activate.Self;
import cn.standardai.lib.base.function.activate.Sigmoid;

public class ConvLayer extends Layer {

	// TODO all protected
	public final Integer stride;

	public final Integer padding;

	public Integer kernelWidth;

	public Integer kernelHeight;

	public FilterFactory filterFactory;

	public List<Filter> filters;

	public Double η;

	public ConvLayer(Integer depth, Integer stride, Integer padding, Double learningRate) {
		this.depth = depth;
		this.stride = stride;
		this.padding = padding;
		this.η = learningRate;
		this.filters = new ArrayList<Filter>();
	}

	public ConvLayer(Integer depth, Integer stride, Integer padding, Integer kernelWidth, Integer kernelHeight, Double learningRate) {
		this.depth = depth;
		this.stride = stride;
		this.padding = padding;
		this.kernelWidth = kernelWidth;
		this.kernelHeight = kernelHeight;
		this.η = learningRate;
		this.filterFactory = new FilterFactory(kernelWidth, kernelHeight);
		this.filters = new ArrayList<Filter>();
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
			Filter filter = filterFactory.getInstance(prevLayer.depth, (this instanceof FCLayer) ? 1 : 1);
			this.filters.add(filter);
		}
		this.data = new Double[this.width][this.height][this.depth];
		this.error = new Double[this.width][this.height][this.depth];
		this.initError();
	}

	@Override
	public void exec(Layer prev) {
		// http://cs231n.github.io/assets/conv-demo/index.html
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				for (int k = 0; k < this.depth; k++) {
					Double sum = 0.0;
					for (int i2 = 0; i2 < this.kernelWidth; i2++) {
						for (int j2 = 0; j2 < this.kernelHeight; j2++) {
							for (int k2 = 0; k2 < this.filters.get(k).depth; k2++) {
								if (i * this.stride + i2 < this.padding) {}
								else if (i * this.stride + i2 >= this.padding + prev.width) {}
								else if (j * this.stride + j2 < this.padding) {}
								else if (j * this.stride + j2 >= this.padding + prev.height) {}
								else {
									//if (this instanceof FCLayer) {
									//	if (k == 0 && i2 == 0 && j2 == 0 && k2 == 0) {
									//		System.out.println("FILTER:"+this.filters.get(k).w[i2][j2][k2]);
									//	}
									//}
									//System.out.print("i="+i+",j="+j+",k="+k+",i2="+i2+",j2="+j2+",k2="+k2+":");
									//System.out.print("data[" + (i * this.stride + i2 - this.padding) + "][" + (j * this.stride + j2 - this.padding) + "][" + k2 + "]=" + data[i * this.stride + i2 - this.padding][j * this.stride + j2 - this.padding][k2]);
									//System.out.println("*" + this.filters.get(k).w[i2][j2][k2]);
									sum += prev.data[i * this.stride + i2 - this.padding][j * this.stride + j2 - this.padding][k2] * this.filters.get(k).w[i2][j2][k2];
								}
							}
						}
					}
					sum += this.filters.get(k).b;
					this.data[i][j][k] = this.aF.getY(sum);
					//if (this instanceof FCLayer) {
					//	System.out.print("sum=" + sum);
					//	System.out.println(",this.data[i][j][k]=" + this.data[i][j][k]);
					//}
				}
			}
		}
	}

	@Override
	public void calcPrevError(Layer prev) {
		super.calcPrevError(prev);
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				for (int k = 0; k < this.depth; k++) {
					for (int  i2 = 0; i2 < this.kernelWidth; i2++) {
						for (int j2 = 0; j2 < this.kernelHeight; j2++) {
							for (int k2 = 0; k2 < this.filters.get(k).depth; k2++) {
								if (i * this.stride + i2 < this.padding) {}
								else if (i * this.stride + i2 >= this.padding + prev.width) {}
								else if (j * this.stride + j2 < this.padding) {}
								else if (j * this.stride + j2 >= this.padding + prev.height) {}
								else {
									//prev.error[i * this.stride + i2 - this.padding][j * this.stride + j2 - this.padding][k2] +=
									//		prev.data[i * this.stride + i2 - this.padding][j * this.stride + j2 - this.padding][k2] * this.error[i][j][k] *
									//		this.aF.getDerivativeY(this.data[i][j][k]);
									prev.error[i * this.stride + i2 - this.padding][j * this.stride + j2 - this.padding][k2] +=
											this.filters.get(k).w[i2][j2][k2] * this.error[i][j][k] *
											prev.aF.getDerivativeY(prev.data[i * this.stride + i2 - this.padding][j * this.stride + j2 - this.padding][k2]);
											//prev.error[i * this.stride + i2 - this.padding][j * this.stride + j2 - this.padding][k2] +=
											//		this.filters.get(k).w[i2][j2][k2] * this.error[i][j][k];
									//System.out.print("error["+(i * this.stride + i2 - this.padding)+"]["+(j * this.stride + j2 - this.padding)+"]["+k2+"]=" +
									//		prev.error[i * this.stride + i2 - this.padding][j * this.stride + j2 - this.padding][k2]);
									//System.out.print("i="+i+",j="+j+",k="+k+",i2="+i2+",j2="+j2+",k2="+k2+":");
									//System.out.println(this.data[i][j][k] + "*" + this.error[i][j][k]+"*"+this.filters.get(k).w[i2][j2][k2]+"="+prev.error[i * this.stride + i2 - this.padding][j * this.stride + j2 - this.padding][k2]);
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void upgrade(Layer prev, int batchNum) {
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				for (int k = 0; k < this.depth; k++) {
					for (int  i2 = 0; i2 < this.kernelWidth; i2++) {
						for (int j2 = 0; j2 < this.kernelHeight; j2++) {
							for (int k2 = 0; k2 < this.filters.get(k).depth; k2++) {
								if (i * this.stride + i2 < this.padding) {}
								else if (i * this.stride + i2 >= this.padding + prev.width) {}
								else if (j * this.stride + j2 < this.padding) {}
								else if (j * this.stride + j2 >= this.padding + prev.height) {}
								else {
									//System.out.print("i="+i+",j="+j+",k="+k+",i2="+i2+",j2="+j2+",k2="+k2+":");
									//System.out.print("data[" + (i * next.stride + i2 - next.padding) + "][" + (j * next.stride + j2 - next.padding) + "][" + k2 + "]=" +
									//		prev.data[i * next.stride + i2 - next.padding][j * next.stride + j2 - next.padding][k2]);
									//System.out.println("*" + next.error[i][j][k]);
									//prev.error[i * this.stride + i2 - this.padding][j * this.stride + j2 - this.padding][k2] +=
									//		prev.data[i * this.stride + i2 - this.padding][j * this.stride + j2 - this.padding][k2] * this.error[i][j][k] *
									//		this.aF.getDerivativeY(this.data[i][j][k]);
									this.filters.get(k).w[i2][j2][k2] -= (η * this.error[i][j][k] *
											prev.data[i * this.stride + i2 - this.padding][j * this.stride + j2 - this.padding][k2] / batchNum);
									//if (i == 0 && j == 0 && k == 0) {
									//	System.out.println("UPGRADE:" + this.error[i][j][k] +"*"+prev.data[i * this.stride + i2 - this.padding][j * this.stride + j2 - this.padding][k2]);
									//}
								}
							}
						}
					}
					this.filters.get(k).b -= (η * this.error[i][j][k] / batchNum);
				}
			}
		}
	}

	@Override
	public void printFilter() {
		System.out.println("filters:");
		for (int k = 0; k < this.depth; k++) {
			for (int k2 = 0; k2 < this.filters.get(k).depth; k2++) {
				for (int j2 = 0; j2 < this.kernelHeight; j2++) {
					for (int  i2 = 0; i2 < this.kernelWidth; i2++) {
						System.out.print(this.filters.get(k).w[i2][j2][k2] + "\t|");
					}
					System.out.println();
				}
				System.out.println("---------------------------------------------");
			}
			System.out.println("-------------------------------------------------------------------------------");
		}
	}
}
