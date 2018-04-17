package com.htsc.alluxioproxy.sql.service;

import com.htsc.alluxioproxy.sql.util.Configuration;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.Reader;

public final class SqlTools {
  public static SqlSessionFactory sessionFactory;

  public static Configuration getConf() {
    return Configuration.INSTANCE;
  }

  /**
   * Get session for sql.sql operation.
   *
   * @param isAuto if is auto commit
   * @return the session instance
   */
  public static SqlSession getSession(boolean isAuto) {
    return sessionFactory.openSession(isAuto);
  }

  /**
   * Refresh connection
   */
  public static void refresh() {
    try {
      Reader reader = Resources.getResourceAsReader("mybatis.cfg.xml");
      sessionFactory = new SqlSessionFactoryBuilder().build(reader, Configuration.getSqlProperties());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
