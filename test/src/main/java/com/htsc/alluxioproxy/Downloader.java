package com.htsc.alluxioproxy;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import java.io.InputStream;

/**
 * Helper class to download a stored data stream.
 */
public class Downloader {
  private final Client mClient;
  private final String mTgtUri;

  /**
   * Constructor for {@link Downloader}.
   *
   * @param client the https client
   * @param baseUri the base uri
   */
  public Downloader(Client client, String baseUri) {
    mClient = client;
    if (baseUri.endsWith("/")) {
      mTgtUri = baseUri + "download";
    } else {
      mTgtUri = baseUri + "/download";
    }
  }

  /**
   * Downloads the stored data stream.
   *
   * @param path the path representing the stored data stream
   * @return the input stream
   * @throws Exception if any error occurs
   */
  public InputStream download(String path) throws Exception {
    WebTarget target = mClient.target(mTgtUri);
    System.out.println("download url " + target.getUri());
    Form form = new Form().param("username", "alluxio").param("password",  "alluxio").
        param("path", path);
    Response response = target.request().post(Entity.form(form));

    int status = response.getStatus();
    switch (status) {
      case 200:
        return response.readEntity(InputStream.class);
      case 400:
        throw new Exception("Bad request " + response.readEntity(String.class));
      case 401:
        throw new Exception("Authentication failed " + response.readEntity(String.class));
      case 503:
        throw new Exception("Proxy server too busy " + response.readEntity(String.class));
      case 500:
        throw new Exception("Proxy internal error " + response.readEntity(String.class));
      case 404:
        throw new Exception("Resource not found " + response.readEntity(String.class));
      default:
        // Unexpected error.
        throw new Exception("Unknown error " + response.readEntity(String.class));
    }
  }
}
