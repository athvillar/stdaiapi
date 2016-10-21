package cn.standardai.api.es.service;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.es.bean.AggVerb;
import cn.standardai.api.es.bean.StatsAggVerb;
import cn.standardai.api.es.bean.StatsAggVerb.StatsType;

public class ESResultParser {

	public static JSON parse(JSONObject esResult, List<AggVerb> aggVerbs) {
		if (esResult.containsKey("aggregations")) {
			JSONObject aggrJson = esResult.getJSONObject("aggregations");
			if (aggrJson != null) {
				return extract(aggrJson, aggVerbs, 0);
			} else {
				return new JSONArray();
			}
		} else {
			return new JSONArray();
		}
	}

	private static JSON extract(JSONObject srcJson, List<AggVerb> aggVerbs, int currLvl) {
		JSONObject dstJson = new JSONObject();
		JSONArray subListJson = null;
		JSONArray dstSubListJson = new JSONArray();
		if (srcJson != null) {
			if (aggVerbs.get(currLvl).isAddCnt()) {
				Integer cnt = srcJson.getInteger("doc_count");
				dstJson.put("cnt", cnt);
				if (aggVerbs.get(currLvl).isParent()) {
					dstSubListJson.add(dstJson);
				}
			}
			switch (aggVerbs.get(currLvl).getAggType()) {
			case stats:
				for (StatsType type : ((StatsAggVerb)(aggVerbs.get(currLvl))).getStatsTypes()) {
					Object value = srcJson.getJSONObject(StatsAggVerb.getStatsTypeString_(type) + aggVerbs.get(currLvl).getField()).get("value");
					dstJson.put(StatsAggVerb.getStatsTypeString(type), value);
				}
				break;
			case term:
				subListJson = srcJson.getJSONObject(
						aggVerbs.get(currLvl).getTypeString_() + aggVerbs.get(currLvl).getField()
				).getJSONArray("buckets");
				for (int i = 0; i < subListJson.size(); i++) {
					dstJson = new JSONObject();
					if (currLvl < aggVerbs.size() - 1) {
						dstJson.put("key", ((JSONObject)subListJson.get(i)).getString("key"));
						dstJson.put("value", extract((JSONObject)subListJson.get(i), aggVerbs, currLvl + 1));
					} else {
						dstJson.put("key", ((JSONObject)subListJson.get(i)).getString("key"));
						dstJson.put("value", new JSONObject());
					}
					if (aggVerbs.get(currLvl).isParent()) {
						dstSubListJson.add(dstJson);
					}
				}
				break;
			case range:
				subListJson = srcJson.getJSONObject(
						aggVerbs.get(currLvl).getTypeString_() + aggVerbs.get(currLvl).getField()
						).getJSONArray("buckets");
				for (int i = 0; i < subListJson.size(); i++) {
					dstJson = new JSONObject();
					if (currLvl < aggVerbs.size() - 1) {
						dstJson.put("key", ((JSONObject)subListJson.get(i)).getString("key"));
						dstJson.put("value", extract((JSONObject)subListJson.get(i), aggVerbs, currLvl + 1));
					} else {
						dstJson.put("key", ((JSONObject)subListJson.get(i)).getString("key"));
						dstJson.put("value", new JSONObject());
					}
					if (aggVerbs.get(currLvl).isParent()) {
						dstSubListJson.add(dstJson);
					}
				}
				break;
			case datehistogram:
				subListJson = srcJson.getJSONObject(
						aggVerbs.get(currLvl).getTypeString_() + aggVerbs.get(currLvl).getField()
				).getJSONArray("buckets");
				for (int i = 0; i < subListJson.size(); i++) {
					dstJson = new JSONObject();
					if (currLvl < aggVerbs.size() - 1) {
						dstJson.put("key", ((JSONObject)subListJson.get(i)).getString("key_as_string"));
						dstJson.put("value", extract((JSONObject)subListJson.get(i), aggVerbs, currLvl + 1));
					} else {
						dstJson.put("key", ((JSONObject)subListJson.get(i)).getString("key_as_string"));
						dstJson.put("value", new JSONObject());
					}
					if (aggVerbs.get(currLvl).isParent()) {
						dstSubListJson.add(dstJson);
					}
				}
				break;
			default:
				break;
			}
		}

		if (aggVerbs.get(currLvl).isParent()) {
			return dstSubListJson;
		} else {
			return dstJson;
		}
	}
}
