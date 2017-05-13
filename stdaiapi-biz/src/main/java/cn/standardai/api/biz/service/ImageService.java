package cn.standardai.api.biz.service;

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

import cn.standardai.api.biz.agent.ImageAgent;
import cn.standardai.api.core.base.BaseService;

@Controller
@RestController
@EnableAutoConfiguration
@RequestMapping("/image")
public class ImageService extends BaseService<ImageAgent> {

	private Logger logger = LoggerFactory.getLogger(ImageService.class);

	@RequestMapping(value = "/import", method = RequestMethod.POST)
	public String importImage(@RequestHeader HttpHeaders headers, @RequestBody JSONObject request) {
		logger.info("stdaiapi-data 收到图片导入请求 ...");
		JSONObject result = null;
		try {
			initAgent(headers, ImageAgent.class);
			result = agent.importImage(request);
			result = successResponse(result);
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-data 结束图片导入 (" + result.toJSONString() + ")");
		return result.toString();
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String updateImage(@RequestHeader HttpHeaders headers, @RequestBody JSONObject request) {
		logger.info("stdaiapi-data 收到图片导入请求 ...");
		JSONObject result = null;
		try {
			initAgent(headers, ImageAgent.class);
			result = agent.updateImage(request);
			result = successResponse(result);
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-data 结束图片导入 (" + result.toJSONString() + ")");
		return result.toString();
	}
	
	@RequestMapping(value = "/export", method = RequestMethod.GET)
	public String exportImage(@RequestHeader HttpHeaders headers, String imageName, String imageFormat, String imageType) {
		logger.info("stdaiapi-data 收到图片导出请求 ...");
		JSONObject result = null;
		try {
			initAgent(headers, ImageAgent.class);
			result = agent.exportImage(imageName, imageFormat, imageType);
			result = successResponse(result);
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-data 结束图片导出(" + result.toJSONString() + ")");
		return result.toString();
	}
}
