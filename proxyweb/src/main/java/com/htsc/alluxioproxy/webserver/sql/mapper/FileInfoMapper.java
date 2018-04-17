package com.htsc.alluxioproxy.webserver.sql.mapper;

import com.htsc.alluxioproxy.webserver.sql.bean.FileInfoBean;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * Mapper interface about fileinfo table sql service
 */
public interface FileInfoMapper {

  public FileInfoBean selectFileInfoByFileId(String id) throws Exception;

  public int insertFileInfo(FileInfoBean fileInfo) throws Exception;

  public int updateFileInfo(@Param("FileInfo") FileInfoBean fileInfo, @Param("id") String id) throws Exception;

  public List<FileInfoBean> selectTranscodeFailedFile() throws
      Exception;
  public List<FileInfoBean> selectNeedTranscodeFiles() throws Exception;


  public List<FileInfoBean> selectUnArchiveFiles(@Param("begin") Timestamp beginDate,
                                                 @Param("end") Timestamp endDate) throws Exception;

  public List<FileInfoBean> selectNeedDeleteFiles(@Param("begin") Timestamp beginDate,
                                                  @Param("end") Timestamp endDate) throws Exception;

  public List<FileInfoBean> selectTranscodeFiles(@Param("begin") Timestamp beginDate,
                                                 @Param("end") Timestamp endDate) throws Exception;

  public int alterTableName() throws Exception;

  public List<FileInfoBean> initFileInfo(@Param("topK") int topK) throws Exception;

  public List<FileInfoBean> initArchiveInfo(@Param("topK") int topK) throws Exception;

  public List<FileInfoBean> initDeleteInfo(@Param("topK") int topK) throws Exception;

  public BigDecimal archiveNum() throws Exception;

}
