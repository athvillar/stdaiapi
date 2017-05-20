package cn.standardai.api.ml.exception;

public class DataException extends MLException {

	private static final long serialVersionUID = 1L;

	public DataException(String msg) {
		super(msg);
	}

	public DataException(String msg, Throwable e) {
		super(msg, e);
	}
}
