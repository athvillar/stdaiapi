package cn.standardai.api.ml.agent;

import com.alibaba.fastjson.JSONObject;

public class ClassifyAgent {

	public JSONObject classify(JSONObject request) {

		String algorithm = request.getString("algorithm");
		if (algorithm == null || "".equals(algorithm) || "KNN".equalsIgnoreCase(algorithm)) {
			KNNClassifier agent = new KNNClassifier();
			return agent.classify(request);
		} else {
			return null;
		}
	}

	public void done() {
		// Nothing to do
	}
}
