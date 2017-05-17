package cn.standardai.api.ash.resource;

import cn.standardai.api.ash.bean.AshCommandParams;
import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.command.base.AshCommand.HttpMethod;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.ash.resource.base.AshResource;
import cn.standardai.api.core.bean.Context;

public class AshModel extends AshResource {

	String modelId;

	@Override
	public void mk() {
		// TODO Auto-generated method stub
		return;
	}

	@Override
	public void ls() {
		// TODO Auto-generated method stub
		return;
	}

	@Override
	public void rm() throws AshException {
		http(HttpMethod.DELETE, Context.getProp().getUrl().getMl() + "/lstm/" + modelId, null, null);
		this.reply.display = "model(" + modelId + ")已删除";
	}

	@Override
	public void parseParam(AshCommandParams params) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AshReply help() {
		this.reply.display = "model，模型资源，指深度学习模型，类型可能是CNN或RNN等深度学习模型中的一种。\n"
				+ "模型通常由模版（template）中的脚本（script）建立，也可能由其它模型复制出来。"
				+ "根据训练程度的不同，同一个模版可能对应多个模型，为了可追溯模型的训练历史，"
				+ "大多数模型都拥有一个父模型，这些模型构成一个模型树，同一个模型树中的模型总能追溯到同一个模版。";
		return this.reply;
	}
}
