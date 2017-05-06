package cn.standardai.api.ml.service;

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

import cn.standardai.api.core.base.BaseService;
import cn.standardai.api.core.exception.StdaiException;
import cn.standardai.api.ml.agent.LstmAgent;
import cn.standardai.api.ml.exception.MLException;

@Controller
@RestController
@EnableAutoConfiguration
@RequestMapping("/lstm")
public class LstmService extends BaseService<LstmAgent> {

	private Logger logger = LoggerFactory.getLogger(LstmService.class);

	@RequestMapping(value = "", method = RequestMethod.POST)
	public String create(@RequestHeader HttpHeaders headers, @RequestBody JSONObject request) {
		logger.info("stdaiapi-ml lstm POST start ...");
		JSONObject result = null;
		try {
			initAgent(headers, LstmAgent.class);
			result = agent.create(request);
			result = successResponse(result);
		} catch (MLException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-ml lstm POST finish (response:" + result.toJSONString() + ")");
		return result.toString();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	public String train(@PathVariable("id") String id, @RequestHeader HttpHeaders headers, @RequestBody JSONObject request) {
		logger.info("stdaiapi-ml lstm/" + id + " POST start");
		JSONObject result = null;
		try {
			initAgent(headers, LstmAgent.class);
			result = agent.process(id, request);
			result = successResponse(result);
		} catch (MLException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-ml lstm/" + id + " POST finish (response:" + result.toJSONString() + ")");
		return result.toString();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String status(@PathVariable("id") String id, @RequestHeader HttpHeaders headers) {
		logger.info("stdaiapi-ml lstm/" + id + " GET start");
		JSONObject result = null;
		try {
			initAgent(headers, LstmAgent.class);
			result = agent.status(id);
			result = successResponse(result);
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-ml lstm/" + id + " GET finish (response:" + result.toJSONString() + ")");
		return result.toString();
	}
}
