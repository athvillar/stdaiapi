package cn.standardai.lib.base.function.activate;

import cn.standardai.lib.algorithm.common.ByteUtil;
import cn.standardai.lib.algorithm.exception.StorageException;
import cn.standardai.lib.base.function.base.DerivableFunction;

public class Multiply extends DerivableFunction {

	public static int BYTES = Double.BYTES + 1;

	public double k = 1;

	public Multiply() {
		super();
		this.k = 1;
	}

	public Multiply(double k) {
		super();
		this.k = k;
	}

	/**
	 * 计算函数值
	 * @param x
	 * x
	 * @return y
	 */
	public double getY(double x) {
		// y = kx
		return k * x;
	}

	/**
	 * 用x计算导数值
	 * @param x
	 * x
	 * @return y'
	 */
	public double getDerivativeX(double x) {
		return k;
	}

	/**
	 * 用y计算导数值
	 * @param y
	 * y
	 * @return y'
	 */
	public double getDerivativeY(double y) {
		// y' = k
		return k;
	}

	@Override
	public byte getSerial() {
		return 0x02;
	}

	@Override
	public byte[] getBytes() {
		byte[] bytes = new byte[Double.BYTES + 1];
		bytes[0] = getSerial();
		ByteUtil.putDouble(bytes, this.k, 1);
		return bytes;
	}

	@Override
	public void load(byte[] bytes) throws StorageException {
		this.k = ByteUtil.getInt(bytes, 1);
	}
}
