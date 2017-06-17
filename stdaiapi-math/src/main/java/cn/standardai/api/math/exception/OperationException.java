package cn.standardai.api.math.exception;

import cn.standardai.api.core.exception.StdaiException;

public class OperationException extends StdaiException {

	private static final long serialVersionUID = 1L;

	public OperationException(String msg) {
		super(msg);
	}

	public OperationException(String msg, Throwable e) {
		super(msg, e);
	}
}
