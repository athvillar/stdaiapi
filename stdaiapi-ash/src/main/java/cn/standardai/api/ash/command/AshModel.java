package cn.standardai.api.ash.command;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.agent.AshDialog;
import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.command.base.AshResourceCommand;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.ash.exception.HttpException;
import cn.standardai.api.core.bean.Context;

public class AshModel extends AshResourceCommand {

	private static final String[][] dialogs = new String[][] {
			new String[] {
					"请输入模型名: ",
					"请输入学习率(0.1, 建议范围0.01 ~ 1.0): ",
					"请输入梯度矩阵秩的阈值(1, 建议范围1 ~ 15): ",
					"请输入训练集，测试集，验证集的比例，用逗号分割(8,1,1): ",
					"请输入batch size: ",
					"请输入中间结果输出间隔(epoch): ",
					"请输入epoch数: ",
					"请输入训练时间(秒): ",
					"请输入测试集Loss增大次数(null): ",
					"是否保留旧模型(N): ",
			},
	};

	public AshModel() {
		setParamRules(new char[] {'t', 'p', 'r', 's'}, null, null, null);
	}

	@Override
	public void invoke() throws AshException {
		if (params.has('t')) {
			train();
		} else if (params.has('p')) {
			execute();
		} else if (params.has('r')) {
			release();
		} else if (params.has('s')) {
			setting();
		}
	}

	private void train() throws HttpException {

		if (params.number() < dialogs[0].length) {
			this.reply = AshDialog.make(this, params);
			return;
		}

		String modelName = params.get(0);
		JSONObject body = new JSONObject();
		JSONObject train = new JSONObject();

		train.put("learningRate", "".equals(params.get(1)) ? 0.1 : Double.parseDouble(params.get(1)));
		train.put("dth", "".equals(params.get(2)) ? 1.0 : Double.parseDouble(params.get(2)));
		String[] rate3 = "".equals(params.get(3)) ? "8,1,1".split(",") : params.get(3).split(",");
		JSONArray rate = new JSONArray();
		for (int i = 0; i < 3; i++) {
			rate.add(i >= rate3.length ? 0 : Integer.parseInt(rate3[i]));
		}
		train.put("diverseDataRate", rate);
		Integer batchSize = Integer.parseInt(params.get(4));
		Integer watchEpoch = Integer.parseInt(params.get(5));
		Integer epoch = Integer.parseInt(params.get(6));
		Integer trainSecond = Integer.parseInt(params.get(7));
		Integer testLossIncreaseTolerance = Integer.parseInt(params.get(8));

		if (batchSize != null) train.put("batchSize", batchSize);
		if (watchEpoch != null) train.put("watchEpoch", watchEpoch);
		if (epoch != null) train.put("epoch", epoch);
		if (trainSecond != null) train.put("trainSecond", trainSecond);
		if (testLossIncreaseTolerance != null) train.put("testLossIncreaseTolerance", testLossIncreaseTolerance);

		body.put("new", !"Y".equals(params.get(9)));
		body.put("train", train);

		http(HttpMethod.POST, Context.getProp().getUrl().getMl() + "/" + userId + "/" + modelName, null, body);

		this.reply.display = "训练开始，使用cat命令查看训练状态";
	}

	private void execute() {
		// TODO Auto-generated method stub
		
	}

	private void release() {
		// TODO Auto-generated method stub
		
	}

	private void setting() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AshReply help() {
		this.reply.display = "model命令格式：model [模型名] -t(rain) -p(redict) -r(elease) -s(etting)";
		return this.reply;
	}

	@Override
	public AshReply man() {
		this.reply.display = "model命令用于执行模型资源，包括训练模型，测试模型，部署模型，设置默认参数等操作\n"
				+ "用法：\n"
				+ "\tmodel -t(rain) -p(redict) -r(elease) -s(etting)\n"
				+ "参数：\n"
				+ "\t-t: 训练模型\n"
				+ "\t-p: 测试模型\n"
				+ "\t-r: 部署模型\n"
				+ "\t-s: 设置参数";
		return this.reply;
	}

	@Override
	public String[][] getDialog() {
		return dialogs;
	}
}
