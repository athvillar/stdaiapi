package cn.standardai.api.math.agent;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class QuestionAgent {

	public JSONObject getPlusMinus(int max, int min, int num) {

		JSONArray questions = new JSONArray();
		for (int i = 0; i < num; i++) {
			Integer sum = new Double(min + Math.random() * (max - min)).intValue();
			Integer p1 = new Double(min / 2 + Math.random() * (sum - min / 2)).intValue();
			Integer p2 = sum - p1;
			JSONObject qa = new JSONObject();
			if (Math.random() > 0.5) {
				qa.put("q", p1 + " + " + p2 + " = ");
				qa.put("a", sum);
			} else {
				qa.put("q", sum + " - " + p1 + " = ");
				qa.put("a", p2);
			}
			questions.add(qa);
		}

		JSONObject result = new JSONObject();
		result.put("qa", questions);
		return result;
	}

	public void done() {
		// Nothing to do
	}
}
