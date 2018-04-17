package com.htsc.alluxioproxy;

import com.htsc.alluxioproxy.tmp.FileInfoSqlService;
import com.htsc.alluxioproxy.tmp.MetricInfoManager;
import com.htsc.alluxioproxy.util.Constants;
import org.glassfish.jersey.client.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Requests to transcode asynchronously.
 */
public class TranscodeRequester implements Runnable {
  private static final Logger LOG = LoggerFactory.getLogger(TranscodeRequester.class);
  private static final String TRANSCODE_URL;

  private final String mPath;
  private final String mFormat;

  private boolean mSuccess;

  private final int RETRY_TIME = ServerContext.getConf().getInt(Constants.
      RETRANSCODE_RETRY_TIME);

  static {
    TRANSCODE_URL = ServerContext.getConf().getString(Constants.TRANSCODE_URL);
  }

  /**
   * Constructor for {@link TranscodeRequester}.
   *
   * @param path the file path
   * @param format the transcode format
   */
  public TranscodeRequester(String path, String format) {
    mPath = path;
    mFormat = format;
    mSuccess = false;
  }

  @Override
  public void run() {
    try {
      transcode();
    } finally {
      MetricInfoManager.updateTranscodeDate(mSuccess);
    }
  }

  public void transcode() {
    try {
      LOG.info("Requests {} to transfer {} into {} format", TRANSCODE_URL, mPath, mFormat);
      requestWithRetry();
      mSuccess = true;
      FileInfoSqlService.updateTranscodeInfo(mPath);
    } catch (IOException e) {
      LOG.error("Failed to write tmpFile path: {} ", mPath);
    }
    finally {
      if (!mSuccess) {
        LOG.error("Failed to request transcode, url: {}, path: {}, format: {}", TRANSCODE_URL,
            mPath, mFormat);
      }
    }
  }

  void requestWithRetry() {
   // throw new RuntimeException();
    int retry = RETRY_TIME;
    do {
      Client client = ClientBuilder.newBuilder()
          .withConfig(new ClientConfig())
          .build();
      WebTarget target = client.target(TRANSCODE_URL);
      Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
          .header("Content-Type", "application/json; charset=utf-8")
          .post(Entity.entity(jsonEntity(), MediaType.APPLICATION_JSON_TYPE));
      TranscodeResponse transcodeResponse =
          TranscodeResponse.fromString(response.readEntity(String.class));
      if(transcodeResponse.getErrorCode() == 0) {
        return;
      }
      if (transcodeResponse.getErrorCode() != 0 && retry == 0) {
        throw new RuntimeException(String.format("Transcode error %d, %s",
            transcodeResponse.getErrorCode(), transcodeResponse.getDescription()));
      }
      retry --;
    } while(true);
  }

  void request() {
    Client client = ClientBuilder.newBuilder()
        .withConfig(new ClientConfig())
        .build();
    WebTarget target = client.target(TRANSCODE_URL);
    Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
        .header("Content-Type", "application/json; charset=utf-8")
        .post(Entity.entity(jsonEntity(), MediaType.APPLICATION_JSON_TYPE));
    TranscodeResponse transcodeResponse =
        TranscodeResponse.fromString(response.readEntity(String.class));
    if (transcodeResponse.getErrorCode() != 0) {
      throw new RuntimeException(String.format("Transcode error %d, %s",
          transcodeResponse.getErrorCode(), transcodeResponse.getDescription()));
    }
  }

  private String jsonEntity() {
    return String.format("{\"Filepath\": \"%s\", \"Format\": \"%s\"}", mPath, mFormat);
  }
}
