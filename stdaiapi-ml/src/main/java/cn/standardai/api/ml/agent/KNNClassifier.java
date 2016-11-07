package cn.standardai.api.ml.agent;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.bean.RequestBody;
import cn.standardai.api.core.util.JsonUtil;
import cn.standardai.api.dao.DataDao;
import cn.standardai.api.dao.base.DaoHandler;
import cn.standardai.api.ml.sample.Sample;
import cn.standardai.lib.algorithm.knn.DoubleNode;
import cn.standardai.lib.algorithm.knn.KNN;
import cn.standardai.lib.algorithm.knn.KNNNode;

public class KNNClassifier implements Classifier {

	private DaoHandler daoHandler = new DaoHandler(false);

	public JSONObject classify(JSONObject request) {

		// TODO Request body归一化处理
		RequestBody body = new RequestBody(request);

		List<JSONObject> trainingSet = loadData(request.getJSONObject("trainingSet"));
		ArrayList<KNNNode<?, ?>> nodeList = new ArrayList<KNNNode<?, ?>>();
		for (int i = 0; i < trainingSet.size(); i++) {
			JSONObject data1 = trainingSet.get(i);
			JSONArray features = data1.getJSONArray("features");
			DoubleNode node = new DoubleNode(JsonUtil.toList(features, Double.class), data1.getString("category"));
			nodeList.add(node);
		}

		KNN knn = new KNN();
		knn.setNodes(nodeList);

		List<JSONObject> targetSet = loadData(request.getJSONObject("targetSet"));
		JSONArray categories = new JSONArray();
		for (int i = 0; i < targetSet.size(); i++) {
			JSONObject data1 = targetSet.get(i);
			JSONArray features = data1.getJSONArray("features");
			DoubleNode node = (DoubleNode)knn.sort(new DoubleNode(JsonUtil.toList(features, Double.class)));
			categories.add(node.getCategory());
		}

		JSONObject result = new JSONObject();
		result.put("data", result);

		return result;
	}

	private List<JSONObject> loadData(JSONObject dataJSONObject) {
		String dataId = dataJSONObject.getString("id");
		if (dataId == null || "".equals(dataId)) {
			JSONArray data = dataJSONObject.getJSONArray("data");
			List<JSONObject> dataList = new ArrayList<JSONObject>();
			for (int i = 0; i < dataJSONObject.size(); i++) {
				dataList.add(data.getJSONObject(i));
			}
			return dataList;
		} else if (dataId.contains("sample")) {
			return Sample.sampleKNNTrainingData();
		} else if ("knnTargetSample".equals(dataId)) {
			return Sample.sampleKNNTrainingData();
		} else {
			DataDao dataDao = daoHandler.getMySQLMapper(DataDao.class);
			return dataDao.selectDataById(dataId);
		}
	}

	public void done() {
		if (daoHandler != null) {
			daoHandler.releaseSession();
			daoHandler = null;
		}
	}
}
