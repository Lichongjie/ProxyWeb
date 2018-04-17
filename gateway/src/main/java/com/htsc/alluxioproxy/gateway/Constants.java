package com.htsc.alluxioproxy.gateway;

/**
 * Constants for the gateway.
 */
public final class Constants {
  public static final String HOSTNAME = "gateway.hostname";
  public static final String PORT = "gateway.port";
  public static final String STORAGE_SERVICE_URL = "gateway.storage.service.url";
  public static final String CONF_DIR = "gateway.conf.dir";
  public static final String USERNAME = "gateway.username";
  public static final String PASSWORD = "gateway.password";
  public static final String TOKEN_VALIDATE_ENABLED = "gateway.token.validate.enabled";

  public static final String REDIS_CLUSTER_BEGIN_ID = "gateway.redis.cluster.begin.id";
  public static final String REDIS_CLUSTER_NUM = "gateway.redis.cluster.num";
  public static final String REDIS_CLUSTER_HOST_PORT_FORMAT =
      "gateway.redis.cluster.host.port.format";

  private Constants() {}
}
