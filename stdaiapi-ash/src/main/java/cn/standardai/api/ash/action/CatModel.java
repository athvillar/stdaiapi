package cn.standardai.api.ash.action;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.base.Action;
import cn.standardai.api.ash.base.AshCommand.HttpMethod;
import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.core.bean.Context;
import cn.standardai.api.core.util.DateUtil;

public class CatModel extends Action {

	private String modelName;

	private String pModelName;

	public CatModel() {
		setParamRules(null, null, 1, 1);
	}

	@Override
	public AshReply exec() throws AshException {

		JSONObject j = comm.http(HttpMethod.GET, Context.getProp().getUrl().getMl() + "/dnn/" + pModelName, null, null);

		JSONObject modelJ = j.getJSONObject("model");
		if (modelJ == null) {
			this.reply.display = "没有找到模型(" + modelName + ")";
			return this.reply;
		}
					
		String result = "基本信息\n";
		result += "\t模型名\t\t" + pModelName + "\n";
		result += "\t算法\t\t\t" + modelJ.getString("algorithm") + "\n";
		result += "\t创建时间\t\t\t" + DateUtil.format(modelJ.getDate("createTime"), DateUtil.YYYY__MM__DD__HH__MM__SS) + "\n";
		result += "\t结构\t\t\t" + modelJ.getString("structure") + "\n";

		JSONObject dataJ = modelJ.getJSONObject("data");
		if (dataJ != null) {
			result += "\n";
			result += "数据信息\n";
			result += "\t数据名\t\t" + dataJ.getString("dataName") + "\n";
			result += "\tx\t\t\t" + dataJ.getString("xColumn") + "\n";
			result += "\tx过滤器\t\t" + dataJ.getString("xFilter") + "\n";
			result += "\ty\t\t\t" + dataJ.getString("yColumn") + "\n";
			result += "\ty过滤器\t\t" + dataJ.getString("yFilter") + "\n";
		}

		JSONObject trainJ = modelJ.getJSONObject("train");
		if (trainJ != null) {
			result += "\n";
			result += "训练信息\n";
			result += "\t状态\t\t\t" + (trainJ.getDate("endTime") == null ? "训练中\n" : "训练结束\n");
			result += "\t训练开始时间\t" + DateUtil.format(trainJ.getDate("startTime"), DateUtil.YYYY__MM__DD__HH__MM__SS) + "\n";
			result += "\t训练结束时间\t" + DateUtil.format(trainJ.getDate("endTime"), DateUtil.YYYY__MM__DD__HH__MM__SS) + "\n";
			Integer seconds = trainJ.getInteger("totalSecond");
			if (seconds != null) result += "\t用时\t\t\t" + seconds / 60 / 60 + "小时" + seconds / 60 + "分\n";
			if (trainJ.getInteger("epochCnt") != null) result += "\tepoch\t\t" + trainJ.getInteger("epochCnt") + "\n";
			result += "\tepoch数据量\t" + trainJ.getInteger("epochDataCnt");

			JSONObject trainIdJ = new JSONObject();
			trainIdJ.put("trainId", trainJ.getString("trainId"));
			this.reply.hidden = trainIdJ;
		}

		this.reply.display = result;
		return this.reply;
	}

	@Override
	public void readParam() throws AshException {
		modelName = param.get(1);
		if (modelName != null && modelName.indexOf('/') != -1) {
			pModelName = modelName;
		} else {
			pModelName = this.userId + "/" + modelName;
		}
	}
}
