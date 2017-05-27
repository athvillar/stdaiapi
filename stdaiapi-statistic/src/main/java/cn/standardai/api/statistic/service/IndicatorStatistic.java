package cn.standardai.api.statistic.service;

import java.util.List;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.dao.base.DaoHandler;
import cn.standardai.api.es.bean.AggVerb;
import cn.standardai.api.es.bean.Filter;
import cn.standardai.api.es.bean.QueryInfoEx;
import cn.standardai.api.es.exception.ESException;
import cn.standardai.api.es.service.ESService;
import cn.standardai.api.statistic.exception.StatisticException;

public class IndicatorStatistic implements Statistic {

	private final static String INDICE = "indicator";

	private DaoHandler daoHandler = new DaoHandler();

	@Override
	public JSONObject statistic(JSONObject queryParam) throws StatisticException {
		try {
			return query(QueryInfoEx.parse(queryParam));
		} catch (ESException e) {
			throw new StatisticException("参数解析失败", e);
		} finally {
			if (daoHandler != null) daoHandler.releaseSession();
		}
	}

	public JSONObject query(QueryInfoEx queryInfo) throws StatisticException {

		return querySingle(queryInfo);
	}

	public JSONObject querySingle(QueryInfoEx queryInfo) throws StatisticException {

		// Query & aggregation from es
		JSON rawData = null;
		try {
			List<AggVerb> aggrVerbs = QueryInfoEx.makeAggrVerbs(queryInfo);
			List<Filter> filters = QueryInfoEx.makeFilters(queryInfo);
			rawData = ESService.aggregate(INDICE, INDICE, filters, aggrVerbs);
		} catch (ESException e) {
			throw new StatisticException("Elasticsearch检索错误", e);
		}

		// Move aggregation results out of {}, add aggregation names
		JSONObject firstData = flattenJSON(rawData, queryInfo.getAggrKeys());

		return firstData;
	}

	private JSONObject flattenJSON(JSON targetJSON, List<String> aggrKeys) {
		if (targetJSON instanceof JSONArray) {
			return flattenJSONArray((JSONArray) targetJSON, aggrKeys);
		} else {
			return flattenJSONObject((JSONObject) targetJSON, aggrKeys);
		}
	}

	private JSONObject flattenJSONArray(JSONArray target, List<String> aggrKeys) {

		JSONObject destiny = new JSONObject();

		if (aggrKeys == null) {
		} else if (aggrKeys.size() > 2) {
			destiny.put("key_field", aggrKeys.get(0));
			destiny.put("value_field", "data");
		} else if (aggrKeys.size() == 2) {
			destiny.put("key_field", aggrKeys.get(0));
			destiny.put("value_field", aggrKeys.get(1));
		} else if (aggrKeys.size() == 1) {
			destiny.put("value_field", aggrKeys.get(0));
		}

		JSONArray mergedJSONArray = new JSONArray();
		for (int i = 0; i < ((JSONArray) target).size(); i++) {
			JSONObject targetJSONObject = ((JSONArray) target).getJSONObject(i);
			JSONObject resultJSONObject = flattenJSONObject(targetJSONObject, aggrKeys);
			mergedJSONArray.add(resultJSONObject);
		}
		destiny.put("datalist", mergedJSONArray);

		return destiny;
	}

	private JSONObject flattenJSONObject(JSONObject target, List<String> aggrKeys) {
		JSONObject result = new JSONObject();
		for (Entry<String, Object> entry : target.entrySet()) {
			switch (entry.getKey()) {
			case "key":
				// {"key":"x"} -> {"aggrKey":"x"}
				result.put(aggrKeys.get(0), entry.getValue());
				break;
			case "value":
				if (entry.getValue() instanceof JSONObject) {
					// {"key":"x", "value":{"key1":"x","key2":"x"}} ->
					// {"key":"x", "key1":"x", "key2":"x"}
					for (Entry<String, Object> subEntry : ((JSONObject) entry.getValue()).entrySet()) {
						result.put(subEntry.getKey(), subEntry.getValue());
					}
				} else {
					// {"key":"x", "value":[{"key":"x",
					// "value":{"key1":"x","key2":"x"}}]} -> {"key":"x",
					// "data":{"key":"x", "key1":"x", "key2":"x"}}
					result.put("data",
							flattenJSONArray((JSONArray) entry.getValue(), aggrKeys.subList(1, aggrKeys.size())));
				}
				break;
			default:
				// {"x":"y"} -> {"x":"y"}
				result.put(entry.getKey(), entry.getValue());
				break;
			}
		}
		return result;
	}
}
