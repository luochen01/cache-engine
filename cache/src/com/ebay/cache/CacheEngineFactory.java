package com.ebay.cache;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ebay.cache.redis.RedisCacheEngine;

public class CacheEngineFactory {
	private static Log log = LogFactory.getLog(CacheEngineFactory.class);
	private static final String[] searchPath = new String[] { "src/cache.properties" };

	public static CacheEngine getRedisCacheEngine()
			throws CacheEngineInitializeException {
		CacheEngine engine = RedisCacheEngine.getInstance();
		if (!engine.isInitialized()) {
			Properties props = getProperties();
			if (props == null) {
				throw new CacheEngineInitializeException(
						"No cache.properties file found from classpath");
			}
			engine.initialize(props);
		}
		return engine;

	}

	private static Properties getProperties() {
		Properties props = null;
		for (String path : searchPath) {
			File file = new File(path);
			if (file.exists()) {
				props = new Properties();
				FileReader reader = null;
				try {
					reader = new FileReader(file);
					props.load(reader);
					return props;
				} catch (Exception e) {
					log.error("Error Occured while reading " + path + " :"
							+ e.getMessage());
				} finally {
					if (reader != null) {
						try {
							reader.close();
						} catch (IOException e) {
						}
					}
				}
			}
		}
		return props;
	}
}
