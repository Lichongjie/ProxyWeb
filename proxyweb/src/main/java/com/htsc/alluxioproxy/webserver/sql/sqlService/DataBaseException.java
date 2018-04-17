package com.htsc.alluxioproxy.webserver.sql.sqlService;

/**
 * Created by lenovo on 2017/12/3.
 */
public class DataBaseException extends Exception {
  /**
   * Constructor for {@link DataBaseException}.
   *
   * @param msg the error message
   */
  public DataBaseException(String msg) {
    super(msg);
  }
}
