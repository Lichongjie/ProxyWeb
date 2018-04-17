package com.htsc.alluxioproxy.webserver.sql.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.htsc.alluxioproxy.webserver.sql.sqlService.FileInfoSqlService;
import com.htsc.alluxioproxy.webserver.web.rest.WebResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class FileInfoBean implements Serializable {
  private static final long serialVersionUID = -6514123645223641850L;

  private Timestamp mUploadDate;
  private String mFileId;
  private boolean mToTranscode;
  @JSONField(name = "transcode")
  private boolean mIsTranscode;
  private String mTranscodeFormat;
  private boolean mIsArchive;
  private boolean mIsMove;

  public FileInfoBean() {
    super();
  }

  public FileInfoBean(String fileId, Timestamp uploadDate, boolean toTranscode, boolean isTranscode,
      String transcodeFormat, boolean isArchive, boolean isMove){
    super();
    mFileId = fileId;
    mUploadDate = uploadDate;
    mToTranscode = toTranscode;
    mIsTranscode = isTranscode;
    mTranscodeFormat = transcodeFormat;
    mIsArchive = isArchive;
    mIsMove = isMove;
  }

  public FileInfoBean(String fileId, Timestamp uploadDate, int toTranscode, int isTranscode,
                      String transcodeFormat, int isArchive, int isMove) {
    this(fileId, uploadDate, toTranscode > 0, isTranscode > 0,
        transcodeFormat, isArchive > 0, isMove > 0);
  }

  @Override
  public String toString() {
    return "FileInfoBean{" +
            "mUploadDate=" + mUploadDate +
            ", mFileId='" + mFileId + '\'' +
            ", mToTranscode=" + mToTranscode +
            ", mIsTranscode=" + mIsTranscode +
            ", mTranscodeFormat='" + mTranscodeFormat + '\'' +
            ", mIsArchive=" + mIsArchive +
            ", mIsMove=" + mIsMove +
            '}';
  }

  public String getFileId() {
    return mFileId;
  }

  public void setFileId(String mFileId) {
    this.mFileId = mFileId;
  }

  public String getUploadDate() {
    try {
      if(mUploadDate.compareTo(new Date(0)) == 0) {
        return "null";
      }
      return FileInfoSqlService.fileInfoDateFormat.format(mUploadDate);
    } catch (Exception e) {
      return mUploadDate.toString();

    }
  }

  public void setUploadDate(Timestamp mUploadDate) {
    this.mUploadDate = mUploadDate;
  }

  public boolean isToTranscode() {
    return mToTranscode;
  }

  public void setToTranscode(boolean mToTranscode) {
    this.mToTranscode = mToTranscode;
  }

  public boolean isTranscode() {
    return mIsTranscode;
  }

  public void setIsTranscode(boolean mIsTranscode) {
    this.mIsTranscode = mIsTranscode;
  }

  public String getTranscodeFormat() {
    return mTranscodeFormat;
  }

  public void setTranscodeFormat(String mTranscodeFormat) {
    this.mTranscodeFormat = mTranscodeFormat;
  }

  @JSONField(name = "archive")
  public boolean isArchive() {
    return mIsArchive;
  }

  public boolean isMove() {
    return mIsMove;
  }

  public void setIsMove(boolean mIsMove) {
    this.mIsMove = mIsMove;
  }

  @JSONField(name = "archive")
  public void setIsArchive(boolean mIsArchive) {
    this.mIsArchive = mIsArchive;
  }
}
