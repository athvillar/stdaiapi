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
					+ "\tash(未完成)\t\t\tcd(未完成)\t\t\tcurl(未完成)\n"
					+ "\techo(未完成)\t\t\thelp\t\t\t\t\thistory(未完成)\n"
					+ "\tman\t\t\t\t\tmessage\t\t\t\tlogin\n"
					+ "\tlogout(未完成)\t\t\tversion(未完成)\n"
					+ "针对某资源的命令\n"
					+ "\tcat(未完成)\t\t\tcp(未完成)\t\t\tdownload(未完成)\n"
					+ "\tfind(未完成)\t\t\tls\t\t\t\t\tmk\n"
					+ "\trm(未完成)\t\t\tupload(未完成)\t\n"
					+ "资源\n"
					+ "\tdata(未完成)\t\t\tdataset(未完成)\t\tdic(未完成)\n"
					+ "\tdoc(未完成)\t\t\tmodel\t\t\t\tnode(未完成)\n"
					+ "\ttemplate(未完成)\t\tuser(未完成)\n"
					+ "查看某个命令的具体使用方式请输入“man 命令名”，查看某项资源介绍请输入“help 资源名”";
		}

		if (params.length != 2) {
			return this.help("参数个数不正确");
		}

		AshResource resource = AshResource.getInstance(params[1]);
		if (resource == null) {
			return "没有找到" + params[1];
		}

		return resource.help();
	}

	@Override
	public String help() {
		return "help命令格式：help [名词]";
	}

	@Override
	public String man() {
		return "help命令用于显示帮助信息\n"
				+ "用法\n"
				+ "\thelp [-c 名词]\n"
				+ "参数\n"
				+ "\t-c: 显示某个名词解释";
	}
}
