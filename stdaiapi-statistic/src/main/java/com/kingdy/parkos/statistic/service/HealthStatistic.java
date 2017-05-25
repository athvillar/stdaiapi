package com.kingdy.parkos.statistic.service;

import java.util.Calendar;

import com.alibaba.fastjson.JSONObject;
import com.kingdy.parkos.statistic.exception.StatisticException;

import cn.standardai.api.core.util.DateUtil;

public class HealthStatistic implements Statistic {

	@Override
	public JSONObject statistic(JSONObject queryParam) throws StatisticException {
		return getHealth(queryParam);
	}

	private JSONObject getHealth(JSONObject queryParam) throws StatisticException {

		JSONObject dataResult = new JSONObject();
		JSONObject[] datapointQueryParams = parseHealthParam(queryParam);

		// 本月累计收入 = 本月临停收入 + 本月套餐收入
		Double srThisMonth = sum(new JSONObject[] { datapointQueryParams[0] });
		// 过去三个月累计收入
		Double sr3Months = sum(new JSONObject[] { datapointQueryParams[2] });
		// 收入增长率 = 本月累计收入 / 过去三个月平均收入
		Double incomeGrowthRateScore = srThisMonth / (sr3Months / 3);
		// 收入增长率修正
		incomeGrowthRateScore = incomeGrowthRateScore + 7;
		incomeGrowthRateScore = narrowValue(0.0, 10.0, incomeGrowthRateScore);

		// 月累计异常操作
		Double unusualOperationThisMonth = sum(new JSONObject[] { datapointQueryParams[4] });
		// 月累计总操作
		Double operationThisMonth = sum(new JSONObject[] { datapointQueryParams[5] });
		// 异常操作 = 月累计异常操作 / 月累计总操作
		Double unusualOperationScore = unusualOperationThisMonth / operationThisMonth;
		// 异常操作修正
		unusualOperationScore = 60 - (unusualOperationScore - 0.03) * 60 / 0.17;
		unusualOperationScore = narrowValue(0.0, 60.0, unusualOperationScore);

		// 修改次数
		Double fixCountThisMonth = sum(new JSONObject[] { datapointQueryParams[6] });
		// 总流量 = 入场流量 + 出场流量
		Double carCountThisMonth = sum(new JSONObject[] { datapointQueryParams[7], datapointQueryParams[8] });
		// 车牌识别率 = 修改次数 / 总流量
		Double carnoRecognizeScore = fixCountThisMonth / carCountThisMonth;
		// 车牌识别率修正
		carnoRecognizeScore = (1 - carnoRecognizeScore) * 10;
		carnoRecognizeScore = narrowValue(0.0, 10.0, carnoRecognizeScore);

		// 月断线累计时长
		Double maxDisconnectionThisMonth = sum(new JSONObject[] { datapointQueryParams[9] });
		// 月总时长（分钟数）
		Double totalMinutesThisMonth = 43200.0;
		// 断线率
		Double disconnectionRateScore = maxDisconnectionThisMonth / totalMinutesThisMonth;
		// 断线率修正
		disconnectionRateScore = 20 - (disconnectionRateScore - 0.05) * 20 / 0.25;
		disconnectionRateScore = narrowValue(0.0, 20.0, disconnectionRateScore);

		// 经营评分 = 收入增长率
		Double bizScore = incomeGrowthRateScore;
		// 管理评分 = 异常操作
		Double mngScore = unusualOperationScore;
		// 系统评分 = 车牌识别率 * 1/3 + 断线率 * 2/3
		Double sysScore = carnoRecognizeScore + disconnectionRateScore;

		// 健康值 ＝ 经营 * 10% + 管理 * 60% + 系统 * 30%
		Double healthScore = bizScore + mngScore + sysScore;

		dataResult.put("incomeGrowthRateScore", incomeGrowthRateScore);
		dataResult.put("unusualOperationScore", unusualOperationScore);
		dataResult.put("carnoRecognizeScore", carnoRecognizeScore);
		dataResult.put("disconnectionRateScore", disconnectionRateScore);
		dataResult.put("bizScore", bizScore);
		dataResult.put("mngScore", mngScore);
		dataResult.put("sysScore", sysScore);
		dataResult.put("healthScore", healthScore);

		return dataResult;
	}

