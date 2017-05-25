package com.kingdy.parkos.statistic.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kingdy.parkos.statistic.exception.StatisticException;

import cn.standardai.api.dao.base.DaoHandler;
import cn.standardai.api.es.bean.AggVerb;
import cn.standardai.api.es.bean.Filter;
import cn.standardai.api.es.bean.QueryInfo;
import cn.standardai.api.es.exception.ESException;
import cn.standardai.api.es.service.ESService;

public class DatapointStatistic implements Statistic {

	private final static String INDICE = "datapoint";

	private DaoHandler daoHandler = new DaoHandler();

	@Override
	public JSONObject statistic(JSONObject queryParam) throws StatisticException {
		try {
			return query(QueryInfo.parse(queryParam));
		} catch (ESException e) {
			throw new StatisticException("参数解析失败", e);
		} finally {
			if (daoHandler != null) daoHandler.releaseSession();
		}
	}

	public JSONObject query(QueryInfo queryInfo) throws StatisticException {
		if (queryInfo.getDatapointId() == null) {
			return querySum(queryInfo);
		} else {
			return querySingle(queryInfo);
		}
	}

	public JSONObject querySum(QueryInfo queryInfo) throws StatisticException {

		String queryInfoTimeFormat = null;
		char prefix;
		char[] suffix = null;
		String[] timeFormats = null;

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());

		switch (queryInfo.getTime().length()) {
		case 4:
			queryInfoTimeFormat = "yyyy";
			prefix = 'Y';
			suffix = new char[] { 'M', 'D', 'H' };
			timeFormats = new String[] { "yyyy", "yyyy/MM", "yyyy/MM/dd" };
			break;
		case 7:
			queryInfoTimeFormat = "yyyy/MM";
			prefix = 'M';
			suffix = new char[] { 'D', 'H' };
			timeFormats = new String[] { "yyyy/MM", "yyyy/MM/dd" };
			break;
		case 10:
			queryInfoTimeFormat = "yyyy/MM/dd";
			prefix = 'D';
			suffix = new char[] { 'H' };
			timeFormats = new String[] { "yyyy/MM/dd" };
			break;
		case 13:
			queryInfoTimeFormat = "yyyy/MM/dd hh";
			prefix = 'H';
			suffix = new char[] {};
			timeFormats = new String[] {};
			break;
		default:
			throw new StatisticException("Wrong time format: (" + queryInfo.getTime() + ")");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(queryInfoTimeFormat);
		Date nowTime = new Date();
		String now = sdf.format(nowTime);
		if (!(now.equals(queryInfo.getTime()))) {
			// Time in query is older than now, no need to get realtime data
			QueryInfo newQueryInfo = new QueryInfo(queryInfo);
			newQueryInfo.setDatapointId(queryInfo.getDatapointBase() + prefix);
			return querySingle(newQueryInfo);
		}

		// Get data from es in the order of month, date, hour
		JSONObject dataSum = null;
		for (int i = 0; i < suffix.length; i++) {
			QueryInfo newQueryInfo = new QueryInfo(queryInfo);
			newQueryInfo.setDatapointId(queryInfo.getDatapointBase() + suffix[i]);
			sdf = new SimpleDateFormat(timeFormats[i]);
			newQueryInfo.setTime(sdf.format(nowTime));
			JSONObject data1 = querySingle(newQueryInfo);
			dataSum = mergeJSONObject(dataSum, data1);
		}

		return dataSum;
	}

