package cn.standardai.api.ml.agent;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.util.MathUtil;

public class ClassifyAgent {

	public JSONObject classify(JSONObject request) {

		String algorithm = request.getString("algorithm");
		if (algorithm == null || "".equals(algorithm) || "KNN".equalsIgnoreCase(algorithm)) {
			Classifier agent = new KNNClassifier();
		}
		agent.load(request.getString("token"), request.getString("dataSet"));
	}

	public void done() {
		// Nothing to do
	}
}
