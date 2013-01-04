package com.ebay.test.cache.redis;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.pool.impl.GenericObjectPool.Config;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.Transaction;

public class RedisTest {

	private Jedis jedis = null;
	private int count = 0;

	@Before
	public void setUp() throws Exception {
		jedis = new Jedis("localhost");
		count = 1000;
	}

	@After
	public void tearDown() throws Exception {
		jedis.disconnect();
	}

	public void output(String... values) {
		for (String value : values) {
			System.out.println(value);
		}
	}

	@SuppressWarnings({ "rawtypes" })
	public void output(Collection c) {
		for (Object value : c) {
			System.out.println(value);
		}
	}

	@Test
	public void testBase() {
		jedis.set("foo", "bar");
		String value = jedis.get("foo");
		Assert.assertEquals("bar", value);
	}

	@Test
	public void testList() {
		jedis.del("list");
		// test lpush
		jedis.lpush("list", "0");
		List<String> list = jedis.lrange("list", 0, -1);
		assertArrayEquals(new Object[] { "0" }, list.toArray());
		// test rpush
		jedis.rpush("list", "1");
		list = jedis.lrange("list", 0, -1);
		assertArrayEquals(new Object[] { "0", "1" }, list.toArray());
		// test llen
		assertEquals(2, jedis.llen("list").intValue());
		// test lindex
		assertEquals("0", jedis.lindex("list", 0));
		// test linsert
		jedis.linsert("list", LIST_POSITION.BEFORE, "1", "0.5");
		list = jedis.lrange("list", 0, -1);
		assertArrayEquals(new Object[] { "0", "0.5", "1" }, list.toArray());
		// test lset
		jedis.lset("list", 1, "0");
		list = jedis.lrange("list", 0, -1);
		assertArrayEquals(new Object[] { "0", "0", "1" }, list.toArray());
		// test lrem
		jedis.lrem("list", 0, "0");
		list = jedis.lrange("list", 0, -1);
		assertArrayEquals(new Object[] { "1" }, list.toArray());
		// test lpushx
		jedis.lpushx("list", "2");
		list = jedis.lrange("list", 0, -1);
		assertArrayEquals(new Object[] { "2", "1" }, list.toArray());
		jedis.ltrim("list", 0, 0);
		list = jedis.lrange("list", 0, -1);
		assertArrayEquals(new Object[] { "2" }, list.toArray());
	}

	@Test
	public void testHash() {
		jedis.del("hash");
		// test hash
		jedis.hset("hash", "name", "lc");
		jedis.hset("hash", "pwd", "123");
		Set<String> set = jedis.hkeys("hash");
		assertArrayEquals(new String[] { "pwd", "name" }, set.toArray());
		// test hget
		assertEquals("lc", jedis.hget("hash", "name"));
	}

	@Test
	public void testSet() {
		jedis.del("set");
		jedis.del("set2");

		jedis.sadd("set", "lc");
		jedis.sadd("set", "lc");
		jedis.sadd("set", "lc");
		jedis.sadd("set", "lc");
		// test set
		assertEquals(true, jedis.sismember("set", "lc"));
		jedis.sadd("set2", "ldy");
		Set<String> set = jedis.sunion("set", "set2");
		assertArrayEquals(new String[] { "lc", "ldy" }, set.toArray());
		assertEquals(1, jedis.scard("set").intValue());
	}

	@Test
	public void testZset() {
		jedis.del("zset");
		jedis.del("zset2");

		jedis.zadd("zset", 1, "lc");
		jedis.zadd("zset", 0, "ldy");
		//test zrange
		Set<String> set = jedis.zrange("zset", 0, 1);
		assertArrayEquals(new String[] { "ldy", "lc" }, set.toArray());
		//test zcount
		assertEquals(2, jedis.zcount("zset", -1, 2).intValue());
	}

