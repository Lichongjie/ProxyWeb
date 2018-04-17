package com.htsc.alluxioproxy.webserver.web.request;

import com.htsc.alluxioproxy.webserver.utils.Configuration;
import com.htsc.alluxioproxy.webserver.utils.Constants;
import com.htsc.alluxioproxy.webserver.utils.PathUtils;
import com.htsc.alluxioproxy.webserver.web.rest.WebResource;

import javax.ws.rs.core.Response;
import java.io.*;

/**
 * Send delete file request to storage server
 */
public class DeleteRequest extends AsyncStorageRequest {
  private final String TMP_PATH = Configuration.INSTANCE.getString(Constants.TMP_PATH);
  private File mTmpFile;


  public DeleteRequest(String fileId) {
    super(fileId, "delete");

  }

  @Override
  public void queryParam() {
    mForm.param("fileId", mFileId);
  }

  private File CreateTmpFile(String id) throws IOException {
    File tmp = new File(PathUtils.concatPath(TMP_PATH, id));
    File t = tmp;
    while (t.getParentFile() == null) {
      PathUtils.mkdirs(t.getParentFile());
      t = t.getParentFile();
    }
    return tmp;
  }

  private boolean downloadTest() {
    try {
      File f = WebResource.downloadOneFile(mFileId);
      InputStream inputStream = new FileInputStream(f);
      BufferedInputStream in = new BufferedInputStream(inputStream);
      mTmpFile = CreateTmpFile(mFileId);

      OutputStream out = new BufferedOutputStream(new FileOutputStream(mTmpFile));
      byte[] buf = new byte[1024];
      int read;
      while ((read = in.read(buf)) != -1) {
        out.write(buf, 0, read);
      }
      return true;
    } catch (Exception e) {
      //LOG.error
      return false;
    } finally {
      if (mTmpFile.exists() && !mTmpFile.delete()) {
        //LOG.warn("Failed to delete uncompleted tmp file {}", tmpFile);
      }
    }
  }

}

