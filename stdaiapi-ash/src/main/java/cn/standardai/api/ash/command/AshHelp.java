package cn.standardai.api.ash.command;

import cn.standardai.api.ash.resource.AshResource;

public class AshHelp extends AshCommand {

	public AshHelp(String token) {
		super(token);
	}

	@Override
	public String exec(String[] params) {

		if (params.length == 1) {
			return "Ash是Athvillar平台专用的命令行系统，用来管理和操作Athvillar平台的资源。\n"
					+ "其中全局命令\n"
					+ "\tash       \tcd        \tcurl\n"
					+ "\techo      \thelp      \thistory\n"
					+ "\tman       \tmessage  \tlogin\n"
					+ "\tlogout    \tversion   \t\n"
					+ "针对某资源的命令\n"
					+ "\tcat       \tcp        \tdownload\n"
					+ "\tfind      \tls        \tmk\n"
					+ "\trm        \tupload    \n"
					+ "资源\n"
					+ "\tdata      \tdataset   \tdic\n"
					+ "\tdoc       \tmodel     \tnode\n"
					+ "\ttemplate \tuser\n"
					+ "查看某个命令的具体使用方式请输入“man 命令名”，查看某项资源介绍请输入“help 资源名”\n";
		}

		if (params.length != 2) {
			return this.help();
		}

		AshResource resource = AshResource.getInstance(params[1]);
		if (resource == null) {
			return "没有找到" + params[1] + "\n";
		}

		return resource.help();
	}

	@Override
	public String help() {
		return "help命令格式：help [名词]\n";
	}

	@Override
	public String man() {
		return "help命令用于显示帮助信息\n"
				+ "用法\n"
				+ "\thelp [-c 名词]\n"
				+ "参数\n"
				+ "\t-c: 显示某个名词解释\n";
	}
}
