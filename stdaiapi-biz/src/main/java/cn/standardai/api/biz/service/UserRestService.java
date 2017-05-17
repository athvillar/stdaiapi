package cn.standardai.api.biz.service;

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

import cn.standardai.api.biz.agent.UserAgent;
import cn.standardai.api.core.base.BaseService;
import cn.standardai.api.core.exception.AuthException;
import cn.standardai.api.core.exception.StdaiException;

@Controller
@RestController
@EnableAutoConfiguration
@RequestMapping("/user")
public class UserRestService extends BaseService<UserAgent> {

	private Logger logger = LoggerFactory.getLogger(UserRestService.class);

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String getUserById(@PathVariable("id") String id, @RequestHeader HttpHeaders headers) {
		logger.info("stdaiapi-biz 收到查看用户请求(id=" + id + ")");
		JSONObject result = null;
		try {
			initAgent(headers, UserAgent.class);
			result = agent.getById(id);
			result = successResponse(result);
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
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
	public String upgradeUserById(@PathVariable("id") String id, @RequestHeader HttpHeaders headers, @RequestBody JSONObject request) {
		logger.info("stdaiapi-biz 收到更新用户请求(id=" + id + ", request=" + request + ")");
		JSONObject result = new JSONObject();
		try {
			initAgent(headers, UserAgent.class);
		} catch (AuthException e) {
			// 验证失败，注册用户
			UserAgent agent = new UserAgent();
			try {
				result = agent.create(id, request);
			} catch (StdaiException e2) {
				result = makeResponse(ReturnType.FAILURE, null, e2.getMessage());
				logger.info("stdaiapi-biz 结束更新用户(" + result + ")");
				return result.toString();
			} catch (Exception e2) {
				e2.printStackTrace();
				result = makeResponse(ReturnType.FAILURE, null, e2.getMessage());
				logger.info("stdaiapi-biz 结束更新用户(" + result + ")");
				return result.toString();
			} finally {
				if (agent != null) agent.done();
				if (this.agent != null) this.agent.done();
			}
			logger.info("stdaiapi-biz 结束更新用户(" + result + ")");
			return result.toString();
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
			if (this.agent != null) this.agent.done();
			logger.info("stdaiapi-biz 结束更新用户(" + result + ")");
			return result.toString();
		}
		try {
			// 验证成功，更新用户
			result = agent.updateById(id, request);
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
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
	public String removeUserById(@PathVariable("id") String id, @RequestHeader HttpHeaders headers) {
		logger.info("stdaiapi-biz 收到删除用户请求(id=" + id + ")");
		JSONObject result = new JSONObject();
		try {
			initAgent(headers, UserAgent.class);
			agent.removeById(id);
			result.put("result", "success");
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
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
