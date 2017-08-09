package com.ehualu.ai.signal.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.ehualu.ai.signal.agent.UserAgent;

import cn.standardai.api.core.base.BaseService;
import cn.standardai.api.core.exception.AuthException;
import cn.standardai.api.core.exception.StdaiException;

@Controller
@RestController
@EnableAutoConfiguration
@RequestMapping("/signal")
public class UserRestService extends BaseService<UserAgent> {

	private Logger logger = LoggerFactory.getLogger(UserRestService.class);

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String getUserById(@PathVariable("id") String id, @RequestHeader HttpHeaders headers) {
		logger.info("stdaiapi-signal 启动");
		JSONObject result = null;
		try {
			initAgent(headers, UserAgent.class);
			result = agent.init();
			result = successResponse(result);
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-biz 结束(" + result + ")");
		return result.toString();
	}
}
