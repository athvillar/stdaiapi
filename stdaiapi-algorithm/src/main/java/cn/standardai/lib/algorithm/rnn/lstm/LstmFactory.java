package cn.standardai.lib.algorithm.rnn.lstm;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.lib.algorithm.cnn.Layer.LayerType;
import cn.standardai.lib.algorithm.common.ByteUtil;
import cn.standardai.lib.algorithm.exception.StorageException;

public class LstmFactory {

	/*
		{
		  "volume" : 100,
		  "hidden" : {
		    "size" : 10
		  }
		}
	 */
	public static Lstm getInstance(JSONObject param) throws LstmException {

		Integer volume = param.getInteger("volume");
		JSONObject hiddenJSONObject = param.getJSONObject("hidden");
		Integer hiddenSize = hiddenJSONObject.getInteger("size");

		Lstm instance = new Lstm(volume, hiddenSize);
		return instance;
	}

	public static byte[] getBytes(LstmFactory cnn) {
		// TODO
		return null;
	}

	public static Lstm getInstance(byte[] bytes) {
		// TODO
		return null;
	}
}
