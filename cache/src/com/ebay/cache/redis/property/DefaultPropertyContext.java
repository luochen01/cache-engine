package com.ebay.cache.redis.property;

import java.util.Map;

public class DefaultPropertyContext implements PropertyContext {
	private Map<String, String> settings = null;

	public Map<String, String> getSettings() {
		return settings;
	}

	public void setSettings(Map<String, String> settings) {
		this.settings = settings;
	}

	public DefaultPropertyContext(Map<String, String> settings) {
		this.settings = settings;
	}
}
