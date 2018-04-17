package com.htsc.alluxioproxy.webserver.web.request;

/**
 * query server info from stroage server
 */
public class ServerInfoRequest extends AsyncStorageRequest {
  public ServerInfoRequest(String fileId) {
    super(fileId, "serverId");
  }

  @Override
  public void queryParam() {
  }
}
