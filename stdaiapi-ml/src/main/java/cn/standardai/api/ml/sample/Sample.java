package cn.standardai.api.ml.sample;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class Sample {

	private static String knnTrainingSample = "["
			+ "{}"
			+ ""
			+ ""
			+ "]";

	private static String knnTargetSample = "["
			+ "{}"
			+ ""
			+ ""
			+ "]";

	public static List<JSONObject> sampleKNNTrainingData() {
		List<JSONObject> data = new ArrayList<JSONObject>();
		JSONArray sampleArray = JSONArray.parseArray(knnTrainingSample);
		for (int i = 0; i < sampleArray.size(); i++) {
			data.add(sampleArray.getJSONObject(i));
		}
		return data;
	}

	public static List<JSONObject> sampleKNNTargetData() {
		List<JSONObject> data = new ArrayList<JSONObject>();
		JSONArray sampleArray = JSONArray.parseArray(knnTargetSample);
		for (int i = 0; i < sampleArray.size(); i++) {
			data.add(sampleArray.getJSONObject(i));
		}
		return data;
	}
}
