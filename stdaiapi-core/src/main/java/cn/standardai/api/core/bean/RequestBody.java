package cn.standardai.api.core.bean;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class RequestBody {

	private JSONObject body;

	public RequestBody(JSONObject body) {
		this.body = body;
	}

	public String getTrainingSetId() {
		if (body == null) return null;
		if (body.getJSONObject("trainingSet") == null) return null;
		return body.getJSONObject("trainingSet").getString("id");
	}

	public JSONArray getTrainingSet() {
		if (body == null) return null;
		if (body.getJSONObject("trainingSet") == null) return null;
		return body.getJSONObject("trainingSet").getJSONArray("data");
	}

	public String getTargetSetId() {
		if (body == null) return null;
		if (body.getJSONObject("targetSet") == null) return null;
		return body.getJSONObject("targetSet").getString("id");
	}

	public JSONArray getTargetSet() {
		if (body == null) return null;
		if (body.getJSONObject("targetSet") == null) return null;
		return body.getJSONObject("targetSet").getJSONArray("data");
	}
}
