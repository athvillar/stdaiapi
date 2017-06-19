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
import cn.standardai.api.ml.agent.FormulaAgent;

@Controller
@RestController
@EnableAutoConfiguration
@RequestMapping("/app")
public class AppService extends BaseService {

	private Logger logger = LoggerFactory.getLogger(AppService.class);

	@RequestMapping(value = "/formula", method = RequestMethod.POST)
	public String formula(@RequestHeader HttpHeaders headers, @RequestBody JSONObject request) {
		logger.info("stdaiapi-ml /app/formula POST start ...");
		JSONObject result = null;
		FormulaAgent agent = new FormulaAgent();
		try {
			result = agent.check(agent.parse(request));
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
}
