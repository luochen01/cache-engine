package com.ebay.cache.redis.property;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ebay.cache.redis.BeanUtil;
import com.ebay.cache.redis.RedisInfo;

public class RedisInstancePropertyEvaluator implements PropertyEvaluator {
	private Log log = LogFactory.getLog(RedisInstancePropertyEvaluator.class);

	@Override
	public boolean qualify(String key) {
		Pattern p = Pattern
				.compile("^redis\\.\\w+\\.(host|port|password|timeout|weight)$");
		Matcher m = p.matcher(key);
		return m.matches();
	}

	@Override
	public void process(String key, String value, PropertyContext context)
			throws EvaluateException {
		String[] keys = key.split("\\.");
		if (keys.length != 3) {
			log.warn("Invalid settings: " + key + " " + value
					+ " for RedisInstancePropertyEvaluator");
			return;
		}
		RedisInstancePropertyContext redisContext = (RedisInstancePropertyContext) context;
		Map<String, RedisInfo> settings = redisContext.getSettings();
		String name = keys[1];
		String redisKey = keys[2];
		RedisInfo redis = null;
		if ((redis = settings.get(name)) == null) {
			redis = new RedisInfo();
			settings.put(name, redis);
		}
		try {
			BeanUtil.setProperty(redis, redisKey, value);
		} catch (Exception e) {
			throw new EvaluateException(
					"Error occured during setting the value of RedisInfo", e);
		}
	}

	@Override
	public int getSequence() {
		return 1;
	}
}
