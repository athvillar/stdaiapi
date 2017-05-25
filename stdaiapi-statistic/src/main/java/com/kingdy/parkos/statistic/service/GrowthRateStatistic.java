package com.kingdy.parkos.statistic.service;

import java.util.Calendar;

import com.alibaba.fastjson.JSONObject;
import com.kingdy.parkos.statistic.exception.StatisticException;

import cn.standardai.api.core.util.DateUtil;

public class GrowthRateStatistic implements Statistic {

	@Override
	public JSONObject statistic(JSONObject queryParam) throws StatisticException {
		JSONObject data = new JSONObject();
		Double growthRate = getGrowthRate(queryParam);
		data.put("growthrate", growthRate);
		return data;
	}

	private Double getGrowthRate(JSONObject queryParam) throws StatisticException {

		JSONObject[] datapointQueryParams = parseGrowthRateParam(queryParam);

		Double valueNew = sum(new JSONObject[] { datapointQueryParams[0] });
		Double valueOld = sum(new JSONObject[] { datapointQueryParams[1] });

		// 计算增长率
		Double growthRate = (valueNew - valueOld) / valueOld;

		return growthRate;
	}

	private JSONObject[] parseGrowthRateParam(JSONObject param) throws StatisticException {

		JSONObject[] datapointQueryParams = new JSONObject[2];
		datapointQueryParams[0] = new JSONObject();
		datapointQueryParams[1] = new JSONObject();

		datapointQueryParams[0].put("parkId", param.getString("parkId"));
		datapointQueryParams[1].put("parkId", param.getString("parkId"));

		switch (param.getString("datapointId")) {
		case "SRI":
			datapointQueryParams[0].put("datapointId", "SRIPD");
			datapointQueryParams[1].put("datapointId", "SRIPD");
			break;
		case "LLI":
			datapointQueryParams[0].put("datapointId", "LLFPD");
			datapointQueryParams[1].put("datapointId", "LLFPD");
			break;
		default:
			throw new StatisticException("Wrong parameter datapointId(" + param.getString("datapointId") + ")");
		}

		switch (param.getString("time")) {
		case "yesterday":
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, -1);
			datapointQueryParams[0].put("time", DateUtil.format(calendar.getTime(), DateUtil.YYYY__MM__DD));
			datapointQueryParams[0].put("method", "sum");
			datapointQueryParams[1].put("method", "sum");
			datapointQueryParams[1].put("param", "yesterday:1");
			break;
		case "lastweekyesterday":
			calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, -8);
			datapointQueryParams[0].put("time", DateUtil.format(calendar.getTime(), DateUtil.YYYY__MM__DD));
			datapointQueryParams[0].put("method", "sum");
			datapointQueryParams[1].put("method", "sum");
			datapointQueryParams[1].put("param", "lastweekyesterday:1");
			break;
		default:
			throw new StatisticException("Wrong parameter time(" + param.getString("time") + ")");
		}

		return datapointQueryParams;
	}

	private Double sum(JSONObject[] datapointQueryParams) throws StatisticException {

		Double sum = 0.0;
		for (JSONObject datapointQueryParam : datapointQueryParams) {
			if ("".equals(datapointQueryParam.getString("method"))) continue;
			Statistic stats = StatisticFactory.getInstance("datapoint");
			JSONObject dataResult = stats.statistic(datapointQueryParam);
			Double result = dataResult.getDouble(datapointQueryParam.getString("method"));
			sum += (result == null ? 0 : result);
		}

		return sum;
	}
}
