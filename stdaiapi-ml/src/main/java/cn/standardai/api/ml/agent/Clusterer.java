package cn.standardai.api.ml.agent;

import com.alibaba.fastjson.JSONObject;

public interface Clusterer {

	public JSONObject cluster(JSONObject request);
}
