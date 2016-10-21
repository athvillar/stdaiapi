package com.kingdy.parkos.redis.agent;

import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;

import com.kingdy.parkos.redis.bean.Token;
import com.kingdy.parkos.redis.core.StringJSONRedisCacheManager;

public class TokenRedisAgent {

	private Cache cache;

	public TokenRedisAgent(String cacheManagerName, String cacheName) {
		this.cache = StringJSONRedisCacheManager.getCache(cacheManagerName, cacheName);
	}

	public Token get(String key) {
		ValueWrapper vw = cache.get(key);
		return (Token)vw.get();
	}

	public void put(String key, Token value) {
		cache.put(key, value);
	}
}
