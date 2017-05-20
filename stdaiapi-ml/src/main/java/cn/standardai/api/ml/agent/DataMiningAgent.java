package cn.standardai.api.ml.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.base.AuthAgent;
import cn.standardai.api.core.util.JsonUtil;
import cn.standardai.api.dao.DataDao;
import cn.standardai.api.dao.DatasetDao;
import cn.standardai.api.dao.TokenDao;
import cn.standardai.api.dao.bean.Data;
import cn.standardai.api.dao.bean.Dataset;
import cn.standardai.lib.algorithm.c45.C45;
import cn.standardai.lib.algorithm.c45.MetaData;
import cn.standardai.lib.algorithm.kmeans.KMeans;
import cn.standardai.lib.algorithm.kmeans.KMeansNode;
import cn.standardai.lib.algorithm.kmeans.NumberNode;
import cn.standardai.lib.algorithm.knn.DoubleNode;
import cn.standardai.lib.algorithm.knn.KNN;
import cn.standardai.lib.algorithm.knn.KNNNode;

public class DataMiningAgent extends AuthAgent {

	public static enum DataType { training, target };

	public static enum DataFormat { json, csv };

	private JSONObject requestJSONObject;

	private Map<DataType, Dataset> datasetMap = new HashMap<DataType, Dataset>();

	private DataDao dataDao = daoHandler.getMySQLMapper(DataDao.class);

	private DatasetDao datasetDao = daoHandler.getMySQLMapper(DatasetDao.class);

	public JSONObject classify(JSONObject request) {

		String algorithm = request.getString("algorithm");
		if (algorithm == null || "".equals(algorithm) || "KNN".equalsIgnoreCase(algorithm)) {

			List<JSONObject> trainingSet = loadData(DataType.training, JSONObject.class);
			DataFormat dataFormat = getDataFormat(DataType.training);
			ArrayList<KNNNode<?, ?>> nodeList = new ArrayList<KNNNode<?, ?>>();
			for (int i = 0; i < trainingSet.size(); i++) {
				///JSONObject data1;
				//if (dataFormat == DataFormat.json)
				JSONObject data1 = trainingSet.get(i);
				JSONArray features = data1.getJSONArray("features");
				DoubleNode node = new DoubleNode(JsonUtil.toList(features, Double.class), data1.getString("category"));
				nodeList.add(node);
			}

			KNN knn = new KNN(nodeList);

			List<JSONObject> targetSet = loadData(DataType.target, JSONObject.class);
			JSONObject result = new JSONObject();
			JSONArray data = new JSONArray();
			for (int i = 0; i < targetSet.size(); i++) {
				JSONObject data1 = targetSet.get(i);
				JSONArray features = data1.getJSONArray("features");
				DoubleNode node = (DoubleNode)knn.sort(new DoubleNode(JsonUtil.toList(features, Double.class)));
				data1.put("category", node.getCategory());
				data.add(data1);
			}

			result.put("data", data);
			return result;
		} else {
			return null;
		}
	}

	public JSONObject cluster(JSONObject request) {

		String algorithm = request.getString("algorithm");
		if (algorithm == null || "".equals(algorithm) || "KMEANS".equalsIgnoreCase(algorithm)) {

			List<JSONObject> trainingSet = loadData(DataType.training, JSONObject.class);
			ArrayList<KMeansNode<?, ?>> nodeList = new ArrayList<KMeansNode<?, ?>>();
			for (int i = 0; i < trainingSet.size(); i++) {
				JSONObject data1 = trainingSet.get(i);
				JSONArray features = data1.getJSONArray("features");
				NumberNode node = new NumberNode(JsonUtil.toList(features, Double.class));
				nodeList.add(node);
			}

			// TODO
			//KMeans kmeans = new KMeans(nodeList, 3, KMeans.InitMethod.KMEANSPLUS, KMeans.FinishCondition.MAX_MOVE, 5);
			Integer clusterNumber = requestJSONObject.getInteger("clusterNumber");
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
		} else {
			return null;
		}
	}

	public JSONObject makeDecisionTree(JSONObject request) {

		String algorithm = request.getString("algorithm");
		if (algorithm == null || "".equals(algorithm) || "C45".equalsIgnoreCase(algorithm)) {

			List<String> trainingSet = loadData(DataType.training, String.class);
			if (trainingSet == null || trainingSet.size() <= 2) return null;

			String[] attrs = trainingSet.get(0).split(",");;
			String[] props = trainingSet.get(1).split(",");
			MetaData metaData = new MetaData(attrs, props);
			List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
			for (int i = 2; i < trainingSet.size(); i++) {
				String[] dataInfo = trainingSet.get(i).split(",");
				Map<String, String> data = new HashMap<String, String>();
				for (int j = 0; j < dataInfo.length; j++) {
					data.put(attrs[j], dataInfo[j]);
				}
				dataList.add(data);
			}

			// TODO
			C45 c45 = new C45(metaData, dataList, 1);
			c45.run();
			System.out.println();

			JSONObject result = new JSONObject();
			result.put("decisionTree", c45.getRoot().toJSONObject());
			return result;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> loadData(DataType dataType, Class<T> type) {
		String dataId = getDatasetId(dataType);
		List<T> dataList = new ArrayList<T>();
		if (dataId == null || "".equals(dataId)) {
			JSONObject dataJSONObject = getDatasetJSONObject(dataType);
			JSONArray data = dataJSONObject.getJSONArray("data");
			if (data == null || data.size() == 0) return null;
			for (int i = 0; i < data.size(); i++) {
				dataList.add(data.getObject(i, type));
			}
		} else {
			List<Data> data = dataDao.selectDataByDatasetId(dataId);
			if (type == JSONObject.class) {
				for (Data data1 : data) {
					dataList.add((T)JSONObject.parse(data1.getX()));
				}
			} else {
				for (Data data1 : data) {
					dataList.add((T)(data1.getX()));
				}
			}
		}
		return dataList;
	}

	public String getDatasetId(DataType dataType) {
		if (datasetMap.get(dataType) != null) return datasetMap.get(dataType).getDatasetId();
		setDataset(dataType);
		if (datasetMap.get(dataType) == null) return null;
		return datasetMap.get(dataType).getDatasetId();
	}

	public DataFormat getDataFormat(DataType dataType) {
		if (datasetMap.get(dataType) != null) return parseDataFormat(datasetMap.get(dataType).getFormat());
		setDataset(dataType);
		if (datasetMap.get(dataType) == null) return null;
		return parseDataFormat(datasetMap.get(dataType).getFormat());
	}

	private DataFormat parseDataFormat(String format) {
		switch (format) {
		case "json":
			return DataFormat.json;
		case "csv":
			return DataFormat.csv;
		default:
			return null;
		}
	}

	private void setDataset(DataType dataType) {
		if (getDatasetJSONObject(dataType) == null) return;
		String datasetId = getDatasetJSONObject(dataType).getString("id");
		if (datasetId != null) datasetMap.put(dataType, datasetDao.selectById(datasetId));
		String datasetName = getDatasetJSONObject(dataType).getString("name");
		if (datasetName == null) return;
		if (userId != null) datasetMap.put(dataType, datasetDao.selectByKey(datasetName, userId));
	}

	private JSONObject getDatasetJSONObject(DataType dataType) {
		switch (dataType) {
		case training:
			return requestJSONObject.getJSONObject("trainingSet");
		case target:
			return requestJSONObject.getJSONObject("targetSet");
		default:
			return null;
		}
	}
}
