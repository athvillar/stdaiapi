package cn.standardai.api.ash.command;

public class AshMan extends AshCommand {

	@Override
	public String exec(String params) {
		return "Ash是Athvillar平台专用的命令行系统，用来管理和操作Athvillar平台的资源。\n"
				+ "其中全局命令有：\n"
				+ "\tash\tcd\tcurl\n"
				+ "\techo\thelp\thistory\n"
				+ "\tman\tmessage\tlogin\n"
				+ "\tlogout\tversion\t\n"
				+ "针对某资源的命令有：\n"
				+ "\tcat\tcp\tdownload\n"
				+ "\tfind\tls\tmk\n"
				+ "\trm\tupload\n"
				+ "包含的资源有：\n"
				+ "\tdata\tdataset\tdic\n"
				+ "\tdoc\tmodel\tnode\n"
				+ "\ttemplate\tuser\n"
				+ "查看某个命令的具体使用方式请输入“man 命令名”，查看某项资源介绍请输入“help 资源名”\n";
	}

	@Override
	public String help() {
		return "help命令格式：help";
	}

	@Override
	public String man() {
		return "help命令用于显示帮助信息\n"
				+ "用法：\thelp";
	}
}
