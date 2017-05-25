package com.kingdy.parkos.statistic.service;

import java.util.Calendar;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kingdy.parkos.statistic.exception.StatisticException;

import cn.standardai.api.core.util.DateUtil;

public class PredictStatistic implements Statistic {

	@Override
	public JSONObject statistic(JSONObject queryParam) throws StatisticException {
		JSONObject data = new JSONObject();
		data.put("datalist", getPredict(queryParam));
		data.put("key_field", "time");
		data.put("value_field", "sum");
		return data;
	}

	private JSONArray getPredict(JSONObject queryParam) throws StatisticException {

		JSONObject datapointQueryParam = parsePredictParam(queryParam);
		Statistic stats = StatisticFactory.getInstance("datapoint");
		JSONObject historyData = stats.statistic(datapointQueryParam);

		String paramStartTime = queryParam.getString("startTime");
		String paramEndTime = queryParam.getString("endTime");
		Calendar startTime = Calendar.getInstance();
		Calendar endTime = Calendar.getInstance();

		String format;
		int field;
		int interval;
		int cnt;
		switch (paramStartTime.length()) {
		case 4:
			format = DateUtil.YYYY;
			field = Calendar.YEAR;
			interval = 1;
			cnt = 3;
			break;
		case 7:
			format = DateUtil.YYYY__MM;
			field = Calendar.MONTH;
			interval = 1;
			cnt = 5;
			break;
		case 10:
			format = DateUtil.YYYY__MM__DD;
			field = Calendar.DATE;
			interval = 7;
			cnt = 20;
			break;
		case 13:
			format = DateUtil.YYYY__MM__DD__HH;
			field = Calendar.HOUR_OF_DAY;
			interval = 7 * 24;
			cnt = 20;
			break;
		default:
			throw new StatisticException("Wrong parameter startTime(" + paramStartTime + "), endTime(" + paramEndTime +")");
		}
		startTime.setTime(DateUtil.parse(paramStartTime, format));
		endTime.setTime(DateUtil.parse(paramEndTime, format));
		JSONArray predictData = predict(historyData.getJSONArray("datalist"), startTime, endTime, format, field, interval, cnt);

		return predictData;
	}

	private JSONArray predict(JSONArray historyData, Calendar startTime, Calendar endTime, String format, int field, int interval, int cnt) {

		Calendar key = Calendar.getInstance();

		JSONArray predictData = new JSONArray();
		while (!startTime.after(endTime)) {

			if ((historyData == null) || (historyData.size() == 0)) {
				JSONObject predictData1 = new JSONObject();
				predictData1.put("time", DateUtil.format(startTime.getTime(), format));
				predictData1.put("sum", 0);
				predictData.add(predictData1);
				historyData.add(predictData1);
				startTime.add(field, 1);
				continue;
			}

			String time1 = null, time2 = null;
			key.setTimeInMillis(startTime.getTimeInMillis());
			time1 = DateUtil.format(key.getTime(), format);
			int index = historyData.size() - 1;
			time2 = historyData.getJSONObject(index).getString("time");

			double sum = 0;
			int currentCnt = 0;
			while (true) {
				if (index < 0 || currentCnt >= cnt) break;
				time1 = DateUtil.format(key.getTime(), format);
				time2 = historyData.getJSONObject(index).getString("time");
				if (time1.compareTo(time2) > 0 ) {
					key.add(field, -interval);
					continue;
				} else if (time1.compareTo(time2) < 0 ) {
					index--;
					continue;
				} else {
					Double value = historyData.getJSONObject(index).getDoubleValue("sum");
					key.add(field, -interval);
					index--;
					if (value != null && value != 0) {
						sum += value;
						currentCnt++;
					}
				}
			}
			JSONObject predictData1 = new JSONObject();
			predictData1.put("time", DateUtil.format(startTime.getTime(), format));
			if (currentCnt == 0) {
				predictData1.put("sum", 0);
			} else {
				predictData1.put("sum", sum / currentCnt);
			}

			predictData.add(predictData1);
			historyData.add(predictData1);

			startTime.add(field, 1);
		}

		return predictData;
	}

	private JSONObject parsePredictParam(JSONObject param) throws StatisticException {

		JSONObject datapointQueryParams = new JSONObject();

		String datapointId = param.getString("datapointId");
		if (datapointId == null) throw new StatisticException("Missing parameter datapointId");

		String paramStartTime = param.getString("startTime");
		String paramEndTime = param.getString("endTime");
		if (paramStartTime == null || paramEndTime == null) throw new StatisticException("Missing parameter startTime, endTime");
		if (paramStartTime.length() != paramEndTime.length()) {
			throw new StatisticException("Wrong parameter startTime(" + paramStartTime + "), endTime(" + paramEndTime +")");
		}

		String datapointBase = datapointId.substring(0, 4);
		Calendar startTime = Calendar.getInstance();
		Calendar endTime = Calendar.getInstance();
		String targetDatapointId = null;
		switch (paramStartTime.length()) {
		case 4:
			startTime.setTime(DateUtil.parse(paramStartTime, DateUtil.YYYY));
			startTime.add(Calendar.YEAR, -3);
			endTime.setTime(DateUtil.parse(paramStartTime, DateUtil.YYYY));
			targetDatapointId = datapointBase + 'Y';
			break;
		case 7:
			startTime.setTime(DateUtil.parse(paramStartTime, DateUtil.YYYY__MM));
			startTime.add(Calendar.MONTH, -5);
			endTime.setTime(DateUtil.parse(paramStartTime, DateUtil.YYYY__MM));
			targetDatapointId = datapointBase + 'M';
			break;
		case 10:
			startTime.setTime(DateUtil.parse(paramStartTime, DateUtil.YYYY__MM__DD));
			startTime.add(Calendar.MONTH, -5);
			endTime.setTime(DateUtil.parse(paramStartTime, DateUtil.YYYY__MM__DD));
			targetDatapointId = datapointBase + 'D';
			break;
		case 13:
			startTime.setTime(DateUtil.parse(paramStartTime, DateUtil.YYYY__MM__DD));
			startTime.add(Calendar.MONTH, -5);
			endTime.setTime(DateUtil.parse(paramStartTime, DateUtil.YYYY__MM__DD));
			targetDatapointId = datapointBase + 'H';
			break;
		default:
			throw new StatisticException("Wrong parameter startTime(" + paramStartTime + "), endTime(" + paramEndTime +")");
		}
		datapointQueryParams.put("datapointId", targetDatapointId);
		datapointQueryParams.put("startTime", DateUtil.format(startTime.getTime(), DateUtil.YYYY__MM__DD__HH__MM__SS));
		datapointQueryParams.put("endTime", DateUtil.format(endTime.getTime(), DateUtil.YYYY__MM__DD__HH__MM__SS));
		datapointQueryParams.put("parkId", param.getString("parkId"));

		return datapointQueryParams;
	}
}
