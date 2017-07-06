package cn.standardai.api.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.app.agent.FormulaAgent;
import cn.standardai.api.core.base.BaseService;
import cn.standardai.api.core.exception.StdaiException;

@Controller
@RestController
@EnableAutoConfiguration
@RequestMapping("/formula")
public class FormulaService extends BaseService<FormulaAgent> {

	private Logger logger = LoggerFactory.getLogger(FormulaService.class);

	@RequestMapping(value = "/model/train", method = RequestMethod.POST)
	public String uploadImage(@RequestHeader HttpHeaders headers) {
		logger.info("stdaiapi-app /model/train POST start ...");
		JSONObject result = null;
		FormulaAgent agent = new FormulaAgent();
		try {
			agent.train();
			result = successResponse(result);
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-app /model/train POST finish (" + result.toJSONString() + ")");
		return result.toString();
	}

	@RequestMapping(value = "/image/split", method = RequestMethod.POST)
	public String uploadImage(@RequestHeader HttpHeaders headers, @RequestParam("files") MultipartFile[] uploadfiles) {
		logger.info("stdaiapi-app /image/split POST start ...");
		JSONObject result = null;
		FormulaAgent agent = new FormulaAgent();
		try {
			result = agent.splitImage(uploadfiles);
			result = successResponse(result);
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-app /image/split POST finish (" + result.toJSONString() + ")");
		return result.toString();
	}

	@RequestMapping(value = "/image/commit", method = RequestMethod.POST)
	public String commitImage(@RequestHeader HttpHeaders headers, @RequestBody JSONObject request) {
		logger.info("stdaiapi-app /image/commit POST start ...");
		JSONObject result = null;
		FormulaAgent agent = new FormulaAgent();
		try {
			result = agent.commitImages(request);
			result = successResponse(result);
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-app /image/commit POST finish (" + result.toJSONString() + ")");
		return result.toString();
	}

	@RequestMapping(value = "/check", method = RequestMethod.POST)
	public String checkFormula(@RequestHeader HttpHeaders headers, @RequestParam("files") MultipartFile[] uploadfiles) {
		logger.info("stdaiapi-app /check POST start ...");
		JSONObject result = null;
		FormulaAgent agent = new FormulaAgent();
		try {
			result = agent.check(uploadfiles);
			result = agent.exportImg(uploadfiles, result);
			result = successResponse(result);
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-app /check POST finish (" + result.toJSONString() + ")");
		return result.toString();
	}
}
