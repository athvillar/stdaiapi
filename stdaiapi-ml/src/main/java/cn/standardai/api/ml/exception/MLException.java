package cn.standardai.api.ml.exception;

import cn.standardai.api.core.exception.StdaiException;

public class MLException extends StdaiException {

	private static final long serialVersionUID = 1L;

	public MLException(String msg) {
		super(msg);
	}

	public MLException(String msg, Throwable e) {
		super(msg, e);
	}
}
