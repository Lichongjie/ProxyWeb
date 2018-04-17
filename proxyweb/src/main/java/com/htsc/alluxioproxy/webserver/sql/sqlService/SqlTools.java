package com.htsc.alluxioproxy.webserver.sql.sqlService;

import com.htsc.alluxioproxy.webserver.utils.Configuration;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.Reader;

/**
 * init sql resource, including JDBC and database configuration
 */
public class SqlTools {
  public static SqlSessionFactory sessionFactory;
  static {
    try {
      Reader reader = Resources.getResourceAsReader("mybatis.cfg.xml");
      sessionFactory = new SqlSessionFactoryBuilder().build(reader, Configuration.getSqlProperties());
      } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * @return sql session
   */
  public static SqlSession getSession(){
    return sessionFactory.openSession();
  }
}
