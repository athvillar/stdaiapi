package cn.standardai.api.core.exception;

public class ParkOSException extends Exception {

	public String moduleName;

	private static final long serialVersionUID = 1L;

	public ParkOSException() {
		super();
	}

	public ParkOSException(String msg) {
		super(msg);
		//LogUtil.error(getModuleName() + "模块异常：" + msg);
	}

	public ParkOSException(Throwable e) {
		super(e);
		//LogUtil.error(e);
	}

	public ParkOSException(String msg, Throwable e) {
		super(msg, e);
		//LogUtil.error(getModuleName() + "模块异常：" + msg, e);
	}
}
