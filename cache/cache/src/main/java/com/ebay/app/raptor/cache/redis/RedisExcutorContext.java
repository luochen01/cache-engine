package com.ebay.app.raptor.cache.redis;


import com.ebay.app.raptor.cache.IExcutorContext;
import com.ebay.app.raptor.cache.redis.client.ShardedJedis;

public class RedisExcutorContext implements IExcutorContext {
	private ShardedJedis jedis;

	public RedisExcutorContext(ShardedJedis jedis) {
		this.jedis = jedis;
	}

	public ShardedJedis getJedis() {
		return jedis;
	}
}
