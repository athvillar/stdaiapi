package cn.standardai.api.ml.agent;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.util.JsonUtil;
import cn.standardai.api.dao.DataDao;
import cn.standardai.api.dao.base.DaoHandler;
import cn.standardai.api.dao.bean.JsonData;
import cn.standardai.lib.algorithm.knn.DoubleNode;
import cn.standardai.lib.algorithm.knn.KNN;
import cn.standardai.lib.algorithm.knn.KNNNode;

public class KNNClassifier implements Classifier {

	private DaoHandler daoHandler = new DaoHandler(false);

	public JSONObject classify(JSONObject request) {

		List<JsonData> jsonDataList = loadJsonData(request.getString("dataSetId"));
		ArrayList<KNNNode<?, ?>> nodeList = new ArrayList<KNNNode<?, ?>>();
		for (int i = 0; i < jsonDataList.size(); i++) {
			JSONObject data1 = JSONObject.parseObject(jsonDataList.get(i).getData());
			JSONArray features = data1.getJSONArray("features");
			DoubleNode node = new DoubleNode(JsonUtil.toList(features, Double.class), data1.getString("category"));
			nodeList.add(node);
		}

		KNN knn = new KNN();
		knn.setNodes(nodeList);

		JSONArray data = request.getJSONArray("data");
		JSONArray categories = new JSONArray();
		for (int i = 0; i < data.size(); i++) {
			JSONObject data1 = JSONObject.parseObject(jsonDataList.get(i).getData());
			JSONArray features = data1.getJSONArray("features");
			DoubleNode node = (DoubleNode)knn.sort(new DoubleNode(JsonUtil.toList(features, Double.class)));
			categories.add(node.getCategory());
		}

		JSONObject result = new JSONObject();
		result.put("data", result);

		return result;
	}

	private List<JsonData> loadJsonData(String dataSetId) {
		DataDao dataDao = daoHandler.getMySQLMapper(DataDao.class);
		return dataDao.selectJsonDataByDataSetId(dataSetId);
	}

	public void done() {
		if (daoHandler != null) {
			daoHandler.releaseSession();
			daoHandler = null;
		}
	}
}
