package com.htsc.alluxioproxy;

import alluxio.collections.ConcurrentHashSet;
import com.htsc.alluxioproxy.sql.util.Configuration;
import com.htsc.alluxioproxy.sql.util.PathUtils;
import com.htsc.alluxioproxy.util.Constants;
import com.htsc.alluxioproxy.util.ExceptionLoggedThreadPool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * The context of the proxy server.
 */
public final class ServerContext {
  private static final String CONFIGURED_FILE = "users.xml";
  private static final Map<String, String> USERNAME_TO_USERID = new ConcurrentHashMap<>();
  private static final Map<String, String> USERID_TO_USERNAME = new ConcurrentHashMap<>();
  private static final Map<String, String> USERNAME_TO_PASSWORD = new ConcurrentHashMap<>();
  private static final Map<String, String> USERNAME_TO_PRIMARY_GROUP = new ConcurrentHashMap<>();
  private static final Map<String, Set<String>> USERNAME_TO_GROUPS = new ConcurrentHashMap<>();
  public static final ExecutorService DATABASE_SERVICE =
          ExceptionLoggedThreadPool.newFixedThreadPool(10);

  static {
    loadUserConfigFile();
  }

  private static void loadUserConfigFile() {
    try {
      String path = PathUtils.concatPath(getConf().getString(Constants.CONF_DIR),
          CONFIGURED_FILE);
      File file = new File(path);
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse(file);
      doc.getDocumentElement().normalize();
      NodeList nodeList = doc.getElementsByTagName("user");
      for (int i = 0; i < nodeList.getLength(); ++i) {
        Node node = nodeList.item(i);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
          Element element = (Element) node;
          String userID = element.getElementsByTagName("id").item(0).getTextContent().trim();
          String username = element.getElementsByTagName("name").item(0).getTextContent().trim();
          String password =
              element.getElementsByTagName("password").item(0).getTextContent().trim();
          String primaryGroup =
              element.getElementsByTagName("primary-group").item(0).getTextContent().trim();
          String[] groups =
              element.getElementsByTagName("groups").item(0).getTextContent().split(",");
          USERNAME_TO_USERID.put(username, userID);
          USERID_TO_USERNAME.put(userID, username);
          USERNAME_TO_PASSWORD.put(username, password);
          USERNAME_TO_PRIMARY_GROUP.put(username, primaryGroup);
          Set<String> groupSet = new ConcurrentHashSet<>();
          groupSet.add(primaryGroup);
          for (String group : groups) {
            String str = group.trim();
            if (str.length() > 0) {
              groupSet.add(str);
            }
          }
          USERNAME_TO_GROUPS.put(username, groupSet);
        }
      }
    } catch (Exception e) {
      throw new Error("Failed to parse users configuration file");
    }
  }

  /**
   * @return the server configuration
   */
  public static Configuration getConf() {
    return Configuration.INSTANCE;
  }

  /**
   * @param username the username
   * @return the user ID
   */
  public static String getUserID(String username) {
    return USERNAME_TO_USERID.get(username);
  }

  /**
   * @param userID the user ID
   * @return the username
   */
  public static String getUsername(String userID) {
    return USERID_TO_USERNAME.get(userID);
  }

  /**
   * @param username the username
   * @return the password
   */
  public static String getPassword(String username) {
    return USERNAME_TO_PASSWORD.get(username);
  }

  /**
   * @param username the username
   * @return the primary group
   */
  public static String getPrimaryGroup(String username) {
    return USERNAME_TO_PRIMARY_GROUP.get(username);
  }

  public static int getServerId() {
    return getConf().getInt(Constants.SERVER_ID);

  }

  /**
   * @param username the username
   * @return the belonged groups
   */
  public static Set<String> getGroups(String username) {
    return USERNAME_TO_GROUPS.get(username);
  }

  private ServerContext() {}
}
