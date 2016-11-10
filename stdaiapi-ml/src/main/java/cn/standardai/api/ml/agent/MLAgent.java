package cn.standardai.api.ml.agent;

import com.alibaba.fastjson.JSONObject;

public class MLAgent {

	public JSONObject classify(JSONObject request) {

		String algorithm = request.getString("algorithm");
		if (algorithm == null || "".equals(algorithm) || "KNN".equalsIgnoreCase(algorithm)) {
			KNNClassifier agent = new KNNClassifier();
			return agent.classify(request);
		} else {
			return null;
		}
	}

	public JSONObject cluster(JSONObject request) {

		String algorithm = request.getString("algorithm");
		if (algorithm == null || "".equals(algorithm) || "KMEANS".equalsIgnoreCase(algorithm)) {
			KMeansClusterer agent = new KMeansClusterer();
			return agent.cluster(request);
		} else {
			return null;
		}
	}

	public JSONObject makeDecisionTree(JSONObject request) {

		String algorithm = request.getString("algorithm");
		if (algorithm == null || "".equals(algorithm) || "C45".equalsIgnoreCase(algorithm)) {
			C45Decider agent = new C45Decider();
			return agent.decide(request);
		} else {
			return null;
		}
	}

	public void done() {
		// Nothing to do
	}
}
