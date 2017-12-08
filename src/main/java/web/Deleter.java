package web;

import javax.ws.rs.core.Response;
import java.io.*;

public class Deleter extends AsyncStorageRequest {
  private final String TMP_PATH = Configuration.INSTANCE.getString(Constants.TMP_PATH);
  private File mTmpFile;


  public Deleter(String fileId) {
    super(fileId, "delete");

  }

  @Override
  public void createForm() {
    mForm.param("fileId", mFileId);
  }

  @Override
  public String call() throws Exception {
    if (!downloadTest()) {
      //LOG.ERROR
      return "";
    }
    return super.call();
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
    Response response = WebResource.download(mFileId);
    int status = response.getStatus();
    if (status == 200) {
      try {
        InputStream inputStream = response.readEntity(InputStream.class);

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
    else {
      //LOG
      return false;
    }
  }

}

