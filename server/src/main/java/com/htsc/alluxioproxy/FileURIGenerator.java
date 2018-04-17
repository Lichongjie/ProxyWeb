package com.htsc.alluxioproxy;

import com.htsc.alluxioproxy.sql.util.Configuration;
import com.htsc.alluxioproxy.sql.util.PathUtils;
import com.htsc.alluxioproxy.util.Constants;

import java.security.SecureRandom;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Generates unique and indexable file URIs for uploading files from users.
 */
public final class FileURIGenerator {
  private static final String UFS_DIR = Configuration.INSTANCE.getString(Constants.ALLUXIO_UFS_DIR);
  private static final String ARCHIVE_DIR =
      Configuration.INSTANCE.getString(Constants.ALLUXIO_ARCHIVE_DIR);
  private static final String DATA_DIR = Configuration.INSTANCE.getString(Constants.DATA_DIR);
  private static final AtomicLong ID = new AtomicLong(1);
  private static final SecureRandom RANDOM = new SecureRandom();
  private static final int SUFFIX_LENGTH =
      Configuration.INSTANCE.getInt(Constants.RANDOM_SUFFIX_LENGTH);
  private static final char[] RANDOM_CHARS;

  static {
    RANDOM_CHARS = new char[62];
    for (int i = 0; i < 10; ++i) {
      RANDOM_CHARS[i] = (char) ('0' + i);
    }
    for (int i = 10; i < 36; ++i) {
      RANDOM_CHARS[i] = (char) ('A' + i - 10);
    }
    for (int i = 36; i < 62; ++i) {
      RANDOM_CHARS[i] = (char) ('a' + i - 36);
    }
  }

  /**
   * Generates a unique target file identifier ID for an uploading request.
   *
   * @param username the username
   * @return the generated file identifier ID
   */
  public static String generate(String username) {
    Date date = new Date(System.currentTimeMillis());
    int year = date.getYear() + 1900;
    int month = date.getMonth() + 1;
    int day = date.getDate();
    int hour = date.getHours();
    String userID = ServerContext.getUserID(username);
    long id = ID.getAndAdd(1);
    String randomSuffix = randomSuffix();
    return PathUtils.concatPath(DATA_DIR, year, month, day, hour,
        String.format("%04d%02d%02d-%d-%s-%s", year, month, day, id, userID, randomSuffix));
  }

  /**
   * @param fileID the file identifier ID
   * @return the Alluxio UFS path for the file
   */
  public static String getUfsPath(String fileID) {
    return PathUtils.concatPath(UFS_DIR, fileID);
  }

  /**
   * @param fileID the file identifier ID
   * @return the tmp path for the file when uploading
   */
  public static String getTmpPath(String fileID) {
    return PathUtils.concatPath(UFS_DIR, DATA_DIR, ".tmp", PathUtils.getFileName(fileID));
  }

  /**
   * @param fileID the file identifier ID
   * @return the tmp path for the file when archive
   */
  public static String getArchivePath(String fileID) {
    return PathUtils.concatPath(ARCHIVE_DIR, fileID);
  }

  public static String randomSuffix() {
    byte[] b = new byte[SUFFIX_LENGTH];
    // This method is thread-safe.
    RANDOM.nextBytes(b);
    char[] c = new char[SUFFIX_LENGTH];
    for (int i = 0; i < SUFFIX_LENGTH; ++i) {
      c[i] = RANDOM_CHARS[(b[i] + 128) % 62];
    }
    return new String(c);
  }

  private FileURIGenerator() {}
}
