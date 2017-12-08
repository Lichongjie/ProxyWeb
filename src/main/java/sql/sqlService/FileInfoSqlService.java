package sql.sqlService;

import sql.bean.FileInfoBean;
import sql.mapper.FileInfoMapper;
import org.apache.ibatis.session.SqlSession;

import java.io.File;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

public class FileInfoSqlService {
  public static final SimpleDateFormat fileInfoDateFormat =
      new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private static final SqlSession session = SqlTools.getSession();
  private static final FileInfoMapper mapper = session.getMapper(FileInfoMapper.class);

  private static void insertFileInfo() {
    try {
      java.util.Date d1 = fileInfoDateFormat.parse("2017-12-3 10:0:0");
      FileInfoBean info = new FileInfoBean("id3",new Timestamp(d1.getTime()),false,
          false, "unknown", true, true);
      mapper.insertFileInfo(info);
      session.commit();
    } catch (Exception e) {
      e.printStackTrace();
      session.rollback();
    }
  }

  private static void updateFileInfo(FileInfoBean fileInfo, String id) {
    try {
      mapper.updateFileInfo(fileInfo, id);
    } catch (Exception e) {
      e.printStackTrace();
      session.rollback();
    }
  }

  public static FileInfoBean selectFileInfoByFileId(String id)  throws Exception {
      return mapper.selectFileInfoByFileId(id);

  }

  public static List<FileInfoBean> selectTranscodeFailedFile() throws Exception {
      return mapper.selectTranscodeFailedFile();
  }

  public static List<FileInfoBean> selectUnArchiveFiles(Timestamp begin, Timestamp end) throws Exception {

    return mapper.selectUnArchiveFiles(begin, end);

  }

  public static List<FileInfoBean> selectNeedDeleteFiles(Timestamp begin, Timestamp end)
          throws Exception {
    return mapper.selectNeedDeleteFiles(begin, end);
  }

  public static void alterTableName() throws Exception {
     mapper.alterTableName();
  }
  public static List<FileInfoBean> initFileInfo(int topK)
          throws Exception {
    return mapper.initFileInfo(topK);
  }


  public static void main(String[] args) throws Exception {
    FileInfoSqlService.insertFileInfo();
    //java.util.Date d1 = fileInfoDateFormat.parse("2017-12-2 22:0:0");
    //FileInfoBean info = new FileInfoBean("id1",new Date(d1.getTime()),false,
   //         false, "unknown", false);
   // FileInfoSqlService.updateFileInfo(info, "id1");
    //System.out.print(selectUnArchiveFiles().toString());

  }
}
