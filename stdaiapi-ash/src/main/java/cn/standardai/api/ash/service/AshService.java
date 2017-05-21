package cn.standardai.api.ash.service;

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

import cn.standardai.api.ash.agent.AshAgent;
import cn.standardai.api.core.base.BaseService;
import cn.standardai.api.core.exception.AuthException;

@Controller
@RestController
@EnableAutoConfiguration
@RequestMapping("/ash")
public class AshService extends BaseService<AshAgent> {

	private Logger logger = LoggerFactory.getLogger(AshService.class);

	@RequestMapping(value = "", method = RequestMethod.POST)
	public String ashCommand(@RequestHeader HttpHeaders headers, @RequestBody JSONObject request) {
		logger.info("stdaiapi-ash 收到ash command请求(request=" + request + ")");
		JSONObject result = new JSONObject();
		AshAgent agent = null;
		try {
			String[] inputs = request.getString("ash").split(" ");
			if ((inputs != null && inputs.length >= 1 && "login".equals(inputs[0])) ||
					(inputs.length >= 2 && "mk".equals(inputs[0]) && "user".equals(inputs[1]))) {
				agent = new AshAgent();
			} else {
				initAgent(headers, AshAgent.class);
				agent = this.agent;
			}
			result = agent.exec(request);
			result.put("result", "success");
		} catch (AuthException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
			result.put("display", "请使用login登陆或mk user注册");
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
			result.put("display", "如需帮助，请使用msg命令联系管理员");
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-ash 结束ash command(" + result + ")");
		return result.toString();
	}

	@RequestMapping(value = "/dialog", method = RequestMethod.POST)
	public String ashDialog(@RequestHeader HttpHeaders headers, @RequestBody JSONObject request) {
		logger.info("stdaiapi-ash 收到ash dialog请求(request=" + request + ")");
		JSONObject result = new JSONObject();
		AshAgent agent = null;
		try {
			agent = new AshAgent();
			agent.setToken(getToken(headers));
			result = agent.dialog(request);
			result.put("result", "success");
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
			result.put("display", "如需帮助，请使用msg命令联系管理员");
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-ash 结束ash dialog(" + result + ")");
		return result.toString();
	}
}
