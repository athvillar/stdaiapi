package com.kingdy.parkos.redis.serializer;

import java.nio.charset.Charset;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class JSONRedisSerializer<T> implements RedisSerializer<T> {

	private static SerializerFeature[] features = {
			SerializerFeature.WriteNullNumberAsZero,
			SerializerFeature.WriteNullStringAsEmpty,
			SerializerFeature.DisableCircularReferenceDetect,
			SerializerFeature.UseISO8601DateFormat
	};

	@Override
	public byte[] serialize(T t) throws SerializationException {
		if (t == null) return null;
		return JSONObject.toJSONString(t, features).getBytes(Charset.forName("UTF8"));
	}

	@SuppressWarnings("unchecked")
	@Override
	public T deserialize(byte[] bytes) throws SerializationException {
		if (bytes == null) return null;
		return (T)JSONObject.parse(new String(bytes, Charset.forName("UTF8")));
	}
}
