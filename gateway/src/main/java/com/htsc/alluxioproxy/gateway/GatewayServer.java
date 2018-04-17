package com.htsc.alluxioproxy.gateway;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

/**
 * The gateway server.
 */
public class GatewayServer {
  private static final Logger LOG = LoggerFactory.getLogger(GatewayServer.class);

  private static final Configuration CONF = Configuration.INSTANCE;
  private static final URI BASE_URI = getBaseURI();

  private final HttpServer mHttpServer;

  /**
   * Constructor for {@link GatewayServer}.
   *
   * @param httpServer the http server
   */
  public GatewayServer(HttpServer httpServer) {
    mHttpServer = httpServer;
  }

  private static URI getBaseURI() {
    return UriBuilder.fromUri("http://" + CONF.getString(Constants.HOSTNAME))
        .port(CONF.getInt(Constants.PORT)).build();
  }

  private void start() throws IOException {
    mHttpServer.start();
  }

  /**
   * Main entry.
   *
   * @param args the arguments
   */
  public static void main(String[] args) {
    ResourceConfig rc = new ResourceConfig()
        .packages("com.htsc.alluxioproxy.gateway")
        .registerClasses(GatewayResource.class)
        .register(MultiPartFeature.class);

    HttpServer grizzlyServer = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);

    GatewayServer server = new GatewayServer(grizzlyServer);
    try {
      server.start();
      LOG.info("Gateway server started on {}", BASE_URI);
    } catch (Throwable t) {
      LOG.error("Failed to start gateway server", t);
      System.exit(-1);
    }
  }
}
