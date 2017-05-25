package com.kingdy.parkos.statistic.service;

import java.util.Calendar;

import com.alibaba.fastjson.JSONObject;
import com.kingdy.parkos.statistic.exception.StatisticException;

import cn.standardai.api.core.util.DateUtil;

public class RatioStatistic implements Statistic {

	@Override
	public JSONObject statistic(JSONObject queryParam) throws StatisticException {
		JSONObject data = new JSONObject();
		Double ratio = getRatio(queryParam);
		data.put("ratio", ratio);
		return data;
	}

	private Double getRatio(JSONObject queryParam) throws StatisticException {

		JSONObject[] datapointQueryParams = parseRatioParam(queryParam);

		// 获得分子
		Double numerator = sum(new JSONObject[] { datapointQueryParams[0] });

		// 获得分母
		Double denominator = sum(new JSONObject[] { datapointQueryParams[1] });

		// 计算增长率
		Double ratio;
		if (denominator == null || denominator == 0) return null;
		switch (queryParam.getString("datapointId")) {
		case "DCN":
			ratio = (denominator - numerator) / denominator;
			break;
		default:
			ratio = numerator / denominator;
			break;
		}

		return ratio;
	}

	private JSONObject[] parseRatioParam(JSONObject param) throws StatisticException {

		JSONObject[] datapointQueryParams = new JSONObject[2];

		datapointQueryParams[0] = new JSONObject();
		datapointQueryParams[1] = new JSONObject();

		switch (param.getString("datapointId")) {
		case "SRI":
			datapointQueryParams[0].put("datapointId", "SRIPD");
			datapointQueryParams[1].put("datapointId", "CWNPD");
			break;
		case "LTI":
			datapointQueryParams[0].put("datapointId", "LTIPD");
			datapointQueryParams[1].put("datapointId", "CCFPD");
			break;
		case "TFT":
			datapointQueryParams[0].put("datapointId", "TFTPD");
			datapointQueryParams[1].put("datapointId", "CCFPD");
			break;
		case "DCN":
			datapointQueryParams[0].put("datapointId", "WBNPD");
			datapointQueryParams[1].put("datapointId", "DCNPD");
			break;
		default:
			throw new StatisticException("Wrong parameter datapointId(" + param.getString("datapointId") + ")");
		}

		switch (param.getString("time")) {
		case "today":
			Calendar calendar = Calendar.getInstance();
			datapointQueryParams[0].put("time", DateUtil.format(calendar.getTime(), DateUtil.YYYY__MM__DD));
			datapointQueryParams[0].put("method", "sum");
			datapointQueryParams[1].put("time", DateUtil.format(calendar.getTime(), DateUtil.YYYY__MM__DD));
			datapointQueryParams[1].put("method", "sum");
			switch (param.getString("datapointId")) {
			case "SRI":
				datapointQueryParams[0].put("datapointId", "SRIP");
				datapointQueryParams[1].put("datapointId", "CWNP");
				break;
			case "LTI":
				datapointQueryParams[0].put("datapointId", "LTIP");
				datapointQueryParams[1].put("datapointId", "CCFP");
				break;
			case "TFT":
				datapointQueryParams[0].put("datapointId", "TFTP");
				datapointQueryParams[1].put("datapointId", "CCFP");
				break;
			case "DCN":
				datapointQueryParams[0].put("datapointId", "WBNP");
				datapointQueryParams[1].put("datapointId", "DCNP");
				break;
			default:
				throw new StatisticException("Wrong parameter datapointId(" + param.getString("datapointId") + ")");
			}
			break;
		case "yesterday":
			calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, -1);
			datapointQueryParams[0].put("time", DateUtil.format(calendar.getTime(), DateUtil.YYYY__MM__DD));
			datapointQueryParams[0].put("method", "sum");
			datapointQueryParams[1].put("time", DateUtil.format(calendar.getTime(), DateUtil.YYYY__MM__DD));
			datapointQueryParams[1].put("method", "sum");
			break;
		case "sameday":
			calendar = Calendar.getInstance();
			datapointQueryParams[0].put("method", "sum");
			datapointQueryParams[0].put("param", "today:20");
			datapointQueryParams[0].put("aggr", "startTime");
			datapointQueryParams[0].put("secondaryMethod", "avg");
			datapointQueryParams[1].put("method", "sum");
			datapointQueryParams[1].put("param", "today:20");
			datapointQueryParams[1].put("aggr", "startTime");
			datapointQueryParams[1].put("secondaryMethod", "avg");
			break;
		case "yesterdaysameday":
			calendar = Calendar.getInstance();
			datapointQueryParams[0].put("method", "sum");
			datapointQueryParams[0].put("param", "yesterday:20");
			datapointQueryParams[0].put("aggr", "startTime");
			datapointQueryParams[0].put("secondaryMethod", "avg");
			datapointQueryParams[1].put("method", "sum");
			datapointQueryParams[1].put("param", "yesterday:20");
			datapointQueryParams[1].put("aggr", "startTime");
			datapointQueryParams[1].put("secondaryMethod", "avg");
			break;
		default:
			throw new StatisticException("Wrong parameter time(" + param.getString("time") + ")");
		}

		// parks
		datapointQueryParams[0].put("parkId", param.getString("parkId"));
		datapointQueryParams[1].put("parkId", param.getString("parkId"));

		return datapointQueryParams;
	}

	private Double sum(JSONObject[] datapointQueryParams) throws StatisticException {

		Double sum = 0.0;
		for (JSONObject datapointQueryParam : datapointQueryParams) {

			if (datapointQueryParam == null) continue;
			String method = datapointQueryParam.getString("secondaryMethod");
			if (method == null || "".equals(method)) method = datapointQueryParam.getString("method");
			if ("".equals(method)) continue;

			Statistic stats = StatisticFactory.getInstance("datapoint");
			JSONObject dataResult = stats.statistic(datapointQueryParam);

			Double dataValue = dataResult.getDouble(method);
			if (dataValue == null) continue;

			sum += dataValue;
		}

		return sum;
	}
}
