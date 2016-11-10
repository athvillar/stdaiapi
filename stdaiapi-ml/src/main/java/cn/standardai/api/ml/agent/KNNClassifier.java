package cn.standardai.api.ml.agent;

import java.text.Format;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.util.JsonUtil;
import cn.standardai.api.dao.base.DaoHandler;
import cn.standardai.api.ml.bean.Request;
import cn.standardai.api.ml.bean.Request.DataFormat;
import cn.standardai.api.ml.bean.Request.DataType;
import cn.standardai.lib.algorithm.knn.DoubleNode;
import cn.standardai.lib.algorithm.knn.KNN;
import cn.standardai.lib.algorithm.knn.KNNNode;

public class KNNClassifier implements Classifier {

	private DaoHandler daoHandler = new DaoHandler(false);

	public JSONObject classify(JSONObject requestJSONObject) {

		Request request = new Request(requestJSONObject, daoHandler);

		List<JSONObject> trainingSet = request.loadData(DataType.training, JSONObject.class);
		DataFormat dataFormat = request.getDataFormat(DataType.training);
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

		List<JSONObject> targetSet = request.loadData(DataType.target, JSONObject.class);
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

	public void done() {
		if (daoHandler != null) {
			daoHandler.releaseSession();
			daoHandler = null;
		}
	}
}
