package com.htsc.alluxioproxy.webserver.sql.sqlService;

/**
 * Exception for user authentication failure.
 */
public class AuthenticationException extends Exception {
  /**
   * Constructor for {@link AuthenticationException}.
   *
   * @param msg the error message
   */
  public AuthenticationException(String msg) {
    super(msg);
  }
}
