package com.htsc.alluxioproxy.tmp;

import com.google.common.base.Preconditions;
import com.htsc.alluxioproxy.sql.bean.FileInfoBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class FileInfoSqlService {
  private static final Logger LOG = LoggerFactory.getLogger(FileInfoSqlService.class);
  private static SqlManager mSqlManger = SqlManager.INSTANCE;
  private static final String SERVICE_NAME = "FileInfoSqlDatabaseService";
  public static final HashMap<String, CountDownLatch> mInsertCheck = new HashMap<>();
  public static final HashSet<String> mFileInfoExist = new HashSet<>();

  /**
   * Writing insert file info operation to tmp file.
   *
   * @param info the file id
   * @throws IOException if error happened when writing tmp file;
   */
  public static void insertFileInfo(FileInfoBean info) throws IOException {
    try {
      String id = info.getFileId();
      SqlTmp tmp = new SqlTmp(SERVICE_NAME, "insertFileInfo", info, id);
      mSqlManger.write(tmp);
      mFileInfoExist.add(id);
    } finally {
      insertFinished(info.getFileId());
    }
  }

  /**
   * Writing update transcode file info operation to tmp file.
   *
   * @param fileId the file id
   * @throws IOException
   */
  public static void updateTranscodeInfo(String fileId) throws IOException {
    Preconditions.checkNotNull(fileId);
    waitInsertFinish(fileId);
    SqlTmp tmp = new SqlTmp(SERVICE_NAME, "updateTranscodeInfo",null,fileId);
    mSqlManger.write(tmp);
  }

  /**
   * Check if insert operation finished.
   *
   * @param path file id
   */
  public static void checkInsert(String path) {
    mInsertCheck.put(path, new CountDownLatch(1));
  }

  /**
   * Insert operation finish.
   *
   * @param path the file id
   */
  public static void insertFinished(String path) {
    mInsertCheck.get(path).countDown();
  }

  /**
   * Waiting for insert operation finish.
   *
   * @param path the file id
   */
  public static void waitInsertFinish(String path) {
    if(!mInsertCheck.containsKey(path))
      return;
    try {
      mInsertCheck.get(path).await(5000, MILLISECONDS);
    } catch (InterruptedException e) {
      LOG.error("interrupted when waiting for insert operation finishing");
    } finally {
      mInsertCheck.remove(path);
    }
  }

  /**
   * Writing update Archive file info operation to tmp file.
   *
   * @param fileId the file id
   * @throws Exception if error happened when writing tmp file;
   */
  public static void updateArchiveInfo(String fileId) throws IOException{
    Preconditions.checkNotNull(fileId);
    SqlTmp tmp = new SqlTmp(SERVICE_NAME, "updateArchiveInfo",null,fileId);
    mSqlManger.write(tmp);
    MetricInfoManager.updateArchiveInfo();
  }

  /**
   * Writing update move file info operation to tmp file.
   *
   * @param fileId the file id
   * @throws Exception if error happened when writing tmp file;
   */
  public static void updateMoveInfo(String fileId) throws Exception {
    Preconditions.checkNotNull(fileId);
    SqlTmp tmp = new SqlTmp(SERVICE_NAME, "updateMoveInfo",null,fileId);
    mSqlManger.write(tmp);
  }

  /**
   * Writing update unArchived file info operation to tmp file.
   *
   * @param fileId the file id
   * @throws Exception if error happened when writing tmp file;
   */
  public static void updateUnArchiveInfo(String fileId) throws Exception {
    Preconditions.checkNotNull(fileId);
    SqlTmp tmp = new SqlTmp(SERVICE_NAME, "updateUnArchiveInfo",null,fileId);
    mSqlManger.write(tmp);
  }
}
