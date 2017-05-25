package com.kingdy.parkos.statistic.service;

import com.alibaba.fastjson.JSONObject;
import com.kingdy.parkos.statistic.exception.StatisticException;

public interface Statistic {

	public JSONObject statistic(JSONObject queryParam) throws StatisticException;
}
