package cn.standardai.api.biz.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.biz.agent.TokenAgent;
import cn.standardai.api.core.base.BaseService;

@Controller
@RestController
@EnableAutoConfiguration
@RequestMapping("/token")
public class TokenRestService extends BaseService {

	private Logger logger = LoggerFactory.getLogger(TokenRestService.class);

	@RequestMapping(value = "", method = RequestMethod.POST)
	public String registToken(@RequestBody JSONObject request) {
		logger.info("stdaiapi-biz 收到生成token请求(request=" + request + ")");
		TokenAgent agent = null;
		JSONObject result = new JSONObject();
		try {
			agent = new TokenAgent();
			result = agent.createToken(request);
			result.put("result", "success");
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-biz 结束更新token(" + result + ")");
		return result.toString();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public String removeToken(@PathVariable("id") String id, String tpParamName) {
		logger.info("stdaiapi-biz 收到删除token请求(id=" + id + ")");
		TokenAgent agent = null;
		JSONObject result = new JSONObject();
		try {
			agent = new TokenAgent();
			agent.removeById(id);
			result.put("result", "success");
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-biz 结束删除token(" + result + ")");
		return result.toString();
	}
}
