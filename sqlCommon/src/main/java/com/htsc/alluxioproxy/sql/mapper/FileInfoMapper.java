package com.htsc.alluxioproxy.sql.mapper;

import com.htsc.alluxioproxy.sql.bean.FileInfoBean;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.sql.Timestamp;

/** Mapper class for table fileinfo needed by mybatis. */
public interface FileInfoMapper {

  public int insertFileInfo(FileInfoBean fileInfo) throws Exception;

  public int updateFileInfo(@Param("FileInfo") FileInfoBean fileInfo, @Param("id") String id) throws Exception;

  public FileInfoBean selectFileInfoById(@Param("id") String fileId) throws Exception;

  public int updateTranscodeInfo(@Param("id") String id) throws Exception;

  public int updateArchiveInfo(@Param("id") String id) throws Exception;

  public int updateMoveInfo(@Param("id") String id) throws Exception;

  public int selectUnarchivedFile(@Param("begin") Timestamp beginTime, @Param("end") Timestamp
      endTime);

  public BigDecimal getArchivedFileNum() throws Exception;

  public int updateUnArchiveInfo(@Param("id") String id) throws Exception;

  public int createNewTable() throws Exception;

}
