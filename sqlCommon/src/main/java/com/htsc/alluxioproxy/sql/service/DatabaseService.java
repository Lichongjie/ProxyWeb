package com.htsc.alluxioproxy.sql.service;


import org.apache.ibatis.session.SqlSession;

/** This class handel sql.sql connection. */
public class DatabaseService {
  static SqlSession session;

  public static void openSession(boolean isAuto) {
    session = SqlTools.getSession(isAuto);
  }

  /**
   * Commit sql.sql operation.
   */
  public static void commit() {
    session.commit();
  }

  /**
   * Rollback sql.sql operation.
   */
  public static void rollback() {
    session.rollback();
  }


  public static boolean needRecover(Exception e) {
    /*
    if ( e instanceof MySQLTimeoutException){
      return true;
    }
    return false;/*/
    return true;

  }

  public static void close() {
    session.close();
  }

}
