/**
* Sigmoid.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.base.function.activate;

import cn.standardai.lib.algorithm.common.ByteUtil;
import cn.standardai.lib.algorithm.exception.StorageException;
import cn.standardai.lib.base.function.base.DerivableFunction;

/**
 * Sigmoid函数
 * @author 韩晴
 *
 */
public class Sigmoid extends DerivableFunction {

	public static int BYTES = Double.BYTES + 1;

	public double k = 1;

	public Sigmoid(double k) {
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
		// y = 1 / (1 + e ^ (-kx))
		return 1 / (1 + Math.exp(-k * x));
	}

	/**
	 * 用x计算导数值
	 * @param x
	 * x
	 * @return y'
	 */
	public double getDerivativeX(double x) {
		// 由于getDerivativeY的存在，可以简化求getDerivativeX 
		return getDerivativeY(getY(x));
	}

	/**
	 * 用y计算导数值
	 * @param y
	 * y
	 * @return y'
	 */
	public double getDerivativeY(double y) {
		// y' = k * y * (1 - y)
		return k * y * (1 - y);
	}

	@Override
	public byte getSerial() {
		return 0x03;
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
