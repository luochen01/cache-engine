package com.ebay.app.raptor.cache.redis.property;

public class DefaultPropertyEvaluator implements PropertyEvaluator {

	@Override
	public boolean qualify(String key) {
		return true;
	}

	@Override
	public void process(String key, String value, PropertyContext context) {
		DefaultPropertyContext defaultContext = (DefaultPropertyContext) context;
		defaultContext.getSettings().put(key, value);
	}

	@Override
	public int getSequence() {
		return 0;
	}
}
