package cn.standardai.api.ml.exception;

public class FilterException extends MLException {

	private static final long serialVersionUID = 1L;

	public FilterException(String msg) {
		super(msg);
	}

	public FilterException(String msg, Throwable e) {
		super(msg, e);
	}
}
