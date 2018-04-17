package com.htsc.alluxioproxy.gateway.security;

import com.htsc.alluxioproxy.gateway.Configuration;
import com.htsc.alluxioproxy.gateway.Constants;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Verifies session tokens by querying redis records.
 */
public final class RedisTokenAuthentication implements TokenAuthentication {
  private static final JedisCluster JEDIS_CLUSTER;
  private static final RedisTokenAuthentication SINGLETON = new RedisTokenAuthentication();

  private RedisTokenAuthentication() {}

  /**
   * @return {@link #SINGLETON}
   */
  public static RedisTokenAuthentication getSingleton() {
    return SINGLETON;
  }

  @Override
  public boolean verify(String token) {
    return JEDIS_CLUSTER.exists(token);
  }

  static {
    List<String> allHostPortStrs = loadAllHostPortStrs();
    Set<HostAndPort> hostAndPorts = new HashSet<>();
    String[] tuple;
    for (String str : allHostPortStrs) {
      tuple = str.split(":");
      hostAndPorts.add(new HostAndPort(tuple[0], Integer.parseInt(tuple[1])));
    }
    JEDIS_CLUSTER = new JedisCluster(hostAndPorts);
  }

  private static List<String> loadAllHostPortStrs() {
    Configuration conf = Configuration.INSTANCE;
    int beginID = conf.getInt(Constants.REDIS_CLUSTER_BEGIN_ID);
    int numClusters = conf.getInt(Constants.REDIS_CLUSTER_NUM);
    String format = conf.getString(Constants.REDIS_CLUSTER_HOST_PORT_FORMAT);
    List<String> ret = new ArrayList<>(numClusters);
    for (int i = beginID; i < beginID + numClusters; ++i) {
      ret.add(conf.getString(String.format(format, i)));
    }
    return ret;
  }
}
