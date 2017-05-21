package cn.standardai.api.ash.resource;

import cn.standardai.api.ash.base.AshResource;

public class ResDic extends AshResource {

	@Override
	public String help() {
		return "dic，数据字典资源，为数据预处理提供字典。\n"
				+ "数据字典由用户建立，每个数据字典可能包含多条字典数据，每个字典数据包括key和value两个值。"
				+ "在数据预处理时，数据字典需要和过滤器一起使用。建立数据字典使用mk dic命令。";
	}
}
