package cn.standardai.api.ash.command;

import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.command.base.AshCommonCommand;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.ash.resource.base.AshResource;

public class AshHelp extends AshCommonCommand {

	public AshHelp() {
		setParamRules(null, null, 1, null);
	}

	@Override
	public void invoke() throws AshException {

		if (params.number() == 0) {
			this.reply.display = "Ash是Athvillar平台专用的命令行系统，用来管理和操作Athvillar平台的资源。\n"
					+ "其中全局命令\n"
					+ "\tash(未完成)\t\t\tcd\t\t\t\t\tcurl(未完成)\n"
					+ "\techo(未完成)\t\t\thelp\t\t\t\t\thistory\n"
					+ "\tman\t\t\t\t\tmsg\t\t\t\t\tlogin\n"
					+ "\tlogout\t\t\t\tversion\n"
					+ "针对某资源的命令\n"
					+ "\tcall(未完成)\t\t\tcat(未完成)\t\t\tcp(未完成)\n"
					+ "\tfind(未完成)\t\t\tls\t\t\t\t\tmk\n"
					+ "\trm\t\t\t\t\tset(未完成)\n"
					+ "资源\n"
					+ "\tdata(未完成)\t\t\tdic(未完成)\t\tdoc(未完成)\n"
					+ "\tfile(未完成)\t\t\tmodel\t\t\t\t\tnode(未完成)\n"
					+ "\tuser\n"
					+ "查看某个命令的具体使用方式请输入“man 命令名”，查看某项资源介绍请输入“help 资源名”";
			return;
		}

		AshResource resource = AshResource.getInstance(params.get(1));
		if (resource == null) {
			this.reply.display = "抱歉，该名词目前没有被收录：" + params.get(1);
			return;
		}
		this.reply.display = resource.help();
		return;
	}

	@Override
	public AshReply help() {
		this.reply.display = "help命令格式：help [名词]";
		return this.reply;
	}

	@Override
	public AshReply man() {
		this.reply.display = "help命令用于显示帮助信息\n"
				+ "用法\n"
				+ "\thelp [名词]\n"
				+ "参数\n"
				+ "\t名词:\t显示该名词解释";
		return this.reply;
	}

	@Override
	public String[][] getDialog() {
		return null;
	}
}
