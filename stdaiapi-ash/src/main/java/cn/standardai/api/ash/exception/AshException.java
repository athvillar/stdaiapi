package cn.standardai.api.ash.exception;

import cn.standardai.api.ash.command.AshCommand;
import cn.standardai.api.core.exception.StdaiException;

public class AshException extends StdaiException {

	private static final long serialVersionUID = 1L;

	public AshException(String msg) {
		super(msg);
	}

	public AshException(String msg, Throwable e) {
		super(msg, e);
	}

	public AshException(String msg, AshCommand comm) {
		super(msg);
	}
}
