package cn.standardai.api.biz.exception;

import cn.standardai.api.core.exception.StdaiException;

public class BizException extends StdaiException {

	private static final String moduleName = "stdaiapi-biz";

	private static final long serialVersionUID = 1L;

	public BizException(String msg) {
		super(moduleName + " exception: " + msg);
	}

	public BizException(String msg, Throwable e) {
		super(moduleName + " exception: " + msg, e);
	}
}
