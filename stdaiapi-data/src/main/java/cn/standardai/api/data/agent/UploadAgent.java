package cn.standardai.api.data.agent;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.util.MathUtil;
import cn.standardai.api.dao.DataDao;
import cn.standardai.api.dao.base.DaoHandler;
import cn.standardai.api.dao.bean.JsonData;

public class UploadAgent {

	private DaoHandler daoHandler = new DaoHandler(true);

	public JSONObject saveJSONData(JSONObject dataRequest) {

		JSONObject result = new JSONObject();
		String token = dataRequest.getString("token");
		String dataGroup = dataRequest.getString("dataGroup");
		String dataSet = dataRequest.getString("dataSet");

		if (token == null) token = MathUtil.random(13);
		if (dataGroup == null) dataGroup = "default";
		if (dataSet == null) dataSet = "default";

		String dataSetId = getDataSetId(token, dataGroup, dataSet);
		JSONArray data = dataRequest.getJSONArray("data");
		saveJSONData(dataSetId, data);

		result.put("token", token);
		result.put("dataGroup", dataGroup);
		result.put("dataSet", dataSet);

		return result;
	}

	private void saveJSONData(String dataSetId, JSONArray data) {
		DataDao dataDao = daoHandler.getMySQLMapper(DataDao.class);
		for (int i = 0; i < data.size(); i++) {
			JSONObject data1 = data.getJSONObject(i);
			JsonData param = new JsonData();
			param.setId(MathUtil.random(18));
			param.setDataSetId(dataSetId);
			param.setData(data1.toJSONString());
			dataDao.insertJsonData(param);
		}
	}

	private String getDataSetId(String token, String dataGroup, String dataSet) {
		// TODO query or save dataset
		return token + dataGroup + dataSet;
	}

	public void done() {
		daoHandler.releaseSession();
	}
}
