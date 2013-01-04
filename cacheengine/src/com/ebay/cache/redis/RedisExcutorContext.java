package com.ebay.cache.redis;

import redis.clients.jedis.ShardedJedis;

import com.ebay.cache.IExcutorContext;

public class RedisExcutorContext implements IExcutorContext {
	private ShardedJedis jedis;

	public RedisExcutorContext(ShardedJedis jedis) {
		this.jedis = jedis;
	}

	public ShardedJedis getJedis() {
		return jedis;
	}
}
