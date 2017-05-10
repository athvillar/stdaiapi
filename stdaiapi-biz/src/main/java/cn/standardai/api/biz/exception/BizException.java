package cn.standardai.api.biz.exception;

import cn.standardai.api.core.exception.StdaiException;

public class BizException extends StdaiException {

	private static final long serialVersionUID = 1L;

	public BizException(String msg) {
		super(msg);
	}

	public BizException(String msg, Throwable e) {
		super(msg, e);
	}
}
