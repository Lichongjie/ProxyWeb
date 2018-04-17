package com.htsc.alluxioproxy;

import alluxio.AlluxioURI;
import alluxio.client.ReadType;
import alluxio.client.file.FileInStream;
import alluxio.client.file.FileSystem;
import alluxio.client.file.options.OpenFileOptions;
import alluxio.exception.AlluxioException;
import com.htsc.alluxioproxy.sql.util.PathUtils;
import com.htsc.alluxioproxy.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ThreadLocalRandom;

/**
 * An instance of {@link TmpAlluxioInputStream} first reads the data from Alluxio with the
 * CACHE_PROMOTE read type. Then the data is written into a temporary local file. Next, the
 * file is opened to act as an input stream. After being read over, the file must be deleted
 * in the close() method.
 */
public final class TmpAlluxioInputStream extends InputStream {
  private static final Logger LOG = LoggerFactory.getLogger(TmpAlluxioInputStream.class);

  private static final FileSystem FS = FileSystem.Factory.get();
  private static final String TMP_DIR =
      ServerContext.getConf().getString(Constants.DOWNLOAD_TMP_DIR);
  private static final int BUF_SIZE =
      (int) ServerContext.getConf().getBytes(Constants.DOWNLOAD_WRITE_BUF_SIZE);

  private final String mFileID;
  private final File mTmpFile;
  private final InputStream mTmpIn;

  /**
   * Opens a tmp input stream from Alluxio.
   *
   * @param fileID the file ID
   * @return an instance of {@link TmpAlluxioInputStream}
   * @throws IOException if any I/O error occurs
   * @throws AlluxioException if any Alluxio error occurs
   */
  public static TmpAlluxioInputStream open(String fileID) throws IOException, AlluxioException {
    File tmpFile = new File(getTmpPath(fileID));
    writeTmpFileFromAlluxio(fileID, tmpFile);
    LOG.info("Alluxio file {} has been successfully written into tmp file {}", fileID, tmpFile);
    InputStream tmpIn = null;
    try {
      tmpIn = new FileInputStream(tmpFile);
    } finally {
      if (tmpIn == null) {
        LOG.error("Failed to open tmp file {}", tmpFile);
        tmpFile.delete();
      }
    }
    return new TmpAlluxioInputStream(fileID, tmpFile, tmpIn);
  }

  @Override
  public int read() throws IOException {
    return mTmpIn.read();
  }

  @Override
  public int read(byte[] b) throws IOException {
    return mTmpIn.read(b);
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    return mTmpIn.read(b, off, len);
  }

  @Override
  public void close() throws IOException {
    try {
      mTmpIn.close();
    } finally {
      if (!mTmpFile.delete()) {
        LOG.error("Failed to delete tmp download file {} when closing", mTmpFile);
      }
    }
  }

  @Override
  public long skip(long n) throws IOException {
    return mTmpIn.skip(n);
  }

  @Override
  public int available() throws IOException {
    return mTmpIn.available();
  }

  @Override
  public void mark(int readLimit) {
    mTmpIn.mark(readLimit);
  }

  @Override
  public void reset() throws IOException {
    mTmpIn.reset();
  }

  @Override
  public boolean markSupported() {
    return mTmpIn.markSupported();
  }

  private static String getTmpPath(String fileID) {
    String filename = PathUtils.getFileName(fileID);
    String randomSuffix = String.valueOf(Math.abs(ThreadLocalRandom.current().nextLong()));
    return PathUtils.concatPath(TMP_DIR, String.format("%s-%s", filename, randomSuffix));
  }

  private static void writeTmpFileFromAlluxio(String fileID, File tmpFile)
      throws IOException, AlluxioException {
    LOG.info("Tries to read {} from Alluxio and write it into tmp file {}", fileID, tmpFile);
    PathUtils.mkdirs(tmpFile.getParentFile());
    AlluxioURI uri = new AlluxioURI(fileID);
    boolean success = false;
    try (FileInStream in = FS.openFile(uri, OpenFileOptions.defaults().setReadType(ReadType.CACHE));
         OutputStream out = new BufferedOutputStream(new FileOutputStream(tmpFile))) {
      byte[] buf = new byte[BUF_SIZE];
      int read;
      while ((read = in.read(buf)) != -1) {
        out.write(buf, 0, read);
      }
      success = true;
    } finally {
      if (!success) {
        LOG.warn("Failed to read {} from Alluxio and write it to {}", fileID, tmpFile);
        if (tmpFile.exists() && !tmpFile.delete()) {
          LOG.warn("Failed to delete download tmp file {}", tmpFile);
        }
      }
    }
  }

  private TmpAlluxioInputStream(String fileID, File tmpFile, InputStream tmpIn) {
    mFileID = fileID;
    mTmpFile = tmpFile;
    mTmpIn = tmpIn;
  }
}
