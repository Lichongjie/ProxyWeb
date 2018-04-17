package com.htsc.alluxioproxy;

import com.htsc.alluxioproxy.sql.util.Configuration;
import com.htsc.alluxioproxy.sql.util.PathUtils;
import com.htsc.alluxioproxy.tmp.MetricInfoManager;
import com.htsc.alluxioproxy.util.Constants;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.net.URI;

/**
 * The Alluxio proxy server.
 */
public final class ProxyServer2 {
  private static final Logger LOG = LoggerFactory.getLogger(ProxyServer2.class);

  private static final Configuration CONF = ServerContext.getConf();
  private static final URI BASE_URI = getBaseURI();

  private final HttpServer mWebServer;

  private ProxyServer2(HttpServer webServer) {
    mWebServer = webServer;
  }

  private static URI getBaseURI() {
    return UriBuilder.fromUri("http://" + CONF.getString(Constants.HOSTNAME))
        .port(CONF.getInt(Constants.PORT)).build();
  }

  private void start() throws Exception {
    mWebServer.start();
    String ufsPath = CONF.getString(Constants.ALLUXIO_UFS_DIR);
    String dataDir = CONF.getString(Constants.DATA_DIR);
    File dir = new File(PathUtils.concatPath(ufsPath, dataDir));
    if (!dir.isDirectory()) {
      PathUtils.mkdirs(dir);
      LOG.info("Storage data directory {} has been created successfully", dir);
    }
  }

  /**
   * Main entry point.
   *
   * @param args the main arguments
   * @throws Exception if any error occurs
   */
  public static void main(String[] args) throws Exception {
    ResourceConfig rc;
    HttpServer grizzlyServer;
    try {
      rc = new ResourceConfig()
          .registerClasses(StorageResource.class)
          .registerClasses(ProxyResource.class)
          .register(MultiPartFeature.class);
      grizzlyServer = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    } catch (Exception e) {
      LOG.error("can't add proxy web rest com.htsc.alluxioproxy.sql.service class");
      rc = new ResourceConfig()
          .registerClasses(StorageResource.class)
          .register(MultiPartFeature.class);
      grizzlyServer = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    ProxyServer2 server = new ProxyServer2(grizzlyServer);
    try {
      server.start();
     // SqlManager.INSTANCE.createTable();
      MetricInfoManager.startMetric();
      //SqlManager.INSTANCE.start();
      LOG.info("Storage server started on {}", BASE_URI);
    } catch (Throwable t) {
      LOG.error("Failed to start storage server", t);
      System.exit(-1);
    }
  }
}
