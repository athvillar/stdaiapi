package cn.standardai.api.ash.command.base;

import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.ash.resource.base.AshResource;
import cn.standardai.api.ash.resource.base.AshResource.Resource;

public abstract class AshResourceCommand extends AshCommand {

	protected Resource resource;

	@Override
	public void invoke() throws AshException {
		AshResource ashResource = AshResource.getInstance(this.resource);
		this.reply = ashResource.invoke(this.getClass(), this.params, this.token);
	}
}
