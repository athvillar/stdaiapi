package cn.standardai.api.ml.exception;

public class JSONFormatException extends MLException {

	private static final long serialVersionUID = 1L;

	public JSONFormatException(String msg) {
		super(msg);
	}

	public JSONFormatException(String msg, Throwable e) {
		super(msg, e);
	}
}
