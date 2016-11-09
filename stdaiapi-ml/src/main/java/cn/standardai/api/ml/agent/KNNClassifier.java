package cn.standardai.api.ml.agent;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.bean.RequestBody;
import cn.standardai.api.core.util.JsonUtil;
import cn.standardai.api.dao.DataDao;
import cn.standardai.api.dao.base.DaoHandler;
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
