package sql.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

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

  public MetricInfoBean(Date date, int serverId, int uploadSum, int uploadSuccSum,
      int uploadFailedSum, int downloadSum, int downloadSuccSum, int downloadFailSum,
      int transcodeSum, int transcodeSuccSum, int transcodeFailSum, int dayIsArchive) {
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
  }

  public MetricInfoBean( int uploadSum, int uploadSuccSum, int uploadFailedSum,
                        int downloadSum, int downloadSuccSum, int downloadFailSum,
                        int transcodeSum, int transcodeSuccSum, int transcodeFailSum, int dayIsArchive) {
    this(null, -1, uploadSum,uploadSuccSum,uploadFailedSum,downloadSum,downloadSuccSum,downloadFailSum,
            transcodeSum,transcodeSuccSum,transcodeFailSum,dayIsArchive);
  }

  public MetricInfoBean( int uploadSum, int uploadSuccSum, int uploadFailedSum,
                         int downloadSum, int downloadSuccSum, int downloadFailSum,
                         int transcodeSum, int transcodeSuccSum, int transcodeFailSum) {
    this(null, -1, uploadSum,uploadSuccSum,uploadFailedSum,downloadSum,downloadSuccSum,downloadFailSum,
            transcodeSum,transcodeSuccSum,transcodeFailSum,0);
  }

    @Override
  public String toString() {
    return "MetricInfoBean{" +
            "mDate=" + mDate +
            ", mServerId=" + mServerId +
            ", mUploadSum=" + mUploadSum +
            ", mUploadSuccSum=" + mUploadSuccNum +
            ", mUploadFailedSum=" + mUploadFailedNum +
            ", mDownloadSum=" + mDownloadSum +
            ", mDownloadSuccSum=" + mDownloadSuccNum +
            ", mDownloadFailsum=" + mDownloadFailNum +
            ", mTranscodeSum=" + mTranscodeSum +
            ", mTranscodeSuccSum=" + mTranscodeSuccNum +
            ", mTranscodeFailSum=" + mTranscodeFailNum +
            ", mDayIsArchive=" + mDayIsArchive +
            '}';
  }

  public Date getDate() {
    return mDate;
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
