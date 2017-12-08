package web;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import java.util.concurrent.Callable;

public class ReTranscoder extends AsyncStorageRequest {
  private String mTransformFormat;
  private int mFailedTimes = 2;



  public ReTranscoder(String fileId, String transformFormat) {
    super(fileId, "reTranscode");
    mTransformFormat = transformFormat;
  }

  @Override
  public void createForm() {
    mForm.param("fileId", mFileId);
    mForm.param("transcodeFormat", mTransformFormat);  }

  private Response reTranscode() {
    Form form = new Form();

    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(URL);

    form.param("fileId", mFileId);
    form.param("transcodeFormat", mTransformFormat);
    return target.request().post(Entity.form(form));
  }

  @Override
  public String call() {
    Response response = reTranscode();
    if(response.getStatus() == 200) {
      return mFileId;
    }
    while (response.getStatus() != 200 && mFailedTimes > 0) {
      mFailedTimes --;
      response = reTranscode();
    }
    return "";
  }
}
