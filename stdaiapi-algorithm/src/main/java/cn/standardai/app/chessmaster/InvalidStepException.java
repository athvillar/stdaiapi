/**
* InvalidStepException.java
* Copyright 2015 standardai Co.ltd.
*/
package cn.standardai.app.chessmaster;

/**
 * 函数异常类
 * @author 韩晴
 *
 */
public class InvalidStepException extends Exception {

	private static final long serialVersionUID = 1L;

	// 异常类性
	public static enum ERRMSG {ALREADY_HAVE_PIECE, KO_VIOLATION, FORBIDDEN_POINT, NONE_POINTER};

	// 异常Message
	private ERRMSG errmsg;

	public InvalidStepException() {
		super();
	}

	public InvalidStepException(ERRMSG errmsg) {
		this.errmsg = errmsg;
	}

	/**
	 * 获得异常信息
	 * @return
	 * 异常信息
	 */
	public String getMessage() {
		switch (this.errmsg) {
		case ALREADY_HAVE_PIECE:
			return "该处已有子";
		case KO_VIOLATION:
			return "打劫禁着点";
		case FORBIDDEN_POINT:
			return "禁着点";
		case NONE_POINTER:
			return "运行错误";
		default:
			return null;
		}
	}
}
