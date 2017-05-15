package cn.standardai.api.ash.resource;

public class AshModel extends AshResource {

	@Override
	public String help() {
		return "model，模型资源，指深度学习模型，类型可能是CNN或RNN等深度学习模型中的一种。\n"
				+ "模型通常由模版（template）中的脚本（script）建立，也可能由别的模型复制出来。"
				+ "根据训练程度的不同，同一个模版可能对应多个模型，为了可追溯模型的训练历史，"
				+ "大多数模型都拥有一个父模型，这些模型构成一个模型树，同一个模型树中的模型总能追溯到同一个模版。\n";
	}
}
