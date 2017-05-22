package cn.standardai.api.ash.command;

import cn.standardai.api.ash.base.AshResourceRelatedCommand;

public class AshCall extends AshResourceRelatedCommand {

	public AshCall() {
		//setParamRules(null, null, null, null);
	}

	@Override
	public String help() {
		return "call [资源类别] [参数]，查看详细说明请使用\"man call\"。";
	}

	@Override
	public String man() {
		return "call命令用于操作资源，对模型资源来说，包括训练模型，测试模型，部署模型，设置默认参数等操作\n"
				+ "语法\n"
				+ "\tcall [资源类别] [参数]\n"
				+ "操作模型\n"
				+ "\tcall model -t(rain) -p(redict) -r(elease) -s(etting)\n"
				+ "\t\t-t\t训练模型\n"
				+ "\t\t-p\t测试模型\n"
				+ "\t\t-r\t部署模型\n"
				+ "\t\t-s\t设置参数\n"
				+ "训练模型\n"
				+ "\tcall model -t [-md 参数] [-lr 参数] [-th 参数] [-dv 参数] [-bs 参数] [-we 参数] [-ep 参数] [-ts 参数] [-tt 参数] [-ko 参数]\n"
				+ "\t\t-md\t模型名\n"
				+ "\t\t-lr\t学习率\n"
				+ "\t\t-th\t梯度矩阵秩的阈值\n"
				+ "\t\t-dv\t训练集、测试集、验证集的比例，逗号分割，如8,1,1\n"
				+ "\t\t-bs\tbatch size\n"
				+ "\t\t-we\t中间结果输出间隔\n"
				+ "\t\t-ep\tepoch数量\n"
				+ "\t\t-ts\t训练时间，以秒为单位\n"
				+ "\t\t-tt\t测试集loss函数增大次数阈值，超过此值训练将停止\n"
				+ "\t\t-ko\t是否保留旧模型，Y是保留，不保留的情况旧模型将被新模型覆盖\n"
				+ "省略参数将以对话形式交互。";
	}
}
