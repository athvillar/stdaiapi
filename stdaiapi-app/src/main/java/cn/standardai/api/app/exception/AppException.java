package cn.standardai.api.app.exception;

import cn.standardai.api.core.exception.StdaiException;

public class AppException extends StdaiException {

	private static final long serialVersionUID = 1L;

	public AppException(String msg) {
		super(msg);
	}

	public AppException(String msg, Throwable e) {
		super(msg, e);
	}
}
