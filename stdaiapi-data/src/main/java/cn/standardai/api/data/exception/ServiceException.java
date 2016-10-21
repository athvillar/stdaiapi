package cn.standardai.api.data.exception;

import cn.standardai.api.core.exception.ParkOSException;

public class ServiceException extends ParkOSException {

	private static final String moduleName = "服务模块";

	private static final long serialVersionUID = 1L;

	public ServiceException(String msg) {
		super(moduleName + "异常：" + msg);
	}

	public ServiceException(String msg, Throwable e) {
		super(moduleName + "异常：" + msg, e);
	}
}
