package com.htsc.alluxioproxy.webserver.sql.sqlService;

import com.htsc.alluxioproxy.webserver.sql.bean.UserBean;
import com.htsc.alluxioproxy.webserver.sql.mapper.UserMapper;
import com.htsc.alluxioproxy.webserver.utils.Configuration;
import com.htsc.alluxioproxy.webserver.utils.Constants;
import org.apache.ibatis.session.SqlSession;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * Handling user database table sql com.htsc.alluxioproxy.sql.service
 */
public class UserSqlService {
  private static final Logger LOG = LoggerFactory.getLogger(UserSqlService.class);
  private static SqlSession session = SqlTools.getSession();
  private static UserMapper mapper = session.getMapper(UserMapper.class);
  private static Configuration mConf = Configuration.INSTANCE;

  /**
   * Insert a user info.
   *
   * @param bean the inserted record
   */
  public static void insertUser(UserBean bean) {
    try {
      mapper.insertUser(bean);
      session.commit();
    } catch (Exception e) {
      LOG.error("can't create user {}", bean);
      e.printStackTrace();
      session.rollback();
    }
  }

  /**
   * Create a user table in database.
   *
   * @throws DataBaseException if database error happened
   */
  public static void createTable() throws DataBaseException {
    try {
      mapper.createNewTable();
      session.commit();
    } catch (Exception e) {
      LOG.error("can't create table user");
      session.rollback();
      throw new DataBaseException("");
    }
  }


  public static UserBean selectUser(String userName) {
    Connection c = null;
    Statement s = null;
    ResultSet r = null;
    String url = mConf.getString(Constants.DATABASE_URL);
    String username = mConf.getString(Constants.DATABASE_USERNAME);
    String password = mConf.getString(Constants.DATABASE_PASSWORD);
    try {
      c = DriverManager.getConnection(url, username, password);
      s = c.createStatement();
      r = s.executeQuery("select * from user");
      while (r.next()) {
        int x = r.getInt("id");
        String y = r.getString("userName");
        String z = r.getString("password");
        if (z.equals(userName)) {
          return new UserBean(x, y, z);
        }
      }
    } catch (SQLException e) {
      LOG.info("{}", e.getMessage());

    } finally {
      try {
        r.close();
        s.close();
        c.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  /**
   * @param userName the user id
   * @return the user record mapping to id
   * @throws Exception if error happened
   */
  public static UserBean getUser(String userName)  {
    LOG.info("select user info, name is {}", userName);
    return selectUser(userName);
  }

  /**
   * Create table, add admin user and download user.
   */
  public static void init() {
    LOG.info("init database table user ");
    try {
      createTable();
    } catch (Exception e) {
      LOG.info("table already exist");
    }
    try {
      if(getUser("admin") == null) {
        insertUser(new UserBean(0,"admin", "admin"));
      }
      if(getUser("download") == null) {
        insertUser(new UserBean(1,"download", "download"));
      }

    } catch (Exception e) {
      LOG.info("can't insert user info");
    }
  }






}
