package sql.sqlService;

import sql.bean.MetricInfoBean;
import sql.bean.MetricSumInfoBean;
import sql.mapper.MetricInfoMapper;
import org.apache.ibatis.session.SqlSession;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

public class MetricInfoSqlService {
  public static final SimpleDateFormat MetricInfoDateFormat =
          new SimpleDateFormat("yyyy-MM-dd");
  private static final SqlSession session = SqlTools.getSession();
  private static final MetricInfoMapper mapper = session.getMapper(MetricInfoMapper.class);

  private static void insertMetricInfo() {
    try {
      java.util.Date d1 = MetricInfoDateFormat.parse("2017-12-04");
      MetricInfoBean info = new MetricInfoBean(new Date(d1.getTime()),2,1,
              1,4,4,1,
              2,55,1,1,
              0);
      mapper.insertMetricInfo(info);
      session.commit();
    } catch (Exception e) {
      e.printStackTrace();
      session.rollback();
    }
  }

  private static void updateMetricInfo(MetricInfoBean metricInfo, Date date, int serverId) {
    try {
      mapper.updateMetricInfo(metricInfo, date, serverId);
      session.commit();
    } catch (Exception e) {
      e.printStackTrace();
      session.rollback();
    }
  }

  public static MetricInfoBean selectMetricInfoByIdAndDate(Date date, int serverId) {
    try {
      return mapper.selectMetricInfoByIdAndDate(date, serverId);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  public static MetricSumInfoBean selectMetricInfoByDate() throws Exception {
   // String date = MetricInfoDateFormat.format(new java.util.Date());
    String date = "2017-12-02";
    return mapper.selectMetricInfoByDate(Date.valueOf(date));

  }

  public static MetricSumInfoBean selectMetricInfo() throws Exception{
      return mapper.selectMetricInfo();
  }

  public static List<MetricSumInfoBean> selectHistoryMetricInfo(Date begin, Date end) throws Exception {
    return mapper.selectHistoryMetricInfo(begin, end);

  }



  public static void main(String []args) throws Exception {

    java.util.Date begin = MetricInfoSqlService.MetricInfoDateFormat.parse("2017-12-01");
    java.util.Date end = MetricInfoSqlService.MetricInfoDateFormat.parse("2017-12-05");
    Date temp = new Date(begin.getTime());
    System.out.print(temp.getTime());
   // updateMetricInfo(info, new Date(d1.getTime()), 1);
    //MetricInfoSqlService.insertMetricInfo();
    //System.out.print(selectMetricInfoByIdAndDate(new Date(d1.getTime()),1));
    //String date = MetricInfoDateFormat.format(new java.util.Date());
   // Date d = Date.valueOf(date);
   // System.out.print(selectHistoryMetricInfo(new Date(begin.getTime()), new Date(end.getTime())));
  }
}
