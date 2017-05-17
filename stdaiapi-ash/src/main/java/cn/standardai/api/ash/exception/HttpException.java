package cn.standardai.api.ash.exception;

public class HttpException extends AshException {

	private static final long serialVersionUID = 1L;

	public HttpException(String msg) {
		super(msg);
	}

	public HttpException(String msg, Throwable e) {
		super(msg, e);
	}
}
