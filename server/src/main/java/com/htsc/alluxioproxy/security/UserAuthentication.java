package com.htsc.alluxioproxy.security;

/**
 * Authenticates valid users according to the given username and password.
 */
public interface UserAuthentication {
  /**
   * Verifies the username and the provided password.
   *
   * @param username the user name
   * @param password the password
   * @return true if verification passed, false otherwise
   */
  boolean verify(String username, String password);

  /**
   * Factory for {@link UserAuthentication}.
   */
  final class Factory {
    /**
     * Creates an instance of {@link UserAuthentication}.
     *
     * @return an instance of {@link UserAuthentication}
     */
    public static UserAuthentication get() {
      return ConfiguredUserAuthentication.INSTANCE;
    }

    private Factory() {}
  }
}
