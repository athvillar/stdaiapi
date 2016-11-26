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

import cn.standardai.api.ml.agent.CnnAgent;
import cn.standardai.api.ml.exception.MLException;

@Controller
@RestController
@EnableAutoConfiguration
@RequestMapping("/cnn")
public class CnnService extends MLService {

	private Logger logger = LoggerFactory.getLogger(CnnService.class);

	@RequestMapping(value = "", method = RequestMethod.POST)
	public String create(@RequestHeader HttpHeaders headers, @RequestBody JSONObject request) {
		logger.info("stdaiapi-ml cnn POST start ...");
		CnnAgent agent = null;
		JSONObject result = null;
		try {
			agent = new CnnAgent();
			result = agent.create(getUserIdByToken(headers), request);
			result = successResponse(result);
		} catch (MLException e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-ml cnn POST finish (response:" + result.toJSONString() + ")");
		return result.toString();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	public String train(@PathVariable("id") String id, @RequestHeader HttpHeaders headers, @RequestBody JSONObject request) {
		logger.info("stdaiapi-ml cnn POST start (id:" + id + ", ...");
		CnnAgent agent = null;
		JSONObject result = null;
		try {
			agent = new CnnAgent();
			result = agent.process(getUserIdByToken(headers), id, request);
			result = successResponse(result);
		} catch (MLException e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-ml cnn POST finish (response:" + result.toJSONString() + ")");
		return result.toString();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String train(@PathVariable("id") String id, @RequestHeader HttpHeaders headers) {
		logger.info("stdaiapi-ml cnn GET start (id:" + id + ")");
		CnnAgent agent = null;
		JSONObject result = null;
		try {
			agent = new CnnAgent();
			result = agent.status(getUserIdByToken(headers), id);
			result = successResponse(result);
		} catch (MLException e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-ml cnn GET finish (response:" + result.toJSONString() + ")");
		return result.toString();
	}
}
