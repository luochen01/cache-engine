package com.ebay.app.raptor.cache.redis.serialize;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class CacheObjectInputStream extends ObjectInputStream {

	public CacheObjectInputStream(InputStream in) throws IOException {
		super(in);
	}

	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException,
			ClassNotFoundException {
		String name = desc.getName();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try {
			return Class.forName(name, false, loader);
		} catch (ClassNotFoundException ex) {
			return super.resolveClass(desc);
		}
	}

}
