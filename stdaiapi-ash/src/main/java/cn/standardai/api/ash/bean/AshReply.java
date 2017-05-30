package cn.standardai.api.ash.bean;

import com.alibaba.fastjson.JSONObject;

public class AshReply {

	public enum Status { success, warn, failure };

	public Status status;

	public String display;

	public JSONObject hidden;

	public String message;
}
