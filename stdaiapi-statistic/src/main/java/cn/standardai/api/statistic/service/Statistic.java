package cn.standardai.api.statistic.service;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.statistic.exception.StatisticException;

public interface Statistic {

	public JSONObject statistic(JSONObject queryParam) throws StatisticException;
}
