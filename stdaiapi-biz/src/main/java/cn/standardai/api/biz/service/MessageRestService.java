package cn.standardai.api.biz.service;

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

import cn.standardai.api.biz.agent.MessageAgent;
import cn.standardai.api.core.base.BaseService;
import cn.standardai.api.core.exception.StdaiException;

@Controller
@RestController
@EnableAutoConfiguration
@RequestMapping("/messages")
public class MessageRestService extends BaseService<MessageAgent> {

	private Logger logger = LoggerFactory.getLogger(MessageRestService.class);

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String getMsg(@RequestHeader HttpHeaders headers, String type, String userId, String all) {
		logger.info("stdaiapi-biz 收到查看消息请求");
		JSONObject result = null;
		try {
			initAgent(headers, MessageAgent.class);
			result = agent.get(type, userId, all);
			result = successResponse(result);
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-biz 结束查看消息(" + result + ")");
		return result.toString();
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	public String createMsg(@RequestHeader HttpHeaders headers, @RequestBody JSONObject request) {
		logger.info("stdaiapi-biz 收到更新消息请求(request=" + request + ")");
		JSONObject result = new JSONObject();
		try {
			initAgent(headers, MessageAgent.class);
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
		logger.info("stdaiapi-biz 结束更新消息(" + result + ")");
		return result.toString();
	}

	@RequestMapping(value = "", method = RequestMethod.DELETE)
	public String removeUserById(@RequestHeader HttpHeaders headers, String type) {
		logger.info("stdaiapi-biz 收到删除消息请求");
		JSONObject result = new JSONObject();
		try {
			initAgent(headers, MessageAgent.class);
			agent.remove(type);
			result.put("result", "success");
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-biz 结束删除消息(" + result + ")");
		return result.toString();
	}
}
