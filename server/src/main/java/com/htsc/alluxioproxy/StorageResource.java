package com.htsc.alluxioproxy;

import com.google.common.base.Preconditions;
import com.htsc.alluxioproxy.exceptions.AuthenticationException;
import com.htsc.alluxioproxy.exceptions.AuthorizationException;
import com.htsc.alluxioproxy.exceptions.ProxyBusyException;
import com.htsc.alluxioproxy.security.UserAuthentication;
import com.htsc.alluxioproxy.sql.service.FileInfoSqlDatabaseService;
import com.htsc.alluxioproxy.sql.util.Configuration;
import com.htsc.alluxioproxy.sql.util.PathUtils;
import com.htsc.alluxioproxy.tmp.FileInfoSqlService;
import com.htsc.alluxioproxy.tmp.MetricInfoManager;
import com.htsc.alluxioproxy.sql.bean.FileInfoBean;
import com.htsc.alluxioproxy.util.Constants;
import com.htsc.alluxioproxy.util.ExceptionLoggedThreadPool;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.sql.Timestamp;
import java.util.Set;
import java.util.concurrent.*;

import static com.htsc.alluxioproxy.util.Constants.DATABASE_THREADS_NUM;

/**
 * The root resource of the storage com.htsc.alluxioproxy.sql.service.
 */
@Path("/")
public class StorageResource extends BaseResource {
  private static final Logger LOG = LoggerFactory.getLogger(StorageResource.class);

  private static final UserAuthentication USER_AUTHENTICATION = UserAuthentication.Factory.get();

  private static final Semaphore DOWNLOAD_SEMAPHORE =
      new Semaphore(Configuration.INSTANCE.getInt(Constants.MAX_DOWNLOAD_TASKS_NUM));
  private static final Semaphore UPLOAD_SEMAPHORE =
      new Semaphore(Configuration.INSTANCE.getInt(Constants.MAX_UPLOAD_TASKS_NUM));

  private static final ExecutorService TRANSCODE_SERVICE =
      ExceptionLoggedThreadPool.newFixedThreadPool(Configuration.INSTANCE.getInt(
          Constants.TRANSCODE_REQUEST_THREADS_NUM));

  private static final ExecutorService DATABASE_SERVICE =
          ExceptionLoggedThreadPool.newFixedThreadPool(Configuration.INSTANCE.getInt(
                  DATABASE_THREADS_NUM));

  private static final Configuration mConf = Configuration.INSTANCE;
  private static final boolean mDownloadEnabled = mConf.getBoolean(Constants.DOWNLOAD_ENABLED);

  /**
   * Constructor for {@link StorageResource}.
   */
  public StorageResource() {}

  /**
   * Index page.
   *
   * @return an index page
   */
  @GET
  public String index() {
    return "<html>\n<head>\n<h1>Huatai Alluxio Proxy Service</h1>\n</head>\n</html>\n";
  }

  /**
   * Download com.htsc.alluxioproxy.sql.service.
   *
   * @param username the username
   * @param password the password
   * @param path the resource path
   * @return an http response containing the resource data
   */
  @POST
  @Path("download")
  public Response downloadFile(
      @FormParam("username") String username,
      @FormParam("password") String password,
      @FormParam("path") String path) {
    LOG.info("Receive a download request with path {}, user {}", path, username);
    if (!mDownloadEnabled) {
      return Response.status(Response.Status.FORBIDDEN).type(MediaType.TEXT_PLAIN)
          .entity("Download com.htsc.alluxioproxy.sql.service disabled").build();
    }
    if (path == null) {
      return Response.status(Response.Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN)
          .entity("No specified download path").build();
    }
    Response response = null;
    boolean permitted = false;
    try {
      authenticateUser(username, password);
      if (!DOWNLOAD_SEMAPHORE.tryAcquire()) {
        throw new ProxyBusyException("Storage system is too busy for downloading com.htsc.alluxioproxy.sql.service");
      }
      permitted = true;
      authorize(username, path);
      InputStream in = new BufferedInputStream(getDownloadStream(path));
      String filename = PathUtils.getFileName(path);
      response = Response
          .ok(in)
          .type(MediaType.APPLICATION_OCTET_STREAM)
          .header("content-disposition", "attachment; filename = " + filename)
          .build();
      LOG.info("Succeed to download {} for user {}", path, username);
    } catch (Throwable t) {
      LOG.error("failed to download file {}", path, t);
      response = generateErrorResponse(t);
    } finally {
      if (permitted) {
        DOWNLOAD_SEMAPHORE.release();
      }
    }
    // update metric info
    try {
      if (!username.equals("webUser")) {
        MetricInfoManager.updateDownloadData(response.getStatus() == 200);
      }
    } catch (Exception e) {
      LOG.error("error happened when update metric info");
    }
    return Preconditions.checkNotNull(response);
  }


