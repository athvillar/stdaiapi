/**
* UsageException.java
* Copyright 2017 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.exception;

/**
 * 使用异常
 * @author 韩晴
 *
 */
public class UsageException extends DnnException {

	private static final long serialVersionUID = 1L;

	public UsageException() {
		super();
	}

	public UsageException(String msg) {
		super(msg);
	}
}
