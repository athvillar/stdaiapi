package cn.standardai.api.es.exception;

import cn.standardai.api.core.exception.ParkOSException;

public class ESException extends ParkOSException {

	private static final String moduleName = "ElasticSearch";

	private static final long serialVersionUID = 1L;

	public ESException(String msg) {
		super(msg);
	}

	public ESException(String msg, Throwable e) {
		super(msg, e);
	}

	public static String getModulename() {
		return moduleName;
	}
}
