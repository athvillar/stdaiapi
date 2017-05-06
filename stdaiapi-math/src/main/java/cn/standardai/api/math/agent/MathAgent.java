package cn.standardai.api.math.agent;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.util.MathUtil;

public class MathAgent {

	public JSONObject rand(char[] chars, int num, int len) {
		JSONObject result = new JSONObject();
		JSONArray data = new JSONArray();

		String randString = MathUtil.random(chars, num * len);
		for (int i = 0; i < num; i++) {
			data.add(randString.substring(i, i + len));
		}
		result.put("data", data);
		return result;
	}

	public void done() {
		// Nothing to do
	}
}
