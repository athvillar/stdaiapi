package cn.standardai.api.redis.exception;

import cn.standardai.api.core.exception.ParkOSException;

public class RedisException extends ParkOSException {

	private static final String moduleName = "Redis";

	private static final long serialVersionUID = 1L;

	public RedisException(String msg) {
		super(msg);
	}

	public RedisException(String msg, Throwable e) {
		super(msg, e);
	}

	public static String getModulename() {
		return moduleName;
	}
}
