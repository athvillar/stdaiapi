package cn.standardai.api.data.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.base.BaseService;
import cn.standardai.api.data.agent.UploadAgent;

@Controller
@RestController
@EnableAutoConfiguration
@RequestMapping("/upload")
public class UploadService extends BaseService {

	private Logger logger = LoggerFactory.getLogger(UploadService.class);

	@RequestMapping(value = "/json", method = RequestMethod.POST)
	public String receiveData(@RequestBody JSONObject request) {
		logger.info("stdaiapi-data 收到数据上传请求 ...");
		UploadAgent agent = null;
		JSONObject result = null;
		try {
			agent = new UploadAgent();
			result = agent.saveJSONData(request);
			result = successResponse(result);
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-data 结束数据上传 (" + result.toJSONString() + ")");
		return result.toString();
	}
}
