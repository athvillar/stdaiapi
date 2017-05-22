package cn.standardai.api.ash.resource;

import cn.standardai.api.ash.base.AshResource;

public class ResUser extends AshResource {

	@Override
	public String help() {
		return "user，用户资源，即平台用户。\n"
				+ "使用平台的大多数功能都需要先注册一个用户，使用mk user命令，按照提示步骤可以注册用户，"
				+ "已注册用户第一次打开平台需要使用login命令登录，login命令的格式是\"login [用户名] [密码]\"。"
				+ "登陆之后的用户可以上传数据，或者使用共享数据建立自己的深度学习模型，并对整个模型的生命周期进行管理。"
				+ "关于模型的介绍可以输入\"help model\"，登出请使用logout命令。\n"
				+ "与用户资源相关的命令包括ls, mk。";
	}
}
