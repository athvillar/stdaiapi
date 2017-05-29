package cn.standardai.api.data.service;

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

import cn.standardai.api.core.base.BaseService;
import cn.standardai.api.core.exception.StdaiException;
import cn.standardai.api.data.agent.DicAgent;

@Controller
@RestController
@EnableAutoConfiguration
@RequestMapping("/dic")
public class DicService extends BaseService<DicAgent> {

	private Logger logger = LoggerFactory.getLogger(DicService.class);

	@RequestMapping(value = "", method = RequestMethod.POST)
	public String receiveData(@RequestHeader HttpHeaders headers, @RequestBody JSONObject request) {
		logger.info("stdaiapi-data /data/dic POST 收到数据字典创建请求 ...");
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
		logger.info("stdaiapi-data /data/dic POST 结束数据字典创建 (" + result.toJSONString() + ")");
		return result.toString();
	}

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String list(@RequestHeader HttpHeaders headers) {
		logger.info("stdaiapi-data /data/dic GET 收到查看数据字典请求");
		JSONObject result = null;
		try {
			initAgent(headers, DicAgent.class);
			result = agent.listDic();
			result = successResponse(result);
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-data /data/dic GET 结束查看数据字典请求(" + result + ")");
		return result.toString();
	}

	@RequestMapping(value = "/{userId}", method = RequestMethod.GET)
	public String listById(@PathVariable("userId") String userId, @RequestHeader HttpHeaders headers) {
		logger.info("stdaiapi-data /data/dic/" + userId + " GET 收到查看数据字典请求");
		JSONObject result = null;
		try {
			initAgent(headers, DicAgent.class);
			result = agent.listDic(userId);
			result = successResponse(result);
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-data /data/dic/" + userId + " GET 结束查看数据字典请求(" + result + ")");
		return result.toString();
	}

	@RequestMapping(value = "/{userId}/{dicName}", method = RequestMethod.GET)
	public String view(@PathVariable("userId") String userId, @PathVariable("dicName") String dicName,
			@RequestHeader HttpHeaders headers) {
		logger.info("stdaiapi-data /data/dic/" + userId + "/" + dicName + " GET 收到查看数据字典请求(userId=" + userId + ", dicName=" + dicName + ")");
		JSONObject result = null;
		try {
			initAgent(headers, DicAgent.class);
			result = agent.viewDic(userId, dicName);
			result = successResponse(result);
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-data /data/dic/" + userId + "/" + dicName + " GET 结束查看数据字典请求(" + result + ")");
		return result.toString();
	}

	@RequestMapping(value = "/{userId}/{dicName}", method = RequestMethod.DELETE)
	public String remove(@PathVariable("userId") String userId, @PathVariable("dicName") String dicName,
			@RequestHeader HttpHeaders headers) {
		logger.info("stdaiapi-data /data/dic/" + userId + "/" + dicName + " DELETE 收到删除数据字典请求(userId=" + userId + ", dicName=" + dicName + ")");
		JSONObject result = new JSONObject();
		try {
			initAgent(headers, DicAgent.class);
			agent.removeDic(userId, dicName);
			result.put("result", "success");
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-data /data/dic/" + userId + "/" + dicName + " DELETE 结束查看数据字典请求(" + result + ")");
		return result.toString();
	}
}
