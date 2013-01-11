package com.ebay.app.raptor.cache.redis;

import java.util.Collection;

import com.ebay.app.raptor.cache.IApplicationKey;
import com.ebay.app.raptor.cache.ISectionKey;

public class RedisKeyUtil {
   private static final String KEY_SPLITTER = ":";

   public static String getKey(IApplicationKey appKey, ISectionKey secKey, String... keys) {
      StringBuilder sb = new StringBuilder(64);
      sb.append(appKey != null ? appKey.name() : "");
      sb.append(KEY_SPLITTER);
      sb.append(secKey != null ? secKey.name() : "");
      sb.append(KEY_SPLITTER);
      for (String key : keys) {
         sb.append(key);
      }
      return sb.toString();
   }

   public static String getKey(IApplicationKey appKey, ISectionKey secKey, Collection<String> keys) {
      StringBuilder sb = new StringBuilder(64);
      sb.append(appKey != null ? appKey.name() : "");
      sb.append(KEY_SPLITTER);
      sb.append(secKey != null ? secKey.name() : "");
      sb.append(KEY_SPLITTER);
      for (String key : keys) {
         sb.append(key);
      }
      return sb.toString();
   }

   public static String[] splitKey(String key) {
      String[] result = key.split(KEY_SPLITTER, 3);
      if (result.length < 3) {
         throw new IllegalArgumentException("Unsuppoted Key " + key);
      }
      return result;
   }

   public static int getExpire(IApplicationKey appKey, ISectionKey secKey, int expire) {
      if (expire > 0) {
         return expire;
      }
      if (secKey != null && secKey.expire() > 0) {
         return secKey.expire();
      }
      if (appKey != null && appKey.expire() > 0) {
         return appKey.expire();
      }
      return 0;
   }
}
