package com.htsc.alluxioproxy.sql;

import com.htsc.alluxioproxy.sql.tmp.SqlManager;
import com.htsc.alluxioproxy.sql.util.Configuration;

import com.htsc.alluxioproxy.sql.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class ProxySqlServer {
  private static final Logger LOG = LoggerFactory.getLogger(ProxySqlServer.class);
  private static final Configuration CONF = SqlTools.getConf();

  public static void main(String[] args) throws Exception {
    try {
      SqlTools.init();
      SqlManager.INSTANCE.createTable();
      SqlManager.INSTANCE.start();
      LOG.info("proxy sql server started on {}", getBaseURI());
    } catch (Throwable t) {
      LOG.error("Failed to start storage server", t);
      System.exit(-1);
    }
  }
  private static URI getBaseURI() {
    return UriBuilder.fromUri("http://" + CONF.getString(Constants.HOSTNAME))
        .port(CONF.getInt(Constants.SQL_PORT)).build();
  }
}
