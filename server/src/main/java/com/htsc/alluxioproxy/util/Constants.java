package com.htsc.alluxioproxy.util;

/**
 * Constants for the proxy.
 */
public final class Constants {
  public static final String HOSTNAME = "proxy.hostname";
  public static final String PORT = "proxy.port";
  public static final String CONF_DIR = "proxy.conf.dir";
  public static final String TRANSCODE_URL = "proxy.transcode.url";
  public static final String TRANSCODE_REQUEST_THREADS_NUM = "proxy.transcode.request.threads.num";
  public static final String DOWNLOAD_ENABLED = "proxy.download.enabled";
  public static final String RANDOM_SUFFIX_LENGTH = "proxy.random.suffix.length";

  public static final String ALLUXIO_UFS_DIR = "proxy.alluxio.ufs.dir";
  public static final String DATA_DIR = "proxy.data.dir";
  public static final String DOWNLOAD_TMP_DIR = "proxy.download.tmp.dir";
  public static final String MAX_DOWNLOAD_TASKS_NUM = "proxy.max.download.tasks.num";
  public static final String MAX_UPLOAD_TASKS_NUM = "proxy.max.upload.tasks.num";
  public static final String UPLOAD_WRITE_BUF_SIZE = "proxy.upload.write.buf.size";
  public static final String DOWNLOAD_WRITE_BUF_SIZE = "proxy.download.write.buf.size";

  public static final String ALLUXIO_ARCHIVE_DIR = "proxy.alluxio.archive.dir";
  public static final String DATABASE_BACKUP_DIR = "database.backup.dir";
  public static final String RECOVER_THREAD_NUM = "recover.thread.num";
  public static final String SERVER_ID = "server.id";
  public static final String UPDATE_DATABASE_INTERNAL = "update.database.internal";
  public static final String SQL_EXECUTE_INTERNAL = "sql.execute.internal";
  public static final String TMP_FILE_NAME = "tmp.file.name";
  public static final String RETRANSCODE_RETRY_TIME = "retranscode.retry.time";
  public static final String DATABASE_THREADS_NUM = "database.threads.num";

  public static final String FINISHED_SQL_COUNT = "finished.sql.sql.count";
  public static final String ALLOW_DOWNLOAD_ARCHIVED_FILE = "allow.download.archived.file";
  public static final String READ_SQL_DIR = "read.sql.dir";

  private Constants() {}
}
