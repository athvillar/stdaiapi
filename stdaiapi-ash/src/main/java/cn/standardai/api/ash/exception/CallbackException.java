package cn.standardai.api.ash.exception;

public class CallbackException extends AshException {

	private static final long serialVersionUID = 1L;

	public String display;

	public String url;

	public String[] params;

	public CallbackException(String msg) {
		super(msg);
	}

	public CallbackException(String msg, String display, String url, String[] params) {
		super(msg);
		this.display = display;
		this.url = url;
		this.params = params;
	}
}
