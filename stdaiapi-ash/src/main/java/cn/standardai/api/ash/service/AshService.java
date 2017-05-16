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
import cn.standardai.api.core.exception.StdaiException;

@Controller
@RestController
@EnableAutoConfiguration
@RequestMapping("/ash")
public class AshService extends BaseService<AshAgent> {

	private Logger logger = LoggerFactory.getLogger(AshService.class);

	@RequestMapping(value = "", method = RequestMethod.POST)
	public String upgradeUserById(@RequestHeader HttpHeaders headers, @RequestBody JSONObject request) {
		logger.info("stdaiapi-ash 收到ash请求(request=" + request + ")");
		JSONObject result = new JSONObject();
		AshAgent agent = null;
		try {
			if ("login".equals(request.getString("ash").split(" ")[0])) {
				agent = new AshAgent();
			} else if ("mk".equals(request.getString("ash").split(" ")[0]) && "user".equals(request.getString("ash").split(" ")[1])) {
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
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-ash 结束ash(" + result + ")");
		return result.toString();
	}
}
