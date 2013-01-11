package com.ebay.app.raptor.cache.serialize;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ebay.app.raptor.cache.redis.serialize.SerializeUtil;

public class SerializeUtilTest {

   public static class A implements Serializable {
      /**
       * 
       */
      private static final long serialVersionUID = 1L;

      private String str;

      private Double dou;

      private Integer inte;

      private A(String str, Double dou, Integer inte) {
         super();
         this.str = str;
         this.dou = dou;
         this.inte = inte;
      }

      public String getStr() {
         return str;
      }

      public void setStr(String str) {
         this.str = str;
      }

      public Double getDou() {
         return dou;
      }

      public void setDou(Double dou) {
         this.dou = dou;
      }

      public Integer getInte() {
         return inte;
      }

      public void setInte(Integer inte) {
         this.inte = inte;
      }

      private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
         str = in.readUTF();
         dou = in.readDouble();
         inte = in.readInt();
      }

      private void writeObject(ObjectOutputStream out) throws IOException {
         out.writeUTF(str);
         out.writeDouble(dou);
         out.writeInt(inte);
      }

   }

   public static class B implements Serializable {
      /**
       * 
       */
      private static final long serialVersionUID = 1L;

      private String str;

      private Double dou;

      private Integer inte;

      private B(String str, Double dou, Integer inte) {
         super();
         this.str = str;
         this.dou = dou;
         this.inte = inte;
      }

      public String getStr() {
         return str;
      }

      public void setStr(String str) {
         this.str = str;
      }

      public Double getDou() {
         return dou;
      }

      public void setDou(Double dou) {
         this.dou = dou;
      }

      public Integer getInte() {
         return inte;
      }

      public void setInte(Integer inte) {
         this.inte = inte;
      }
   }

   @BeforeClass
   public static void setUpBeforeClass() throws Exception {
   }

   @AfterClass
   public static void tearDownAfterClass() throws Exception {
   }

   @Test
   public void test() {
      try {
         Class.forName("com.ebay.app.raptor.cache.redis.serialize.SerializeUtil");
      } catch (ClassNotFoundException e) {
         e.printStackTrace();
      }
      int count = 1000;
      byte[] as = null;
      byte[] bs = null;
      A a = new A("A", 1.1, 2);
      B b = new B("B", 1.1, 2);
      long begin = System.currentTimeMillis();
      for (int i = 0; i < count; i++) {
         as = SerializeUtil.serialize(a);
         SerializeUtil.unserialize(as);
      }
      long end = System.currentTimeMillis();
      System.out.println((end - begin));
      begin = System.currentTimeMillis();
      for (int i = 0; i < count; i++) {
         bs = SerializeUtil.serialize(b);
         SerializeUtil.unserialize(bs);
      }
      end = System.currentTimeMillis();
      System.out.println((end - begin));

   }

}
