package cn.standardai.api.ash.resource;

import cn.standardai.api.ash.base.AshResource;

public class ResFile extends AshResource {

	@Override
	public String help() {
		return "file，文件资源，由用户建立的脚本文件或数据文件，用以脚本化执行命令或执行批处理程序。";
	}
}
