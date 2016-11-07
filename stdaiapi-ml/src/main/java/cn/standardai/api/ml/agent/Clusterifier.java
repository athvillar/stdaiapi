package cn.standardai.api.ml.agent;

import com.alibaba.fastjson.JSONObject;

public interface Clusterifier {

	public JSONObject clusterify(JSONObject request);
}
