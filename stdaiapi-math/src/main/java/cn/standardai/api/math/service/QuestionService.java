package cn.standardai.api.math.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.base.BaseService;
import cn.standardai.api.math.agent.QuestionAgent;

@Controller
@RestController
@EnableAutoConfiguration
@RequestMapping("/question")
public class QuestionService extends BaseService {

	private Logger logger = LoggerFactory.getLogger(QuestionService.class);

	@RequestMapping(value = "/plusMinus", method = RequestMethod.GET)
	public String plusMinus(String max, String min, String num) {
		logger.info("Start /question/plusMinus?min=" + min + "&max=" + max + "&num=" + num);
		QuestionAgent agent = null;
		JSONObject result = null;
		try {
			agent = new QuestionAgent();
			int maxInt = (max == null ? 100 : Integer.parseInt(max));
			int minInt = (min == null ? 0 : Integer.parseInt(min));
			int numInt = (num == null ? 20 : Integer.parseInt(num));
			result = agent.getPlusMinus(maxInt, minInt, numInt);
			result = successResponse(result);
		} catch (Exception e) {
			e.printStackTrace();
			result = makeResponse(ReturnType.FAILURE, null, e.getMessage());
		} finally {
			if (agent != null) {
				agent.done();
			}
		}
		logger.info("Finish /question/plusMinus (" + result + ")");
		return result.toString();
	}

	@RequestMapping(value = "/pm", method = RequestMethod.GET)
	public String generate1(String max, String min, String num) {
		logger.info("Start /question/pm?min=" + min + "&max=" + max + "&num=" + num);

		JSONObject result = JSONObject.parseObject(plusMinus(max, min, num));
		if (!"success".equals(result.getString("result"))) {
			return "生成失败(" + result.getString("message") + ")";
		}
		String resultString = "";
		JSONArray qa = result.getJSONArray("qa");
		for (int i = 0; i < qa.size(); i++) {
			resultString += (i + 1) + ". ";
			resultString += qa.getJSONObject(i).getString("q");
			resultString += "<br/>";
		}

		logger.info("Finish /question/pm (" + result + ")");
		return resultString;
	}
}
