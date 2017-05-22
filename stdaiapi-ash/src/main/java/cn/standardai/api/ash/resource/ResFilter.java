package cn.standardai.api.ash.resource;

import cn.standardai.api.ash.base.AshResource;

public class ResFilter extends AshResource {

	@Override
	public String help() {
		return "filter，过滤器资源，用于训练前的数据预处理。\n"
				+ "与过滤器资源相关的命令包括cat, ls。";
	}
}
