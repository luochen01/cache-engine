package com.ebay.app.raptor.cache;

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

   public void set(IApplicationKey appKey, ISectionKey secKey, String key, Object value, int expire);

   public void set(IApplicationKey appKey, ISectionKey secKey, List<String> key, Object value, int expire);

   public Object get(IApplicationKey appKey, ISectionKey secKey, String... key);

   public Object get(IApplicationKey appKey, ISectionKey secKey, List<String> key);

   public void remove(IApplicationKey appKey, ISectionKey secKey, String... key);

   public void addToList(IApplicationKey appKey, ISectionKey secKey, String key, Object value, int index, int expire);

   public void popFromList(IApplicationKey appKey, ISectionKey secKey, String key, boolean tail);

   public void removeFromList(IApplicationKey appKey, ISectionKey secKey, String key, Object value);

   public List<Object> getList(IApplicationKey appKey, ISectionKey secKey, String key, int begin, int end);

   public List<Object> getList(IApplicationKey appKey, ISectionKey secKey, String key);

   public int getListSize(IApplicationKey appKey, ISectionKey secKey, String key);

   public void addToSet(IApplicationKey appKey, ISectionKey secKey, String key, Object value, int expire);

   public void removeFromSet(IApplicationKey appKey, ISectionKey secKey, String key, Object value);

   public Set<Object> getSet(IApplicationKey appKey, ISectionKey secKey, String key);

   public boolean inSet(IApplicationKey appKey, ISectionKey secKey, String key, Object value);
}
