package cn.standardai.api.data.service;

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
import cn.standardai.api.data.agent.ScratchAgent;
import cn.standardai.api.data.agent.TokenAgent;
import cn.standardai.api.data.agent.UploadAgent;

@Controller
@RestController
@EnableAutoConfiguration
@RequestMapping("/scratch")
public class ScratchService extends BaseService {

	private Logger logger = LoggerFactory.getLogger(ScratchService.class);

	@RequestMapping(value = "", method = RequestMethod.POST)
	public String scratch(@RequestHeader HttpHeaders headers, @RequestBody JSONObject request) {
		logger.info("stdaiapi-data 收到数据抓取请求 ...");
		ScratchAgent agent = null;
		JSONObject result = null;
		try {
			agent = new ScratchAgent();
			result = agent.uploadLocalImages(getUserIdByToken(headers), request);
			result = successResponse(result);
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-data 结束数据抓取 (" + result.toJSONString() + ")");
		return result.toString();
	}

	public String getUserIdByToken(HttpHeaders headers) throws Exception {

		TokenAgent tokenAgent = new TokenAgent();
		try {
			return tokenAgent.getUserIdByToken(headers.get("token"));
		} catch (Exception e) {
			throw e;
		} finally {
			if (tokenAgent != null) tokenAgent.done();
		}
	}
}
