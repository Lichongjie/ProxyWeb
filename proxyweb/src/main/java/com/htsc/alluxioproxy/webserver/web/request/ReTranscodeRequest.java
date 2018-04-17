package com.htsc.alluxioproxy.webserver.web.request;

import com.htsc.alluxioproxy.webserver.web.request.AsyncStorageRequest;
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

/**
 * Send retranscode file request to storage server
 */
public class ReTranscodeRequest extends AsyncStorageRequest {
  private static final Logger LOG = LoggerFactory.getLogger(ReTranscodeRequest.class);

  private String mTransformFormat;

  public ReTranscodeRequest(String fileId, String transformFormat) {
    super(fileId, "reTranscode");
    mTransformFormat = transformFormat;
  }

  @Override
  public void queryParam() {
    mForm.param("fileId", mFileId);
    mForm.param("transcodeFormat", mTransformFormat);
  }


  /**
   * @return retranscode response from storage server
   */
  private Response reTranscode() {
    LOG.info("url, {} {}", URL, mForm);
    Client client = ClientBuilder.newClient();
    return client.target(URL).request().post(Entity.form(mForm));
    //return Response.ok().build();

  }

  @Override
  public String call() {
    LOG.info("start to retranscode file, id is {}", mFileId);
    Response response;
    queryParam();
    response = reTranscode();
    if(response.getStatus() == 200) {
      return mFileId;
    }
    LOG.error("failed to get response caused by {} {}", response.getStatus(), response.getStatusInfo
        ());
    return "";
  }
}
