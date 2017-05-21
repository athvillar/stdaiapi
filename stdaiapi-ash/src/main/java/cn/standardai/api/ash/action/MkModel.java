package cn.standardai.api.ash.action;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.command.base.AshCommand.HttpMethod;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.core.bean.Context;

public class MkModel extends Action {

	String[] s1 = new String[] {
			"请输入模型名：",
			"请输入数据集名(userId/datasetName)：",
			"请输入x过滤器，多个过滤器用｜分割(支持的过滤器请参考文档－－过滤器)：",
			"请输入y过滤器，多个过滤器用｜分割(支持的过滤器请参考文档－－过滤器)：",
			"请输入算法名(CNN, LSTM)：",
			"请输入算法JSON结构(结构说明请参考文档－－建立模型)：",
	};

	private String modelName;

	private String datasetName;

	private String xFilter;

	private String yFilter;

	private String algorithm;

	private String structure;

	@Override
	public void exec() throws AshException {
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

		JSONObject j = comm.http(HttpMethod.POST, Context.getProp().getUrl().getMl() + "/dnn", null, body);

		comm.reply.display = "模型(id=" + j.getString("modelId") + ", name=" + comm.userId + "/" + modelName + ")建立成功";
	}

	@Override
	public void setParam() throws AshException {
//tODO
	}
}
