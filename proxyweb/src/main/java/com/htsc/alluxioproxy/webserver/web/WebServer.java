package com.htsc.alluxioproxy.webserver.web;

import com.htsc.alluxioproxy.webserver.sql.sqlService.UserSqlService;
import com.htsc.alluxioproxy.webserver.utils.Configuration;
import com.htsc.alluxioproxy.webserver.utils.Constants;
import com.htsc.alluxioproxy.webserver.web.rest.WebResource;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.SimpleInstanceManager;
import org.apache.tomcat.util.scan.StandardJarScanner;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.jsp.JettyJspServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Start a web server
 */
public final class WebServer {
  private static final Logger LOG = LoggerFactory.getLogger(WebServer.class);
  private Server mServer;
  private final String mHost;
  private final int mPort;
  private ServletContextHandler mServletContextHandler;
  private static final String WEBROOT_INDEX = "/webapp/";
  private static final String HOST_NAME = Configuration.INSTANCE.getString(Constants.HOST_NAME);
  private static final int PORT = Configuration.INSTANCE.getInt(Constants.PORT);

  public WebServer(String host, int port) {
    mHost = host;
    mPort = port;
  }

  public static void main(String[] args) throws Exception {
    WebServer m = new WebServer(HOST_NAME, PORT);
    UserSqlService.init();
    m.start();
  }

  /**
   * Start web server
   *
   * @throws Exception
   */
  public void start() throws Exception {
    mServer = new Server();

    mServer.addConnector(connector());

    URI baseUri = getWebRootResourceUri();
    mServletContextHandler  = getServletContextHandler(baseUri, getScratchDir());
    //mServletContextHandler.addFilter(IpFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
    mServer.setHandler(mServletContextHandler);
    addRestHandlerServlet();

    mServer.start();
    mServer.join();
  }

  /**
   * add rest com.htsc.alluxioproxy.sql.service.
   */
  public void addRestHandlerServlet() {
    ServletContainer servlet = new ServletContainer(new ProxyResourceConfig());

    ServletHolder sh = new ServletHolder(servlet);
    mServletContextHandler.addServlet(sh, "/rest/*");
  }

  private ServletContextHandler getServletContextHandler(URI baseUri, File scratchDir) {
    ServletContextHandler sch = new ServletContextHandler(ServletContextHandler.SESSIONS);
    sch.setContextPath("/");
    sch.setAttribute("javax.servlet.context.tempdir", scratchDir);
    sch.setResourceBase(baseUri.toASCIIString());
    sch.setAttribute(InstanceManager.class.getName(),  new SimpleInstanceManager());
    sch.addBean(new JspStarter(sch));
    sch.setClassLoader(getUrlClassLoader());
    sch.addServlet(jspServletHolder(), "*.jsp");
    sch.addServlet(defaultServletHolder(baseUri), "/*");
    return sch;
  }

  /**
   * @param baseUri the base uri
   * @return the serverHolder
   */
  private ServletHolder defaultServletHolder(URI baseUri) {
    ServletHolder holderDefault = new ServletHolder("default", DefaultServlet.class);
    holderDefault.setInitParameter("resourceBase", baseUri.toASCIIString());
    holderDefault.setInitParameter("dirAllowed", "true");
    return holderDefault;
  }

  /**
   * @return the ServletHolder
   */
  private ServletHolder jspServletHolder() {
    ServletHolder holderJsp = new ServletHolder("jsp", JettyJspServlet.class);
    holderJsp.setInitOrder(0);
    holderJsp.setInitParameter("logVerbosityLevel", "WARN");
    holderJsp.setInitParameter("fork", "false");
    holderJsp.setInitParameter("xpoweredBy", "false");
    holderJsp.setInitParameter("compilerTargetVM", "1.8");
    holderJsp.setInitParameter("compilerSourceVM", "1.8");
    holderJsp.setInitParameter("keepgenerated", "true");
    return holderJsp;
  }

  /**
   * @return the url class loader
   */
  private ClassLoader getUrlClassLoader() {
    return new URLClassLoader(new URL[0], this.getClass().getClassLoader());
  }

  /**
   * @return Scratch Dir
   * @throws IOException if IO error happened
   */
  private File getScratchDir() throws IOException {
    File tempDir = new File(System.getProperty("java.io.tmpdir"));
    File scratchDir = new File(tempDir.toString(), "embedded-jetty-jsp");

    if (!scratchDir.exists()) {
      if (!scratchDir.mkdirs()) {
        throw new IOException("Unable to get scratch directory: " + scratchDir);
      }
    }
    //LOG.info("The Scratch Directory is {}" , scratchDir);
    return scratchDir;
  }

  /**
   * @return Web Root Resource Uri
   * @throws FileNotFoundException if file not found
   * @throws URISyntaxException if URI Syntax error happens
   */
  private URI getWebRootResourceUri() throws FileNotFoundException, URISyntaxException {
    URL indexUri = this.getClass().getResource(WEBROOT_INDEX);
    if (indexUri == null) {
      throw new FileNotFoundException("Unable to find resource " + WEBROOT_INDEX);
    }
    // Points to wherever /webroot/ (the resource) is
    return indexUri.toURI();
  }

  /**
   * @return ServerConnector
   */
  private ServerConnector connector() {
    ServerConnector connector = new ServerConnector(mServer);
    connector.setPort(mPort);
    connector.setHost(mHost);
    return connector;
  }

  /**
   * JspStarter.
   *
   * This is added as a com.htsc.alluxioproxy.sql.bean that is a jetty LifeCycle on the ServletContextHandler.
   * This com.htsc.alluxioproxy.sql.bean's doStart method will be called as the ServletContextHandler starts,
   * and will call the ServletContainerInitializer for the jsp engine.
   *
   */
  private static class JspStarter extends AbstractLifeCycle implements ServletContextHandler
          .ServletContainerInitializerCaller {
    JettyJasperInitializer mSci;
    ServletContextHandler mContext;

    /**
     * Constructor.
     *
     * @param context ServletContextHandler
     */
    JspStarter(ServletContextHandler context) {
      mSci = new JettyJasperInitializer();
      mContext = context;
      mContext.setAttribute("org.apache.tomcat.JarScanner", new StandardJarScanner());
    }

    @Override
    protected void doStart() throws Exception {
      ClassLoader old = Thread.currentThread().getContextClassLoader();
      Thread.currentThread().setContextClassLoader(mContext.getClassLoader());
      try {
        mSci.onStartup(null, mContext.getServletContext());
        super.doStart();
      } finally {
        Thread.currentThread().setContextClassLoader(old);
      }
    }
  }
}
