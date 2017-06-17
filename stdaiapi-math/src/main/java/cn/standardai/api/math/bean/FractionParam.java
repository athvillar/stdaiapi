package cn.standardai.api.math.bean;

import cn.standardai.api.core.util.MathUtil;
import cn.standardai.api.math.exception.OperationException;

public class FractionParam extends BinaryParam<Integer> {

	public FractionParam(int max, int min) {
		this.p1 = new Double(Math.random() * (max - min)).intValue() + min;
		this.p2 = new Double(Math.random() * (max - min)).intValue() + min;
		if (this.p2 == 0) this.p2 = 1;
		reduce();
	}

	@Override
	public void plus(BinaryParam<Integer> p) {
		this.p1 = this.p1 * p.p2 + this.p2 * p.p1;
		this.p2 = this.p2 * p.p2;
		reduce();
	}

	@Override
	public void minus(BinaryParam<Integer> p) {
		this.p1 = this.p1 * p.p2 - this.p2 * p.p1;
		this.p2 = this.p2 * p.p2;
		reduce();
	}

	@Override
	public void multiply(BinaryParam<Integer> p) {
		this.p1 = this.p1 * p.p1;
		this.p2 = this.p2 * p.p2;
		reduce();
	}

	@Override
	public void devide(BinaryParam<Integer> p) throws OperationException {
		if (p.p1 == 0) throw new OperationException("0除错误");
		this.p1 = this.p1 * p.p2;
		this.p2 = this.p2 * p.p1;
		reduce();
	}

	public void reduce() {
		Integer gcd = MathUtil.getGreatestCommonDivisor(this.p1, this.p2);
		if (gcd == null) {
			return;
		}
		this.p1 /= gcd;
		this.p2 /= gcd;
	}

	@Override
	public String toString() {
		if (this.p1 == 0) return "0";
		if (this.p2 < 0) {
			this.p1 *= -1;
			this.p2 *= -1;
		}
		if (this.p2 == 1) {
			return "" + this.p1;
		} else {
			return this.p1 + "/" + this.p2;
		}
	}

	@Override
	public boolean negative() {
		return (this.p1 * this.p2 < 0);
	}
}
