package cn.standardai.api.statistic.service;

import java.util.List;
import java.util.Map;

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
			Map<String, String> sorts = QueryInfoEx.makeSorts(queryInfo);
			rawData = ESService.aggregateEx(INDICE, INDICE, filters, aggrVerbs, sorts);
		} catch (ESException e) {
			throw new StatisticException("Elasticsearch检索错误", e);
		}

		// Move aggregation results out of {}, add aggregation names
		JSONObject firstData = flattenJSON(rawData);

		return firstData;
	}

	private JSONObject flattenJSON(JSON targetJSON) {
		return flattenJSONObject((JSONObject) targetJSON);
	}

	private JSONObject flattenJSONObject(JSONObject target) {
		JSONObject data = new JSONObject();
		JSONArray hits = target.getJSONObject("hits").getJSONArray("hits");
		for (int i = 0; i < hits.size(); i++) {
			JSONObject hit = hits.getJSONObject(i);
			JSONObject source = hit.getJSONObject("_source");
			if (data.getJSONArray(source.getString("indicator")) == null) {
				JSONArray indicator = new JSONArray();
				JSONObject simple = new JSONObject();
				simple.put("epoch", source.get("epoch"));
				simple.put("value", source.get("value"));
				indicator.add(simple);
				data.put(source.getString("indicator"), indicator);
			} else {
				JSONObject simple = new JSONObject();
				simple.put("epoch", source.get("epoch"));
				simple.put("value", source.get("value"));
				data.getJSONArray(source.getString("indicator")).add(simple);
			}
		}
		return data;
	}
}
