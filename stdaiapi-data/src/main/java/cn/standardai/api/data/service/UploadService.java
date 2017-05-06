package cn.standardai.api.data.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.base.BaseService;
import cn.standardai.api.core.exception.StdaiException;
import cn.standardai.api.data.agent.UploadAgent;

@Controller
@RestController
@EnableAutoConfiguration
@RequestMapping("/upload")
public class UploadService extends BaseService<UploadAgent> {

	private Logger logger = LoggerFactory.getLogger(UploadService.class);

	@RequestMapping(value = "", method = RequestMethod.POST)
	public String receiveData(@RequestHeader HttpHeaders headers, @RequestBody JSONObject request) {
		logger.info("stdaiapi-data 收到数据上传请求 ...");
		JSONObject result = null;
		try {
			initAgent(headers, UploadAgent.class);
			result = agent.saveJSONData(request);
			result = successResponse(result);
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-data 结束数据上传 (" + result.toJSONString() + ")");
		return result.toString();
	}

	@RequestMapping(value = "/file", method = RequestMethod.POST)
	public String uploadFile(@RequestHeader HttpHeaders headers, @RequestParam("files") MultipartFile[] uploadfiles) {
		logger.info("stdaiapi-data 收到文件上传请求 ...");
		JSONObject result = null;
		try {
			initAgent(headers, UploadAgent.class);
			result = agent.saveUploadFile(uploadfiles);
			result = successResponse(result);
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-data 结束文件上传 (" + result.toJSONString() + ")");
		return result.toString();
	}
}
