package cn.standardai.api.ml.agent;

import com.alibaba.fastjson.JSONObject;

public class ClusterAgent {

	public JSONObject cluster(JSONObject request) {

		String algorithm = request.getString("algorithm");
		if (algorithm == null || "".equals(algorithm) || "KMEANS".equalsIgnoreCase(algorithm)) {
			KMeansCluster agent = new KMeansCluster();
			return agent.cluster(request);
		} else {
			return null;
		}
	}

	public void done() {
		// Nothing to do
	}
}
