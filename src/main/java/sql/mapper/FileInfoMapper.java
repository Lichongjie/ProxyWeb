package sql.mapper;

import sql.bean.FileInfoBean;
import org.apache.ibatis.annotations.Param;

import java.io.File;
import java.util.List;

public interface FileInfoMapper {

  public FileInfoBean selectFileInfoByFileId(String id) throws Exception;

  public int insertFileInfo(FileInfoBean fileInfo) throws Exception;

  public int updateFileInfo(@Param("FileInfo")FileInfoBean fileInfo, @Param("id")String id) throws Exception;

  public List<FileInfoBean> selectTranscodeFailedFile() throws Exception;

  public List<FileInfoBean> selectUnArchiveFiles() throws Exception;
}
