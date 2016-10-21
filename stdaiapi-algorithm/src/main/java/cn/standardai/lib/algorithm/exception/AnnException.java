/**
* AnnException.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.exception;

/**
 * 神经网络异常类
 * @author 韩晴
 *
 */
public class AnnException extends Exception {

	private static final long serialVersionUID = 1L;

	// 异常类性
	public static enum ERRMSG {TRAINING_EXAMPLE_ERR, SYSTEM_ERR};

	// 异常Message
	private ERRMSG errmsg;

	public AnnException() {
		super();
	}

	public AnnException(ERRMSG errmsg) {
		this.errmsg = errmsg;
	}

	/**
	 * 获得异常信息
	 * @return
	 * 异常信息
	 */
	public String getMessage() {
		switch (this.errmsg) {
		case TRAINING_EXAMPLE_ERR:
			return "训练样例错误";
		case SYSTEM_ERR:
			return "系统异常";
		default:
			return null;
		}
	}

}