	public void testExpire() {
		jedis.set("foo", "bar");
		String foo = jedis.get("foo");
		assertEquals("bar", foo);
		jedis.expire("foo", 5);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}
		foo = jedis.get("bar");
		assertEquals(null, foo);
	}

	@SuppressWarnings("unused")
	@Test
	public void testPool() {
		JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
		Jedis jedis = pool.getResource();
		try {
			// / ... do stuff here ... for example
			jedis.set("foo", "bar");
			String foobar = jedis.get("foo");
			Assert.assertEquals("bar", foobar);
			jedis.zadd("sose", 0, "car");
			jedis.zadd("sose", 0, "bike");
			Set<String> sose = jedis.zrange("sose", 0, -1);
		} finally {
			pool.returnResource(jedis);
		}
		pool.destroy();
	}

	@Test
	public void testTransaction() {
		jedis.watch("name");
		Transaction t = jedis.multi();
		t.set("foo", "bar1");
		Response<String> bar1 = t.get("foo");
		t.exec();
		Assert.assertEquals("bar1", bar1.get());
	}

	@Test
	public void testPipeline() {
		jedis.del("foo");
		Pipeline p = jedis.pipelined();
		p.set("fool", "bar");
		p.zadd("foo", 1, "barowitch");
		p.zadd("foo", 0, "barinsky");
		p.zadd("foo", 0, "barikoviev");
		Response<String> pipeString = p.get("fool");
		@SuppressWarnings("unused")
		Response<Set<String>> sose = p.zrange("foo", 0, -1);
		p.sync();
		Assert.assertEquals("bar", pipeString.get());
	}

	@Test
	public void testSharding() {
		List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
		JedisShardInfo si = new JedisShardInfo("localhost", 6379);
		shards.add(si);
		si = new JedisShardInfo("localhost", 6380);
		shards.add(si);
		ShardedJedis jedis = new ShardedJedis(shards);
		jedis.set("a", "a");
		jedis.set("b", "b");
		jedis.set("c", "c");
		jedis.set("d", "d");
		jedis.set("e", "e");
		jedis.set("f", "f");
		jedis.set("g", "g");

		Assert.assertEquals("a", jedis.get("a"));
		Assert.assertEquals("b", jedis.get("b"));
		Assert.assertEquals("c", jedis.get("c"));
		Assert.assertEquals("d", jedis.get("d"));
		Assert.assertEquals("e", jedis.get("e"));
		Assert.assertEquals("f", jedis.get("f"));
		Assert.assertEquals("g", jedis.get("g"));
	}

	public void testPerformance() {

		JedisPool pool = new JedisPool("localhost");
		Random random = new Random();
		String[] values = new String[count];
		for (int i = 0; i < count; i++) {
			values[i] = String.valueOf(random.nextInt());
		}

		long begin = System.currentTimeMillis();
		for (int i = 0; i < count; i++) {
			Jedis jedis = pool.getResource();
			jedis.set(values[i], "luochen");
			jedis.get(values[i]);
			pool.returnResource(jedis);
		}
		long end = System.currentTimeMillis();
		output("Time of " + count + " excution =" + String.valueOf(end - begin)
				+ "ms");
		pool.destroy();
	}

	public void testShardPerformance() {
		List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
		JedisShardInfo si = new JedisShardInfo("localhost", 6379);
		shards.add(si);
		si = new JedisShardInfo("localhost", 6380);
		shards.add(si);
		ShardedJedisPool pool = new ShardedJedisPool(new Config(), shards);
		Random random = new Random();
		String[] values = new String[count];

		for (int i = 0; i < count; i++) {
			values[i] = String.valueOf(random.nextInt());
		}
		long begin = System.currentTimeMillis();
		for (int i = 0; i < count; i++) {
			ShardedJedis jedis = pool.getResource();
			jedis.set(values[i], "luochen");
			jedis.get(values[i]);
			pool.returnResource(jedis);
		}
		long end = System.currentTimeMillis();
		output("Time of sharded " + count + " excution ="
				+ String.valueOf(end - begin) + "ms");
	}

	public void testUnifiedPerforance() {
	}

}
