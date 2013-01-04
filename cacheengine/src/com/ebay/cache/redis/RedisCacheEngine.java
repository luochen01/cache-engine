package com.ebay.cache.redis;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.impl.GenericObjectPool.Config;

import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.util.SafeEncoder;

import com.ebay.cache.CacheEngine;
import com.ebay.cache.IApplicationKey;
import com.ebay.cache.IExcutor;
import com.ebay.cache.IExcutorContext;
import com.ebay.cache.ISectionKey;
import com.ebay.cache.redis.property.DefaultPropertyContext;
import com.ebay.cache.redis.property.DefaultPropertyEvaluator;
import com.ebay.cache.redis.property.EvaluateException;
import com.ebay.cache.redis.property.PropertyContext;
import com.ebay.cache.redis.property.PropertyEvaluator;
import com.ebay.cache.redis.property.PropertyEvaluatorComparator;
import com.ebay.cache.redis.property.RedisInstancePropertyContext;
import com.ebay.cache.redis.property.RedisInstancePropertyEvaluator;

public class RedisCacheEngine implements CacheEngine, IConstants {
	private static Log log = LogFactory.getLog(RedisCacheEngine.class);

	protected ShardedJedisPool pool = null;
	protected Properties props = null;
	protected Map<String, RedisInfo> redisInfos = null;
	protected Map<String, String> settings = null;
	protected boolean initialized = false;

	protected Map<PropertyEvaluator, PropertyContext> evaluators = null;

	private static RedisCacheEngine instance = null;

	private RedisCacheEngine() {

	}

	public static CacheEngine getInstance() {
		if (instance == null) {
			instance = new RedisCacheEngine();
		}
		return instance;
	}

	@Override
	public void initialize(Properties props) {
		if (initialized) {
			throw new UnsupportedOperationException(
					"Init method has been called before!");
		}
		if (props == null) {
			throw new NullPointerException("properties is null");
		}
		this.props = props;
		redisInfos = new HashMap<String, RedisInfo>();
		settings = new HashMap<>();
		// initialize the evaluators
		evaluators = new TreeMap<>(new PropertyEvaluatorComparator());
		evaluators.put(new DefaultPropertyEvaluator(),
				new DefaultPropertyContext(settings));
		evaluators.put(new RedisInstancePropertyEvaluator(),
				new RedisInstancePropertyContext(redisInfos));
		loadProperties(this.props);
		initConnection();
		configure(settings);
	}

	@Override
	public boolean isInitialized() {
		return initialized;
	}

	@Override
	public void destroy() {
		if (pool != null) {
			pool.destroy();
			initialized = false;
		}
	}

	@Override
	public void configure(String param, String value) {
		ShardedJedis shardedJedis = pool.getResource();
		Collection<Jedis> jedises = shardedJedis.getAllShards();
		try {
			for (Jedis j : jedises) {
				j.configSet(param, value);
			}
		} catch (Exception ex) {
			String msg = MessageFormat
					.format("Error occurred during apply the setting {0}={1}, cause:{2}",
							param, value, ex.getMessage());
			log.error(msg);
		}
		pool.returnResource(shardedJedis);
	}

	@Override
	public void configure(Map<String, String> settings) {
		ShardedJedis shardedJedis = pool.getResource();
		Collection<Jedis> jedises = shardedJedis.getAllShards();
		for (Entry<String, String> e : settings.entrySet()) {
			String key = e.getKey();
			String value = e.getValue();
			try {
				for (Jedis j : jedises) {
					j.configSet(key, value);
				}
			} catch (Exception ex) {
				String msg = MessageFormat
						.format("Error occurred during apply the setting {0}={1}, cause:{2}",
								key, value, ex.getMessage());
				log.error(msg);
			}
		}
		pool.returnResource(shardedJedis);
	}

	private void loadProperties(Properties props) {
		int line = 0;
		for (Entry<Object, Object> pair : props.entrySet()) {
			++line;
			String key = (String) pair.getKey();
			String value = (String) pair.getValue();
			for (Entry<PropertyEvaluator, PropertyContext> entry : evaluators
					.entrySet()) {
				if (entry.getKey().qualify(key)) {
					try {
						entry.getKey().process(key, value, entry.getValue());
					} catch (EvaluateException e) {
						String msg = MessageFormat
								.format("Error Occured while proccessing line:{0},{1}={2}",
										line, key, value);
						log.error(msg);
					}
					break;
				}
			}
		}
	}

