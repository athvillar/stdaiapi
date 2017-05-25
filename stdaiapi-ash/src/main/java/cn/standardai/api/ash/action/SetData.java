package cn.standardai.api.ash.action;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.agent.ArgsHelper;
import cn.standardai.api.ash.base.Action;
import cn.standardai.api.ash.base.AshCommand.HttpMethod;
import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.ash.exception.ParamException;
import cn.standardai.api.core.bean.Context;

public class SetData extends Action {

	private static String[][][] dialog = new String[][][] {
		new String[][] {
			new String[] {"dn", "请输入数据名:"},
			new String[] {"nf", "更新或追加(U:更新, A:追加, U):"},
			new String[] {"us", "若更新，请输入更新起始编号(0):"},
			new String[] {"dt", "请输入数据及标签\n[\n\t[\"数据1数据\",\"数据1标签\"],\n\t[\"数据2数据\",\"数据2标签\"],\n\t ...\n]\n"},
			new String[] {"bs", "批量更新，请输入脚本\n{\n\t\"标签1\":{\"start\":标签1开始编号,\"end\":标签1结束编号},\n\t\"标签2\":{\"start\":标签2开始编号,\"end\":标签2结束编号}, \n\t... \n}\n"}
		}
	};

	static {
		ArgsHelper.regist(SetData.class, dialog);
	}

	public SetData() {
		setVp(dialog);
	}

	private String dataName;

	private Boolean newFlag;

	private String updateBaseIdx;

	private JSONArray data;

	private JSONObject batchSet;

	@Override
	public AshReply exec() throws AshException {

		JSONObject body = new JSONObject();
		if (!newFlag && updateBaseIdx != null) {
			body.put("updateBaseIdx", updateBaseIdx);
		}
		if (data != null) body.put("data", data);
		if (batchSet != null) body.put("batchSet", batchSet);

		comm.http(HttpMethod.POST, Context.getProp().getUrl().getData() + "/data/" + this.userId + "/" + dataName, null, body);

		this.reply.display = "数据设置成功(" + this.userId + "/" + dataName + ")";
		return this.reply;
	}

	@Override
	public void readParam() throws AshException {

		this.dataName = this.param.getString("dn");
		if (dataName == null || "".equals(dataName)) throw new ParamException("缺少数据名");

		this.newFlag = "A".equalsIgnoreCase(this.param.getString("nf"));
		this.updateBaseIdx = this.param.getString("us");
		try {
			this.data = this.param.getJSONArray("dt");
		} catch (JSONException e) {
			throw new ParamException("数据格式不正确，参考\n[\n\t[\"数据1数据\",\"数据1标签\"],\n\t[\"数据2数据\",\"数据2标签\"],\n\t ...\n]\n");
		}
		try {
			this.batchSet = this.param.getJSONObject("bs");
		} catch (JSONException e) {
			throw new ParamException("数据格式不正确，参考\n{\n\t\"标签1\":{\"start\":标签1开始编号,\"end\":标签1结束编号},\n\t\"标签2\":{\"start\":标签2开始编号,\"end\":标签2结束编号}, \n\t... \n}\n");
		}
	}
}
