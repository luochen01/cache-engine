package com.ebay.app.raptor.cache;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ebay.app.raptor.cache.redis.RedisExcutorContext;
import com.ebay.app.raptor.cache.redis.client.ShardedJedis;

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
	private static int count = 0;

	@BeforeClass
	public static void setUp() throws Exception {
		count = 1000;
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
				RedisExcutorContext redisContext = (RedisExcutorContext) context;
				ShardedJedis jedis = redisContext.getJedis();
				jedis.set("rawkey", "rawvalue");
				String value = jedis.get("rawkey");
				assertEquals("rawvalue", value);
			}
		});
	}

	@Test
	public void testKey() {
		engine.set(appKey, secKey1, "name", "luochen");
		engine.set(appKey, secKey2, "name", "tonya");
		String str = (String) engine.get(appKey, secKey1, "name");
		assertEquals("luochen", str);
		str = (String) (engine.get(appKey, secKey2, "name"));
		assertEquals("tonya", str);
		engine.set(null, null, "name", "nokey");
		str = (String) (engine.get(null, null, "name"));
		assertEquals("nokey", str);
		engine.set(appKey, secKey1, "expire", "luochen", 1);
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
		engine.addToList(appKey, secKey1, "list", "a", 0);
		engine.addToList(appKey, secKey1, "list", "b", -1);
		List<Serializable> list = engine.getList(appKey, secKey1, "list");
		assertArrayEquals(new String[] { "a", "b" }, list.toArray());
		engine.removeFromList(appKey, secKey1, "list", "b");
		list = engine.getList(appKey, secKey1, "list");
		assertArrayEquals(new String[] { "a" }, list.toArray());
		int size = engine.getListSize(appKey, secKey1, "list");
		assertEquals(1, size);
	}

	@Test
	public void testSet() {
		engine.remove(appKey, secKey1, "set");
		engine.addToSet(appKey, secKey1, "set", "a");
		engine.addToSet(appKey, secKey1, "set", "a");
		engine.addToSet(appKey, secKey1, "set", "b");
		boolean in = engine.inSet(appKey, secKey1, "set", "a");
		assertTrue(in);
		in = engine.inSet(appKey, secKey1, "set", "d");
		assertFalse(in);
		engine.removeFromSet(appKey, secKey1, "set", "a");
		Set<Serializable> set = engine.getSet(appKey, secKey1, "set");
		assertArrayEquals(new String[] { "b" }, set.toArray());
	}

	@Test
	public void testPerformance() {
		Random random = new Random();
		String[] values = new String[count];

		for (int i = 0; i < count; i++) {
			values[i] = String.valueOf(random.nextInt());
		}
		long begin = System.currentTimeMillis();
		for (int i = 0; i < count; i++) {
			engine.set(null, null, values[i], "luochen");
		}
		long end = System.currentTimeMillis();
		System.out.println("Time of sharded " + count + " excution ="
				+ String.valueOf(end - begin) + "ms");
	}
}