	private void initConnection() {
		List<JedisShardInfo> shards = new ArrayList<>();
		for (Entry<String, RedisInfo> entry : redisInfos.entrySet()) {
			RedisInfo info = entry.getValue();
			JedisShardInfo shard = new JedisShardInfo(info.getHost(),
					info.getPort(), info.getTimeout(), Integer.valueOf(info
							.getWeight()));
			shard.setPassword(info.getPassword());
			shards.add(shard);
		}
		pool = new ShardedJedisPool(new Config(), shards);
	}

	@Override
	public void excute(IExcutor excutor) {
		ShardedJedis jedis = null;
		try {
			jedis = pool.getResource();
			IExcutorContext context = new RedisExcutorContext(jedis);
			excutor.excute(context);
		} finally {
			if (jedis != null) {
				pool.returnResource(jedis);
			}
		}
	}

	@Override
	public void set(IApplicationKey appKey, ISectionKey secKey, String key,
			byte[] value) {
		set(appKey, secKey, key, value, 0);
	}

	@Override
	public void set(IApplicationKey appKey, ISectionKey secKey, String key,
			byte[] value, int expire) {
		ShardedJedis jedis = null;
		try {
			jedis = pool.getResource();
			String realKey = RedisKeyUtil.getKey(appKey, secKey, key);
			int realExpire = RedisKeyUtil.getExpire(appKey, secKey, expire);
			byte[] binKey = SafeEncoder.encode(realKey);
			jedis.set(binKey, value);
			if (realExpire > 0) {
				jedis.expire(binKey, realExpire);
			}
		} finally {
			if (jedis != null) {
				pool.returnResource(jedis);
			}
		}
	}

	@Override
	public byte[] get(IApplicationKey appKey, ISectionKey secKey, String key) {
		ShardedJedis jedis = null;
		try {
			jedis = pool.getResource();
			String realKey = RedisKeyUtil.getKey(appKey, secKey, key);
			byte[] binKey = SafeEncoder.encode(realKey);
			return jedis.get(binKey);
		} finally {
			if (jedis != null) {
				pool.returnResource(jedis);
			}
		}
	}

	@Override
	public void remove(IApplicationKey appKey, ISectionKey secKey, String key) {
		ShardedJedis jedis = null;
		try {
			jedis = pool.getResource();
			String realKey = RedisKeyUtil.getKey(appKey, secKey, key);
			jedis.del(realKey);
		} finally {
			if (jedis != null) {
				pool.returnResource(jedis);
			}
		}
	}

	@Override
	public void addToList(IApplicationKey appKey, ISectionKey secKey,
			String key, byte[] value, int index) {
		addToList(appKey, secKey, key, value, index, 0);
	}

	@Override
	public void addToList(IApplicationKey appKey, ISectionKey secKey,
			String key, byte[] value, int index, int expire) {
		ShardedJedis jedis = null;
		try {
			jedis = pool.getResource();
			String realKey = RedisKeyUtil.getKey(appKey, secKey, key);
			int realExpire = RedisKeyUtil.getExpire(appKey, secKey, expire);
			byte[] binKey = SafeEncoder.encode(realKey);
			if (index == 0) {
				jedis.lpush(binKey, value);
			} else if (index == -1) {
				jedis.rpush(binKey, value);
			} else {
				byte[] pivot = jedis.lindex(binKey, index);
				jedis.linsert(binKey, LIST_POSITION.BEFORE, pivot, value);
			}
			if (realExpire > 0) {
				jedis.expire(binKey, realExpire);
			}
		} finally {
			if (jedis != null) {
				pool.returnResource(jedis);
			}
		}
	}

	@Override
	/**
	 * 
	 * @param appKey
	 * @param secKey
	 * @param key
	 * @param index
	 *            0 to remove the value from the head, -1 to remove the value
	 *            from the tail
	 */
	public void popFromList(IApplicationKey appKey, ISectionKey secKey,
			String key, boolean tail) {
		ShardedJedis jedis = null;
		try {
			jedis = pool.getResource();
			String realKey = RedisKeyUtil.getKey(appKey, secKey, key);
			if (tail) {
				jedis.rpop(realKey);
			} else {
				jedis.lpop(realKey);
			}
		} finally {
			if (jedis != null) {
				pool.returnResource(jedis);
			}
		}
	}

