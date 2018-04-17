package com.htsc.alluxioproxy.webserver.web;

import com.htsc.alluxioproxy.webserver.web.rest.WebResource;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * add web rest resource.
 */
public class ProxyResourceConfig extends ResourceConfig {
  public ProxyResourceConfig() {
    packages("com.htsc.alluxioproxy.webserver.web.rest");
    registerClasses(WebResource.class);
    register(MultiPartFeature.class);
  }
}
