package web;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * Created by lenovo on 2017/12/3.
 */
public abstract class Server {

  private final URI BASE_URI = getBaseURI();

  //private HttpServer mHttpServer;

  private URI getBaseURI() {
    return UriBuilder.fromUri("http://" + getHostName()).port(getPort()).build();
  }

   void start() {
    ResourceConfig rc = new ResourceConfig()
            .packages("web")
            .registerClasses(WebResource.class)
            .register(MultiPartFeature.class);
    //mHttpServer = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);

    try {
    //  mHttpServer.start();
     // LOG.info("Gateway server started on {}", BASE_URI);
    } catch (Throwable t) {
      t.printStackTrace();
    //  LOG.error("Failed to start gateway server", t);
     // System.exit(-1);
    }
  }

  public abstract String getPackage();

  public abstract Class getResourceClass();

  public abstract String getHostName();

  public abstract int getPort();

}