	private JSONObject mergeJSONObject(JSONObject object1, JSONObject object2) {

		if (object1 == null)
			return object2;
		if (object2 == null)
			return object1;

		String keyField = object1.getString("key_field");
		String valueField = object1.getString("value_field");
		if (keyField == null || valueField == null) {
			Double add1 = object1.getDouble("sum");
			Double add2 = object2.getDouble("sum");
			if (add1 == null && add2 == null)
				return new JSONObject();
			if (add1 == null)
				add1 = 0d;
			if (add2 == null)
				add2 = 0d;
			JSONObject sumJSONObject = new JSONObject();
			sumJSONObject.put("sum", add1 + add2);
			return sumJSONObject;
		}

		JSONArray array1 = object1.getJSONArray("datalist");
		JSONArray array2 = object2.getJSONArray("datalist");
		JSONArray sumArray = null;

		JSONObject destinyJSONObject = new JSONObject();
		destinyJSONObject.put("key_field", keyField);
		destinyJSONObject.put("value_field", valueField);

		if (array1 == null) {
			sumArray = array2;
			destinyJSONObject.put("datalist", sumArray);
			return destinyJSONObject;
		} else if (array2 == null) {
			sumArray = array1;
			destinyJSONObject.put("datalist", sumArray);
			return destinyJSONObject;
		} else if (array1.size() == 0) {
			sumArray = array2;
			destinyJSONObject.put("datalist", sumArray);
			return destinyJSONObject;
		} else if (array2.size() == 0) {
			sumArray = array1;
			destinyJSONObject.put("datalist", sumArray);
			return destinyJSONObject;
		} else {
			sumArray = new JSONArray();
		}

		int index1 = 0, index2 = 0;
		while (true) {
			if (index1 >= array1.size() && index2 >= array2.size()) {
				break;
			}
			if (index1 >= array1.size()) {
				for (int i = index2; i < array2.size(); i++) {
					sumArray.add(array2.get(index2));
				}
				break;
			}
			if (index2 >= array2.size()) {
				for (int i = index1; i < array1.size(); i++) {
					sumArray.add(array1.get(index1));
				}
				break;
			}
			String key1 = array1.getJSONObject(index1).getString(keyField);
			String key2 = array2.getJSONObject(index2).getString(keyField);
			if (key1.compareTo(key2) > 0) {
				sumArray.add(array2.get(index2));
				index2++;
				continue;
			} else if (key1.compareTo(key2) < 0) {
				sumArray.add(array1.get(index1));
				index1++;
				continue;
			} else {
				double add1 = array1.getJSONObject(index1).getDouble(valueField);
				double add2 = array2.getJSONObject(index2).getDouble(valueField);
				JSONObject sumJSONObject = new JSONObject();
				sumJSONObject.put(keyField, key1);
				sumJSONObject.put(valueField, add1 + add2);
				sumArray.add(sumJSONObject);
				index1++;
				index2++;
			}
		}

		destinyJSONObject.put("datalist", sumArray);
		return destinyJSONObject;
	}

	public JSONObject querySingle(QueryInfo queryInfo) throws StatisticException {

		// Query & aggregation from es
		String datapointId = queryInfo.getDatapointId();
		JSON rawData = null;
		try {
			List<AggVerb> aggrVerbs = QueryInfo.makeAggrVerbs(queryInfo);
			List<Filter> filters = QueryInfo.makeFilters(queryInfo);
			rawData = ESService.aggregate(INDICE, datapointId, filters, aggrVerbs);
		} catch (ESException e) {
			throw new StatisticException("Elasticsearch检索错误", e);
		}

		// Move aggregation results out of {}, add aggregation names
		JSONObject firstData = flattenJSON(rawData, queryInfo.getAggrKeys());
		JSONObject secondaryData = secondaryProcess(firstData, queryInfo.getSecondaryMethods());

		return secondaryData;
	}

