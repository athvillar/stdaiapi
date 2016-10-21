package cn.standardai.api.math.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.base.BaseService;
import cn.standardai.api.math.agent.MathAgent;

@Controller
@RestController
@EnableAutoConfiguration
@RequestMapping("/rand")
public class RandService extends BaseService {

	private Logger logger = LoggerFactory.getLogger(RandService.class);

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String getRandomNumber(String letters, String num, String len) {
		logger.info("Start /math/rand?letters=" + letters + "&num=" + num + "&len=" + len);
		MathAgent math = null;
		JSONObject result = null;
		try {
			math = new MathAgent();
			char[] chars = (letters == null ? null : letters.toCharArray());
			int number = (num == null ? 1 : Integer.parseInt(num));
			int length = (len == null ? 8 : Integer.parseInt(len));
			result = math.rand(chars, number, length);
			result = successResponse(result);
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (math != null) {
				math.done();
			}
		}
		logger.info("Finish /math/rand (" + result + ")");
		return result.toString();
	}
}
