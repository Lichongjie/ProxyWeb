package com.htsc.alluxioproxy.tmp;

import com.htsc.alluxioproxy.sql.service.FileInfoSqlDatabaseService;
import com.htsc.alluxioproxy.sql.util.Configuration;
import com.htsc.alluxioproxy.util.Constants;
import com.htsc.alluxioproxy.ServerContext;
import com.htsc.alluxioproxy.sql.bean.MetricInfoBean;
import com.htsc.alluxioproxy.util.ExceptionLoggedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class MetricInfoManager {
  private static final Configuration CONF = ServerContext.getConf();
  private static final Logger LOG = LoggerFactory.getLogger(MetricInfoManager.class);

  private static final int SERVER_ID = CONF.getInt(Constants.SERVER_ID);
  private static AtomicInteger UPLOAD_SUM = new AtomicInteger(0);
  private static AtomicInteger UPLOAD_SUCC_NUM = new AtomicInteger(0);
  private static AtomicInteger UPLOAD_FAILED_NUM = new AtomicInteger(0);
  private static AtomicInteger DOWNLOAD_SUM = new AtomicInteger(0);
  private static AtomicInteger DOWNLOAD_SUCC_NUM = new AtomicInteger(0);
  private static AtomicInteger DOWNLOAD_FAILED_NUM = new AtomicInteger(0);
  private static AtomicInteger TRANSCODE_SUM = new AtomicInteger(0);
  private static AtomicInteger TRANSCODE_SUCC_NUM = new AtomicInteger(0);
  private static AtomicInteger TRANSCODE_FAILED_NUM = new AtomicInteger(0);
  private static AtomicInteger RE_TRANSCODE_SUCC_NUM = new AtomicInteger(0);
  private static Date NOW = null;

  public static final SimpleDateFormat MetricInfoDateFormat =
      new SimpleDateFormat("yyyy-MM-dd");

  private static long UPDATE_DATABASE_INTERNAL = CONF.getLong(Constants.UPDATE_DATABASE_INTERNAL);
  private static final ExecutorService METRIC_SERVICE =
          ExceptionLoggedThreadPool.newFixedThreadPool(10);

  private static AtomicBoolean IS_CHANGE = new AtomicBoolean(false);

  // if all file are archived in date NOW, the var will set to 1
  private static int IS_ARCHIVE = 0;

  /**
   * Update update info
   *
   * @param isSucceed if upload operation succeed
   */
  public static void updateUploadData(boolean isSucceed) {
    UPLOAD_SUM.incrementAndGet();
    if (isSucceed) {
      UPLOAD_SUCC_NUM.incrementAndGet();
    } else {
      UPLOAD_FAILED_NUM.incrementAndGet();
    }
    IS_CHANGE.getAndSet(true);
  }

  /**
   * Update download info
   *
   * @param isSucceed if download operation succeed
   */
  public static void updateDownloadData(boolean isSucceed) {
    DOWNLOAD_SUM.incrementAndGet();
    if (isSucceed) {
      DOWNLOAD_SUCC_NUM.incrementAndGet();
    } else {
      DOWNLOAD_FAILED_NUM.incrementAndGet();
    }
    IS_CHANGE.getAndSet(true);
  }

  /**
   * Update transcode info
   *
   * @param isSucceed if transcode info succeed
   */
  public static void updateTranscodeDate(boolean isSucceed) {
    TRANSCODE_SUM.incrementAndGet();
    if (isSucceed) {
      TRANSCODE_SUCC_NUM.incrementAndGet();
    } else {
      TRANSCODE_FAILED_NUM.incrementAndGet();
    }
    IS_CHANGE.getAndSet(true);
  }

  /**
   * Update reTranscode info
   */
  public static void updateReTranscodeData() {
    RE_TRANSCODE_SUCC_NUM.incrementAndGet();
    IS_CHANGE.getAndSet(true);
  }

  /**
   * Update archive info
   */
  public static void updateArchiveInfo() {
    IS_CHANGE.getAndSet(true);
  }

  private static MetricInfoBean generateBean() {
    MetricInfoBean bean = new MetricInfoBean(NOW, SERVER_ID, UPLOAD_SUM.getAndSet(0),
        UPLOAD_SUCC_NUM.getAndSet(0), UPLOAD_FAILED_NUM.getAndSet(0),
        DOWNLOAD_SUM.getAndSet(0), DOWNLOAD_SUCC_NUM.getAndSet(0),
        DOWNLOAD_FAILED_NUM.getAndSet(0), TRANSCODE_SUM.getAndSet(0),
        TRANSCODE_SUCC_NUM.getAndSet(0), TRANSCODE_FAILED_NUM.getAndSet(0),
        RE_TRANSCODE_SUCC_NUM.getAndSet(0),IS_ARCHIVE);
    return bean;
  }

  private static final class updateMetricThread implements Runnable {
    @Override
    public void run() {
      while(true) {
        try {
          String day = MetricInfoDateFormat.format(new java.util.Date());
          java.util.Date d = MetricInfoDateFormat.parse(day);
          Date nowDate = new Date(d.getTime());

          if ( NOW == null || nowDate.compareTo(NOW) != 0 ) {
            // New day begin!
            //update last days info
            if (IS_CHANGE.get()) {
              try {
                IS_ARCHIVE = FileInfoSqlDatabaseService.dayIsUnarchived(day);
                MetricInfoSqlService.updateMetricInfo(generateBean());
                IS_CHANGE.getAndSet(false);
              } catch (Exception e) {
                LOG.error("update MetricInfo failed, tmp file can't write");
              }
            }

            //insert new days empty info
            NOW = nowDate;
            IS_ARCHIVE = 1;
            try {
              MetricInfoSqlService.insertMetricInfo(generateBean());
            } catch (Exception e) {
              LOG.error("insert metric info failed, tmp file can't write");
              return;
            }
          }

          // update last time internal's info
          if (IS_CHANGE.get()) {
						IS_ARCHIVE = FileInfoSqlDatabaseService.dayIsUnarchived(day);
            MetricInfoSqlService.updateMetricInfo(generateBean());
            IS_CHANGE.getAndSet(false);
          }
          Thread.sleep(UPDATE_DATABASE_INTERNAL);
        } catch (InterruptedException e) {
          LOG.error("metric info update thread was interrupted");
          return;
        } catch (Exception e) {
          IS_CHANGE.getAndSet(true);
          LOG.error("update MetricInfo failed" );
        }
      }
    }
  }

  public static void startMetric() {
    METRIC_SERVICE.submit(new updateMetricThread());
  }
}
