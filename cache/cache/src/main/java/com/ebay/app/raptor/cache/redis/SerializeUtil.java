package com.ebay.app.raptor.cache.redis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SerializeUtil {
	private static Log log = LogFactory.getLog(SerializeUtil.class);

	public static byte[] serialize(Object object) {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			byte[] bytes = baos.toByteArray();
			return bytes;
		} catch (Exception e) {
			String msg = MessageFormat.format(
					"Fail to serialize class {0}, caused by {1}", object
							.getClass().getName(), e.getMessage());
			log.error(msg);
		} finally {
			try {
				if (baos != null) {
					baos.close();
				}
				if (oos != null) {
					oos.close();
				}
			} catch (IOException e) {
			}
		}
		return null;
	}

	public static Serializable unserialize(byte[] bytes) {
		if(bytes == null){
			return null;
		}
		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;
		try {
			bais = new ByteArrayInputStream(bytes);
			ois = new ObjectInputStream(bais);
			return (Serializable) ois.readObject();
		} catch (Exception e) {
			String msg = MessageFormat.format(
					"Fail to unserialize object from byte[], caused by {0}",
					e.getMessage());
			log.error(msg);
		} finally {
			try {
				if (bais != null) {
					bais.close();
				}
				if (ois != null) {
					ois.close();
				}
			} catch (IOException e) {
			}
		}
		return null;
	}

	public static Collection<Serializable> unserialize(Collection<byte[]> input) {
		Collection<Serializable> result = null;
		if (input instanceof List<?>) {
			result = new ArrayList<Serializable>();
		} else if (input instanceof Set<?>) {
			result = new HashSet<Serializable>();
		} else {
			return null;
		}
		for (byte[] bytes : input) {
			result.add(unserialize(bytes));
		}
		return result;
	}

}
