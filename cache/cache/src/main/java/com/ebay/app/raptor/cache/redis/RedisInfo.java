package com.ebay.app.raptor.cache.redis;

import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RedisInfo {
	private static Log log = LogFactory.getLog(RedisInfo.class);

	private String host;
	private int port;
	private int timeout;
	private int weight;
	private String password;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(String port) {
		try {
			this.port = Integer.valueOf(port);
		} catch (NumberFormatException e) {
			String msg = MessageFormat.format(
					"Invalid value {0} for redis.port, use default value 6379",
					port);
			log.error(msg);
		}
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		try {
			this.timeout = Integer.valueOf(timeout);
		} catch (NumberFormatException e) {
			String msg = MessageFormat
					.format("Invalid value {0} for redis.timeout, use default value 2000",
							timeout);
			log.error(msg);
		}
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		try {
			this.weight = Integer.valueOf(weight);
		} catch (NumberFormatException e) {
			String msg = MessageFormat.format(
					"Invalid value {0} for redis.weight, use default value 1",
					weight);
			log.error(msg);
		}
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}