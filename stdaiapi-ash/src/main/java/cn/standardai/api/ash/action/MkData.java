package cn.standardai.api.ash.action;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.agent.ArgsHelper;
import cn.standardai.api.ash.base.Action;
import cn.standardai.api.ash.base.AshCommand.HttpMethod;
import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.ash.exception.ParamException;
import cn.standardai.api.core.bean.Context;

public class MkData extends Action {

	private static String[][][] dialog = new String[][][] {
		new String[][] {
			new String[] {"dn", "请输入数据名:"},
			new String[] {"dc", "请输入数据描述:"},
			new String[] {"sp", "请输入共享策略(1:public, 2:protected, 3:private):"},
			new String[] {"fm", "请输入格式(csv,bmp,...):"},
			new String[] {"kw", "请输入关键词(多个关键词用逗号分隔):"},
			new String[] {"fl", "二进制数据请输入要上传的本地文件或文件夹全路径(可以使用通配符*):"},
			new String[] {"se", "请输入通配符的起止范围，用逗号分隔(仅支持数字，例:1,90):"},
			new String[] {"dt", "文本数据请输入数据标签([[\"数据1数据\",\"数据1标签\"],[\"数据2数据\",\"数据2标签\"], ...]):"}
		}
	};

	static {
		ArgsHelper.regist(MkData.class, dialog);
	}

	public MkData() {
		setVp(dialog);
	}

	private String dataName;

	private String pUserId;

	private String pDataName;

	private String description;

	private String sharePolicy;

	private String format;

	private String keywords;

	private String files;

	private Integer start;

	private Integer end;

	private JSONArray data;

	@Override
	public AshReply exec() throws AshException {

		JSONObject body = new JSONObject();
		body.put("dataName", pDataName);
		body.put("description", description);
		body.put("sharePolicy", sharePolicy);
		body.put("format", format);
		body.put("keywords", keywords);
		body.put("data", data);

		if (files != null && !"".equals(files)) {
			body.put("type", "FILE");
		} else {
			body.put("type", "DATA");
			body.put("data", data);
		}
		// 建立数据集
		JSONObject j = comm.http(HttpMethod.POST, Context.getProp().getUrl().getData() + "/data", null, body);

		this.reply.display = "数据建立成功(dataId=" + j.getString("dataId") + ", name=" + this.userId + "/" + dataName + ")";
		if (files != null && !"".equals(files)) {
			String[] fileList = files.split(",");
			String fileSendS = "";
			for (int i = 0; i < fileList.length; i++) {
				if (this.start != null && this.end != null) {
					for (Integer num = this.start; num <= this.end; num++) {
						fileSendS += " -F files=@'" + fileList[i].replace("*", num.toString()) + "'";
					}
				} else {
					fileSendS += " -F files=@'" + fileList[i] + "'";
				}
			}
			this.reply.display += "\n请使用命令\"curl -XPOST -H 'token: " + this.token + "'" + fileSendS + " " +
					Context.getProp().getUrl().getData() + "/data/" + this.userId + "/" + dataName + "/files\"上传数据。";
			// 回调文件上传
			//String display = "数据上传中(dataId=" + j.getString("dataId") + ", name=" + this.userId + "/" + dataName + ")";
			//throw new CallbackException("数据上传回调", display,
			//		Context.getProp().getUrl().getData() + "/data" + this.userId + "/" + dataName, files.split(","));
		}
		return this.reply;
	}

	@Override
	public void readParam() throws AshException {
		this.dataName = this.param.getString("dn");
		if (dataName == null || "".equals(dataName)) throw new ParamException("缺少数据名");
		int idx = dataName.indexOf('/');
		if (idx != -1 && idx != dataName.length() - 1) {
			pUserId = dataName.substring(0, idx);
			pDataName = dataName.substring(idx + 1);
		} else {
			pUserId = this.userId;
			pDataName = dataName;
		}
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
		this.format = this.param.getString("fm");
		this.keywords = this.param.getString("kw");
		this.files = this.param.getString("fl");
		String se = this.param.getString("se");
		if (se != null && !"".equals(se)) {
			String[] ses = se.split(",");
			if (ses != null && ses.length == 2) {
				this.start = Integer.parseInt(ses[0]);
				this.end = Integer.parseInt(ses[1]);
			}
		}
		this.data = this.param.getJSONArray("dt");
	}
}
