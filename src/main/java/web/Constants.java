package web;

/**
 * Constants for the gateway.
 */
public final class Constants {
  public static final String HOSTNAME = "gateway.hostname";
  public static final String PORT = "gateway.port";
  public static final String STORAGE_SERVICE_URL = "storage.service.url";
  public static final String CONF_DIR = "gateway.conf.dir";
  public static final String USERNAME = "webserver.username";
  public static final String PASSWORD = "webserver.password";
  public static final String TOKEN_VALIDATE_ENABLED = "gateway.token.validate.enabled";

  public static final String REDIS_CLUSTER_BEGIN_ID = "gateway.redis.cluster.begin.id";
  public static final String REDIS_CLUSTER_NUM = "gateway.redis.cluster.num";
  public static final String REDIS_CLUSTER_HOST_PORT_FORMAT =
      "gateway.redis.cluster.host.port.format";

  public static final String HISTORU_INIT_NUM = "history.init.num";
  public static final String FILEINFO_INIT_NUM = "fileinfo.init.num";
  public static final String TMP_PATH="tmp.path";

  private Constants() {}
}