	private JSONObject secondaryProcess(JSONObject firstData, String[] methods) throws StatisticException {

		if (methods == null || methods.length == 0)
			return firstData;

		String keyField = firstData.getString("key_field");
		String valueField = firstData.getString("value_field");
		if (keyField == null || valueField == null)
			return firstData;

		JSONObject data = new JSONObject();
		for (int methodIndex = 0; methodIndex < methods.length; methodIndex++) {
			String method = methods[methodIndex];
			switch (method) {
			case "maxavg":
				String maxavgKey = null;
				Double maxavgValue = Double.MIN_VALUE;
				JSONArray datalist = firstData.getJSONArray("datalist");
				if (datalist != null && datalist.size() > 0) {
					for (int i = 0; i < datalist.size(); i++) {
						JSONObject data1 = datalist.getJSONObject(i);
						JSONObject subData = data1.getJSONObject("data");
						JSONObject subDataAggrResult = null;
						if (subData != null) {
							// 多层aggr，先做里层
							subDataAggrResult = secondaryProcess(subData, new String[] { "avg" });
							subDataAggrResult.put(keyField, data1.getString(keyField));
							data1 = subDataAggrResult;
						}
						Double tmpValue = data1.getDouble("avg");
						if (tmpValue != null) {
							if (tmpValue > maxavgValue) {
								maxavgKey = data1.getString(keyField);
								maxavgValue = tmpValue;
							}
						}
					}
					data.put(method + keyField, maxavgKey);
					data.put(method + valueField, maxavgValue);
					data.put(method, maxavgValue);
					if (methods.length == 1) {
						data.put(keyField, maxavgKey);
						data.put(valueField, maxavgValue);
					}
				}
				break;
			case "max":
				String maxKey = null;
				Double maxValue = Double.MIN_VALUE;
				datalist = firstData.getJSONArray("datalist");
				if (datalist != null && datalist.size() > 0) {
					for (int i = 0; i < datalist.size(); i++) {
						JSONObject data1 = datalist.getJSONObject(i);
						if (data1.getDouble(valueField) > maxValue) {
							maxKey = data1.getString(keyField);
							maxValue = data1.getDouble(valueField);
						}
					}
					data.put(method + keyField, maxKey);
					data.put(method + valueField, maxValue);
					data.put(method, maxValue);
					if (methods.length == 1) {
						data.put(keyField, maxKey);
						data.put(valueField, maxValue);
					}
				}
				break;
			case "min":
				String minKey = null;
				Double minValue = Double.MAX_VALUE;
				datalist = firstData.getJSONArray("datalist");
				if (datalist != null && datalist.size() > 0) {
					for (int i = 0; i < datalist.size(); i++) {
						JSONObject data1 = datalist.getJSONObject(i);
						if (data1.getDouble(valueField) < minValue) {
							minKey = data1.getString(keyField);
							minValue = data1.getDouble(valueField);
						}
					}
					data.put(method + keyField, minKey);
					data.put(method + valueField, minValue);
					data.put(method, minValue);
					if (methods.length == 1) {
						data.put(keyField, minKey);
						data.put(valueField, minValue);
					}
				}
				break;
			case "avg":
				Double sumValue = 0.0;
				datalist = firstData.getJSONArray("datalist");
				if (datalist == null || datalist.size() == 0)
					return data;

				for (int i = 0; i < datalist.size(); i++) {
					JSONObject data1 = datalist.getJSONObject(i);
					sumValue += data1.getDouble(valueField);
				}
				data.put(method, sumValue / datalist.size());
				break;
			case "sum":
				sumValue = 0.0;
				datalist = firstData.getJSONArray("datalist");
				if (datalist == null || datalist.size() == 0)
					return data;

				for (int i = 0; i < datalist.size(); i++) {
					JSONObject data1 = datalist.getJSONObject(i);
					sumValue += data1.getDouble(valueField);
				}
				data.put(method, sumValue);
				break;
			default:
				throw new StatisticException("Unknown secondary method(" + method + ")");
			}
		}

		return data;
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

	public Double sum(QueryInfo[] queryInfos) throws StatisticException {

		Double sum = 0.0;
		for (QueryInfo queryInfo : queryInfos) {
			if (queryInfo == null)
				continue;
			sum += querySingle(queryInfo).getDouble("sum");
		}

		return sum;
	}
}
