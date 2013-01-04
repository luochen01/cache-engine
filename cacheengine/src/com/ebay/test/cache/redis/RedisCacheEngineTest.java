package com.ebay.test.cache.redis;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import redis.clients.jedis.ShardedJedis;

import com.ebay.cache.CacheEngine;
import com.ebay.cache.CacheEngineFactory;
import com.ebay.cache.IApplicationKey;
import com.ebay.cache.IExcutor;
import com.ebay.cache.IExcutorContext;
import com.ebay.cache.ISectionKey;
import com.ebay.cache.redis.RedisExcutorContext;

class AppKey implements IApplicationKey {
	private String appName;
	private int expire;

	public AppKey(String appName, int expire) {
		super();
		this.appName = appName;
		this.expire = expire;
	}

	@Override
	public String getName() {
		return appName;
	}

	@Override
	public int getExpire() {
		return expire;
	}
}

class SectionKey implements ISectionKey {
	private String secName;
	private int expire;

	public SectionKey(String secName, int expire) {
		super();
		this.secName = secName;
		this.expire = expire;
	}

	@Override
	public String getName() {
		return secName;
	}

	@Override
	public int getExpire() {
		return expire;
	}
}

public class RedisCacheEngineTest {

	private static CacheEngine engine = null;
	private static IApplicationKey appKey = null;
	private static ISectionKey secKey1 = null;
	private static ISectionKey secKey2 = null;

	@BeforeClass
	public static void setUp() throws Exception {
		engine = CacheEngineFactory.getRedisCacheEngine();
		appKey = new AppKey("test", 0);
		secKey1 = new SectionKey("sec1", 10);
		secKey2 = new SectionKey("sec2", 5);
	}

	@AfterClass
	public static void tearDown() throws Exception {
		engine.destroy();
	}

	@Test
	public void testExcute() {
		engine.excute(new IExcutor() {
			@Override
			public void excute(IExcutorContext context) {
				RedisExcutorContext redisContext = (RedisExcutorContext)context;
				ShardedJedis jedis = redisContext.getJedis();
				jedis.set("rawkey","rawvalue");
				String value = jedis.get("rawkey");
				assertEquals("rawvalue", value);
			}
		});
	}

	@Test
	public void testKey() {
		engine.set(appKey, secKey1, "name", "luochen".getBytes());
		engine.set(appKey, secKey2, "name", "tonya".getBytes());
		String str = new String(engine.get(appKey, secKey1, "name"));
		assertEquals("luochen", str);
		str = new String(engine.get(appKey, secKey2, "name"));
		assertEquals("tonya", str);
		engine.set(null, null, "name", "nokey".getBytes());
		str = new String(engine.get(null, null, "name"));
		assertEquals("nokey", str);
		engine.set(appKey, secKey1, "expire", "luochen".getBytes(), 1);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertNull(engine.get(appKey, secKey1, "expire"));

		engine.remove(appKey, secKey1, "name");
		assertNull(engine.get(appKey, secKey1, "name"));
	}

	@Test
	public void testList() {
		engine.remove(appKey, secKey1, "list");
		engine.addToList(appKey, secKey1, "list", "a".getBytes(), 0);
		engine.addToList(appKey, secKey1, "list", "b".getBytes(), -1);
		List<byte[]> list = engine.getList(appKey, secKey1, "list");
		assertArrayEquals(new byte[][] { "a".getBytes(), "b".getBytes() },
				list.toArray());
		engine.removeFromList(appKey, secKey1, "list", "b".getBytes());
		list = engine.getList(appKey, secKey1, "list");
		assertArrayEquals(new byte[][] { "a".getBytes() }, list.toArray());
		int size = engine.getListSize(appKey, secKey1, "list");
		assertEquals(1, size);
	}

	@Test
	public void testSet() {
		engine.remove(appKey, secKey1, "set");
		engine.addToSet(appKey, secKey1, "set", "a".getBytes());
		engine.addToSet(appKey, secKey1, "set", "a".getBytes());
		engine.addToSet(appKey, secKey1, "set", "b".getBytes());
		boolean in = engine.inSet(appKey, secKey1, "set", "a".getBytes());
		assertTrue(in);
		in = engine.inSet(appKey, secKey1, "set", "d".getBytes());
		assertFalse(in);
		engine.removeFromSet(appKey, secKey1, "set", "a".getBytes());
		Set<byte[]> set = engine.getSet(appKey, secKey1, "set");
		assertArrayEquals(new byte[][] { "b".getBytes() }, set.toArray());
	}

}
