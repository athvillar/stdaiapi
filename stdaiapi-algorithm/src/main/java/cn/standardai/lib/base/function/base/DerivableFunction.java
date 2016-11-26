/**
* DerivableFunction.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.base.function.base;

import cn.standardai.lib.algorithm.common.Storable;
import cn.standardai.lib.algorithm.exception.StorageException;
import cn.standardai.lib.base.function.activate.Multiply;
import cn.standardai.lib.base.function.activate.Self;
import cn.standardai.lib.base.function.activate.Sigmoid;

/**
 * 可微函数基类
 * @author 韩晴
 *
 */
public abstract class DerivableFunction extends Function implements Storable {

	public DerivableFunction() {
		super();
	}

	/**
	 * 计算函数值
	 * @param x
	 * x
	 * @return y
	 */
	public abstract double getY(double x);

	/**
	 * 计算导数值
	 * @param x
	 * x
	 * @return y'
	 */
	public abstract double getDerivativeX(double x);

	/**
	 * 计算导数值
	 * @param y
	 * y
	 * @return y'
	 */
	public abstract double getDerivativeY(double y);

	public abstract byte getSerial();

	public static DerivableFunction getInstance(byte[] bytes) throws StorageException {

		DerivableFunction f;
		if (bytes == null || bytes.length < 1) throw new StorageException("DerivableFunction load failure");
		switch (bytes[0]) {
		case 0x01:
			return new Self();
		case 0x02:
			if (bytes.length != Multiply.BYTES) throw new StorageException("Multiply load failure");
			f = new Multiply(1);
			f.load(bytes);
			return f;
		case 0x03:
			if (bytes.length != Sigmoid.BYTES) throw new StorageException("Sigmoid load failure");
			f = new Sigmoid(1);
			f.load(bytes);
			return f;
		default:
			return null;
		}
	}
}
