package com.htsc.alluxioproxy.exceptions;

/**
 * Exception for authorization failure.
 */
public class AuthorizationException extends Exception {
  /**
   * Constructor for {@link AuthorizationException}.
   *
   * @param msg the error message
   */
  public AuthorizationException(String msg) {
    super(msg);
  }
}
