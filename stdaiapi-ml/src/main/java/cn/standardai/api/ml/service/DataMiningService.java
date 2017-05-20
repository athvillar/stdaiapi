package cn.standardai.api.ml.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.base.BaseService;
import cn.standardai.api.core.exception.StdaiException;
import cn.standardai.api.ml.agent.DataMiningAgent;

@Controller
@RestController
@EnableAutoConfiguration
@RequestMapping("/dm")
public class DataMiningService extends BaseService<DataMiningAgent> {

	private Logger logger = LoggerFactory.getLogger(DataMiningService.class);

	@RequestMapping(value = "/classify", method = RequestMethod.POST)
	public String classify(@RequestHeader HttpHeaders headers, @RequestBody JSONObject request) {
		logger.info("stdaiapi-ml /dm/classify start ...");
		JSONObject result = null;
		try {
			initAgent(headers, DataMiningAgent.class);
			result = agent.classify(request);
			result = successResponse(result);
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-ml /dm/classify finish (response:" + result.toJSONString() + ")");
		return result.toString();
	}

	@RequestMapping(value = "/cluster", method = RequestMethod.POST)
	public String cluster(@RequestHeader HttpHeaders headers, @RequestBody JSONObject request) {
		logger.info("stdaiapi-ml /dm/clusterify start ...");
		JSONObject result = null;
		try {
			initAgent(headers, DataMiningAgent.class);
			result = agent.cluster(request);
			result = successResponse(result);
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-ml /dm/clusterify finish (response:" + result.toJSONString() + ")");
		return result.toString();
	}

	@RequestMapping(value = "/decision", method = RequestMethod.POST)
	public String decision(@RequestHeader HttpHeaders headers, @RequestBody JSONObject request) {
		logger.info("stdaiapi-ml /dm/decision start ...");
		JSONObject result = null;
		try {
			initAgent(headers, DataMiningAgent.class);
			result = agent.makeDecisionTree(request);
			result = successResponse(result);
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-ml /dm/decision finish (response:" + result.toJSONString() + ")");
		return result.toString();
	}
}
