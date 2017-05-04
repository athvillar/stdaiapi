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

import cn.standardai.api.biz.agent.JDAgent;
import cn.standardai.api.biz.agent.UserAgent;
import cn.standardai.api.core.base.BaseService;

@Controller
@RestController
@EnableAutoConfiguration
@RequestMapping("/jd")
public class JDataRestService extends BaseService {

	private Logger logger = LoggerFactory.getLogger(JDataRestService.class);

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String upgradeUserById(@PathVariable("id") String id) {
		logger.info("stdaiapi-biz 收到JD请求(id=" + id);
		JDAgent agent = null;
		JSONObject result = new JSONObject();
		try {
			agent = new JDAgent();
			agent.insert(id);
			result.put("result", "success");
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-biz 结束JD(" + result + ")");
		return result.toString();
	}
}
