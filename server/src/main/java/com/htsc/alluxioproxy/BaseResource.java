package com.htsc.alluxioproxy;

import com.htsc.alluxioproxy.exceptions.AuthenticationException;
import com.htsc.alluxioproxy.exceptions.AuthorizationException;
import com.htsc.alluxioproxy.exceptions.ProxyBusyException;
import com.htsc.alluxioproxy.sql.util.Configuration;
import com.htsc.alluxioproxy.sql.util.PathUtils;
import com.htsc.alluxioproxy.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;

public class BaseResource {
  private static final Logger LOG = LoggerFactory.getLogger(BaseResource.class);
  static final String REALM = "Storage system authentication";
  static final Configuration mConf = Configuration.INSTANCE;
  private static final int BUF_SIZE =
      (int) Configuration.INSTANCE.getBytes(Constants.UPLOAD_WRITE_BUF_SIZE);

  /**
   * Get download stream
   *
   * @param fileID the file id
   * @return download stream
   * @throws IOException if error happened
   */
  InputStream getDownloadStream(String fileID) throws IOException {
    boolean allowArchived = mConf.getBoolean(Constants.ALLOW_DOWNLOAD_ARCHIVED_FILE);
    if (allowArchived) {
      return getDownloadStreamAllowArchived(fileID);
    } else {
      return getDownloadStreamPreventArchived(fileID);
    }
  }


  /**
   * Get download stream, preventing download from archived directory
   *
   * @param fileID the file id
   * @return download stream
   * @throws IOException if error happened
   */
  private InputStream getDownloadStreamPreventArchived(String fileID) throws IOException {
    InputStream in = null;
    try {
      in = TmpAlluxioInputStream.open(fileID);
    } catch (Exception e) {
      LOG.warn("Failed to read {} from Alluxio. Caused by {}", fileID, e.getMessage());
    }
    if (in == null) {
      // Read the file directly from Alluxio UFS, which is the NFS.
      String ufsPath = FileURIGenerator.getUfsPath(fileID);
      LOG.warn("Starts to read {} directly from UFS file {}", fileID, ufsPath);
      in = new FileInputStream(new File(ufsPath));
    }
    return in;
  }

  /**
   * Get download stream, allowing download from archived directory
   *
   * @param fileID the file id
   * @return download stream
   * @throws IOException if error happened
   */
  private InputStream getDownloadStreamAllowArchived(String fileID) throws IOException {
    InputStream in = null;

    try {
      String archivedFileId = PathUtils.concatPath("/archive", fileID);
      in = TmpAlluxioInputStream.open(archivedFileId);
    } catch (Exception e) {
      LOG.warn("Failed to read archived {} from Alluxio. Caused by {}", fileID, e.getMessage());
      try {
        in = TmpAlluxioInputStream.open(fileID);
      } catch (Exception e2) {
        LOG.warn("Failed to read {} from Alluxio. Caused by {}", fileID, e2.getMessage());
      }
    }
    if (in == null) {
      // Read the file directly from Alluxio UFS, which is the NFS.
      String archivePath = FileURIGenerator.getArchivePath(fileID);
      File archivedFile = new File(archivePath);
      if(archivedFile.exists()) {
        LOG.warn("Starts to read {} directly from archived file {}", fileID, archivePath);
        in = new FileInputStream(new File(archivePath));
      }
      else {
        String ufsPath = FileURIGenerator.getUfsPath(fileID);
        LOG.warn("Starts to read {} directly from archived file {}", fileID, ufsPath);
        in = new FileInputStream(new File(ufsPath));
      }
    }
    return in;
  }

  void writeTmpFile(InputStream in, File tmpFile) throws IOException {
    LOG.info("Starts to write the uploading stream into tmp file {}", tmpFile);
    boolean success = false;
    PathUtils.mkdirs(tmpFile.getParentFile());
    try (OutputStream out = new BufferedOutputStream(new FileOutputStream(tmpFile))) {
      byte[] buf = new byte[BUF_SIZE];
      int read;
      while ((read = in.read(buf)) != -1) {
        out.write(buf, 0, read);
      }
      success = true;
    } finally {
      if (!success) {
        LOG.error("Failed to write the tmp file {}, starts to delete it", tmpFile);
        if (tmpFile.exists() && !tmpFile.delete()) {
          LOG.warn("Failed to delete uncompleted tmp file {}", tmpFile);
        }
      }
    }
  }

  Response generateErrorResponse(Throwable t) {
    if (t instanceof AuthenticationException) {
      return Response
          .status(Response.Status.PROXY_AUTHENTICATION_REQUIRED)
          .header("WWW-Authenticate", "Basic realm=\"" + REALM + "\"")
          .type(MediaType.TEXT_PLAIN)
          .entity(t.getMessage())
          .build();
    } else if (t instanceof AuthorizationException) {
      return Response
          .status(Response.Status.UNAUTHORIZED)
          .type(MediaType.TEXT_PLAIN)
          .entity(t.getMessage())
          .build();
    } else if (t instanceof ProxyBusyException) {
      return Response
          .status(Response.Status.SERVICE_UNAVAILABLE)
          .type(MediaType.TEXT_PLAIN)
          .entity(t.getMessage())
          .build();
    } else if (t instanceof FileNotFoundException) {
      return Response
          .status(Response.Status.NOT_FOUND)
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
