package com.ebay.cache.redis.property;

import java.util.Map;

import com.ebay.cache.redis.RedisInfo;

public class RedisInstancePropertyContext implements PropertyContext {
	private Map<String, RedisInfo> settings;

	public RedisInstancePropertyContext(Map<String, RedisInfo> settings) {
		this.settings = settings;
	}

	public Map<String, RedisInfo> getSettings() {
		return settings;
	}

	public void setSettings(Map<String, RedisInfo> settings) {
		this.settings = settings;
	}
}
