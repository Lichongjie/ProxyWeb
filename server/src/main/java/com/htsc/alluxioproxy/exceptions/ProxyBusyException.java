package com.htsc.alluxioproxy.exceptions;

/**
 * Exception for the proxy to be too busy.
 */
public class ProxyBusyException extends Exception {
  /**
   * Constructor for {@link ProxyBusyException}.
   *
   * @param msg the error message
   */
  public ProxyBusyException(String msg) {
    super(msg);
  }
}
