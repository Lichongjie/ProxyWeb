package sql.mapper;

import sql.bean.MetricInfoBean;
import org.apache.ibatis.annotations.Param;
import sql.bean.MetricSumInfoBean;

import java.sql.Date;
import java.util.List;

public interface MetricInfoMapper {

  public MetricInfoBean selectMetricInfoByIdAndDate(@Param("date")Date date,
      @Param("serverId")int serverId) throws Exception;

  public int insertMetricInfo(MetricInfoBean metricInfo) throws Exception;

  public int updateMetricInfo(@Param("MetricInfoBean")MetricInfoBean metricInfo,
      @Param("date")Date date, @Param("serverId")int serverId) throws Exception;

  public MetricSumInfoBean selectMetricInfoByDate(@Param("date")Date date) throws Exception;

  public MetricSumInfoBean selectMetricInfo() throws Exception;

  public List<MetricSumInfoBean> selectHistoryMetricInfo(@Param("begin")Date beginDate,
      @Param("end")Date endDate) throws Exception;

}
