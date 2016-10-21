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
		logger.info("Start /data/upload/json ...");
		UploadAgent uploader = null;
		JSONObject result = null;
		try {
			uploader = new UploadAgent();
			result = uploader.saveJSONData(request);
			result = successResponse(result);
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (uploader != null) {
				uploader.done();
			}
		}
		logger.info("Finish /data/upload/json (" + result + ")");
		return result.toString();
	}
}
