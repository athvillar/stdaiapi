package cn.standardai.api.node.exception;

import cn.standardai.api.core.exception.StdaiException;

public class NodeException extends StdaiException {

	private static final String moduleName = "服务模块";

	private static final long serialVersionUID = 1L;

	public NodeException(String msg) {
		super(moduleName + "异常：" + msg);
	}

	public NodeException(String msg, Throwable e) {
		super(moduleName + "异常：" + msg, e);
	}
}
