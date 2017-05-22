package cn.standardai.api.ml.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.base.BaseService;
import cn.standardai.api.core.exception.StdaiException;
import cn.standardai.api.ml.agent.FilterAgent;

@Controller
@RestController
@EnableAutoConfiguration
@RequestMapping("/filters")
public class FilterService extends BaseService<FilterAgent> {

	private Logger logger = LoggerFactory.getLogger(FilterService.class);

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String list(@RequestHeader HttpHeaders headers) {
		logger.info("stdaiapi-ml /filters GET start");
		JSONObject result = null;
		try {
			initAgent(headers, FilterAgent.class);
			result = agent.list();
			result = successResponse(result);
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-ml /filters GET finish (response:" + result.toJSONString() + ")");
		return result.toString();
	}

	@RequestMapping(value = "/{filterName}", method = RequestMethod.GET)
	public String view(@PathVariable("filterName") String filterName, @RequestHeader HttpHeaders headers) {
		logger.info("stdaiapi-ml /filters/" + filterName + " GET start");
		JSONObject result = null;
		try {
			initAgent(headers, FilterAgent.class);
			result = agent.view(filterName);
			result = successResponse(result);
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-ml /filters/" + filterName + " GET finish (response:" + result.toJSONString() + ")");
		return result.toString();
	}
}
