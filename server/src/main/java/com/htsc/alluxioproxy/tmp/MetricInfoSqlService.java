package com.htsc.alluxioproxy.tmp;

import com.htsc.alluxioproxy.sql.SqlTmp;
import com.htsc.alluxioproxy.sql.bean.MetricInfoBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The com.htsc.alluxioproxy.sql.service class of writing tmp file about metricinfo com.htsc.alluxioproxy.sql.service.*/
public class MetricInfoSqlService {
  private static SqlManager mSqlManger = SqlManager.INSTANCE;
  private static final String SERVICE_NAME = "MetricInfoSqlDatabaseService";
  private static final Logger LOG = LoggerFactory.getLogger(MetricInfoSqlService.class);


  /**
   * Writes inserting metricinfo sql.sql operation to tmp file
   *
   * @param infoBean the data need to be insert
   * @throws Exception if error happened when writing tmp file
   */
  public static void insertMetricInfo(MetricInfoBean infoBean) throws Exception{
    String id = infoBean.getDate().toString() + infoBean.getServerId();
    SqlTmp tmp = new SqlTmp(SERVICE_NAME, "insertMetricInfo",infoBean,id);
    mSqlManger.write(tmp);
  }

  /**
   * Writes metricinfo sql.sql operation to tmp file
   *
   * @param infoBean the data need to be update
   * @throws Exception if error happened when writing tmp file
   */
  public static void updateMetricInfo(MetricInfoBean infoBean) throws Exception {
    String id = infoBean.getDate().toString() + infoBean.getServerId();
    SqlTmp tmp = new SqlTmp(SERVICE_NAME, "updateMetricInfo",infoBean,id);
    mSqlManger.write(tmp);
  }
}
