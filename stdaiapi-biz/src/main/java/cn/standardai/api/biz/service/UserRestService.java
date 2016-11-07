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

import cn.standardai.api.biz.agent.UserAgent;
import cn.standardai.api.core.base.BaseService;

@Controller
@RestController
@EnableAutoConfiguration
@RequestMapping("/user")
public class UserRestService extends BaseService {

	private Logger logger = LoggerFactory.getLogger(UserRestService.class);

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String getUserById(@PathVariable("id") String id) {
		logger.info("stdaiapi-biz 收到查看用户请求(id=" + id + ")");
		UserAgent agent = null;
		JSONObject result = null;
		try {
			agent = new UserAgent();
			result = agent.getById(id);
			result = successResponse(result);
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-biz 结束查看用户(" + result + ")");
		return result.toString();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	public String upgradeUserById(@PathVariable("id") String id, @RequestBody JSONObject request) {
		logger.info("stdaiapi-biz 收到更新用户请求(id=" + id + ", request=" + request + ")");
		UserAgent agent = null;
		JSONObject result = new JSONObject();
		try {
			agent = new UserAgent();
			agent.upgradeById(id, request);
			result.put("result", "success");
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-biz 结束更新用户(" + result + ")");
		return result.toString();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public String removeUserById(@PathVariable("id") String id, String tpParamName) {
		logger.info("stdaiapi-biz 收到删除用户请求(id=" + id + ")");
		UserAgent agent = null;
		JSONObject result = new JSONObject();
		try {
			agent = new UserAgent();
			agent.removeById(id);
			result.put("result", "success");
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-biz 结束删除用户(" + result + ")");
		return result.toString();
	}
}
