package cn.standardai.api.ash.action;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.base.Action;
import cn.standardai.api.ash.base.AshCommand.HttpMethod;
import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.core.bean.Context;
import cn.standardai.api.core.util.DateUtil;

public class CatData extends Action {

	private String dataName;

	private String pDataName;

	public CatData() {
		setParamRules(null, null, 1, 1);
	}

	@Override
	public AshReply exec() throws AshException {

		JSONObject j = comm.http(HttpMethod.GET, Context.getProp().getUrl().getData() + "/data/" + pDataName, null, null);

		JSONObject dataJ = j.getJSONObject("data");
		if (dataJ == null) {
			this.reply.display = "没有找到数据(" + dataName + ")";
			return this.reply;
		}

		String result = "数据名\t\t" + dataJ.getString("dataName") + "\n";
		result += "描述\t\t\t" + dataJ.getString("description") + "\n";
		result += "类型\t\t\t" + dataJ.getString("type") + "\n";
		result += "格式\t\t\t" + dataJ.getString("format") + "\n";
		result += "关键词\t\t" + dataJ.getString("keywords") + "\n";
		result += "标题\t\t\t" + dataJ.getString("titles") + "\n";
		result += "共享\t\t\t" + dataJ.getString("sharePolicy") + "\n";
		result += "创建时间\t\t" + DateUtil.format(dataJ.getDate("createTime"), DateUtil.YYYY__MM__DD__HH__MM__SS) + "\n";
		result += "数据量\t\t" + dataJ.getInteger("count") + "\n";
		if (dataJ.getInteger("count") != 0) {
			String ref;
			String x;
			String y;
			if ((ref = dataJ.getString("ref")) != null) {
				if (ref.length() > 30) ref = ref.substring(0, 30);
			}
			if ((x = dataJ.getString("x")) != null) {
				if (x.length() > 30) x = x.substring(0, 30);
			}
			if ((y = dataJ.getString("y")) != null) {
				if (y.length() > 30) y = y.substring(0, 30);
			}
			result += "数据预览\n";
			result += "\tREF\t\t\t" + ref + "\n";
			result += "\tX\t\t\t" + x + "\n";
			result += "\tY\t\t\t" + y + "\n";
		}
		this.reply.display = result;
		return this.reply;
	}

	@Override
	public void readParam() throws AshException {
		dataName = param.get(1);
		if (dataName != null && dataName.indexOf('/') != -1) {
			pDataName = dataName;
		} else {
			pDataName = this.userId + "/" + dataName;
		}
	}
}
