package cn.standardai.lib.algorithm.test;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.lib.algorithm.cnn.CNN;

public class TestCnn {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			test1();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void test1() throws Exception {

		String param = "" +
		"{" +
		"  \"layers\" : [" +
		"    {\"type\": \"INPUT\", \"width\": 8, \"height\": 8, \"depth\": 3 }," +
		"    {\"type\": \"CONV\", \"depth\": 5, \"stride\": 1, \"padding\":1," +
		"      \"filter\": {\"width\":3, \"height\":3}" +
		"    }," +
		"    {\"type\": \"RELU\", \"function\": \"max\"}," +
		"    {\"type\": \"POOL\", \"method\": \"max\", \"spatial\": 2, \"stride\": 2}," +
		"    {\"type\": \"FC\", \"depth\": 2 }" +
		"  ]" +
		"}";

		String data = "" +
		"{" +
		"  \"data\": [" +
		"    [" +
		"      [1,2,3,1,2,3,1,2]," +
		"      [1,2,3,1,2,3,1,2]," +
		"      [1,2,3,1,2,3,1,2]," +
		"      [1,2,3,1,2,3,1,2]," +
		"      [1,2,3,1,2,3,1,2]," +
		"      [1,2,3,1,2,3,1,2]," +
		"      [1,2,3,1,2,3,1,2]," +
		"      [1,2,3,1,2,3,1,2]" +
		"    ]," +
		"    [" +
		"      [1,2,3,1,2,3,1,2]," +
		"      [1,2,3,1,2,3,1,2]," +
		"      [1,2,3,1,2,3,1,2]," +
		"      [1,2,3,1,2,3,1,2]," +
		"      [1,2,3,1,2,3,1,2]," +
		"      [1,2,3,1,2,3,1,2]," +
		"      [1,2,3,1,2,3,1,2]," +
		"      [1,2,3,1,2,3,1,2]" +
		"    ]," +
		"    [" +
		"      [1,2,3,1,2,3,1,2]," +
		"      [1,2,3,1,2,3,1,2]," +
		"      [1,2,3,1,2,3,1,2]," +
		"      [1,2,3,1,2,3,1,2]," +
		"      [1,2,3,1,2,3,1,2]," +
		"      [1,2,3,1,2,3,1,2]," +
		"      [1,2,3,1,2,3,1,2]," +
		"      [1,2,3,1,2,3,1,2]" +
		"    ]" +
		"  ]," +
		"  \"target\": [0,1]" +
		"}";

		// 创建网络
		CNN cnn = CNN.getInstance(JSONObject.parseObject(param));
		cnn.loadData(JSONObject.parseObject(data));

		// 训练
		cnn.train();

		// 预测
		Double[][][] predict = cnn.predict(JSONObject.parseObject(data));

		// 输出预测
		for (int i = 0; i < predict[0][0].length; i++) {
			System.out.println(predict[0][0][i]);
		}
	}
}
