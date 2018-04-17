package com.htsc.alluxioproxy.webserver.web.request;

import com.htsc.alluxioproxy.webserver.utils.Configuration;
import com.htsc.alluxioproxy.webserver.utils.Constants;
import com.htsc.alluxioproxy.webserver.web.rest.WebResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.Callable;

/**
 * base class handling send rest request to storage server.
 */
public abstract class AsyncStorageRequest implements Callable {
  private static final Logger LOG = LoggerFactory.getLogger(AsyncStorageRequest.class);
  String URL;
  String mFileId;
  Form mForm;
  static String baseUrl;

  static {
    baseUrl = Configuration.INSTANCE.getString(Constants.STORAGE_SERVICE_URL);
    if (!baseUrl.endsWith("/")) {
      baseUrl += "/";
    }
  }

  public AsyncStorageRequest(String fileId, String requestName) {
    mFileId = fileId;
    URL = baseUrl + requestName;
    mForm = new Form();
  }

  /**
   * Add form parms.
   */
  public abstract void queryParam();

  @Override
  public String call() {
    queryParam();
    LOG.info("start to send async request {} to storage server, form info{}",URL, mForm.asMap().toString());
    Client client = ClientBuilder.newClient();
    Response response = client.target(URL).request().post(Entity.form
        (mForm));
    if (response.getStatus() == 200) {
      return mFileId;
    }
    LOG.error("failed to get response caused by {} {}", response.getStatus(), response.getStatusInfo
        ());
    return "";
  }
}
