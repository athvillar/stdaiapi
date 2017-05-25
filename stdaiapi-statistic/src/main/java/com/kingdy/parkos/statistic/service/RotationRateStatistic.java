package com.kingdy.parkos.statistic.service;

import com.alibaba.fastjson.JSONObject;
import com.kingdy.parkos.statistic.exception.StatisticException;

public class RotationRateStatistic implements Statistic {

	@Override
	public JSONObject statistic(JSONObject queryParam) throws StatisticException {
		JSONObject data = new JSONObject();
		Double rotationRate = getRotationRate(queryParam);
		data.put("rotationRate", rotationRate);
		return data;
	}

	private Double getRotationRate(JSONObject queryParam) throws StatisticException {

		JSONObject[] datapointQueryParams = parseRotationRateParam(queryParam);

		Double inCars = sum(new JSONObject[] { datapointQueryParams[0] });
		Double outCars = sum(new JSONObject[] { datapointQueryParams[1] });
		Double capacity = sum(new JSONObject[] { datapointQueryParams[2] });

		// 计算周转率
		if (inCars == null || outCars == null || capacity == null || capacity == 0) return null;
		Double rotationRate = (inCars + outCars) / capacity / 2;

		return rotationRate;
	}

	private JSONObject[] parseRotationRateParam(JSONObject param) throws StatisticException {

		JSONObject[] datapointQueryParams = new JSONObject[3];

		// 同期日入场车流量
		datapointQueryParams[0] = new JSONObject();
		datapointQueryParams[0].put("parkId", param.getString("parkId"));
		datapointQueryParams[0].put("datapointId", "JCFPD");
		datapointQueryParams[0].put("method", "avg");
		datapointQueryParams[0].put("param", "today:20");

		// 同期日出场车流量
		datapointQueryParams[1] = new JSONObject();
		datapointQueryParams[1].put("parkId", param.getString("parkId"));
		datapointQueryParams[1].put("datapointId", "CCFPD");
		datapointQueryParams[1].put("method", "avg");
		datapointQueryParams[1].put("param", "today:20");

		// 同期日车位数
		datapointQueryParams[2] = new JSONObject();
		datapointQueryParams[2].put("parkId", param.getString("parkId"));
		datapointQueryParams[2].put("datapointId", "CWNPD");
		datapointQueryParams[2].put("method", "avg");
		datapointQueryParams[2].put("param", "today:20");

		return datapointQueryParams;
	}

	private Double sum(JSONObject[] datapointQueryParams) throws StatisticException {

		Double sum = 0.0;
		for (JSONObject datapointQueryParam : datapointQueryParams) {
			if ("".equals(datapointQueryParam.getString("method"))) continue;
			Statistic stats = StatisticFactory.getInstance("datapoint");
			JSONObject dataResult = stats.statistic(datapointQueryParam);
			Double value = dataResult.getDouble(datapointQueryParam.getString("method"));
			if (value != null) {
				sum += value;
			}
		}

		return sum;
	}
}
