package cn.standardai.lib.base.function.activate;

import cn.standardai.lib.algorithm.exception.StorageException;
import cn.standardai.lib.base.function.base.DerivableFunction;

public class Tanh extends DerivableFunction {

	public static int BYTES = 1;

	public Tanh() {
		super();
	}

	/**
	 * 计算函数值
	 * @param x
	 * x
	 * @return y
	 */
	public double getY(double x) {
		Double v1 = Math.exp(x);
		Double v2 = Math.exp(-x);
		return (v1 - v2) / (v1 + v2);
	}

	@Override
	public byte getSerial() {
		return 0x04;
	}

	@Override
	public byte[] getBytes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void load(byte[] bytes) throws StorageException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getDerivativeX(double x) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getDerivativeY(double y) {
		// TODO Auto-generated method stub
		return 0;
	}
}
