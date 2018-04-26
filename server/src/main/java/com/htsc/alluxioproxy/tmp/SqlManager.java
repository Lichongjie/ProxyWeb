package com.htsc.alluxioproxy.tmp;

import com.google.common.base.Preconditions;
import com.htsc.alluxioproxy.ServerContext;

import com.htsc.alluxioproxy.sql.SqlTmp;
import com.htsc.alluxioproxy.sql.util.Configuration;
import com.htsc.alluxioproxy.sql.util.PathUtils;
import com.htsc.alluxioproxy.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.channels.FileLock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by lenovo on 2017/12/14.
 */
public enum SqlManager {
  INSTANCE;
  private static final String DATABASE_BACKUP_DIR =
          Configuration.INSTANCE.getString(Constants.DATABASE_BACKUP_DIR);
  private static final String TMP_FILE_NAME =
          Configuration.INSTANCE.getString(Constants.TMP_FILE_NAME);
  private static final Logger LOG = LoggerFactory.getLogger(SqlManager.class);
  private File mTmpFile = null;
  private ReentrantLock fileLock = new ReentrantLock();

  public byte[] getObject(SqlTmp tmp) throws IOException {
    ByteArrayOutputStream bo = new ByteArrayOutputStream();
    ObjectOutputStream oo = new ObjectOutputStream(bo);
    oo.writeObject(tmp);
    oo.close();
    return bo.toByteArray();
  }

  /**
   * Write sql.sql operation to tmp file
   *
   * @param tmp the sql.sql operation
   * @throws IOException if error happened when writing tmp file
   */
  public synchronized void write(SqlTmp tmp) throws IOException {
    Preconditions.checkNotNull(tmp);
    LOG.info("start to write tmp file, tmp entity is {}", tmp.toString());
    FileLock lock = null;
    RandomAccessFile f = null;
    byte[] tmpObj = getObject(tmp);
    try {
      tmpFileExistCheck();
      f = new RandomAccessFile(mTmpFile, "rw");
      lock = f.getChannel().lock();
      f.seek(f.length());
      f.write(tmpObj);
    } finally {
      if (lock != null) {
        lock.release();
      }
      if (f != null) {
        f.close();
        LOG.info("write tmp file finished");
      }
    }
  }

  /**
   * Checks if tmp file exists.
   *
   * @throws IOException if error happened when create tmp file
   */
  public void tmpFileExistCheck() throws IOException {
    String tmpPath = PathUtils.concatPath(DATABASE_BACKUP_DIR, TMP_FILE_NAME +ServerContext
        .getServerId());
    mTmpFile = new File(tmpPath);
    if(!mTmpFile.exists()) {
      LOG.info("create tmp file");
      if (!mTmpFile.getParentFile().exists()) {
        PathUtils.mkdirs(mTmpFile.getParentFile());
      }
      if (!mTmpFile.createNewFile()) {
        LOG.error("can't create tmp file");
      }
    }
  }
}

