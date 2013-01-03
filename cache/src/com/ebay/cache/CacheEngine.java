package com.ebay.cache;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public interface CacheEngine {
	public void initialize(Properties props);

	public boolean isInitialized();

	public void destroy();

	public void configure(String param, String value);

	public void configure(Map<String, String> settings);

	public void excute(IExcutor excutor);

	// key/value operation
	public void set(IApplicationKey appKey, ISectionKey secKey, String key,
			byte[] value);

	public void set(IApplicationKey appKey, ISectionKey secKey, String key,
			byte[] value, int expire);

	public byte[] get(IApplicationKey appKey, ISectionKey secKey, String key);

	public void remove(IApplicationKey appKey, ISectionKey secKey, String key);

	// list
	/**
	 * 
	 * @param appKey
	 * @param secKey
	 * @param key
	 * @param value
	 * @param index
	 *            0 to push the value to the head, -1 to push the value to the
	 *            tail
	 */
	public void addToList(IApplicationKey appKey, ISectionKey secKey,
			String key, byte[] value, int index);

	public void addToList(IApplicationKey appKey, ISectionKey secKey,
			String key, byte[] value, int index, int expire);

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
			String key, boolean tail);

	public void removeFromList(IApplicationKey appKey, ISectionKey secKey,
			String key, byte[] value);

	public List<byte[]> getList(IApplicationKey appKey, ISectionKey secKey,
			String key, int begin, int end);

	public List<byte[]> getList(IApplicationKey appKey, ISectionKey secKey,
			String key);

	public int getListSize(IApplicationKey appKey, ISectionKey secKey,
			String key);

	// set
	public void addToSet(IApplicationKey appKey, ISectionKey secKey,
			String key, byte[]... value);

	public void addToSet(IApplicationKey appKey, ISectionKey secKey,
			String key, int expire, byte[]... values);

	public void removeFromSet(IApplicationKey appKey, ISectionKey secKey,
			String key, byte[]... values);

	public Set<byte[]> getSet(IApplicationKey appKey, ISectionKey secKey,
			String key);

	public boolean inSet(IApplicationKey appKey, ISectionKey secKey,
			String key, byte[] value);
}
