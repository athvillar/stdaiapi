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
import cn.standardai.api.ml.agent.DnnAgent;

@Controller
@RestController
@EnableAutoConfiguration
@RequestMapping("/dnn")
public class DnnService extends BaseService<DnnAgent> {

	private Logger logger = LoggerFactory.getLogger(DnnService.class);

	@RequestMapping(value = "", method = RequestMethod.POST)
	public String create(@RequestHeader HttpHeaders headers, @RequestBody JSONObject request) {
		logger.info("stdaiapi-ml /dnn POST start ...");
		JSONObject result = null;
		try {
			initAgent(headers, DnnAgent.class);
			result = agent.create(request);
			result = successResponse(result);
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-ml /dnn POST finish (response:" + result.toJSONString() + ")");
		return result.toString();
	}

	@RequestMapping(value = "/{userId}/{modelTemplateName}", method = RequestMethod.POST)
	public String train(@PathVariable("userId") String userId, @PathVariable("modelTemplateName") String modelTemplateName,
			@RequestHeader HttpHeaders headers, @RequestBody JSONObject request) {
		logger.info("stdaiapi-ml /dnn/" + userId + "/" + modelTemplateName + " POST start");
		JSONObject result = null;
		try {
			initAgent(headers, DnnAgent.class, userId);
			result = agent.train(userId, modelTemplateName, request);
			result = successResponse(result);
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-ml /dnn/" + userId + "/" + modelTemplateName + " POST finish (response:" + result.toJSONString() + ")");
		return result.toString();
	}

	@RequestMapping(value = "/{userId}/{modelTemplateName}", method = RequestMethod.GET)
	public String status(@PathVariable("userId") String userId, @PathVariable("modelTemplateName") String modelTemplateName, @RequestHeader HttpHeaders headers) {
		logger.info("stdaiapi-ml /dnn/" + userId + "/" + modelTemplateName + " GET start");
		JSONObject result = null;
		try {
			initAgent(headers, DnnAgent.class, userId);
			result = agent.status(modelTemplateName);
			result = successResponse(result);
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-ml /dnn/" + userId + "/" + modelTemplateName + " GET finish (response:" + result.toJSONString() + ")");
		return result.toString();
	}

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String list(@RequestHeader HttpHeaders headers) {
		logger.info("stdaiapi-ml /dnn GET start");
		JSONObject result = null;
		try {
			initAgent(headers, DnnAgent.class);
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
		logger.info("stdaiapi-ml /dnn GET finish (response:" + result.toJSONString() + ")");
		return result.toString();
	}
	
	@RequestMapping(value = "/{userId}/{modelTemplateName}", method = RequestMethod.DELETE)
	public String delete(@PathVariable("userId") String userId, @PathVariable("modelTemplateName") String modelTemplateName, @RequestHeader HttpHeaders headers) {
		logger.info("stdaiapi-ml /dnn/" + userId + "/" + modelTemplateName + " DELETE start");
		JSONObject result = null;
		try {
			initAgent(headers, DnnAgent.class, userId);
			result = agent.delete(userId, modelTemplateName);
			result = successResponse(result);
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-ml /dnn/" + userId + "/" + modelTemplateName + " DELETE finish (response:" + result.toJSONString() + ")");
		return result.toString();
	}
}
