package cn.standardai.api.ml.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.base.BaseService;
import cn.standardai.api.core.exception.StdaiException;
import cn.standardai.api.ml.agent.FormulaAgent;

@Controller
@RestController
@EnableAutoConfiguration
@RequestMapping("/app")
public class AppService extends BaseService<FormulaAgent> {

	private Logger logger = LoggerFactory.getLogger(AppService.class);

	@RequestMapping(value = "/formula/data", method = RequestMethod.POST)
	public String makeData(@RequestHeader HttpHeaders headers, @RequestParam("files") MultipartFile[] uploadfiles) {
		logger.info("stdaiapi-ml /app/formula POST start ...");
		JSONObject result = null;
		FormulaAgent agent = new FormulaAgent();
		try {
			result = agent.process(uploadfiles);
			result = successResponse(result);
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-ml /app/formula POST finish (" + result.toJSONString() + ")");
		return result.toString();
	}

	@RequestMapping(value = "/formula/check", method = RequestMethod.POST)
	public String checkFormula(@RequestHeader HttpHeaders headers, @RequestParam("files") MultipartFile[] uploadfiles) {
		logger.info("stdaiapi-ml /app/formula POST start ...");
		JSONObject result = null;
		FormulaAgent agent = new FormulaAgent();
		try {
			result = agent.process(uploadfiles);
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
		logger.info("stdaiapi-ml /app/formula POST finish (" + result.toJSONString() + ")");
		return result.toString();
	}
}
