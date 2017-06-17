package cn.standardai.api.math.bean;

import cn.standardai.api.math.exception.OperationException;

public class IntegerParam extends UnaryParam<Integer> {

	public IntegerParam(int max, int min) {
		this.p = new Double(Math.random() * (max - min)).intValue() + min;
	}

	@Override
	public void plus(UnaryParam<Integer> p) {
		this.p = this.p + p.p;
	}

	@Override
	public void minus(UnaryParam<Integer> p) {
		this.p = this.p - p.p;
	}

	@Override
	public void multiply(UnaryParam<Integer> p) {
		this.p = this.p * p.p;
	}

	@Override
	public void devide(UnaryParam<Integer> p) throws OperationException {
		if (p.p == 0) throw new OperationException("0除错误");
		this.p = this.p / p.p;
	}

	@Override
	public String toString() {
		return this.p.toString();
	}

	@Override
	public boolean negative() {
		return this.p < 0;
	}
}
