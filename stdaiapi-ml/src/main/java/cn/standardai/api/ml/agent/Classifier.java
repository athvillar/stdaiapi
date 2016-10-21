package cn.standardai.api.ml.agent;

import com.alibaba.fastjson.JSONObject;

public interface Classifier {

	public JSONObject classify(JSONObject request);
}
