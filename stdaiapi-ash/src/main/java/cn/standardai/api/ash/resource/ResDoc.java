package cn.standardai.api.ash.resource;

import cn.standardai.api.ash.base.AshResource;

public class ResDoc extends AshResource {

	@Override
	public String help() {
		return "doc，文档资源，Athvillar平台相关文档。\n"
				+ "包括系统介绍性文档，专题文档，ash使用文档，sdk文档等资源。";
	}
}
