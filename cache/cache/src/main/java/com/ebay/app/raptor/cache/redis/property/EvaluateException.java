package com.ebay.app.raptor.cache.redis.property;

public class EvaluateException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EvaluateException(String msg, Exception e) {
		super(msg, e);
	}
}
