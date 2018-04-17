package com.htsc.alluxioproxy.exceptions;

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
