package cn.standardai.api.core.exception;

import cn.standardai.api.core.exception.ParkOSException;

public class SignatureException extends ParkOSException {

	private static final long serialVersionUID = 1L;

	public SignatureException(String msg) {
		super(msg);
	}

	public SignatureException(String msg, Throwable e) {
		super(msg, e);
	}
}
