package com.htsc.alluxioproxy.sql;

import javax.sql.rowset.serial.SerialArray;
import java.io.Serializable;

/**
 * Created by lenovo on 2017/12/14.
 */
public class SqlTmp implements Serializable
{
  private static final long serialVersionUID = 8964804364945252404L;
  private String mServiceName;
  private String mOperationName;
  private Serializable mBean;
  private boolean isFileUpdate;
  private String mId;

  public SqlTmp(String serviceName, String operationName, Serializable bean, String id) {
    mServiceName = serviceName;
    mOperationName = operationName;
    mBean = bean;
    if(mBean == null) {
      isFileUpdate = true;
    }
    mId = id;
  }

  public boolean isUpdate() {
    return isFileUpdate;
  }

  public String getID() {
    return mId;
  }

  public void setID(String ID) {
    this.mId = ID;
  }

  public String getServiceName() {
    return mServiceName;
  }

  public String getOperationName() {
    return mOperationName;
  }

  public Serializable getBean() {
    return mBean;
  }

  public void setServiceName(String serviceName) {
    mServiceName = serviceName;
  }

  public void setOperationName(String operationName) {
    mOperationName = operationName;
  }

  public void setBean(Serializable bean) {
    mBean = bean;
  }

  @Override
  public String toString() {
    return "SqlTmp{" +
            "mServiceName='" + mServiceName + '\'' +
            ", mOperationName='" + mOperationName + '\'' +
            ", mId='" + mId + '\'' +
            '}';
  }
}