  /**
   * Upload com.htsc.alluxioproxy.sql.service.
   *
   * @param username the username
   * @param password the password
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
      @FormDataParam("username") String username,
      @FormDataParam("password") String password,
      @FormDataParam("in") InputStream in,
      @FormDataParam("in") FormDataContentDisposition disposition,
      @FormDataParam("isTranscode") int isTranscode,
      @FormDataParam("transcodeFormat") String transcodeFormat) {
    LOG.info("Receives upload request from {}, isTranscode {}, transcodeFormat {}",
        username, isTranscode, transcodeFormat);
    if (in == null) {
      return Response.status(Response.Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN)
          .entity("No specified input stream").build();
    }
    if (isTranscode == 1 && transcodeFormat == null) {
      try {
        in.close();
      } catch (Exception e) {
        LOG.error("Failed to close uploaded input stream");
      }
      return Response.status(Response.Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN)
          .entity("No specified transcode format").build();
    }
    boolean permitted = false;
    String fileID = null;
    Response response = null;
    File ufsFile = null;
    try (BufferedInputStream in0 = new BufferedInputStream(in)) {
      authenticateUser(username, password);
      if (!UPLOAD_SEMAPHORE.tryAcquire()) {
        throw new ProxyBusyException("Storage system is too busy for uploading com.htsc.alluxioproxy.sql.service");
      }
      permitted = true;
      fileID = FileURIGenerator.generate(username);
      File tmpFile = new File(FileURIGenerator.getTmpPath(fileID));
      ufsFile = new File(FileURIGenerator.getUfsPath(fileID));
      writeTmpFile(in0, tmpFile);
      renameOrDelete(tmpFile, ufsFile);
      if (isTranscode == 1) {
        // get database lock first
        FileInfoSqlService.checkInsert(fileID);
        requestTranscodeAsync(fileID, transcodeFormat);
      }
      response = Response.ok(fileID).type(MediaType.TEXT_PLAIN).build();
      LOG.info("Succeed to upload {} for user {}", fileID, username);
    } catch (Throwable t) {
      LOG.error("Failed to upload {}", fileID, t);
      response = generateErrorResponse(t);
    } finally {
      if (permitted) {
        UPLOAD_SEMAPHORE.release();
      }

      try {
        UploadDatabaseOperation(response.getStatus() == 200, fileID, isTranscode, transcodeFormat);
      } catch (Exception e) {
        LOG.error("error happened when update upload info");
      }
    }
    return Preconditions.checkNotNull(response);
  }

  /**
   * Writing insert file info operation to tmp file, updating metric info
   *
   * @param isSucceed if upload operation succeed
   * @param fileID the file id
   * @param isTranscode if need transcode
   * @param transcodeFormat the transcode format
   */
  private void UploadDatabaseOperation(boolean isSucceed, String fileID, int isTranscode, String transcodeFormat) {
    if(isSucceed) {
      Timestamp nowTime = FileInfoSqlDatabaseService.getTime();

      final FileInfoBean bean;
      if(isTranscode != 1) {
        bean = new FileInfoBean(fileID,  nowTime, false,false,"",false,false);
      } else {
        bean = new FileInfoBean(fileID, nowTime, true, false, transcodeFormat, false, false);
      }
      final String fileid = fileID;
      DATABASE_SERVICE.submit(new Runnable() {
        @Override
        public void run() {
          try {
            FileInfoSqlService.insertFileInfo(bean);
          } catch (Exception e) {
            LOG.error("failed to write tmp file when insert fileinfo {}", fileid);
          }
        }
      });
    }
    MetricInfoManager.updateUploadData(isSucceed);
  }



  private void renameOrDelete(File tmpFile, File ufsFile) throws IOException {
    LOG.info("Starts to rename tmp file {} to ufs file {}", tmpFile, ufsFile);
    PathUtils.mkdirs(ufsFile.getParentFile());
    if (!tmpFile.renameTo(ufsFile)) {
      LOG.error("Failed to rename {} to {}, starts to delete the tmp file", tmpFile, ufsFile);
      if (!tmpFile.delete()) {
        LOG.warn("Failed to delete completed tmp file {}", tmpFile);
      }
      throw new IOException(String.format("Failed to rename %s to %s", tmpFile, ufsFile));
    }
  }

  private void authenticateUser(String username, String password) throws AuthenticationException {
    if (username == null || password == null) {
      throw new AuthenticationException("Username and password required");
    }
    if (!USER_AUTHENTICATION.verify(username, password)) {
      throw new AuthenticationException("Invalid username and password ");
    }
  }

  private void authorize(String username, String fileID) throws AuthorizationException {
    if (username.equals("webUser")) {
      return;
    }
    String name = PathUtils.getFileName(fileID);
    String userID = name.split("-")[2];
    String username0 = ServerContext.getUsername(userID);
    if (username.equals(username0)) {
      return;
    }
    String group0 = ServerContext.getPrimaryGroup(username0);
    Set<String> groups = ServerContext.getGroups(username);
    if (groups.contains(group0)) {
      return;
    }
    throw new AuthorizationException("Authorization failed");
  }

  private void requestTranscodeAsync(String path, String format) {
    TRANSCODE_SERVICE.submit(new TranscodeRequester(path, format));
  }


}
