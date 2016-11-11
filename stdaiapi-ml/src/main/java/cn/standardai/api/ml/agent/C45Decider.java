package cn.standardai.api.ml.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.dao.base.DaoHandler;
import cn.standardai.api.ml.bean.Request;
import cn.standardai.api.ml.bean.Request.DataType;
import cn.standardai.lib.algorithm.c45.C45;
import cn.standardai.lib.algorithm.c45.MetaData;

public class C45Decider implements Decider {

	private DaoHandler daoHandler = new DaoHandler(false);

	public JSONObject decide(JSONObject requestJSONObject) {

		Request request = new Request(requestJSONObject, daoHandler);

		List<String> trainingSet = request.loadData(DataType.training, String.class);
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
	}

	public void done() {
		if (daoHandler != null) {
			daoHandler.releaseSession();
			daoHandler = null;
		}
	}
}
