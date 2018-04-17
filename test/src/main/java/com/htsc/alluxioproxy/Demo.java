package com.htsc.alluxioproxy;

import javax.ws.rs.client.Client;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Demo application.
 */
public final class Demo {
  // private static final String BASE_URI = "http://122.96.150.161:443/";
  private static final String BASE_URI = "http://168.168.207.1:2666/";

  /**
   * Hello world.
   *
   * @param args the main arguments
   * @throws Exception if any error occurs
   */
  public static void main(String[] args) throws Exception {
    Client client = HttpsUtils.getHttpClient();

    byte [] b = "Hello world".getBytes();
    ByteArrayInputStream in = new ByteArrayInputStream(b);

    Uploader uploader = new Uploader(client, BASE_URI);
    String path = uploader.upload(in);
    System.out.println("Access path: " + path);

    Downloader downloader = new Downloader(client, BASE_URI);
    InputStream in2 = downloader.download(path);

    int read;
    while ((read = in2.read()) != -1) {
      System.out.print((char) read);
    }
    System.out.println();
    in2.close();

    System.exit(0);
  }

  private Demo() {}
}
