/**
* FunctionException.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.base.function;

/**
 * 函数异常类
 * @author 韩晴
 *
 */
public class FunctionException extends Exception {

	private static final long serialVersionUID = 1L;

	// 异常类性
	public static enum ERRMSG {ILLEGAL_PARAM, ZERO_DEVIDE};

	// 异常Message
	private ERRMSG errmsg;

	public FunctionException() {
		super();
	}

	public FunctionException(ERRMSG errmsg) {
		this.errmsg = errmsg;
	}

	/**
	 * 获得异常信息
	 * @return
	 * 异常信息
	 */
	public String getMessage() {
		switch (this.errmsg) {
		case ILLEGAL_PARAM:
			return "非法参数";
		case ZERO_DEVIDE:
			return "零除错误";
		default:
			return null;
		}
	}
}
