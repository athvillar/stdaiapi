package cn.standardai.api.dao.exception;

public class DaoException extends Exception {

	private static final String moduleName = "DAO模块";

	private static final long serialVersionUID = 1L;

	public DaoException(String msg) {
		super(msg);
	}

	public DaoException(String msg, Throwable e) {
		super(msg, e);
	}

	public static String getModulename() {
		return moduleName;
	}
}
