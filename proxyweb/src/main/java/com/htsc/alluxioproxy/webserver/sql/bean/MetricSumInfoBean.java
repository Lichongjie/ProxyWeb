package com.htsc.alluxioproxy.webserver.sql.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.sql.Date;

public class MetricSumInfoBean
{
  @JSONField(format="yyyy-MM-dd")
  private Date mDate;
  private BigDecimal mUploadSum;
  private BigDecimal mUploadSuccNum;
  private BigDecimal mUploadFailedNum;
  private BigDecimal mDownloadSum;
  private BigDecimal mDownloadSuccNum;
  private BigDecimal mDownloadFailNum;
  private BigDecimal mTranscodeSum;
  private BigDecimal mTranscodeSuccNum;
  private BigDecimal mTranscodeFailNum;
  private String mDayIsArchive;
  private BigDecimal mReTranscodeSuccNum;

  public BigDecimal getmUploadSum() {
    return mUploadSum;
  }

  public void setmUploadSum(BigDecimal mUploadSum) {
    this.mUploadSum = mUploadSum;
  }

  public BigDecimal getmUploadSuccNum() {
    return mUploadSuccNum;
  }

  public void setmUploadSuccNum(BigDecimal mUploadSuccNum) {
    this.mUploadSuccNum = mUploadSuccNum;
  }

  public BigDecimal getmUploadFailedNum() {
    return mUploadFailedNum;
  }

  public void setmUploadFailedNum(BigDecimal mUploadFailedNum) {
    this.mUploadFailedNum = mUploadFailedNum;
  }

  public BigDecimal getmDownloadSum() {
    return mDownloadSum;
  }

  public void setmDownloadSum(BigDecimal mDownloadSum) {
    this.mDownloadSum = mDownloadSum;
  }

  public BigDecimal getmDownloadSuccNum() {
    return mDownloadSuccNum;
  }

  public void setmDownloadSuccNum(BigDecimal mDownloadSuccNum) {
    this.mDownloadSuccNum = mDownloadSuccNum;
  }

  public BigDecimal getmDownloadFailNum() {
    return mDownloadFailNum;
  }

  public void setmDownloadFailNum(BigDecimal mDownloadFailNum) {
    this.mDownloadFailNum = mDownloadFailNum;
  }

  public BigDecimal getmTranscodeSum() {
    return mTranscodeSum;
  }

  public void setmTranscodeSum(BigDecimal mTranscodeSum) {
    this.mTranscodeSum = mTranscodeSum;
  }

  public BigDecimal getmTranscodeSuccNum() {
    return mTranscodeSuccNum;
  }

  public void setmTranscodeSuccNum(BigDecimal mTranscodeSuccNum) {
    this.mTranscodeSuccNum = mTranscodeSuccNum;
  }

  public BigDecimal getmTranscodeFailNum() {
    return mTranscodeFailNum;
  }

  public void setmTranscodeFailNum(BigDecimal mTranscodeFailNum) {
    this.mTranscodeFailNum = mTranscodeFailNum;
  }

  public String getmDayIsArchive() {
    return mDayIsArchive;
  }

  public Date getmDate() {
    return mDate;
  }

  public void setmDate(Date mDate) {
    this.mDate = mDate;
  }

  public void setmDayIsArchive(Long mDayIsArchive) {
    this.mDayIsArchive = (mDayIsArchive > 0 ? "true" : "false");
  }

  public MetricSumInfoBean(Date date, BigDecimal uploadSum, BigDecimal uploadSuccSum, BigDecimal uploadFailedSum,
                           BigDecimal downloadSum, BigDecimal downloadSuccSum, BigDecimal downloadFailSum,
                           BigDecimal transcodeSum, BigDecimal transcodeSuccSum, BigDecimal transcodeFailSum,BigDecimal reTranscodeSuccNum,
                           BigDecimal dayIsArchive) {
    mDate = date;
    mUploadSum =uploadSum;
    mUploadSuccNum = uploadSuccSum;
    mUploadFailedNum = uploadFailedSum;
    mDownloadSum = downloadSum;
    mDownloadSuccNum = downloadSuccSum;
    mDownloadFailNum = downloadFailSum;

    mTranscodeSum = transcodeSum;
    mTranscodeSuccNum = transcodeSuccSum;
    mTranscodeFailNum = transcodeFailSum;
    mDayIsArchive = (dayIsArchive.compareTo(new BigDecimal(0)) > 0 ? "true" : "false");
    mReTranscodeSuccNum = reTranscodeSuccNum;
  }

  public MetricSumInfoBean(BigDecimal uploadSum, BigDecimal uploadSuccSum, BigDecimal uploadFailedSum,
                           BigDecimal downloadSum, BigDecimal downloadSuccSum, BigDecimal downloadFailSum,
                           BigDecimal transcodeSum, BigDecimal transcodeSuccSum, BigDecimal transcodeFailSum, BigDecimal reTranscodeSuccNum,
                           long dayIsArchive) {
    mUploadSum =uploadSum;
    mUploadSuccNum = uploadSuccSum;
    mUploadFailedNum = uploadFailedSum;
    mDownloadSum = downloadSum;
    mDownloadSuccNum = downloadSuccSum;
    mDownloadFailNum = downloadFailSum;

    mTranscodeSum = transcodeSum;
    mTranscodeSuccNum = transcodeSuccSum;
    mTranscodeFailNum = transcodeFailSum;
    mDayIsArchive = (dayIsArchive > 0 ? "true" : "false");
    mReTranscodeSuccNum = reTranscodeSuccNum;
  }

  public BigDecimal getmReTranscodeSuccNum() {
    return mReTranscodeSuccNum;
  }

  public void setmReTranscodeSuccNum(BigDecimal mReTranscodeSuccNum) {
    this.mReTranscodeSuccNum = mReTranscodeSuccNum;
  }

  @Override
  public String toString() {
    return "MetricSumInfoBean{" +
            "mDate=" + mDate +
            ", mUploadSum=" + mUploadSum +
            ", mUploadSuccNum=" + mUploadSuccNum +
            ", mUploadFailedNum=" + mUploadFailedNum +
            ", mDownloadSum=" + mDownloadSum +
            ", mDownloadSuccNum=" + mDownloadSuccNum +
            ", mDownloadFailNum=" + mDownloadFailNum +
            ", mTranscodeSum=" + mTranscodeSum +
            ", mTranscodeSuccNum=" + mTranscodeSuccNum +
            ", mTranscodeFailNum=" + mTranscodeFailNum +
            ", mDayIsArchive='" + mDayIsArchive + '\'' +
            ", mReTranscodeSuccNum=" + mReTranscodeSuccNum +
            '}';
  }

}
