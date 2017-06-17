package cn.standardai.api.math.bean;

import cn.standardai.api.math.exception.OperationException;

public class DoubleParam extends UnaryParam<Double> {

	public int round;

	public DoubleParam(int max, int min, int round) {
		this.round = round;
		this.p = (0.0 + new Double(Math.random() * (max - min) * Math.pow(10, round)).intValue()) / Math.pow(10, round) + min;
	}

	@Override
	public void plus(UnaryParam<Double> p) {
		this.p = this.p + p.p;
	}

	@Override
	public void minus(UnaryParam<Double> p) {
		this.p = this.p - p.p;
	}

	@Override
	public void multiply(UnaryParam<Double> p) {
		this.p = this.p * p.p;
	}

	@Override
	public void devide(UnaryParam<Double> p) throws OperationException {
		if (p.p == 0.0) throw new OperationException("0除错误");
		this.p = this.p / p.p;
	}

	@Override
	public String toString() {
		return "" + ((double)Math.round(this.p * Math.pow(10, this.round)) / Math.pow(10, this.round));
	}

	@Override
	public boolean negative() {
		return (this.p < 0);
	}
}
