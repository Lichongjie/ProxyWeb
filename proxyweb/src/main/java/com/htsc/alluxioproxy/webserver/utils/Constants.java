package com.htsc.alluxioproxy.webserver.utils;

/**
 * Constants for the gateway.
 */
public final class Constants {
  public static final String HOST_NAME = "proxyweb.hostname";
  public static final String PORT = "proxyweb.port";
  public static final String STORAGE_SERVICE_URL = "storage.service.url";
  public static final String USERNAME = "webserver.username";
  public static final String PASSWORD = "webserver.password";

  public static final String CONF_DIR = "web.conf.dir";

  public static final String MYSQ_CONF_DIR = "mysql.conf.dir";

  public static final String HISTORU_INIT_NUM = "history.init.num";
  public static final String FILEINFO_INIT_NUM = "fileinfo.init.num";
  public static final String TMP_PATH="tmp.path";

  public static final String ARCHIVE_INIT_NUM="archive.init.num";
  public static final String DELETE_INIT_NUM = "delete.init.num";

  public static final String FILE_INIT_ALLOW= "file.init.allow";

  public static final String LOGIN_TIMEOUT_INTERNAL = "login.timeout.internal";
  public static final String FILE_DOWNLOAD_NUM = "file.download.num";

  public static final String ALLUXIO_UFS_DIR = "proxy.alluxio.ufs.dir";

  public static final String DATABASE_URL = "jdbc.url";
  public static final String DATABASE_USERNAME="jdbc.username";
  public static final String DATABASE_PASSWORD="jdbc.password";

  private Constants() {}
}
