package sql.mapper;

import sql.bean.FileInfoBean;
import org.apache.ibatis.annotations.Param;
import sql.bean.MetricSumInfoBean;

import java.io.File;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

public interface FileInfoMapper {

  public FileInfoBean selectFileInfoByFileId(String id) throws Exception;

  public int insertFileInfo(FileInfoBean fileInfo) throws Exception;

  public int updateFileInfo(@Param("FileInfo")FileInfoBean fileInfo, @Param("id")String id) throws Exception;

  public List<FileInfoBean> selectTranscodeFailedFile() throws Exception;

  public List<FileInfoBean> selectUnArchiveFiles(@Param("begin")Timestamp beginDate,
                                                @Param("end")Timestamp endDate) throws Exception;

  public List<FileInfoBean> selectNeedDeleteFiles(@Param("begin")Timestamp beginDate,
                                                  @Param("end")Timestamp endDate) throws Exception;

  public int alterTableName() throws Exception;

  public List<FileInfoBean> initFileInfo(@Param("topK")int topK) throws Exception;
}
