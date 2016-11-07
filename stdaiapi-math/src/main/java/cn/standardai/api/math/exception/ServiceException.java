package cn.standardai.api.math.exception;

import cn.standardai.api.core.exception.StdaiException;

public class ServiceException extends StdaiException {

	private static final String moduleName = "服务模块";

	private static final long serialVersionUID = 1L;

	public ServiceException(String msg) {
		super(moduleName + "异常：" + msg);
	}

	public ServiceException(String msg, Throwable e) {
		super(moduleName + "异常：" + msg, e);
	}
}
