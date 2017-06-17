package cn.standardai.api.node.service;

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
import cn.standardai.api.core.base.BaseService.ReturnType;
import cn.standardai.api.core.exception.StdaiException;
import cn.standardai.api.node.agent.NodeAgent;

@Controller
@RestController
@EnableAutoConfiguration
@RequestMapping("/node")
public class NodeService extends BaseService<NodeAgent> {

	private Logger logger = LoggerFactory.getLogger(NodeService.class);

	@RequestMapping(value = "", method = RequestMethod.POST)
	public String getRandomNumber(@RequestHeader HttpHeaders headers, @RequestBody JSONObject request) {
		logger.info("Start /node/node POST 收到node请求");
		JSONObject result = null;
		try {
			initAgent(headers, NodeAgent.class);
			result = agent.exec(request);
			result = successResponse(result);
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-data /data/dic POST 结束node请求 (" + result.toJSONString() + ")");
		return result.toString();
	}
}
