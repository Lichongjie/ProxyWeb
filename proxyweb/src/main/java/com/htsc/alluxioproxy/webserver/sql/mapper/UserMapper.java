package com.htsc.alluxioproxy.webserver.sql.mapper;

import com.htsc.alluxioproxy.webserver.sql.bean.UserBean;
import org.apache.ibatis.annotations.Param;

/**
 * Mapper interface about userinfo table sql service
 */
public interface UserMapper {

  public int createNewTable() throws Exception;

  public int insertUser(UserBean bean) throws Exception;

  public UserBean getUser(@Param("userName")String userName) throws Exception;
}
