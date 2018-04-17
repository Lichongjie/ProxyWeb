package com.htsc.alluxioproxy.gateway.exceptions;

/**
 * Exception for invalid session token.
 */
public class InvalidTokenException extends Exception {
  /**
   * Constructor for {@link InvalidTokenException}.
   *
   * @param msg the error message
   */
  public InvalidTokenException(String msg) {
    super(msg);
  }
}
