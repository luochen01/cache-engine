package com.ebay.app.raptor.cache;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ebay.app.raptor.cache.redis.RedisCacheEngine;

public class CacheEngineFactory {
	private static Log log = LogFactory.getLog(CacheEngineFactory.class);

	private static final String[] searchPath = new String[] {
			"/META-INF/cache.properties", "META-INF/cache.properties",
			"/resources/META-INF/cache.properties", "/WEB-INF/cache.properties" };

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
		ClassLoader loader = CacheEngineFactory.class.getClassLoader();
		for (String path : searchPath) {
			InputStream input = loader.getResourceAsStream(path);
			if (input != null) {
				props = new Properties();
				InputStreamReader reader = null;
				try {
					reader = new InputStreamReader(input);
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
