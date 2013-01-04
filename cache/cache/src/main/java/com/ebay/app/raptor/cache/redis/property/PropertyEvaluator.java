package com.ebay.app.raptor.cache.redis.property;

public interface PropertyEvaluator {
	/**
	 * indicate a given key is qualified for the evaluator
	 * 
	 * @param key
	 * @return
	 */
	public boolean qualify(String key);

	/**
	 * process a given key/value pair, with the proper context. If exception
	 * throws, then the key/value pair will be processed by next evaluator
	 * 
	 * @param key
	 * @param value
	 * @param context
	 * @throws EvaluateException
	 */
	public void process(String key, String value, PropertyContext context)
			throws EvaluateException;

	/**
	 * the sequence of the evaluator, the bigger it is, the former it would
	 * excute
	 * 
	 * @return
	 */
	public int getSequence();
}
