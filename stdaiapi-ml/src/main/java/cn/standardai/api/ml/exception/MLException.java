package cn.standardai.api.ml.exception;

import cn.standardai.api.core.exception.StdaiException;

public class MLException extends StdaiException {

	private static final String moduleName = "机器学习模块";

	private static final long serialVersionUID = 1L;

	public MLException(String msg) {
		super(moduleName + "异常：" + msg);
	}

	public MLException(String msg, Throwable e) {
		super(moduleName + "异常：" + msg, e);
	}
}
