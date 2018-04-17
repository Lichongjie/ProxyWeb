package com.htsc.alluxioproxy.webserver.web.rest;

import com.htsc.alluxioproxy.webserver.sql.bean.FileInfoBean;
import com.htsc.alluxioproxy.webserver.sql.bean.MetricSumInfoBean;
import com.htsc.alluxioproxy.webserver.utils.Configuration;
import com.htsc.alluxioproxy.webserver.utils.Constants;
import sun.security.krb5.Config;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserSessionManager {
  final ConcurrentHashMap<String, HistroyInfoBean> HISTORY_MAP =
      new ConcurrentHashMap<>();
  final ConcurrentHashMap<String, FileInfoPageBean> TRANSCODE_INFO_MAP =
      new ConcurrentHashMap<>();
  final ConcurrentHashMap<String, FileInfoPageBean> DOWNLOAD_INFO_MAP =
      new ConcurrentHashMap<>();
  final ConcurrentHashMap<String, FileInfoPageBean> ARCHIVE_INFO_MAP =
      new ConcurrentHashMap<>();
  final ConcurrentHashMap<String, FileInfoPageBean> DELETE_INFO_MAP =
      new ConcurrentHashMap<>();
  final Hashtable<String, UserLevel> USER_SESSION = new Hashtable<>();
  public static final String LOGIN_TIMEOUT_INTERNAL = Configuration.INSTANCE.getString(Constants.LOGIN_TIMEOUT_INTERNAL);

  public static final long mTimeOutInternal = getTimtoutMills();

  private Date mLoginTime;

  public enum UserLevel{
    ADMIN,
    NORMAL
  }

  public static long getTimtoutMills() {
    int length = LOGIN_TIMEOUT_INTERNAL.length();
    double res = 0;
    String time = LOGIN_TIMEOUT_INTERNAL.substring(0, length - 1);
    String unit = LOGIN_TIMEOUT_INTERNAL.substring(length - 1, length);
    if(unit.equals("h") || unit.equals("H")){
      res = Double.parseDouble(time) * 60 * 60 * 1000;
    } else if(unit.equals("m") || unit.equals("M")) {
      res = Double.parseDouble(time) * 60 * 1000;
    } else if (unit.equals("s") || unit.equals("S")) {
      res = Double.parseDouble(time) * 1000;
    } else if(unit.equals("ms") || unit.equals("MS")) {
      res = Double.parseDouble(time);
    }
    return (long)res;
  }
  public UserSessionManager() {
    mLoginTime = new Date();
  }

  public boolean loginTimeOutCheck() {
    if (new Date().getTime() - mLoginTime.getTime() >= mTimeOutInternal ) {
      return true;
    }
    return false;
  }

  public void clearUserLevelInfo(String user) {
    if(USER_SESSION.containsKey(user)){
      USER_SESSION.remove(user);
    }
  }

  public boolean loginCheck(String user) {
    if(USER_SESSION.containsKey(user)) {
      return true;
    } else {
      return false;
    }
  }

  public void addUserLevelInfo(String addr, UserLevel level ) {
    USER_SESSION.put(addr, level);
  }

  public  UserLevel getUserLevel(String addr) {
    return USER_SESSION.get(addr);
  }

  public void clearHistoryInfo(String user) {
    if(HISTORY_MAP.containsKey(user)){
      HISTORY_MAP.remove(user);
    }
  }

  public void addHistoryInfo(String addr, List<MetricSumInfoBean> res) {
    sortMetricList(res);
    HISTORY_MAP.put(addr, new HistroyInfoBean(res));
  }

  public HistroyInfoBean getHistoryInfo(String addr) {
    return HISTORY_MAP.get(addr);
  }

  public void clearTranscodeInfo(String user) {
    if(TRANSCODE_INFO_MAP.containsKey(user)){
      TRANSCODE_INFO_MAP.remove(user);
    }
  }

  public void addTranscodeInfo(String addr, List<FileInfoBean> res) {
    sortFileList(res);
    TRANSCODE_INFO_MAP.put(addr, new FileInfoPageBean(res));
  }

  public FileInfoPageBean getTranscodeInfo(String addr) {
    return TRANSCODE_INFO_MAP.get(addr);
  }

  public void clearDownloadInfo(String user) {
    if(DOWNLOAD_INFO_MAP.containsKey(user)){
      DOWNLOAD_INFO_MAP.remove(user);
    }
  }

  public void addDownloadInfo(String addr, List<FileInfoBean> res) {
    sortFileList(res);
    DOWNLOAD_INFO_MAP.put(addr, new FileInfoPageBean(res));
  }

  public FileInfoPageBean getDownloadInfo(String addr) {
    return DOWNLOAD_INFO_MAP.get(addr);
  }


  public void clearArchiveInfo(String user) {
    if(ARCHIVE_INFO_MAP.containsKey(user)){
      ARCHIVE_INFO_MAP.remove(user);
    }
  }

  public void addArchiveInfo(String addr, List<FileInfoBean> res) {
    sortFileList(res);
    ARCHIVE_INFO_MAP.put(addr, new FileInfoPageBean(res));
  }

  public FileInfoPageBean getArchiveInfo(String addr) {
    return ARCHIVE_INFO_MAP.get(addr);
  }

  public void clearDeleteInfo(String user) {
    if(DELETE_INFO_MAP.containsKey(user)){
      DELETE_INFO_MAP.remove(user);
    }
  }

  public void addDeleteInfo(String addr, List<FileInfoBean> res) {
    sortFileList(res);
    DELETE_INFO_MAP.put(addr, new FileInfoPageBean(res));
  }

  public FileInfoPageBean getDeleteInfo(String addr) {
    return DELETE_INFO_MAP.get(addr);
  }


  class InfoBean<T> {
    private List<T> mResult;
    private int mCurrentIndex;
    private int mPageNum;

    InfoBean(List<T> result) {
      mResult = result;
      mCurrentIndex = 1;
      mPageNum = 25;
    }

    private int getPageSum() {
      int baseNum = mResult.size() / mPageNum;
      if(mResult.size() % mPageNum != 0 ) {
        baseNum ++;
      }
      return baseNum;
    }

    public List<T> getForwardPage() {
      if(mCurrentIndex == getPageSum()) {
        return getResultByIndex((mCurrentIndex));
      }
      mCurrentIndex ++;
      return getResultByIndex(mCurrentIndex);
    }

    public List<T> getBackwordPage() {
      if(mCurrentIndex == 1) {
        return getResultByIndex(mCurrentIndex);
      }
      mCurrentIndex --;
      return getResultByIndex(mCurrentIndex);
    }

    public List<T> getResultByIndex(int index) {
      mCurrentIndex = index;
      int begin = mPageNum * (index - 1);
      if (begin >= mResult.size()) {
        index = index - 1;
        begin = mPageNum * (index - 1);
      }
      int end = mPageNum * index;

      if(end >= mResult.size()) {
        end = mResult.size();
      }
      return mResult.subList(begin, end);
    }

    public List<T> getResult() {
      return mResult;
    }
  }

  private void sortMetricList(List<MetricSumInfoBean> result) {
    result.sort(new Comparator<MetricSumInfoBean>() {
      @Override
      public int compare(MetricSumInfoBean o1, MetricSumInfoBean o2) {
        return o1.getmDate().compareTo(o2.getmDate());
      }
    });

  }
  private void sortFileList(List<FileInfoBean> result) {
    result.sort(new Comparator<FileInfoBean>() {
      @Override
      public int compare(FileInfoBean o1, FileInfoBean o2) {
        return o1.getUploadDate().compareTo(o2.getUploadDate());
      }
    });
  }

  class HistroyInfoBean extends InfoBean<MetricSumInfoBean> {
    public HistroyInfoBean(List<MetricSumInfoBean> result) {
      super(result);
    }
  }

  class FileInfoPageBean extends InfoBean<FileInfoBean> {
    public FileInfoPageBean(List<FileInfoBean> result) {
      super(result);
    }
  }

  public boolean authentic(String pageName, String user) {
    if(getUserLevel(user) == UserLevel.NORMAL && (!pageName.equals("download2") && !pageName.equals("index"))) {
      return false;
    }
    return true;
  }

}
