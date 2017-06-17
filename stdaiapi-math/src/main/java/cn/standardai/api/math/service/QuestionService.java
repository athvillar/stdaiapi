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
@RequestMapping("/questions")
public class QuestionService extends BaseService {

	private Logger logger = LoggerFactory.getLogger(QuestionService.class);

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String generate(String max, String min, String num, String round, String chain, String level, String type) {
		logger.info("Start /question/plusMinus?max=" + max + "&min=" + min + "&round=" + round + "&num=" + num
				 + "&chain=" + chain + "&level=" + level + "&type=" + type);
		QuestionAgent agent = null;
		JSONObject result = null;
		try {
			agent = new QuestionAgent();
			int maxInt = (max == null ? 100 : Integer.parseInt(max));
			int minInt = (min == null ? 0 : Integer.parseInt(min));
			int numInt = (num == null ? 20 : Integer.parseInt(num));
			int chainInt = (chain == null ? 1 : Integer.parseInt(chain));
			if (chainInt < 1) chainInt = 1;
			int levelInt = (level == null ? 3 : Integer.parseInt(level));
			if (levelInt > 3 || levelInt < 1) levelInt = 3;
			int typeInt = (type == null ? 1 : Integer.parseInt(type));
			if (typeInt > 3 || typeInt < 1) typeInt = 1;
			int roundInt = (round == null ? 0 : Integer.parseInt(round));
			if (roundInt < 1) roundInt = 1;
			result = agent.generate(maxInt, minInt, numInt, chainInt, levelInt, typeInt, roundInt);
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

	@RequestMapping(value = "/plain", method = RequestMethod.GET)
	public String generate1(String max, String min, String num, String round, String chain, String level, String type) {

		JSONObject result = JSONObject.parseObject(generate(max, min, num, round, chain, level, type));
		if (!"success".equals(result.getString("result"))) {
			return "生成失败(" + result.getString("message") + ")";
		}
		String resultString = "";
		JSONArray qa = result.getJSONArray("qa");
		for (int i = 0; i < qa.size(); i++) {
			resultString += "<font size=4 style=\"line-height:150%;\" color=\"#BBB\">" + (i + 1) + ". </font>";
			resultString += "<font size=4 color=\"#000\">" + qa.getJSONObject(i).getString("q")+ "</font>";
			resultString += "<font size=4 color=\"#FFF\">" + qa.getJSONObject(i).getString("a") + "</font>";
			resultString += "<br/>";
		}

		return resultString;
	}
}
