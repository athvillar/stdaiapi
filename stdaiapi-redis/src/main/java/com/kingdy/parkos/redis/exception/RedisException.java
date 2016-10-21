package com.kingdy.parkos.redis.exception;

import com.kingdy.parkos.core.exception.ParkOSException;

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
