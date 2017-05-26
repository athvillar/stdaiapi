package cn.standardai.api.statistic.service;

import java.util.Calendar;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.util.DateUtil;
import cn.standardai.api.statistic.exception.StatisticException;

public class PeakStatistic implements Statistic {

	@Override
	public JSONObject statistic(JSONObject queryParam) throws StatisticException {
		return getPeak(queryParam);
	}

	private JSONObject getPeak(JSONObject queryParam) throws StatisticException {
		JSONObject datapointQueryParam = parsePeakParam(queryParam);
		Statistic stats = StatisticFactory.getInstance("datapoint");
		JSONObject data = stats.statistic(datapointQueryParam);
		return formatResult(data, queryParam);
	}

	private JSONObject formatResult(JSONObject data, JSONObject queryParam) {

		JSONObject result = new JSONObject();
		String aggrField = queryParam.getString("aggr");
		if (aggrField == null) {
			String format = null;
			switch (queryParam.getString("datapointId").charAt(4)) {
			case 'H':
				format = DateUtil.YYYY__MM__DD__HH;
				break;
			case 'D':
				format = DateUtil.YYYY__MM__DD;
				break;
			case 'M':
				format = DateUtil.YYYY__MM;
				break;
			case 'Y':
				format = DateUtil.YYYY;
				break;
			default:
				format = DateUtil.YYYY__MM__DD__HH__MM__SS;
				break;
			}

			Calendar c = Calendar.getInstance();
			String maxStartTime = data.getString("maxstartTime");
			if (maxStartTime != null) {
				c.setTimeInMillis(Long.parseLong(maxStartTime));
				result.put("highestTime", DateUtil.format(c.getTime(), format));
				result.put("highestValue", data.getDouble("maxsum"));
			}
			String minStartTime = data.getString("minstartTime");
			if (minStartTime != null) {
				c.setTimeInMillis(Long.parseLong(minStartTime));
				result.put("lowestTime", DateUtil.format(c.getTime(), format));
				result.put("lowestValue", data.getDouble("minsum"));
			}
		} else {
			String maxField = data.getString("max" + aggrField);
			if (maxField != null) {
				result.put("highestTime", data.getString("max" + aggrField));
				result.put("highestValue", data.getDouble("maxsum"));
			}
			String minField = data.getString("min" + aggrField);
			if (minField != null) {
				result.put("lowestTime", data.getString("min" + aggrField));
				result.put("lowestValue", data.getDouble("minsum"));
			}
		}

		return result;
	}

	private JSONObject parsePeakParam(JSONObject param) throws StatisticException {

		JSONObject datapointQueryParams = new JSONObject();

		String datapointId = param.getString("datapointId");
		if (datapointId == null) throw new StatisticException("Missing parameter datapointId");
		datapointQueryParams.put("datapointId", datapointId);

		String time = param.getString("time");
		if (time == null) throw new StatisticException("Missing parameter time");
		datapointQueryParams.put("time", time);

		String parkId = param.getString("parkId");
		//if (parkId == null) throw new StatisticException("Missing parameter parkId");
		datapointQueryParams.put("parkId", parkId);

		datapointQueryParams.put("startTime", param.getString("startTime"));
		datapointQueryParams.put("endTime", param.getString("endTime"));

		String aggrField = param.getString("aggr");
		if (aggrField == null) {
			datapointQueryParams.put("method", "sum");
			datapointQueryParams.put("aggr", "startTime");
			datapointQueryParams.put("secondaryMethod", "max,min");
		} else {
			datapointQueryParams.put("method", "sum");
			datapointQueryParams.put("aggr", aggrField);
			datapointQueryParams.put("secondaryMethod", "max,min");
		}

		return datapointQueryParams;
	}
}
