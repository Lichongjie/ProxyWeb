package com.htsc.alluxioproxy.gateway;

import com.google.common.base.Preconditions;
import com.htsc.alluxioproxy.gateway.exceptions.InvalidTokenException;
import com.htsc.alluxioproxy.gateway.security.TokenAuthentication;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

/**
 * The gateway resource.
 */
@Path("/")
public class GatewayResource {
  private static final Logger LOG = LoggerFactory.getLogger(GatewayResource.class);

  private static final TokenAuthentication TOKEN_AUTHENTICATION = TokenAuthentication.Factory.get();
  private static final boolean TOKEN_ENABLED;
  private static final String DOWNLOAD_URL;
  private static final String UPLOAD_URL;
  private static final String USERNAME;
  private static final String PASSWORD;

  static {
    Configuration conf = Configuration.INSTANCE;
    USERNAME = conf.getString(Constants.USERNAME);
    PASSWORD = conf.getString(Constants.PASSWORD);
    String baseUrl = conf.getString(Constants.STORAGE_SERVICE_URL);
    if (!baseUrl.endsWith("/")) {
      baseUrl += "/";
    }
    DOWNLOAD_URL = baseUrl + "download";
    UPLOAD_URL = baseUrl + "upload";
    TOKEN_ENABLED = conf.getBoolean(Constants.TOKEN_VALIDATE_ENABLED);
  }

  /**
   * Constructor for {@link GatewayResource}.
   */
  public GatewayResource() {}

  /**
   * Index page.
   *
   * @return an index page
   */
  @GET
  public String index() {
    return "<html>\n<head>\n<h1>Huatai Alluxio Proxy Gateway Service</h1>\n</head>\n</html>\n";
  }

  /**
   * Download service.
   *
   * @param token the session token
   * @param path the resource path
   * @return an http response containing the resource data
   */
  @GET
  @Path("download")
  public Response downloadFile(
      @QueryParam("token") String token,
      @QueryParam("path") String path) {
    Response response;
    try {
      validateToken(token);

      Client client = ClientBuilder.newClient();
      WebTarget target = client.target(DOWNLOAD_URL);
      Form form = new Form();
      form.param("username", USERNAME)
          .param("password", PASSWORD)
          .param("path", path);
      response = target.request().post(Entity.form(form));
    } catch (Throwable t) {
      LOG.error("Failed to download path {} with token {}", path, token, t);
      response = generateErrorResponse(t);
    }
    return Preconditions.checkNotNull(response);
  }

  /**
   * Upload service.
   *
   * @param token the session token
   * @param in the uploading input stream
   * @param disposition the uploading input stream disposition
   * @param isTranscode whether to transcode, 0 represents no and 1 represents yes
   * @param transcodeFormat the format to transcode
   * @return an http response with the result of the uploading operation
   */
  @POST
  @Path("upload")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response uploadFile(
      @FormDataParam("token") String token,
      @FormDataParam("in") InputStream in,
      @FormDataParam("in") FormDataContentDisposition disposition,
      @FormDataParam("isTranscode") int isTranscode,
      @FormDataParam("transcodeFormat") String transcodeFormat) {
    if (in == null) {
      return Response.status(Response.Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN)
          .entity("No specified input stream").build();
    }
    Response response;
    try {
      validateToken(token);

      ClientConfig clientConfig = new ClientConfig().register(MultiPartFeature.class);
      Client client = ClientBuilder.newBuilder()
          .withConfig(clientConfig)
          .build();
      WebTarget target = client.target(UPLOAD_URL);
      FormDataMultiPart multiPart =  new FormDataMultiPart();
      multiPart.field("username", USERNAME);
      multiPart.field("password", PASSWORD);
      multiPart.field("isTranscode", String.valueOf(isTranscode));
      if (isTranscode == 1) {
        multiPart.field("transcodeFormat", transcodeFormat);
      }
      multiPart.bodyPart(new StreamDataBodyPart("in", in));
      response = target.request().post(Entity.entity(multiPart, multiPart.getMediaType()));
    } catch (Throwable t) {
      LOG.error("Failed to upload file with token {}", token, t);
      response = generateErrorResponse(t);
    }
    return Preconditions.checkNotNull(response);
  }

  private void validateToken(String token) throws InvalidTokenException {
    if (TOKEN_ENABLED && (token == null || !TOKEN_AUTHENTICATION.verify(token))) {
      if (token == null) {
        throw new InvalidTokenException("Session token required");
      }
      throw new InvalidTokenException("Invalid session token " + token);
    }
  }

  private Response generateErrorResponse(Throwable t) {
    if (t instanceof InvalidTokenException) {
      return Response
          .status(Response.Status.PROXY_AUTHENTICATION_REQUIRED)
          .type(MediaType.TEXT_PLAIN)
          .entity(t.getMessage())
          .build();
    } else {
      return Response
          .status(Response.Status.INTERNAL_SERVER_ERROR)
          .type(MediaType.TEXT_PLAIN)
          .entity(t.getMessage())
          .build();
    }
  }
}
