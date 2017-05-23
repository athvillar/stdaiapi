package cn.standardai.api.ash.base;

import cn.standardai.api.ash.exception.AshException;

public abstract class AshResourceRelatedCommand extends AshCommand {

	private AshResource resource;

	@Override
	public Executable getExecutor() throws AshException {

		String comCls = this.getClass().getName();
		comCls = comCls.substring(comCls.lastIndexOf('.') + 1).substring(3);
		String resCls = this.resource.getClass().getName();
		resCls = resCls.substring(resCls.lastIndexOf('.') + 1).substring(3);
		try {
			Action action = (Action) Class.forName("cn.standardai.api.ash.action." + comCls + resCls).newInstance();
			action.comm = this;
			action.res = this.resource;
			return action;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			//e.printStackTrace();
			throw new AshException("该资源没有对应的命令");
		}
	}

	public void setResource(AshResource resource) {
		this.resource = resource;
	}
}
