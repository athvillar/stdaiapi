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
import cn.standardai.api.core.base.BaseService.ReturnType;
import cn.standardai.api.core.exception.StdaiException;
import cn.standardai.api.data.agent.DicAgent;
import cn.standardai.api.data.agent.DataAgent;

@Controller
@RestController
@EnableAutoConfiguration
@RequestMapping("/dic")
public class DicService extends BaseService<DicAgent> {

	private Logger logger = LoggerFactory.getLogger(DicService.class);

	@RequestMapping(value = "", method = RequestMethod.POST)
	public String receiveData(@RequestHeader HttpHeaders headers, @RequestBody JSONObject request) {
		logger.info("stdaiapi-data /dic 收到数据字典创建请求 ...");
		JSONObject result = null;
		try {
			initAgent(headers, DicAgent.class);
			result = agent.createDic(request);
			result = successResponse(result);
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-data /dic 结束数据字典创建 (" + result.toJSONString() + ")");
		return result.toString();
	}
}
