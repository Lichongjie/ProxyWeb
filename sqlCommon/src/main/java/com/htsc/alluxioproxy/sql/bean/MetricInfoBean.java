package com.htsc.alluxioproxy.sql.bean;

import com.htsc.alluxioproxy.sql.util.Configuration;
import com.htsc.alluxioproxy.sql.util.Constants;

import java.io.Serializable;
import java.sql.Date;

/**Bean class for metricinfo database table*/
public class MetricInfoBean implements Serializable{
  private static final long serialVersionUID = -2247869646874623212L;
  private Date mDate;
  private int mServerId;
  private int mUploadSum;
  private int mUploadSuccNum;
  private int mUploadFailedNum;
  private int mDownloadSum;
  private int mDownloadSuccNum;
  private int mDownloadFailNum;
  private int mTranscodeSum;
  private int mTranscodeSuccNum;
  private int mTranscodeFailNum;
  private boolean mDayIsArchive;
  private int mReTranscodeSuccNum;

  public MetricInfoBean(Date date, int serverId, int uploadSum, int uploadSuccSum,
      int uploadFailedSum, int downloadSum, int downloadSuccSum, int downloadFailSum,
      int transcodeSum, int transcodeSuccSum, int transcodeFailSum, int reTranscodeSuccNum,int dayIsArchive) {
    super();
    mDate = date;
    mServerId = serverId;
    mUploadSum =uploadSum;
    mUploadSuccNum = uploadSuccSum;
    mUploadFailedNum = uploadFailedSum;
    mDownloadSum = downloadSum;
    mDownloadSuccNum = downloadSuccSum;
    mDownloadFailNum = downloadFailSum;
    mTranscodeSum = transcodeSum;
    mTranscodeSuccNum = transcodeSuccSum;
    mTranscodeFailNum = transcodeFailSum;
    mDayIsArchive = dayIsArchive > 0;
    mReTranscodeSuccNum = reTranscodeSuccNum;
  }

  public MetricInfoBean(int uploadSum, int uploadSuccSum, int uploadFailedSum, int downloadSum,
      int downloadSuccSum, int downloadFailSum, int transcodeSum, int transcodeSuccSum,
          int transcodeFailSum, int reTranscodeSuccNum, int dayIsArchive) {
    this(null, Configuration.INSTANCE.getInt(Constants.SERVER_ID), uploadSum,uploadSuccSum,uploadFailedSum,downloadSum,downloadSuccSum,downloadFailSum,
            transcodeSum,transcodeSuccSum,transcodeFailSum,reTranscodeSuccNum,dayIsArchive);
  }

  public MetricInfoBean(int uploadSum, int uploadSuccSum, int uploadFailedSum, int downloadSum,
       int downloadSuccSum, int downloadFailSum, int transcodeSum, int transcodeSuccSum, int reTranscodeSuccNum,
           int transcodeFailSum) {
    this(null, Configuration.INSTANCE.getInt(Constants.SERVER_ID), uploadSum, uploadSuccSum, uploadFailedSum, downloadSum,
        downloadSuccSum, downloadFailSum, transcodeSum,transcodeSuccSum,transcodeFailSum,0,reTranscodeSuccNum);
  }


  public int getReTranscodeSuccNum() {
    return mReTranscodeSuccNum;
  }

  public void setReTranscodeSuccNum(int mReTranscodeSuccNum) {
    this.mReTranscodeSuccNum = mReTranscodeSuccNum;
  }

  public Date getDate() {
    return mDate;
  }

  @Override
  public String toString() {
    return "MetricInfoBean{" +
            "mDate=" + mDate +
            ", mServerId=" + mServerId +
            ", mUploadSum=" + mUploadSum +
            ", mUploadSuccNum=" + mUploadSuccNum +
            ", mUploadFailedNum=" + mUploadFailedNum +
            ", mDownloadSum=" + mDownloadSum +
            ", mDownloadSuccNum=" + mDownloadSuccNum +
            ", mDownloadFailNum=" + mDownloadFailNum +
            ", mTranscodeSum=" + mTranscodeSum +
            ", mTranscodeSuccNum=" + mTranscodeSuccNum +
            ", mTranscodeFailNum=" + mTranscodeFailNum +
            ", mDayIsArchive=" + mDayIsArchive +
            ", mReTranscodeSuccNum=" + mReTranscodeSuccNum +
            '}';
  }

  public void setDate(Date mDate) {
    this.mDate = mDate;
  }

  public int getServerId() {
    return mServerId;
  }

  public void setServerId(int mServerId) {
    this.mServerId = mServerId;
  }

  public int getUploadSum() {
    return mUploadSum;
  }

  public void setUploadSum(int mUploadSum) {
    this.mUploadSum = mUploadSum;
  }

  public int getUploadSuccSum() {
    return mUploadSuccNum;
  }

  public void setUploadSuccSum(int mUploadSuccSum) {
    this.mUploadSuccNum = mUploadSuccSum;
  }

  public int getUploadFailedSum() {
    return mUploadFailedNum;
  }

  public void setUploadFailedSum(int mUploadFailedSum) {
    this.mUploadFailedNum = mUploadFailedSum;
  }

  public int getDownloadSum() {
    return mDownloadSum;
  }

  public void setDownloadSum(int mDownloadSum) {
    this.mDownloadSum = mDownloadSum;
  }

  public int getDownloadSuccSum() {
    return mDownloadSuccNum;
  }

  public void setDownloadSuccSum(int mDownloadSuccSum) {
    this.mDownloadSuccNum = mDownloadSuccSum;
  }

  public int getDownloadFailsum() {
    return mDownloadFailNum;
  }

  public void setDownloadFailsum(int mDownloadFailsum) {
    this.mDownloadFailNum = mDownloadFailsum;
  }

  public int getTranscodeSum() {
    return mTranscodeSum;
  }

  public void setTranscodeSum(int mTranscodeSum) {
    this.mTranscodeSum = mTranscodeSum;
  }

  public int getTranscodeSuccSum() {
    return mTranscodeSuccNum;
  }

  public void setTranscodeSuccSum(int mTranscodeSuccSum) {
    this.mTranscodeSuccNum = mTranscodeSuccSum;
  }

  public int getTranscodeFailSum() {
    return mTranscodeFailNum;
  }

  public void setTranscodeFailSum(int mTranscodeFailSum) {
    this.mTranscodeFailNum = mTranscodeFailSum;
  }

  public boolean isDayIsArchive() {
    return mDayIsArchive;
  }

  public void setDayIsArchive(boolean mDayIsArchive) {
    this.mDayIsArchive = mDayIsArchive;
  }
}
