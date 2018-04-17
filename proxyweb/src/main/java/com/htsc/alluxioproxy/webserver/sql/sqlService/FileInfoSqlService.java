package com.htsc.alluxioproxy.webserver.sql.sqlService;

import com.htsc.alluxioproxy.webserver.sql.bean.FileInfoBean;
import com.htsc.alluxioproxy.webserver.sql.mapper.FileInfoMapper;
import com.htsc.alluxioproxy.webserver.web.rest.WebResource;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Handle fileinfo table sql operation.
 */
public class FileInfoSqlService {
  private static final Logger LOG = LoggerFactory.getLogger(FileInfoSqlService.class);
  public static final SimpleDateFormat fileInfoDateFormat =
      new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private static  SqlSession session = SqlTools.getSession();
  private static  FileInfoMapper mapper = session.getMapper(FileInfoMapper.class);

  private static void updateFileInfo(FileInfoBean fileInfo, String id) {
    try {
      mapper.updateFileInfo(fileInfo, id);
    } catch (Exception e) {
      e.printStackTrace();
      session.rollback();
    }
  }

  /**
   * Select transcode info by date internal.
   *
   * @param begin the begin date
   * @param end the end date
   * @return select result
   * @throws Exception if error happened
   */
  public static List<FileInfoBean> selectTranscodeFiles(Timestamp begin, Timestamp end) throws Exception {
    LOG.info("select unArchived files");
    refreshSession();
    return mapper.selectTranscodeFiles(begin, end);

  }

  /**
   * @return today's transcode info
   * @throws Exception if error happened
   */
  public static List<FileInfoBean> initTranscodeInfo() throws Exception {
    String day = MetricInfoSqlService.MetricInfoDateFormat.format(new java.util.Date());
    String beginDate = day + " 00:00:00";
    String endDate = day + " 23:59:59";
    java.util.Date begin = fileInfoDateFormat.parse(beginDate);
    java.util.Date end = fileInfoDateFormat.parse(endDate);
    return selectTranscodeFiles(new Timestamp(begin.getTime()), new Timestamp(end.getTime()));
  }

  /**
   * Select file info by id
   *
   * @param id the file id
   * @return select result
   * @throws Exception if error happened
   */
  public static FileInfoBean selectFileInfoByFileId(String id)  throws Exception {
    LOG.info("select file info, id is {}", id);
    return mapper.selectFileInfoByFileId(id);
  }

  public static List<FileInfoBean> selectTranscodeFailedFile() throws Exception {
    LOG.info("select Transcode failed files");
    refreshSession();
    return mapper.selectTranscodeFailedFile();
  }

  /**
   * Select need transcode info
   *
   * @return select result
   * @throws Exception if error happened
   */
  public static List<FileInfoBean> selectNeedTranscodeFiles() throws Exception {
    LOG.info("select need Transcode files");
    return mapper.selectNeedTranscodeFiles();

  }

  /**
   * Select un archived info by date internal
   *
   * @param begin
   * @param end
   * @return
   * @throws Exception
   */
  public static List<FileInfoBean> selectUnArchiveFiles(Timestamp begin, Timestamp end) throws Exception {
    LOG.info("select unArchived files");
    refreshSession();
    return mapper.selectUnArchiveFiles(begin, end);

  }

  public static List<FileInfoBean> selectNeedDeleteFiles(Timestamp begin, Timestamp end)
          throws Exception {
    LOG.info("select files needed to delete");
    refreshSession();
    return mapper.selectNeedDeleteFiles(begin, end);
  }

  public static void alterTableName() throws Exception {
     mapper.alterTableName();
  }
  public static List<FileInfoBean> initFileInfo(int topK)
          throws Exception {
    LOG.info("init File info from database");
    refreshSession();
    return mapper.initFileInfo(topK);
  }

  public static List<FileInfoBean> initArchiveInfo(int topK) throws Exception {
    LOG.info("init archive info from database");
    refreshSession();
    return mapper.initArchiveInfo(topK);
  }

  public static List<FileInfoBean> initDeleteInfo(int topK) throws Exception {
    LOG.info("init delete info from database");
    refreshSession();
    return mapper.initDeleteInfo(topK);
  }

  public static BigDecimal archiveNum() throws Exception {
    refreshSession();
    return mapper.archiveNum();
  }

  private static void refreshSession() {
    session.close();
    session = SqlTools.getSession();
    mapper = session.getMapper(FileInfoMapper.class);
  }

  public static void main(String[] args) throws Exception {
    System.out.print(initDeleteInfo(30));
    //com.htsc.alluxioproxy.sql.mapper.selectFileInfoByFileId("id1");
    //java.com.htsc.alluxioproxy.sql.util.Date d1 = fileInfoDateFormat.parse("2017-12-2 22:0:0");
    //FileInfoBean info = new FileInfoBean("id1",new Date(d1.getTime()),false,
   //         false, "unknown", false);
   // FileInfoSqlService.updateFileInfo(info, "id1");
    //System.out.print(selectUnArchiveFiles().toString());

  }
}
