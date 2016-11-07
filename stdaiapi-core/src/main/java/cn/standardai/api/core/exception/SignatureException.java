package cn.standardai.api.core.exception;

import cn.standardai.api.core.exception.StdaiException;

public class SignatureException extends StdaiException {

	private static final long serialVersionUID = 1L;

	public SignatureException(String msg) {
		super(msg);
	}

	public SignatureException(String msg, Throwable e) {
		super(msg, e);
	}
}
