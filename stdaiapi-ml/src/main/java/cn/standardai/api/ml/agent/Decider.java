package cn.standardai.api.ml.agent;

import com.alibaba.fastjson.JSONObject;

public interface Decider {

	public JSONObject decide(JSONObject request);
}
