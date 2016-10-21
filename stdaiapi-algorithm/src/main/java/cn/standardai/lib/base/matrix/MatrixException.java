/**
* MatrixException.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.base.matrix;

/**
 * 矩阵异常类
 * @author 韩晴
 *
 */
public class MatrixException extends Exception {

	private static final long serialVersionUID = 1L;

	// 异常类性
	public static enum ERRMSG {NULL_ELEMENT, LENTH_DISMATCH, ZERO_DEVIDE};

	// 异常Message
	private ERRMSG errmsg;

	public MatrixException() {
		super();
	}

	public MatrixException(ERRMSG errmsg) {
		this.errmsg = errmsg;
	}

	/**
	 * 获得异常信息
	 * @return
	 * 异常信息
	 */
	public String getMessage() {
		switch (this.errmsg) {
		case NULL_ELEMENT:
			return "矩阵未初始化";
		case LENTH_DISMATCH:
			return "矩阵长度不匹配";
		case ZERO_DEVIDE:
			return "零除错误";
		default:
			return null;
		}
	}

}
