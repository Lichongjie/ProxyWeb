package com.htsc.alluxioproxy.webserver.web.request;

/**
 * Send archive request to stroage server
 */
public class ArchiveRequest extends AsyncStorageRequest {

  public ArchiveRequest(String fileId) {
    super(fileId, "archive");
  }

  @Override
  public void queryParam() {
    mForm.param("fileId", mFileId);
  }
}
