package web;

public class RestServer extends Server {
  @Override
  public String getPackage() {
    return "web.rest";
  }

  @Override
  public Class getResourceClass() {
    return WebResource.class;
  }

  @Override
  public String getHostName() {
    return "localhost";
  }

  @Override
  public int getPort() {
    return 8080;
  }

  public static void main(String []args) throws Exception {
    WebServer m = new WebServer("localhost", 8080);
    m.start();
    //new RestServer().start();
  }
}
