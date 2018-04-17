package com.htsc.alluxioproxy.sql.mapper;


import com.htsc.alluxioproxy.sql.bean.MetricInfoBean;
import org.apache.ibatis.annotations.Param;

import java.sql.Date;

/** Mapper class for table metricinfo needed by mybatis. */
public interface MetricInfoMapper {

  public int insertMetricInfo(MetricInfoBean metricInfo) throws Exception;

  public int updateMetricInfo(@Param("MetricInfoBean") MetricInfoBean metricInfo,
                              @Param("date") Date date, @Param("serverId") int serverId) throws Exception;

  public MetricInfoBean selectMetricInfo(@Param("date") Date date, @Param("serverId") int id);

  public int isExistMetricInfo(@Param("date") Date date, @Param("serverId") int id);

  public int createNewTable() throws Exception;

  public int addId() throws Exception;

}
