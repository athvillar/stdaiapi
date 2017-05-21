package cn.standardai.api.ash.resource;

import cn.standardai.api.ash.base.AshResource;

public class ResData extends AshResource {

	@Override
	public String help() {
		return "data，数据资源，由用户建立并上传供模型训练。\n"
				+ "mk data建立数据集，数据来源可以是用户上传或网络资源；\n"
				+ "ls data列出可用数据集；\n"
				+ "cat data查看一个数据集的详细信息；\n"
				+ "rm data删除数据集\n"
				+ "set data更新数据集";
	}
}
