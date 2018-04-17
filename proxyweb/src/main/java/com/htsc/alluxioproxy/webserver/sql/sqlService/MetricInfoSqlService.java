package com.htsc.alluxioproxy.webserver.sql.sqlService;

import com.htsc.alluxioproxy.webserver.sql.bean.MetricInfoBean;
import com.htsc.alluxioproxy.webserver.sql.bean.MetricSumInfoBean;
import com.htsc.alluxioproxy.webserver.sql.mapper.MetricInfoMapper;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * handle metricinfo database operation
 */
public class MetricInfoSqlService {
  private static final Logger LOG = LoggerFactory.getLogger(FileInfoSqlService.class);
  public static final SimpleDateFormat MetricInfoDateFormat =
          new SimpleDateFormat("yyyy-MM-dd");
  private static SqlSession session = SqlTools.getSession();
  private static MetricInfoMapper mapper = session.getMapper(MetricInfoMapper.class);

  /**
   * Update Metric info to database
   *
   * @param metricInfo the record to update
   * @param date the date of record
   * @param serverId the id of server in metricinfo
   */
  private static void updateMetricInfo(MetricInfoBean metricInfo, Date date, int serverId) {
    try {
      mapper.updateMetricInfo(metricInfo, date, serverId);
      session.commit();
    } catch (Exception e) {
      e.printStackTrace();
      session.rollback();
    }
  }

  /**
   * Select metricinfo from database
   *
   * @param date the date of record
   * @param serverId the id of server in metricinfo
   * @return
   */
  public static MetricInfoBean selectMetricInfoByIdAndDate(Date date, int serverId) {
    try {
      return mapper.selectMetricInfoByIdAndDate(date, serverId);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * @return select result
   * @throws Exception if error happened
   */
  public static MetricSumInfoBean selectMetricInfoByDate() throws Exception {
    String date = MetricInfoDateFormat.format(new java.util.Date());
    refreshSession();
    return mapper.selectMetricInfoByDate(Date.valueOf(date));
  }

  /**
   * @return select result
   * @throws Exception
   */
  public static MetricSumInfoBean selectMetricInfo() throws Exception{
    refreshSession();
    return mapper.selectMetricInfo();
  }

  /**
   * select metricinfo by date internal
   * @param begin the begin date
   * @param end the end date
   * @return select result
   * @throws Exception if error happened
   */
  public static List<MetricSumInfoBean> selectHistoryMetricInfo(Date begin, Date end) throws Exception {
    LOG.info("select history metric info from {} to {}", begin, end);
    refreshSession();
    return mapper.selectHistoryMetricInfo(begin, end);
  }

  /**
   * select top K metricinfo.
   *
   * @param topK the number of select result
   * @return select result
   * @throws Exception if error happened
   */
  public static List<MetricSumInfoBean> initHistoryInfo(int topK) throws Exception {
    LOG.info("init history info from database");
    refreshSession();
    return mapper.initHistoryInfo(topK);
  }

  private static void refreshSession() {
    session.close();
    session = SqlTools.getSession();
    mapper = session.getMapper(MetricInfoMapper.class);
  }
}
