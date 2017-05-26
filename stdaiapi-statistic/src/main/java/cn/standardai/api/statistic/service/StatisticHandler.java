package cn.standardai.api.statistic.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.statistic.exception.StatisticException;

public class StatisticHandler {

	public JSONObject handle(JSONObject request) throws StatisticException  {
		return handle(request.getJSONArray("query"));
	}

	public JSONObject handle(JSONArray queryParam) throws StatisticException  {

		JSONObject result = new JSONObject();

		if (queryParam != null && queryParam.size() != 0) {
			if (queryParam.size() == 1) {
				// 1 query
				return dispatch(queryParam.getJSONObject(0));
			} else {
				// Multiple queries
				JSONArray queryResults = new JSONArray();
				for (int i = 0; i < queryParam.size(); i++) {
					JSONObject queryResult = dispatch(queryParam.getJSONObject(i));
					JSONObject dataJSONObject = new JSONObject();
					dataJSONObject.put("data", queryResult);
					dataJSONObject.put("query", i + 1);
					queryResults.add(dataJSONObject);
				}
				result.put("datalist", queryResults);
				result.put("key_field" , "query");
				result.put("value_field", "data");
			}
		} else {
			throw new StatisticException("缺少query参数");
		}

		return result;
	}

	public JSONObject dispatch(JSONObject queryParam) throws StatisticException {
		String statisticType = queryParam.getString("type");
		if (statisticType == null) statisticType = "datapoint";
		Statistic stats = StatisticFactory.getInstance(statisticType);
		return stats.statistic(queryParam);
	}
}
