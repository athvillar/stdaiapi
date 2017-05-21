package cn.standardai.api.ash.action;

import cn.standardai.api.ash.command.base.AshCommand;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.ash.resource.base.AshResource;

public abstract class Action {

	public AshCommand comm;

	public AshResource res;

	public abstract void exec() throws AshException;

	public abstract void setParam() throws AshException;
}
