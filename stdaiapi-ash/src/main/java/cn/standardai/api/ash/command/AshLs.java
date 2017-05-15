package cn.standardai.api.ash.command;

public class AshLs extends AshCommand {

	@Override
	public String exec(String params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String help() {
		return "ls命令格式：ls [-l]";
	}

	@Override
	public String man() {
		return "ls命令用于显示当前资源类别下的所有资源\n"
				+ "用法：\tls -参数"
				+ "参数：\t-l\t显示详细信息";
	}
}
