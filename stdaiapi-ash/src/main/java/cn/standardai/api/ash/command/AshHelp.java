package cn.standardai.api.ash.command;

import cn.standardai.api.ash.base.AshCommonCommand;
import cn.standardai.api.ash.base.AshResource;
import cn.standardai.api.ash.exception.AshException;

public class AshHelp extends AshCommonCommand {

	public AshHelp() {
		setParamRules(null, null, 1, null);
	}

	@Override
	public void invoke() throws AshException {

		if (param.number() == 0) {
			this.reply.display = "Ash是Athvillar平台专用的命令行系统，用来管理和操作Athvillar平台的资源。\n"
					+ "其中全局命令\n"
					+ "\tash(未完成)\t\t\tcd\t\t\t\t\tcurl(未完成)\n"
					+ "\techo(未完成)\t\t\thelp\t\t\t\t\thistory\n"
					+ "\tman\t\t\t\t\tmsg\t\t\t\t\tlogin\n"
					+ "\tlogout\t\t\t\tversion\n"
					+ "针对某资源的命令\n"
					+ "\tcall\t\t\t\t\tcat\t\t\t\t\tcp(未完成)\n"
					+ "\tfind(未完成)\t\t\tls\t\t\t\t\tmk\n"
					+ "\trm\t\t\t\t\tset(未完成)\n"
					+ "资源\n"
					+ "\tdata(未完成)\t\t\tdic(未完成)\t\t\tdoc\n"
					+ "\tfile(未完成)\t\t\tfilter\t\t\t\t\tmodel\n"
					+ "\tnode(未完成)\t\t\tuser\n"
					+ "查看某个命令的具体使用方式请输入\"man 命令名\"，查看某项资源介绍请输入\"help 资源类别\"，"
					+ "使用cd命令可以在各资源间切换。";
			return;
		}

		AshResource resource = AshResource.getInstance(param.get(1));
		if (resource == null) {
			this.reply.display = "抱歉，该名词目前没有被收录：" + param.get(1);
			return;
		}
		this.reply.display = resource.help();
		return;
	}

	@Override
	public String help() {
		return "help [名词]";
	}

	@Override
	public String man() {
		return "help命令用于显示帮助信息\n"
				+ "语法\n"
				+ "\thelp [名词]\n"
				+ "用例\n"
				+ "\thelp model，显示model的名词解释。";
	}

	@Override
	public void readParam() throws AshException {
		// TODO Auto-generated method stub
		
	}
}
