package com.ebay.app.raptor.cache.redis;

import java.lang.reflect.Method;
import java.text.MessageFormat;

import com.ebay.kernel.logger.LogLevel;
import com.ebay.kernel.logger.Logger;

public class BeanUtil {
	private static Logger logger = Logger.getInstance(BeanUtil.class);

	public static void setProperty(Object bean, String property, String value) {
		StringBuilder sb = new StringBuilder("set");
		sb.append(Character.toUpperCase(property.charAt(0)));
		if (property.length() > 1) {
			sb.append(property.substring(1));
		}
		try {
			Method method = bean.getClass().getMethod(sb.toString(),
					String.class);
			method.invoke(bean, value);
		} catch (Exception e) {
			String msg = MessageFormat.format("Fail to set {0} with {1} for {2}", property,value,bean.getClass().getName());
			logger.log(LogLevel.ERROR,msg,e);
		}
	}
}
