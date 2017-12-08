package web;


public class Archiver extends AsyncStorageRequest {


  public Archiver(String fileId) {
    super(fileId, "archive");

  }

  @Override
  public void createForm() {
    mForm.param("fileId", mFileId);
  }
}