	@Override
	public void removeFromList(IApplicationKey appKey, ISectionKey secKey,
			String key, byte[] value) {
		ShardedJedis jedis = null;
		try {
			jedis = pool.getResource();
			String realKey = RedisKeyUtil.getKey(appKey, secKey, key);
			byte[] binKey = SafeEncoder.encode(realKey);
			jedis.lrem(binKey, 0, value);
		} finally {
			if (jedis != null) {
				pool.returnResource(jedis);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<byte[]> getList(IApplicationKey appKey, ISectionKey secKey,
			String key, int begin, int end) {
		if (begin > end && end != -1) {
			throw new IllegalArgumentException(
					"Wrong index of begin and end of the list");
		}
		ShardedJedis jedis = null;
		List<byte[]> result = Collections.EMPTY_LIST;
		try {
			jedis = pool.getResource();
			String realKey = RedisKeyUtil.getKey(appKey, secKey, key);
			byte[] binKey = SafeEncoder.encode(realKey);
			result = jedis.lrange(binKey, begin, end);
		} finally {
			if (jedis != null) {
				pool.returnResource(jedis);
			}
		}
		return result;
	}

	@Override
	public List<byte[]> getList(IApplicationKey appKey, ISectionKey secKey,
			String key) {
		return getList(appKey, secKey, key, 0, -1);
	}

	@Override
	public int getListSize(IApplicationKey appKey, ISectionKey secKey,
			String key) {
		ShardedJedis jedis = null;
		try {
			jedis = pool.getResource();
			String realKey = RedisKeyUtil.getKey(appKey, secKey, key);
			return jedis.llen(realKey).intValue();
		} finally {
			if (jedis != null) {
				pool.returnResource(jedis);
			}
		}
	}

	@Override
	public void addToSet(IApplicationKey appKey, ISectionKey secKey,
			String key, byte[]... values) {
		addToSet(appKey, secKey, key, 0, values);
	}

	@Override
	public void addToSet(IApplicationKey appKey, ISectionKey secKey,
			String key, int expire, byte[]... values) {
		ShardedJedis jedis = null;
		try {
			jedis = pool.getResource();
			String realKey = RedisKeyUtil.getKey(appKey, secKey, key);
			byte[] binKey = SafeEncoder.encode(realKey);
			int realExpire = RedisKeyUtil.getExpire(appKey, secKey, expire);
			jedis.sadd(binKey, values);
			if (realExpire > 0) {
				jedis.expire(binKey, realExpire);
			}
		} finally {
			if (jedis != null) {
				pool.returnResource(jedis);
			}
		}
	}

	@Override
	public void removeFromSet(IApplicationKey appKey, ISectionKey secKey,
			String key, byte[]... values) {
		ShardedJedis jedis = null;
		try {
			jedis = pool.getResource();
			String realKey = RedisKeyUtil.getKey(appKey, secKey, key);
			byte[] binKey = SafeEncoder.encode(realKey);
			jedis.srem(binKey, values);
		} finally {
			if (jedis != null) {
				pool.returnResource(jedis);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<byte[]> getSet(IApplicationKey appKey, ISectionKey secKey,
			String key) {
		ShardedJedis jedis = null;
		Set<byte[]> result = Collections.EMPTY_SET;
		try {
			jedis = pool.getResource();
			String realKey = RedisKeyUtil.getKey(appKey, secKey, key);
			byte[] binKey = SafeEncoder.encode(realKey);
			result = jedis.smembers(binKey);
		} finally {
			if (jedis != null) {
				pool.returnResource(jedis);
			}
		}
		return result;
	}

	@Override
	public boolean inSet(IApplicationKey appKey, ISectionKey secKey,
			String key, byte[] value) {
		ShardedJedis jedis = null;
		try {
			jedis = pool.getResource();
			String realKey = RedisKeyUtil.getKey(appKey, secKey, key);
			byte[] binKey = SafeEncoder.encode(realKey);
			return jedis.sismember(binKey, value);
		} finally {
			if (jedis != null) {
				pool.returnResource(jedis);
			}
		}
	}
}
