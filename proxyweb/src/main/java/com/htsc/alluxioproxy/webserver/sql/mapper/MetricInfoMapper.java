package com.htsc.alluxioproxy.webserver.sql.mapper;

import com.htsc.alluxioproxy.webserver.sql.bean.MetricInfoBean;
import com.htsc.alluxioproxy.webserver.sql.bean.MetricSumInfoBean;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

/**
 * Mapper interface about Metric info table sql service
 */
public interface MetricInfoMapper {

  public MetricInfoBean selectMetricInfoByIdAndDate(@Param("date") Date date,
                                                    @Param("serverId") int serverId) throws Exception;

  public int insertMetricInfo(MetricInfoBean metricInfo) throws Exception;

  public int updateMetricInfo(@Param("MetricInfoBean") MetricInfoBean metricInfo,
                              @Param("date") Date date, @Param("serverId") int serverId) throws Exception;

  public MetricSumInfoBean selectMetricInfoByDate(@Param("date") Date date) throws Exception;

  public MetricSumInfoBean selectMetricInfo() throws Exception;

  public List<MetricSumInfoBean> selectHistoryMetricInfo(@Param("begin") Date beginDate,
                                                         @Param("end") Date endDate) throws Exception;

  public List<MetricSumInfoBean> initHistoryInfo(@Param("topK") int topK) throws Exception;

  public List<MetricSumInfoBean> initHistoryInfoByMonth() throws Exception;

}
