package com.ebay.app.raptor.cache.redis;

import com.ebay.app.raptor.cache.IApplicationKey;
import com.ebay.app.raptor.cache.ISectionKey;

public class RedisKeyUtil {
	private static final String KEY_SPLITTER = ":";

	public static String getKey(IApplicationKey appKey, ISectionKey secKey,
			String key) {
		StringBuilder sb = new StringBuilder(64);
		sb.append(appKey != null ? appKey.getName() : "");
		sb.append(KEY_SPLITTER);
		sb.append(secKey != null ? secKey.getName() : "");
		sb.append(KEY_SPLITTER);
		sb.append(key);
		return sb.toString();
	}

	public static String[] splitKey(String key) {
		String[] result = key.split(KEY_SPLITTER);
		if (result.length < 3) {
			throw new IllegalArgumentException("Unsuppoted Key " + key);
		}
		return result;
	}

	public static int getExpire(IApplicationKey appKey, ISectionKey secKey,
			int expire) {
		if (expire >= 0) {
			return expire;
		}
		if (secKey != null && secKey.getExpire() >= 0) {
			return secKey.getExpire();
		}
		if (appKey != null && appKey.getExpire() >= 0) {
			return appKey.getExpire();
		}
		return 0;
	}
	
	public static void main(String[] args){
		String key = ":hello";
		String [] result = splitKey(key);
		for(String str : result){
			System.out.println(str);
		}
	}
}
