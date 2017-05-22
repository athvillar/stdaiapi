package cn.standardai.api.ash.command;

import cn.standardai.api.ash.base.AshResourceRelatedCommand;

public class AshMk extends AshResourceRelatedCommand {

	@Override
	public String help() {
		return "mk [资源类别] [参数]，查看详细说明请使用\"man mk\"。";
	}

	@Override
	public String man() {
		return "mk命令用于创建资源\n"
				+ "语法\n"
				+ "\tmk [资源类别] [参数]\n"
				+ "创建用户\n"
				+ "\tmk user -un 用户名 -pw 密码 -em 邮箱\n"
				+ "创建模型\n"
				+ "\tmk model -md 模型名 -dt 数据名 -ag 算法名 -xf X过滤器 -yf Y过滤器 -sr模型结构\n"
				+ "省略参数将以对话形式交互。";
	}
}
