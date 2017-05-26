package cn.standardai.api.statistic.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.base.BaseService;
import cn.standardai.api.statistic.exception.StatisticException;

@Controller
@RestController
@EnableAutoConfiguration
@RequestMapping("/statistic")
public class StatisticService extends BaseService {

	private Logger logger = LoggerFactory.getLogger(StatisticService.class);

	/**
	 * 统计
	 * @param request
	 * @return 正常 异常（失败，异常信息）
	 */
	@RequestMapping(value = "", method = {RequestMethod.POST})
	public String statistic(@RequestBody JSONObject request) {

		setBeautify("y".equalsIgnoreCase(request.getString("beautify")));
		JSONObject result = new JSONObject();
		try {
			logger.info("Statistic query(" + request +")");
			StatisticHandler sh = new StatisticHandler();
			result = sh.handle(request);
			return makeResponse(ReturnType.SUCCESS, result.toJSONString());
		} catch (StatisticException e) {
			e.printStackTrace();
			return makeResponse(ReturnType.FAILURE, e.getMessage());
		}
	}

}
