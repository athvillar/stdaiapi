package cn.standardai.api.data.exception;

import cn.standardai.api.core.exception.StdaiException;

public class DataException extends StdaiException {

	private static final String moduleName = "stdaiapi-data";

	private static final long serialVersionUID = 1L;

	public DataException(String msg) {
		super(moduleName + " exception: " + msg);
	}

	public DataException(String msg, Throwable e) {
		super(moduleName + " exception: " + msg, e);
	}
}
