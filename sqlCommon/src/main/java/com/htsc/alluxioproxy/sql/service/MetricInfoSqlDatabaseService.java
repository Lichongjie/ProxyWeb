package com.htsc.alluxioproxy.sql.service;

import com.htsc.alluxioproxy.sql.bean.MetricInfoBean;
import com.htsc.alluxioproxy.sql.mapper.MetricInfoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class MetricInfoSqlDatabaseService extends DatabaseService {
  private static final Logger LOG = LoggerFactory.getLogger(MetricInfoSqlDatabaseService.class);

  private static MetricInfoMapper mapper;

  public static final SimpleDateFormat MetricInfoDateFormat =
      new SimpleDateFormat("yyyy-MM-dd");

  public static void createTable() throws DataBaseException {
    try {
      mapper.createNewTable();
      commit();
    } catch (Exception e) {
      LOG.error("can't create table metricinfo");
      rollback();
      throw new DataBaseException("");
    }
  }

  /**
   * Insert a metric info record.
   *
   * @param info the record need to be insert
   * @throws DataBaseException if sql.sql error happened
   */
  public static void insertMetricInfo(Serializable info) throws DataBaseException {
    MetricInfoBean infoBean = (MetricInfoBean)info;
    try {
      if(mapper.isExistMetricInfo(infoBean.getDate(), infoBean.getServerId()) > 0) {
        return;
      }
      LOG.info("start to insert data to table metricinfo {}", infoBean.toString());
      mapper.insertMetricInfo(infoBean);
      LOG.info("insert succeed");
    } catch (Exception e) {
      LOG.error("failed to insert data to table metricinfo {}", infoBean.toString());
      throw new DataBaseException(e.getMessage());
    }
  }

  public static void addId()throws DataBaseException {
    try {
      LOG.info("add primary key to insert data to table metricinfo");
      mapper.addId();
      LOG.info("succeed");
    } catch (Exception e) {
      LOG.error("can't add primary key");
      rollback();
      throw new DataBaseException(e.getMessage());
    }
  }

  /**
   * Add to metric info record.
   *
   * @param b1 the metric info
   * @param b2 the metric info
   * @return sum of two metric info records
   */
  private static MetricInfoBean updateMetricInfoBean (MetricInfoBean b1, MetricInfoBean b2) {
    return new MetricInfoBean(b1.getDate(),b1.getServerId(),
        b1.getUploadSum() + b2.getUploadSum(),
        b1.getUploadSuccSum() + b2.getUploadSuccSum(),
        b1.getUploadFailedSum() + b2.getDownloadFailsum(),
        b1.getDownloadSum() + b2.getDownloadSum(),
        b1.getDownloadSuccSum() + b2.getDownloadSuccSum(),
        b1.getDownloadFailsum() + b2.getDownloadFailsum(),
        b1.getTranscodeSum() + b2.getTranscodeSum(),
        b1.getTranscodeSuccSum()+ b2.getTranscodeSuccSum(),
        b1.getTranscodeFailSum()+ b2.getTranscodeFailSum(),
        b1.getReTranscodeSuccNum()+ b2.getReTranscodeSuccNum(),
        b1.isDayIsArchive() ? 1 : 0);
  }

  private static MetricInfoBean insertnew(int serverId) throws Exception {
    String day = MetricInfoDateFormat.format(new java.util.Date());
    java.util.Date d = MetricInfoDateFormat.parse(day);
    Date nowDate = new Date(d.getTime());
    MetricInfoBean nowInfo = new MetricInfoBean(nowDate, serverId, 0, 0, 0,
        0, 0, 0, 0,
        0, 0, 0, 1);
    insertMetricInfo(nowInfo);
    return nowInfo;
  }

  /**
   * Update metric info table.
   *
   * @param metricInfoBean the record need to be add to original record
   * @throws DataBaseException if error happened
   */
  public static void updateMetricInfo(Serializable metricInfoBean) throws DataBaseException {
    MetricInfoBean metricInfo = (MetricInfoBean)metricInfoBean;
    Date date = metricInfo.getDate();
    int serverId = metricInfo.getServerId();
    MetricInfoBean nowInfo;
    try {
      try {
        nowInfo = mapper.selectMetricInfo(date, serverId);
        if(nowInfo == null) {
          nowInfo = insertnew(serverId);
        }
      } catch(Exception e) {
        nowInfo = insertnew(serverId);
      }
      // MetricInfoBean nowInfo = com.htsc.alluxioproxy.sql.mapper.selectMetricInfo(date, serverId);
      MetricInfoBean updateInfo = updateMetricInfoBean(metricInfo, nowInfo);

      LOG.info("start to update data to table metricinfo {} ", updateInfo.toString());
      mapper.updateMetricInfo(updateInfo, date, serverId);
      LOG.info("update succeed");
    } catch (Exception e) {
      LOG.error("failed to update data to table metricinfo {}, caused by {}",
          metricInfo.toString(), e.getMessage());
      throw new DataBaseException(e.getMessage());
    }
  }

  public static void open() {
    mapper = session.getMapper(MetricInfoMapper.class);
  }
}
