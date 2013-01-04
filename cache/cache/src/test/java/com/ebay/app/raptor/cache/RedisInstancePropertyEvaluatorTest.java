package com.ebay.app.raptor.cache;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ebay.app.raptor.cache.redis.property.RedisInstancePropertyEvaluator;

public class RedisInstancePropertyEvaluatorTest {
	private RedisInstancePropertyEvaluator evaluator = new RedisInstancePropertyEvaluator();
	private static Log log = LogFactory
			.getLog(RedisInstancePropertyEvaluatorTest.class);

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
		log.info("Test Complete");
	}

	@Test
	public void testProcess() {
	}

	@Test
	public void testGetSequence() {
	}

}
  