package com.htsc.alluxioproxy;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.InputStream;

/**
 * Helper class to upload an input stream.
 */
public class Uploader {
  private final Client mClient;
  private final String mTgtUri;

  /**
   * Constructor for {@link Uploader}.
   *
   * @param client the https client
   * @param baseUri the base uri
   */
  public Uploader(Client client, String baseUri) {
    mClient = client;
    if (baseUri.endsWith("/")) {
      mTgtUri = baseUri + "upload";
    } else {
      mTgtUri = baseUri + "/upload";
    }
  }

  /**
   * Uploads the input stream.
   *
   * @param in the stream to upload
   * @return the unique path representing the uploaded stream
   * @throws Exception if any error occurs
   */
  public String upload(InputStream in) throws Exception {
    WebTarget target = mClient.target(mTgtUri);
    FormDataMultiPart multiPart =  new FormDataMultiPart();
    multiPart.field("username", "alluxio");
    multiPart.field("password", "alluxio");
    multiPart.field("token", "");
    multiPart.field("isTranscode", "1");
    //multiPart.field("isTranscode", "0");

    multiPart.field("transcodeFormat", ".mp4");
    multiPart.bodyPart(new StreamDataBodyPart("in", in));
    System.out.println("send rest url: " + target.getUri());
    Response response = target.request()
        .post(Entity.entity(multiPart, multiPart.getMediaType()));

    int status = response.getStatus();
    switch (status) {
      case 200:
        return response.readEntity(String.class);
      case 400:
        throw new Exception("Bad request " + response.readEntity(String.class));
      case 401:
        throw new Exception("Authentication failed " + response.readEntity(String.class));
      case 503:
        throw new Exception("Proxy server too busy " + response.readEntity(String.class));
      case 500:
        throw new Exception("Proxy internal error " + response.readEntity(String.class));
      default:
        // Unexpected error.
        throw new Exception("Unknown error");
    }
  }
}
