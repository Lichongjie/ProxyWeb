package com.htsc.alluxioproxy.sql.service;

import com.htsc.alluxioproxy.sql.bean.FileInfoBean;
import com.htsc.alluxioproxy.sql.mapper.FileInfoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** Sql operation for table fileinfo. */
public class FileInfoSqlDatabaseService extends DatabaseService {
  private static final Logger LOG = LoggerFactory.getLogger(FileInfoSqlDatabaseService.class);
  public static final SimpleDateFormat fileInfoDateFormat =
      new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private static FileInfoMapper mapper;
  private static final String SERVICE_NAME = "insertFileInfo";

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
   * Insert a record to fileinfo table.
   *
   * @param info the fileinfo record
   * @throws DataBaseException if sql.sql error happened
   */
  public synchronized static void insertFileInfo(Serializable info) throws DataBaseException {
    FileInfoBean infoBean = (FileInfoBean) info;
    LOG.info("start to insert data to table fileinfo {}", infoBean.toString());
    try {
      mapper.insertFileInfo(infoBean);
      LOG.info("insert file info succeed");
    } catch (Exception e) {
      LOG.error("failed to insert data by com.htsc.alluxioproxy.sql.service {}", SERVICE_NAME);
      throw new DataBaseException(e.getMessage());
    }
  }

  /**
   * Select file info.
   *
   * @param fileId the file id
   * @return the selected result
   * @throws Exception if sql.sql error happened
   */
  public synchronized static FileInfoBean selectFileInfoById(String fileId) throws Exception {
    return mapper.selectFileInfoById(fileId);
  }

  /**
   * Update transcode info
   *
   * @param fileId
   * @throws DataBaseException
   */
  public synchronized static void updateTranscodeInfo(String fileId) throws DataBaseException {
    try {
      LOG.info("start to update TranscodeInfo to table fileinfo which id is {}", fileId);
      mapper.updateTranscodeInfo(fileId);
      LOG.info("update TranscodeInfo succeed");
    } catch (Exception e) {
      LOG.error("failed to update Transcode Info data by table fineinfo, id is {}", fileId);
      throw new DataBaseException(e.getMessage());
    }
  }

  /**
   * Update Archive info.
   *
   * @param fileId the file id
   * @throws DataBaseException if sql.sql error happened
   */
  public synchronized static void updateArchiveInfo(String fileId) throws DataBaseException {
    try {
      LOG.info("start to update ArchiveInfo to table fileinfo which id is {}", fileId);
      mapper.updateArchiveInfo(fileId);
      LOG.info("update Archive info succeed");
    } catch (Exception e) {
      LOG.error("failed to update Transcode Info data by com.htsc.alluxioproxy.sql.service {}", SERVICE_NAME);
      throw new DataBaseException(e.getMessage());
    }
  }

  /**
   * Update move info.
   *
   * @param fileId the file id
   * @throws DataBaseException if sql.sql error happened
   */
  public synchronized static void updateMoveInfo(String  fileId) throws DataBaseException {
    try {
      LOG.info("start to update MoveInfo to table fileinfo which id is {}", fileId);
      mapper.updateMoveInfo(fileId);
      LOG.info("update Move Info succeed");
    } catch (Exception e) {
      LOG.error("failed to update Transcode Info data by table {}", fileId);
      throw new DataBaseException(e.getMessage());
    }
  }

  /**
   * Update unArchived info.
   *
   * @param fileId the file id
   * @throws DataBaseException if sql.sql error happened
   */
  public synchronized static void updateUnArchiveInfo(String fileId) throws DataBaseException {
    try {
      LOG.info("start to update unArchivedinfo to table fileinfo which id is {}", fileId);
      mapper.updateUnArchiveInfo(fileId);
      LOG.info("update Move Info succeed");
    } catch (Exception e) {
      LOG.error("failed to update Transcode Info data by table {}", fileId);
      throw new DataBaseException(e.getMessage());
    }
  }

  /**
   * @return current time
   */
  public synchronized static Timestamp getTime() {
    try {
      String now = fileInfoDateFormat.format(new Date());
      Date nowTime = fileInfoDateFormat.parse(now);
      return new Timestamp(nowTime.getTime());
    } catch (ParseException e) {
      //never happen
      return null;
    }
  }

  public synchronized static Timestamp getOriginalTime() {
    try {
      String now = FileInfoSqlDatabaseService.fileInfoDateFormat.format(new Date(0));
      Date nowTime = FileInfoSqlDatabaseService.fileInfoDateFormat.parse(now);
      return new Timestamp(nowTime.getTime());
    } catch (ParseException e) {
      //never happen
      return null;
    }
  }

  /**
   * If todays data archived
   *
   * @param day today
   * @return 1 if archived
   */
  public synchronized static int dayIsUnarchived(String day) {
    try {
      String beginDate = day + " 00:00:00";
      String endDate = day + " 23:59:59";
      Date begin = fileInfoDateFormat.parse(beginDate);
      Date end = fileInfoDateFormat.parse(endDate);
      int unarchivedNum = mapper.selectUnarchivedFile(new Timestamp(begin.getTime()),
          new Timestamp(end.getTime()));
      return unarchivedNum == 0 ? 1 : 0;
    } catch (ParseException e) {
      LOG.error("parse date {} error" + day);
      return 0;
    } catch (Exception e) {
      LOG.error("failed to select unarchived file" + day);
      return 0;
    }
  }

  /**
   * @return archived failed num
   */
  public static BigDecimal getArchivedFileNum() {
    try {
      return mapper.getArchivedFileNum();
    } catch (Exception e) {
      LOG.error("failed to get archived file num");
      return null;
    }
  }

  public static void open() {
    mapper = session.getMapper(FileInfoMapper.class);
  }

}
