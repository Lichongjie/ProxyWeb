package web;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import java.util.concurrent.Callable;

/**
 * Created by lenovo on 2017/12/7.
 */
public abstract class AsyncStorageRequest implements Callable {
  static String URL;
  String mFileId;
  Form mForm = new Form();

  static {
    String baseUrl = Configuration.INSTANCE.getString(Constants.STORAGE_SERVICE_URL);
    if (!baseUrl.endsWith("/")) {
      baseUrl += "/";
    }
    URL = baseUrl;
  }

  public AsyncStorageRequest(String fileId, String requestName) {
    mFileId = fileId;
    URL = URL + requestName;
  }

  public abstract void createForm();

  @Override
  public String call() throws Exception {

    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(URL);
    Response response = target.request().post(Entity.form(mForm));
    if (response.getStatus() == 200) {
      return mFileId;
    }
    return "";
  }

}
