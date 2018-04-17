package com.htsc.alluxioproxy.security;

import com.google.common.base.Preconditions;
import com.htsc.alluxioproxy.ServerContext;
import com.htsc.alluxioproxy.StorageResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Authenticates users via a configuration file.
 */
public enum ConfiguredUserAuthentication implements UserAuthentication {
  INSTANCE;
  private static final Logger LOG = LoggerFactory.getLogger(ConfiguredUserAuthentication.class);

  /**
   * Constructor for {@link ConfiguredUserAuthentication}.
   */
  ConfiguredUserAuthentication() {}

  @Override
  public boolean verify(String username, String password) {
    Preconditions.checkNotNull(password, "Null password");
    String pwd = ServerContext.getPassword(username);
    LOG.info("download test:{}, {}", pwd, password);
    return password.equals(pwd);
  }
}
