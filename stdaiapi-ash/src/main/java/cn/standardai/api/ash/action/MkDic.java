package cn.standardai.api.ash.action;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.agent.ArgsHelper;
import cn.standardai.api.ash.base.Action;
import cn.standardai.api.ash.base.AshCommand.HttpMethod;
import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.core.bean.Context;

public class MkDic extends Action {

	private static String[][][] dialog = new String[][][] {
		new String[][] {
			new String[] {"dn", "请输入数据字典名:"},
			new String[] {"dc", "请输入数据字典描述:"},
			new String[] {"sp", "请输入共享策略(1:public, 2:protected, 3:private):"},
			new String[] {"dt", "请输入字典数据\n[\n\t{\"key\":\"key1\",\"value\":\"value1\"},\n\t{\"key\":\"key2\",\"value\":\"value2\"}, \n\t...\n]"}
		}
	};

	static {
		ArgsHelper.regist(MkDic.class, dialog);
	}

	public MkDic() {
		setVp(dialog);
	}

	private String dicName;

	private String description;

	private String sharePolicy;

	private JSONArray data;

	@Override
	public AshReply exec() throws AshException {

		JSONObject body = new JSONObject();
		body.put("dicName", dicName);
		body.put("description", description);
		body.put("sharePolicy", sharePolicy);
		body.put("data", data);

		comm.http(HttpMethod.POST, Context.getProp().getUrl().getData() + "/dic", null, body);

		this.reply.display = "数据字典(" + this.userId + "/" + dicName + ")建立成功";
		return this.reply;
	}

	@Override
	public void readParam() throws AshException {
		this.dicName = this.param.getString("dn");
		this.description = this.param.getString("dc");
		Integer sharePolicyI = this.param.getInteger("sp");
		if (sharePolicyI == null) sharePolicyI = 1;
		switch (sharePolicyI) {
		case 2:
			sharePolicy = "protected";
			break;
		case 3:
			sharePolicy = "private";
			break;
		default:
			sharePolicy = "public";
			break;
		}
		this.data = this.param.getJSONArray("dt");
	}
}
