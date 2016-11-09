package cn.standardai.api.ml.agent;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.util.JsonUtil;
import cn.standardai.api.dao.DataDao;
import cn.standardai.api.dao.base.DaoHandler;
import cn.standardai.lib.algorithm.kmeans.KMeans;
import cn.standardai.lib.algorithm.kmeans.KMeansNode;
import cn.standardai.lib.algorithm.kmeans.NumberNode;

public class KMeansCluster implements Clusterer {

	private DaoHandler daoHandler = new DaoHandler(false);

	public JSONObject cluster(JSONObject request) {

		List<JSONObject> trainingSet = loadData(request.getJSONObject("trainingSet"));
		ArrayList<KMeansNode<?, ?>> nodeList = new ArrayList<KMeansNode<?, ?>>();
		for (int i = 0; i < trainingSet.size(); i++) {
			JSONObject data1 = trainingSet.get(i);
			JSONArray features = data1.getJSONArray("features");
			NumberNode node = new NumberNode(JsonUtil.toList(features, Double.class));
			nodeList.add(node);
		}

		Integer clusterNumber = request.getInteger("clusterNumber");
		// TODO
		//KMeans kmeans = new KMeans(nodeList, 3, KMeans.InitMethod.KMEANSPLUS, KMeans.FinishCondition.MAX_MOVE, 5);
		KMeans kmeans = new KMeans(nodeList, clusterNumber == null ? 3 : clusterNumber);
		kmeans.sort();

		JSONArray clusters = new JSONArray();
		for (KMeansNode<?,?> centroid : kmeans.getCentroids()) {
			System.out.println("cluster start:");
			JSONArray records = new JSONArray();
			for (KMeansNode<?,?> theNode : kmeans.getClusters().get(centroid)) {
				JSONArray features = new JSONArray();
				for (Object theFeature : theNode.getFeature()) {
					features.add(theFeature);
				}
				records.add(features);
			}
			clusters.add(records);
		}

		JSONObject result = new JSONObject();
		result.put("cluster", clusters);
		return result;
	}

	private List<JSONObject> loadData(JSONObject dataJSONObject) {
		String dataId = dataJSONObject.getString("id");
		List<JSONObject> dataList = new ArrayList<JSONObject>();
		if (dataId == null || "".equals(dataId)) {
			JSONArray data = dataJSONObject.getJSONArray("data");
			for (int i = 0; i < data.size(); i++) {
				dataList.add(data.getJSONObject(i));
			}
		} else {
			DataDao dataDao = daoHandler.getMySQLMapper(DataDao.class);
			List<String> dataString = dataDao.selectDataByDatasetId(dataId);
			for (String dataString1 : dataString) {
				dataList.add(JSONObject.parseObject(dataString1));
			}
		}
		return dataList;
	}

	public void done() {
		if (daoHandler != null) {
			daoHandler.releaseSession();
			daoHandler = null;
		}
	}
}
