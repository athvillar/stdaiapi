package cn.standardai.api.biz.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.biz.agent.DatasetAgent;
import cn.standardai.api.core.base.BaseService;

@Controller
@RestController
@EnableAutoConfiguration
@RequestMapping("/dataset")
public class DatasetRestService extends BaseService {

	private Logger logger = LoggerFactory.getLogger(DatasetRestService.class);

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public String removeDataset(@PathVariable("id") String id) {
		logger.info("stdaiapi-biz 收到删除dataset请求(id=" + id + ")");
		DatasetAgent agent = null;
		JSONObject result = new JSONObject();
		try {
			agent = new DatasetAgent();
			agent.removeById(id);
			result.put("result", "success");
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) agent.done();
		}
		logger.info("stdaiapi-biz 结束删除dataset(" + result + ")");
		return result.toString();
	}
}
