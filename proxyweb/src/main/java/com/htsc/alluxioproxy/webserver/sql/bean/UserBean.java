package com.htsc.alluxioproxy.webserver.sql.bean;

public class UserBean {
  private int mId;
  private String mUserName;
  private String mPassword;

  public UserBean(int id, String user, String password) {
    mId = id;
    mUserName = user;
    mPassword = password;
  }

  public UserBean(String user, String password) {
    mUserName = user;
    mPassword = password;
  }


  public int getId() {
    return mId;
  }

  public void setId(int id) {
    this.mId = id;
  }

  public String getUserName() {
    return mUserName;
  }

  public void setUserName(String mUser) {
    this.mUserName = mUser;
  }

  public String getPassword() {
    return mPassword;
  }

  public void setPassword(String mPassword) {
    this.mPassword = mPassword;
  }
}
