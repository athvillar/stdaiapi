package cn.standardai.api.ash.resource;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.bean.AshCommandParams;
import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.command.base.AshCommand.HttpMethod;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.ash.resource.base.AshResource;
import cn.standardai.api.core.bean.Context;
import cn.standardai.api.core.util.DateUtil;

public class ResModel extends AshResource {

	private String modelName;

	private String datasetName;

	private String xFilter;

	private String yFilter;

	private String algorithm;

	private String structure;

	@Override
	public void mk() throws AshException {

		JSONObject body = new JSONObject();
		body.put("name", modelName);
		body.put("algorithm", algorithm);

		JSONObject dataXJ = new JSONObject();
		dataXJ.put("filter", xFilter);
		JSONObject dataYJ = new JSONObject();
		dataXJ.put("filter", yFilter);

		JSONObject dataJ = new JSONObject();
		dataJ.put("datasetName", datasetName);
		dataJ.put("x", dataXJ);
		dataJ.put("y", dataYJ);
		body.put("data", dataJ);
		body.put("structure", structure);

		JSONObject j = http(HttpMethod.POST, Context.getProp().getUrl().getMl() + "/dnn/" + this.userId + "/" + modelName, null, body);

		this.reply.display = "模型(id=" + j.getString("modelId") + ", name=" + userId + "/" + modelName + ")建立成功";
	}

	@Override
	public void ls() throws AshException {

		JSONObject j = http(HttpMethod.GET, Context.getProp().getUrl().getMl() + "/dnn", null, null);

		JSONArray models = j.getJSONArray("models");
		if (models == null) this.reply.display = "没有记录";

		String result;
		if (params.has('l')) {
			result = "modelName\t\t\t\t\t\t\tupdateTime\t\t";
		} else {
			result = "modelName";
		}
		for (int i = 0; i < models.size(); i++) {
			result += "\n" + models.getJSONObject(i).getString("modelName") + "\t";
			if (params.has('l')) result += DateUtil.format(models.getJSONObject(i).getDate("updateTime"), DateUtil.YYYY__MM__DD__HH__MM__SS);
		}
		result += "\n共" + models.size() + "条记录";
		this.reply.display = result;
	}

	@Override
	public void rm() throws AshException {
		http(HttpMethod.DELETE, Context.getProp().getUrl().getMl() + "/dnn/" + userId + "/" + modelName, null, null);
		this.reply.display = "模型(" + userId + "/" + modelName + ")已删除";
	}

	@Override
	public void parseParam(AshCommandParams params) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AshReply help() {
		this.reply.display = "model，模型资源，指深度学习模型，类型可能是CNN或RNN等深度学习模型中的一种。\n"
				+ "模型通常由模版（template）中的脚本（script）建立，也可能由其它模型复制出来。"
				+ "根据训练程度的不同，同一个模版可能对应多个模型，为了可追溯模型的训练历史，"
				+ "大多数模型都拥有一个父模型，这些模型构成一个模型树，同一个模型树中的模型总能追溯到同一个模版。\n"
				+ "model同时也是一个资源命令，可以通过man model了解关于model作为一个命令时的用法。";
		return this.reply;
	}

	@Override
	public String[] getMkSteps() {
		String[] s1 = new String[] {
				"请输入模型名：",
				"请输入数据集名(userId/datasetName)：",
				"请输入x过滤器，多个过滤器用｜分割(支持的过滤器请参考文档－－过滤器)：",
				"请输入y过滤器，多个过滤器用｜分割(支持的过滤器请参考文档－－过滤器)：",
				"请输入算法名(CNN, LSTM)：",
				"请输入算法JSON结构(结构说明请参考文档－－建立模型)：",
		};
		return s1;
	}
}