	private JSONObject[] parseHealthParam(JSONObject param) throws StatisticException {

		JSONObject[] datapointQueryParams = new JSONObject[10];

		Calendar startTime30Days = Calendar.getInstance();
		startTime30Days.add(Calendar.DATE, -30);
		startTime30Days.set(Calendar.HOUR_OF_DAY, 0);
		startTime30Days.set(Calendar.MINUTE, 0);
		startTime30Days.set(Calendar.SECOND, 0);
		startTime30Days.set(Calendar.MILLISECOND, 0);
		Calendar endTime30Days = Calendar.getInstance();
		endTime30Days.set(Calendar.HOUR_OF_DAY, 0);
		endTime30Days.set(Calendar.MINUTE, 0);
		endTime30Days.set(Calendar.SECOND, 0);
		endTime30Days.set(Calendar.MILLISECOND, 0);

		// 本月收入
		datapointQueryParams[0] = new JSONObject();
		datapointQueryParams[0].put("datapointId", "SRIPD");
		datapointQueryParams[0].put("startTime", DateUtil.format(startTime30Days.getTime(), DateUtil.YYYY__MM__DD__HH__MM__SS));
		datapointQueryParams[0].put("endTime", DateUtil.format(endTime30Days.getTime(), DateUtil.YYYY__MM__DD__HH__MM__SS));
		datapointQueryParams[0].put("method", "sum");
		datapointQueryParams[0].put("parkId", param.getString("parkId"));

		Calendar startTimePast3Month = Calendar.getInstance();
		startTimePast3Month.add(Calendar.DATE, -120);
		startTimePast3Month.set(Calendar.HOUR_OF_DAY, 0);
		startTimePast3Month.set(Calendar.MINUTE, 0);
		startTimePast3Month.set(Calendar.SECOND, 0);
		startTimePast3Month.set(Calendar.MILLISECOND, 0);
		Calendar endTimePast3Month = Calendar.getInstance();
		endTimePast3Month.add(Calendar.DATE, -30);
		endTimePast3Month.set(Calendar.HOUR_OF_DAY, 0);
		endTimePast3Month.set(Calendar.MINUTE, 0);
		endTimePast3Month.set(Calendar.SECOND, 0);
		endTimePast3Month.set(Calendar.MILLISECOND, 0);

		// -120 - -30日收入
		datapointQueryParams[2] = new JSONObject();
		datapointQueryParams[2].put("datapointId", "SRIPD");
		datapointQueryParams[2].put("parkId", param.getString("parkId"));
		datapointQueryParams[2].put("startTime", DateUtil.format(startTimePast3Month.getTime(), DateUtil.YYYY__MM__DD__HH__MM__SS));
		datapointQueryParams[2].put("endTime", DateUtil.format(endTimePast3Month.getTime(), DateUtil.YYYY__MM__DD__HH__MM__SS));
		datapointQueryParams[2].put("method", "sum");

		// 月累计异常操作数
		datapointQueryParams[4] = new JSONObject();
		datapointQueryParams[4].put("datapointId", "YCFPD");
		datapointQueryParams[4].put("parkId", param.getString("parkId"));
		datapointQueryParams[4].put("startTime", DateUtil.format(startTime30Days.getTime(), DateUtil.YYYY__MM__DD__HH__MM__SS));
		datapointQueryParams[4].put("endTime", DateUtil.format(endTime30Days.getTime(), DateUtil.YYYY__MM__DD__HH__MM__SS));
		datapointQueryParams[4].put("method", "sum");

		// 月累计总操作数
		datapointQueryParams[5] = new JSONObject();
		datapointQueryParams[5].put("datapointId", "CCFPH");
		datapointQueryParams[5].put("parkId", param.getString("parkId"));
		datapointQueryParams[5].put("startTime", DateUtil.format(startTime30Days.getTime(), DateUtil.YYYY__MM__DD__HH__MM__SS));
		datapointQueryParams[5].put("endTime", DateUtil.format(endTime30Days.getTime(), DateUtil.YYYY__MM__DD__HH__MM__SS));
		datapointQueryParams[5].put("method", "sum");

		// 本月车牌修改次数
		datapointQueryParams[6] = new JSONObject();
		datapointQueryParams[6].put("datapointId", "XGNPD");
		datapointQueryParams[6].put("parkId", param.getString("parkId"));
		datapointQueryParams[6].put("startTime", DateUtil.format(startTime30Days.getTime(), DateUtil.YYYY__MM__DD__HH__MM__SS));
		datapointQueryParams[6].put("endTime", DateUtil.format(endTime30Days.getTime(), DateUtil.YYYY__MM__DD__HH__MM__SS));
		datapointQueryParams[6].put("method", "sum");

		// 本月入场流量
		datapointQueryParams[7] = new JSONObject();
		datapointQueryParams[7].put("datapointId", "JCFPH");
		datapointQueryParams[7].put("parkId", param.getString("parkId"));
		datapointQueryParams[7].put("startTime", DateUtil.format(startTime30Days.getTime(), DateUtil.YYYY__MM__DD__HH__MM__SS));
		datapointQueryParams[7].put("endTime", DateUtil.format(endTime30Days.getTime(), DateUtil.YYYY__MM__DD__HH__MM__SS));
		datapointQueryParams[7].put("method", "sum");

		// 本月出场流量
		datapointQueryParams[8] = new JSONObject();
		datapointQueryParams[8].put("datapointId", "CCFPH");
		datapointQueryParams[8].put("parkId", param.getString("parkId"));
		datapointQueryParams[8].put("startTime", DateUtil.format(startTime30Days.getTime(), DateUtil.YYYY__MM__DD__HH__MM__SS));
		datapointQueryParams[8].put("endTime", DateUtil.format(endTime30Days.getTime(), DateUtil.YYYY__MM__DD__HH__MM__SS));
		datapointQueryParams[8].put("method", "sum");

		// 本月所有设备的断线时间
		datapointQueryParams[9] = new JSONObject();
		datapointQueryParams[9].put("datapointId", "DXTDD");
		datapointQueryParams[9].put("parkId", param.getString("parkId"));
		datapointQueryParams[9].put("startTime", DateUtil.format(startTime30Days.getTime(), DateUtil.YYYY__MM__DD__HH__MM__SS));
		datapointQueryParams[9].put("endTime", DateUtil.format(endTime30Days.getTime(), DateUtil.YYYY__MM__DD__HH__MM__SS));
		datapointQueryParams[9].put("method", "sum");
		datapointQueryParams[9].put("aggr", "deviceId");
		datapointQueryParams[9].put("secondaryMethod", "max");

		return datapointQueryParams;
	}

	private Double narrowValue(Double min, Double max, Double target) {
		if (target < min) return min;
		if (target > max) return max;
		return target;
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
