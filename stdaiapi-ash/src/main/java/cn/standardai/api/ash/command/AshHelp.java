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
			this.reply.display = "Athvillar是一个深度学习平台，用户可以在平台上上传数据，建立并训练深度学习模型。ash是Athvillar平台专用的命令行系统，用来管理和操作Athvillar平台的资源。\n"
					+ "ash的命令分为全局命令和资源命令两种，全局命令不针对某一类资源，包括\n"
					+ "\tash(未完成)\t解析ash脚本，执行批处理任务\n"
					+ "\tcd\t\t\t在资源之间切换\n"
					+ "\tcurl(未完成)\t模拟shell的curl命令，发送http request\n"
					+ "\thelp\t\t显示帮助信息\n"
					+ "\tman\t\t\t查看命令帮助\n"
					+ "\tmsg\t\t\t收发系统内消息\n"
					+ "\tlogin\t\t用户登陆\n"
					+ "\tlogout\t\t用户登出\n"
					+ "\tversion\t\t查看版本信息\n"
					+ "全局命令的使用格式为\"命令名 [参数]\"。\n"
					+ "\n"
					+ "资源命令必须针对某一类资源才有意义，包括\n"
					+ "\tcall\t\t\t调用资源，例如训练模型\n"
					+ "\tcat\t\t\t查看某资源详细信息，例如查看模型结构\n"
					+ "\tcp(未完成)\t复制资源\n"
					+ "\tfind(未完成)\t查找资源\n"
					+ "\tls\t\t\t资源列表，例如列出模型一览\n"
					+ "\tmk\t\t\t创建资源，例如建模\n"
					+ "\trm\t\t\t删除资源\n"
					+ "\tset\t\t\t设置资源，例如为数据打标签\n"
					+ "资源命令的使用格式为\"命令名 [资源类别] [参数]\"，如果省略资源类别，默认将使用当前目录作为资源类别。\n"
					+ "\n"
					+ "平台包含的资源包括\n"
					+ "\tdata\t\t\t数据，用户上传的数据，供模型训练和预测使用\n"
					+ "\tdic\t\t\t数据字典，一般用作数据预处理\n"
					+ "\tdoc\t\t\t文档，包括平台使用说明和SDK\n"
					+ "\tfile(未完成)\t用户脚本\n"
					+ "\tfilter\t\t\t过滤器，数据预处理使用\n"
					+ "\tmodel\t\t模型，用户建立的深度学习模型\n"
					+ "\tnode(未完成)\t执行节点\n"
					+ "\tuser\t\t\t用户\n"
					+ "可以使用\"cd [资源类别]\"命令在各资源间切换。可通过提示符查看切换是否成功，例如\"$model>\"代表目前处于model资源目录下。\n"
					+ "\n"
					+ "查看某个命令的具体使用方式请输入\"man 命令名\"，查看某项资源介绍请输入\"help 资源类别\"，"
					+ "第一次使用平台可输入\"cat doc tutorial\"获得进一步帮助。";
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
		return;
	}
}
