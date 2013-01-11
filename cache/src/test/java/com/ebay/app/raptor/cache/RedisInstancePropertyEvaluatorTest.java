package com.ebay.app.raptor.cache;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ebay.app.raptor.cache.redis.property.RedisInstancePropertyEvaluator;
import com.ebay.kernel.logger.LogLevel;
import com.ebay.kernel.logger.Logger;

public class RedisInstancePropertyEvaluatorTest {
	private RedisInstancePropertyEvaluator evaluator = new RedisInstancePropertyEvaluator();
	private static Logger log = Logger.getInstance(RedisInstancePropertyEvaluatorTest.class);

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testQualify() {
		assertFalse(evaluator.qualify("redis"));
		assertFalse(evaluator.qualify("redis."));
		assertFalse(evaluator.qualify("redis.a.b"));
		assertTrue(evaluator.qualify("redis.a.host"));
		log.log(LogLevel.ERROR,"Test Complete");
	}

	@Test
	public void testProcess() {
	}

	@Test
	public void testGetSequence() {
	}

}
  