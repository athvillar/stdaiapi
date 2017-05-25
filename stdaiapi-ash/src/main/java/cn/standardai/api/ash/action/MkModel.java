package cn.standardai.api.ash.action;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.agent.ArgsHelper;
import cn.standardai.api.ash.base.Action;
import cn.standardai.api.ash.base.AshCommand.HttpMethod;
import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.ash.exception.ParamException;
import cn.standardai.api.core.bean.Context;

public class MkModel extends Action {

	private static String[][][] dialog = new String[][][] {
		new String[][] {
			new String[] {"md", "请输入模型名:"},
			new String[] {"dn", "请输入数据名(userId/dataName):"},
			new String[] {"ag", "请输入算法名(CNN, LSTM):"},
			new String[] {"xf", "请输入x过滤器(过滤器格式可参考文档\"过滤器/filter\"，默认使用系统推荐过滤器):"},
			new String[] {"yf", "请输入y过滤器(过滤器格式可参考文档\"过滤器/filter\"，默认使用系统推荐过滤器):"},
			new String[] {"sr", "请输入算法JSON结构(结构说明请参考文档－－建立模型):",}
		}
	};

	static {
		ArgsHelper.regist(MkModel.class, dialog);
	}

	public MkModel() {
		setVp(dialog);
	}

	private String modelName;

	private String datasetName;

	private String xFilter;

	private String yFilter;

	private String algorithm;

	private JSONObject structure;

	@Override
	public AshReply exec() throws AshException {

		if (modelName == null || "".equals(modelName)) throw new ParamException("缺少模型名");

		JSONObject body = new JSONObject();
		body.put("name", modelName);
		body.put("algorithm", algorithm);

		JSONObject dataXJ = new JSONObject();
		dataXJ.put("filter", xFilter);
		JSONObject dataYJ = new JSONObject();
		dataYJ.put("filter", yFilter);

		JSONObject dataJ = new JSONObject();
		dataJ.put("datasetName", datasetName);
		dataJ.put("x", dataXJ);
		dataJ.put("y", dataYJ);
		body.put("data", dataJ);
		body.put("structure", structure);

		JSONObject j = comm.http(HttpMethod.POST, Context.getProp().getUrl().getMl() + "/dnn", null, body);

		this.reply.display = "模型(id=" + j.getString("id") + ", name=" + this.userId + "/" + modelName + ")建立成功";
		return this.reply;
	}

	@Override
	public void readParam() throws AshException {
		this.modelName = this.param.getString("md");
		this.datasetName = this.param.getString("dn");
		this.xFilter = this.param.getString("xf");
		this.yFilter = this.param.getString("yf");
		this.algorithm = this.param.getString("ag");
		try {
			this.structure = this.param.getJSONObject("sr");
		} catch (JSONException e) {
			throw new ParamException("数据格式不正确，参考算法" + this.algorithm + "结构");
		}
	}
}
