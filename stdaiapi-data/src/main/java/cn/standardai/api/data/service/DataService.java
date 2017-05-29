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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.base.BaseService;
import cn.standardai.api.core.exception.StdaiException;
import cn.standardai.api.data.agent.DataAgent;

@Controller
@RestController
@EnableAutoConfiguration
@RequestMapping("/data")
public class DataService extends BaseService<DataAgent> {

	private Logger logger = LoggerFactory.getLogger(DataService.class);

	@RequestMapping(value = "", method = RequestMethod.POST)
	public String receiveData(@RequestHeader HttpHeaders headers, @RequestBody JSONObject request) {
		logger.info("stdaiapi-data /data/data POST 收到创建数据请求 ...");
		JSONObject result = null;
		try {
			initAgent(headers, DataAgent.class);
			result = agent.createData(request);
			result = successResponse(result);
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-data /data/data POST 结束创建数据 (" + result.toJSONString() + ")");
		return result.toString();
	}

	@RequestMapping(value = "/{userId}/{dataName}", method = RequestMethod.POST)
	public String uploadFiles(@PathVariable("userId") String userId, @PathVariable("dataName") String dataName,
			@RequestHeader HttpHeaders headers, @RequestBody JSONObject request) {
		logger.info("stdaiapi-data /data/data/" + userId + "/" + dataName + " POST 收到更新数据请求(userId=" + userId + ", dataName=" + dataName + ")");
		JSONObject result = null;
		try {
			initAgent(headers, DataAgent.class);
			result = agent.upgradeData(userId, dataName, request);
			result = successResponse(result);
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-data /data/data/" + userId + "/" + dataName + " POST 结束更新数据 (" + result.toJSONString() + ")");
		return result.toString();
	}

	@RequestMapping(value = "/{userId}/{dataName}/files", method = RequestMethod.POST)
	public String uploadFiles(@PathVariable("userId") String userId, @PathVariable("dataName") String dataName,
			@RequestHeader HttpHeaders headers, @RequestParam("files") MultipartFile[] uploadfiles) {
		logger.info("stdaiapi-data /data/data/" + userId + "/" + dataName + "/files POST 收到上传文件请求(userId=" + userId + ", dataName=" + dataName + ")");
		JSONObject result = null;
		try {
			initAgent(headers, DataAgent.class);
			result = agent.saveUploadFiles(userId, dataName, uploadfiles);
			result = successResponse(result);
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-data /data/data/" + userId + "/" + dataName + "/files POST 结束文件上传 (" + result.toJSONString() + ")");
		return result.toString();
	}

	@RequestMapping(value = "/{userId}/{dataName}/scratch", method = RequestMethod.POST)
	public String scratch(@PathVariable("userId") String userId, @PathVariable("dataName") String dataName, @RequestHeader HttpHeaders headers, @RequestBody JSONObject request) {
		logger.info("stdaiapi-data /data/data/" + userId + "/" + dataName + "/scratch POST 收到数据抓取请求 (userId=" + userId + ", dataName=" + dataName + ")");
		JSONObject result = null;
		try {
			initAgent(headers, DataAgent.class);
			result = agent.saveScratchFiles(userId, dataName, request);
			result = successResponse(result);
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-data /data/data/" + userId + "/" + dataName + "/scratch POST 结束数据抓取 (" + result.toJSONString() + ")");
		return result.toString();
	}

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String listData(@RequestHeader HttpHeaders headers) {
		logger.info("stdaiapi-data /data/data GET 收到查看数据请求");
		JSONObject result = null;
		try {
			initAgent(headers, DataAgent.class);
			result = agent.listData();
			result = successResponse(result);
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-data /data/data GET 结束查看数据(" + result + ")");
		return result.toString();
	}

	@RequestMapping(value = "/{userId}", method = RequestMethod.GET)
	public String listDataByUserId(@PathVariable("userId") String userId, @RequestHeader HttpHeaders headers) {
		logger.info("stdaiapi-data /data/data/" + userId + " GET 收到查看数据请求");
		JSONObject result = null;
		try {
			initAgent(headers, DataAgent.class);
			result = agent.listData(userId);
			result = successResponse(result);
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-data /data/data/" + userId + " GET 结束查看数据(" + result + ")");
		return result.toString();
	}

	@RequestMapping(value = "/{userId}/{dataName}", method = RequestMethod.GET)
	public String viewData(@PathVariable("userId") String userId, @PathVariable("dataName") String dataName,
			@RequestHeader HttpHeaders headers) {
		logger.info("stdaiapi-data /data/data/" + userId + "/" + dataName + " GET 收到查看数据请求(userId=" + userId + ", dataName=" + dataName + ")");
		JSONObject result = null;
		try {
			initAgent(headers, DataAgent.class);
			result = agent.viewData(userId, dataName);
			result = successResponse(result);
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-data /data/data/" + userId + "/" + dataName + " GET 结束查看数据(" + result + ")");
		return result.toString();
	}

	@RequestMapping(value = "/{userId}/{dataName}", method = RequestMethod.DELETE)
	public String remove(@PathVariable("userId") String userId, @PathVariable("dataName") String dataName,
			@RequestHeader HttpHeaders headers) {
		logger.info("stdaiapi-data /data/data/" + userId + "/" + dataName + " DELETE 收到删除数据请求(userId=" + userId + ", dataName=" + dataName + ")");
		JSONObject result = new JSONObject();
		try {
			initAgent(headers, DataAgent.class);
			agent.removeData(userId, dataName);
			result.put("result", "success");
		} catch (StdaiException e) {
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-data /data/data/" + userId + "/" + dataName + " DELETE 结束删除数据(" + result + ")");
		return result.toString();
	}
}
