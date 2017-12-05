package web;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Created by lenovo on 2017/12/4.
 */
public class ProxyResourceConfig extends ResourceConfig {
  public ProxyResourceConfig() {
    packages("web.rest");
    registerClasses(WebResource.class);
    register(MultiPartFeature.class);
  }
}
