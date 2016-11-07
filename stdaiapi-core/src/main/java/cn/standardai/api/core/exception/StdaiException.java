package cn.standardai.api.core.exception;

public class StdaiException extends Exception {

	public String moduleName;

	private static final long serialVersionUID = 1L;

	public StdaiException() {
		super();
	}

	public StdaiException(String msg) {
		super(msg);
		//LogUtil.error(getModuleName() + "模块异常：" + msg);
	}

	public StdaiException(Throwable e) {
		super(e);
		//LogUtil.error(e);
	}

	public StdaiException(String msg, Throwable e) {
		super(msg, e);
		//LogUtil.error(getModuleName() + "模块异常：" + msg, e);
	}
}
