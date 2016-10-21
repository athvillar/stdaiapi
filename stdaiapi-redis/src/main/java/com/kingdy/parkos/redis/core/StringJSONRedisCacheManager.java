package com.kingdy.parkos.redis.core;

import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.kingdy.parkos.core.bean.PropertyConfig.Redis;
import com.kingdy.parkos.redis.serializer.JSONRedisSerializer;

public class StringJSONRedisCacheManager {

	private static JedisConnectionFactory factory;

	private static Map<String, RedisCacheManager> rcmMap;

	private static long defaultExpireSeconds = 1800L;

	public static void init(Redis redis) {
		StringJSONRedisCacheManager.rcmMap = new HashMap<String, RedisCacheManager>();
		factory = new JedisConnectionFactory();
		factory.setHostName(redis.getHost());
		factory.setPort(redis.getPort());
		factory.setTimeout(redis.getTimeout());
		factory.setDatabase(redis.getDatabase());
		factory.setPassword(redis.getPassword());
		factory.afterPropertiesSet();
	}

	public static <T> RedisCacheManager createRedisCacheManager(long expireSeconds) {

		RedisTemplate<String, T> rt = new RedisTemplate<String, T>();
		rt.setConnectionFactory(factory);
		RedisSerializer<String> serializer1 = new JSONRedisSerializer<String>();
		RedisSerializer<T> serializer2 = new JSONRedisSerializer<T>();
		rt.setKeySerializer(serializer1);
		rt.setValueSerializer(serializer2);
		rt.setHashKeySerializer(serializer1);
		rt.setHashValueSerializer(serializer2);
		rt.afterPropertiesSet();

		RedisCacheManager rcm = new RedisCacheManager(rt);
		rcm.setDefaultExpiration(expireSeconds);
		rcm.afterPropertiesSet();
		return rcm;
	}

	public static Cache getCache(String cacheManagerName, String cacheName) {

		if (rcmMap.containsKey(cacheManagerName)) {
			return rcmMap.get(cacheManagerName).getCache(cacheName);
		} else {
			RedisCacheManager rcm = createRedisCacheManager(defaultExpireSeconds);
			rcmMap.put(cacheManagerName, rcm);
			return rcm.getCache(cacheName);
		}
	}
}
