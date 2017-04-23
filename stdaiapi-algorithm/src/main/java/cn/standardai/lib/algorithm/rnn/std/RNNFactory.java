package cn.standardai.lib.algorithm.rnn.std;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.lib.algorithm.cnn.Layer.LayerType;
import cn.standardai.lib.algorithm.common.ByteUtil;
import cn.standardai.lib.algorithm.exception.StorageException;

public class RNNFactory {

	/*
		{
		  "volume" : 100,
		  "hidden" : {
		    "size" : 10
		  }
		}
	 */
	public static RNN getInstance(JSONObject param) throws RNNException {

		Integer volume = param.getInteger("volume");
		JSONObject hiddenJSONObject = param.getJSONObject("hidden");
		Integer hiddenSize = hiddenJSONObject.getInteger("size");

		RNN instance = new RNN(volume, hiddenSize);
		return instance;
	}

	public static byte[] getBytes(RNNFactory cnn) {
		// TODO
		return null;
	}

	public static RNN getInstance(byte[] bytes) {
		// TODO
		return null;
	}
}
