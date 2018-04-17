package com.htsc.alluxioproxy.gateway.security;

/**
 * Authenticates session tokens.
 */
public interface TokenAuthentication {
  /**
   * Verifies whether a session token is illegal.
   *
   * @param token the session token string
   * @return <code>true</code> if the session token is illegal, <code>false</code> otherwise
   */
  boolean verify(String token);

  /**
   * Factory for {@link TokenAuthentication}.
   */
  final class Factory {
    /**
     * @return an instance of {@link TokenAuthentication}
     */
    public static TokenAuthentication get() {
      return RedisTokenAuthentication.getSingleton();
    }

    private Factory() {}
  }
}
