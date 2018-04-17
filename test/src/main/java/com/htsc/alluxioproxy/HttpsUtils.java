package com.htsc.alluxioproxy;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.security.cert.X509Certificate;

/**
 * Util class for HTTPS.
 */
public final class HttpsUtils {
  private static final SSLContext SSL_CONTEXT;

  static {
    try {
      SSL_CONTEXT = trustAll();
    } catch (Exception e) {
      throw new Error("Failed to init HTTPS settings", e);
    }
  }

  /**
   * Gets an instance of HTTPS client.
   *
   * @param username the username
   * @param password the password
   * @return an instance of https client
   */
  public static Client getHttpsClient(String username, String password) {
    ClientConfig clientConfig = new ClientConfig().register(MultiPartFeature.class);
    Client client = ClientBuilder.newBuilder()
        .withConfig(clientConfig)
        .sslContext(SSL_CONTEXT)
        .build();
    client.register(HttpAuthenticationFeature.basic(username, password));
    return client;
  }

  /**
   * Gets an instance of HTTP client.
   *
   * @return an instance of https client
   */
  public static Client getHttpClient() {
    ClientConfig clientConfig = new ClientConfig().register(MultiPartFeature.class);
    Client client = ClientBuilder.newBuilder()
        .withConfig(clientConfig)
        .build();
    return client;
  }

  /**
   * Trusts all hosts and certificates in HTTPS connections.
   *
   * @return SSL context
   * @throws Exception if any error occurs
   */
  private static SSLContext trustAll() throws Exception {
    TrustManager trm = new X509TrustManager() {
      @Override
      public X509Certificate[] getAcceptedIssuers() {
        return null;
      }

      @Override
      public void checkClientTrusted(X509Certificate[] certs, String authType) {}

      @Override
      public void checkServerTrusted(X509Certificate[] certs, String authType) {}
    };

    SSLContext sc = SSLContext.getInstance("SSL");
    sc.init(null, new TrustManager[] { trm }, null);
    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

    HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
      @Override
      public boolean verify(String s, SSLSession sslSession) {
        return true;
      }
    });
    return sc;
  }

  private HttpsUtils() {}
}
