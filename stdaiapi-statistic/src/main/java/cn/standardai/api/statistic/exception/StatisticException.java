package cn.standardai.api.statistic.exception;

import cn.standardai.api.core.exception.StdaiException;

public class StatisticException extends StdaiException {

	private static final long serialVersionUID = 1L;

	public StatisticException(String msg) {
		super(msg);
	}

	public StatisticException(String msg, Throwable e) {
		super(msg, e);
	}
}
