package cn.standardai.api.ash.action;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.agent.ArgsHelper;
import cn.standardai.api.ash.base.Action;
import cn.standardai.api.ash.base.AshCommand.HttpMethod;
import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.ash.exception.HttpException;
import cn.standardai.api.ash.exception.ParamException;
import cn.standardai.api.core.bean.Context;

public class CallModel extends Action {

	private static String[][][] dialog = new String[][][] { 
		new String[][] {
			new String[] {"md", "请输入模型名:"},
			new String[] {"lr", "请输入学习率(0.1, 建议范围0.01 ~ 1.0):"},
			new String[] {"th", "请输入梯度矩阵秩的阈值(1, 建议范围1 ~ 15):"},
			new String[] {"ep", "请输入epoch数:"},
			new String[] {"we", "请输入中间结果输出间隔(epoch):"},
			new String[] {"bs", "请输入batch size:"},
			new String[] {"dv", "请输入训练集，测试集，验证集的比例，用逗号分割(8,1,1):"},
			new String[] {"ts", "请输入训练时间(秒):"},
			new String[] {"tt", "请输入测试集Loss增大次数(null):"},
			new String[] {"ko", "是否保留旧模型(N):"},
		},
		new String[][] {
			new String[] {"md", "请输入模型名:"},
			new String[] {"dn", "请输入数据名(userId/dataName):"},
			new String[] {"xf", "请输入x过滤器(过滤器格式可参考文档\"过滤器/filter\"，默认使用系统推荐过滤器):"},
			new String[] {"yf", "请输入y过滤器(过滤器格式可参考文档\"过滤器/filter\"，默认使用系统推荐过滤器):"},
			new String[] {"tm", "请输入结束符编号(LSTM专有):"},
			new String[] {"st", "请输入预测步数(LSTM专有):"},
		}
	};

	static {
		ArgsHelper.regist(CallModel.class, dialog);
	}

	public CallModel() {
		setParamRules(new char[] { 't', 'p', 'r', 's'}, null, null, null);
		setVp(dialog);
	}

	private String modelName;

	private Double learningRate;

	private Double dth;

	private Integer[] diverseDataRate;

	private Integer batchSize;

	private Integer watchEpoch;

	private Integer epoch;

	private Integer trainSecond;

	private Integer testLossIncreaseTolerance;

	private Boolean keepOld;

	private String datasetName;

	private String xFilter;

	private String yFilter;

	private Integer terminator;

	private Integer steps;

	@Override
	public AshReply exec() throws AshException {
		if (this.param.has('t')) {
			train();
		} else if (this.param.has('p')) {
			predict();
		} else if (this.param.has('r')) {
			release();
		} else if (this.param.has('s')) {
			setting();
		}
		return this.reply;
	}

	private void train() throws HttpException, ParamException {

		if (modelName == null || "".equals(modelName)) throw new ParamException("缺少模型名");

		JSONObject body = new JSONObject();
		JSONObject train = new JSONObject();

		train.put("learningRate", learningRate == null ? 0.1 : learningRate);
		train.put("dth", dth == null ? 1.0 : dth);
		if (diverseDataRate != null && diverseDataRate.length == 3) {
			JSONArray rateJ = new JSONArray();
			for (int i = 0; i < 3; i++) {
				rateJ.add(diverseDataRate[i]);
			}
			train.put("diverseDataRate", rateJ);
		}
		if (batchSize != null) train.put("batchSize", batchSize);
		if (watchEpoch != null) train.put("watchEpoch", watchEpoch);
		if (epoch != null) train.put("epoch", epoch);
		if (trainSecond != null) train.put("trainSecond", trainSecond);
		if (testLossIncreaseTolerance != null) train.put("testLossIncreaseTolerance", testLossIncreaseTolerance);

		body.put("new", !keepOld);
		body.put("train", train);

		comm.http(HttpMethod.POST, Context.getProp().getUrl().getMl() + "/dnn/" + this.userId + "/" + modelName, null, body);

		this.reply.display = "训练开始，使用cat命令查看训练状态";
	}

	private void predict() throws ParamException, HttpException {

		if (modelName == null || "".equals(modelName)) throw new ParamException("缺少模型名");

		JSONObject body = new JSONObject();
		body.put("name", modelName);

		JSONObject dataXJ = new JSONObject();
		dataXJ.put("filter", xFilter);
		JSONObject dataYJ = new JSONObject();
		dataYJ.put("filter", yFilter);

		JSONObject dataJ = new JSONObject();
		dataJ.put("datasetName", datasetName);
		dataJ.put("x", dataXJ);
		dataJ.put("y", dataYJ);
		body.put("data", dataJ);

		if (terminator != null || steps != null) {
			JSONObject lstmJ = new JSONObject();
			if (terminator != null) lstmJ.put("terminator", terminator);
			if (steps != null) lstmJ.put("steps", steps);
			body.put("lstm", lstmJ);
		}

		JSONObject j = comm.http(HttpMethod.POST, Context.getProp().getUrl().getMl() + "/dnn/" + this.userId + "/" + modelName + "/predict", null, body);

		this.reply.display = j.getJSONArray("value").toJSONString();
	}

	private void release() {
		// TODO Auto-generated method stub
		
	}

	private void setting() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readParam() throws AshException {

		if (this.param.has('t')) {
			modelName = param.getString("md");
			learningRate = param.getDouble("lr");
			dth = param.getDouble("th");

			if (param.getString("dv") == null || "".equals(param.getString("dv"))) {
				diverseDataRate = null;
			} else {
				String[] rate3 = param.getString("dv").split(",");
				diverseDataRate = new Integer[3];
				for (int i = 0; i < diverseDataRate.length; i++) {
					diverseDataRate[i] = i >= rate3.length ? 0 : Integer.parseInt(rate3[i]);
				}
			}
			batchSize = param.getInteger("bs");
			watchEpoch = param.getInteger("we");
			epoch = param.getInteger("ep");
			trainSecond = param.getInteger("ts");
			testLossIncreaseTolerance = param.getInteger("tt");
			keepOld = "y".equalsIgnoreCase(param.getString("ko"));
		} else if (this.param.has('p')) {
			this.modelName = this.param.getString("md");
			this.datasetName = this.param.getString("dn");
			this.xFilter = this.param.getString("xf");
			this.yFilter = this.param.getString("yf");
			this.terminator = this.param.getInteger("tm");
			this.steps = this.param.getInteger("st");
		} else if (this.param.has('r')) {

		} else if (this.param.has('s')) {

		}
	}

	@Override
	public int getDialogIndex() {
		if (this.param.has('t')) {
			return 0;
		} else if (this.param.has('p')) {
			return 1;
		} else if (this.param.has('r')) {
			return 2;
		} else if (this.param.has('s')) {
			return 3;
		}
		return 0;
	}
}
