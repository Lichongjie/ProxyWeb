package com.htsc.alluxioproxy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htsc.alluxioproxy.sql.service.FileInfoSqlDatabaseService;
import com.htsc.alluxioproxy.sql.util.Configuration;
import com.htsc.alluxioproxy.sql.util.PathUtils;
import com.htsc.alluxioproxy.tmp.FileInfoSqlService;
import com.htsc.alluxioproxy.tmp.MetricInfoManager;
import com.htsc.alluxioproxy.sql.bean.FileInfoBean;
import com.htsc.alluxioproxy.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.htsc.alluxioproxy.util.Constants.DATA_DIR;

/**
 * The root resource of the storage com.htsc.alluxioproxy.sql.service from proxy.
 */
@Path("/")
public class ProxyResource extends BaseResource {
  private static final Logger LOG = LoggerFactory.getLogger(ProxyResource.class);
  private static final int SERVER_ID = mConf.getInt(Constants.SERVER_ID);
  private static final String ARCHIVE_PATH = Configuration.INSTANCE.getString(Constants.ALLUXIO_ARCHIVE_DIR);

  /**
   * @return server info
   */
  @GET
  @Path("serverInfo")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getServerInfo() {
    List<String> info = getDiskCapacity();
    JSONObject res = new JSONObject();
    res.put("id", SERVER_ID);
    res.put("capacity", info.get(0));
    res.put("used", info.get(1));
    res.put("notUsed", info.get(2));
    res.put("name", "main" + SERVER_ID);
    res.put("usedBar", Integer.parseInt(info.get(3)));
    res.put("notUsedBar", Integer.parseInt(info.get(4)));
    res.put("archivePath", ARCHIVE_PATH);

    return Response.ok().entity(res.toJSONString()).type(MediaType.APPLICATION_JSON)
        .build();
  }

  /**
   * ReTranscode a file.
   *
   * @param id file id
   * @param transcodeFormat the transcode format of file
   * @return an http response with the result of the reTranscode operation
   */
  @POST
  @Path("reTranscode")
  @SuppressWarnings("unchecked")
  public Response reTranscode(@FormParam("fileId")String id, @FormParam("transcodeFormat")
      String transcodeFormat) {
    try {
      LOG.info("reTranscode file id {}, format {}", id, transcodeFormat);
      TranscodeRequester requester = new TranscodeRequester(id, transcodeFormat);
      requester.transcode();
      MetricInfoManager.updateReTranscodeData();
      return Response.ok(id).type(MediaType.TEXT_PLAIN).build();
    } catch(RuntimeException e) {
      LOG.error("Failed to reTranscode {}", id);
      return generateErrorResponse(e);
    }
  }

  /**
   * Archive a file.
   *
   * @return an http response with the result of the archive operation
   */
  @POST
  @Path("archive")
  public Response archive(@FormParam("fileId") String path) {
    File archiveFile = new File(FileURIGenerator.getArchivePath(path));
    LOG.info("start to archive file {}", archiveFile.getAbsolutePath());
    try {
      if(archiveFile.exists()) {
        FileInfoBean bean = FileInfoSqlDatabaseService.selectFileInfoById(path);
        if(!bean.isArchive()) {
          FileInfoSqlService.updateArchiveInfo(path);
        }
        LOG.info("file already archived");
        return Response.ok(path).type(MediaType.TEXT_PLAIN).build();
      }
      if(!archiveFile.getParentFile().exists()) {
        PathUtils.mkdirs(archiveFile.getParentFile());
      }

      InputStream in = new BufferedInputStream(getDownloadStream(path));
      writeTmpFile(in, archiveFile);
      FileInfoSqlService.updateArchiveInfo(path);
      LOG.info("succeed archive file {}", path);
      return Response.ok(path).type(MediaType.TEXT_PLAIN).build();
    } catch (Exception e){
      if (archiveFile.exists() && !archiveFile.delete()) {
        LOG.warn("Failed to delete uncompleted tmp file {}", archiveFile);
      }
      return generateErrorResponse(e);
    }
  }

  /**
   * delete a archived file
   *
   * @param path the file id
   * @return an http response with the result of the delete operation
   */
  @POST
  @Path("delete")
  @SuppressWarnings("unchecked")
  public Response delete(@FormParam("fileId")String path)  {
    File originalFile = new File(FileURIGenerator.getUfsPath(path));
    File archivedFile = new File(FileURIGenerator.getArchivePath(path));
    LOG.info("Start to delete file {}", path);
    if(!originalFile.exists() && archivedFile.exists()) {
      try {
        FileInfoSqlService.updateMoveInfo(path);
      } catch (Exception e) {
        LOG.error("failed to write Tmp file when updating file {}'s unArchived info", path);
      }
      return Response.ok(path).type(MediaType.TEXT_PLAIN).build();
    }
    if(!archivedFile.exists()) {
      LOG.error("file {} hasn't archived!", path);
      try {
        FileInfoSqlService.updateUnArchiveInfo(path);
      } catch (Exception e) {
        LOG.error("failed to write Tmp file when updating file {}'s unArchived info", path);
      }
      return generateErrorResponse(new Exception());
    }
    File needDeleteFile = originalFile.getParentFile();
    if(!originalFile.delete()) {
      String errorMsg = "Failed to delete the archived file "+ path;
      LOG.error(errorMsg);
      return generateErrorResponse(new Exception(errorMsg));
    }
    // delete empty directory
    while((needDeleteFile.listFiles() == null || needDeleteFile.listFiles().length == 0) &&
        !("/" + needDeleteFile.getName()).equals(DATA_DIR) ) {
      File tmp = needDeleteFile.getParentFile();
      if(needDeleteFile.delete()) {
        needDeleteFile = tmp;
      } else {
        LOG.error("failed to delete empty directory {}" + needDeleteFile.getAbsolutePath());
        break;
      }
    }
    try {
      FileInfoSqlService.updateMoveInfo(path);
    } catch (Exception e) {
      LOG.error("failed to write Tmp file when updating file {}'s moved info", path);
    }
    return Response.ok(path).type(MediaType.TEXT_PLAIN).build();
  }

  /**
   * @return server metric info
   */
  private List<String> getDiskCapacity() {
    Process pro = null;
    Process pro2 = null;
    Runtime r = Runtime.getRuntime();
    String capacity = null;
    String used = null;
    String notUse =null;
    String usedBar = null;
    String notUsedBar = null;
    try {
      String UFS_DIR = Configuration.INSTANCE.getString(Constants.ALLUXIO_UFS_DIR);
      String command = "df -ah " + UFS_DIR;
      pro = r.exec(command);
      BufferedReader in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
      String line = null;
      String tmpline;
      while((tmpline = in.readLine()) != null) {
        line = tmpline;
      }
      capacity = line.split("\\s+")[1];
      used = line.split("\\s+")[2];
      notUse = line.split("\\s+")[3];
      in.close();
      pro.destroy();

      command = "df -a " + UFS_DIR;
      pro2 = r.exec(command);
      in = new BufferedReader(new InputStreamReader(pro2.getInputStream()));
      line = null;
      while((tmpline = in.readLine()) != null) {
        line = tmpline;
      }
      usedBar = line.split("\\s+")[2];
      notUsedBar = line.split("\\s+")[3];

      in.close();
      pro2.destroy();

    } catch (IOException e) {
      LOG.error("Failed to get disk IO usage", e.getMessage());
      e.printStackTrace();
    }
    List<String> res = new ArrayList<>();
    res.add(capacity);
    res.add(used);
    res.add(notUse);
    res.add(usedBar);
    res.add(notUsedBar);
    return res;
  }

  @POST
  @Path("oneFileInfo")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAFileInfo(@FormParam("fileId")String path) {
    LOG.info("get one file info {}", path);
    FileInfoBean bean = getOneFileInfo(path);
    String res= JSON.toJSONString(bean);
    return Response.ok().entity(res).type(MediaType.APPLICATION_JSON)
        .build();
  }

  @POST
  @Path("fileInfo")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getFileInfo(@FormParam("fileId")String path) {
    LOG.info("get file info {}", path);
    String[] fileIds = path.trim().split(";");
    List<FileInfoBean> res = new ArrayList<>();

    for(String fileId : fileIds) {
      res.add(getOneFileInfo(fileId.trim()));
    }

    JSONObject result = new JSONObject();
    JSONArray jj = new JSONArray();
    jj.addAll(res);
    result.put("root", jj);

    return Response.ok().entity(result.toJSONString()).type(MediaType.APPLICATION_JSON)
        .build();
  }

  public FileInfoBean getOneFileInfo(String fileId) {
    FileInfoBean.Builder builder = new FileInfoBean.Builder();
    builder.fileId(fileId);
    String archivePath = FileURIGenerator.getArchivePath(fileId);
    String ufsPath = FileURIGenerator.getUfsPath(fileId);
    if(new File(archivePath).exists()) {
      builder.isArchive(true);
      if(!new File(ufsPath).exists()) {
        builder.isMove(true);
      }
    }
    if(new File(ufsPath).exists()) {
      try {
        java.nio.file.Path p = java.nio.file.Paths.get(ufsPath);
        BasicFileAttributes att = Files.readAttributes(p, BasicFileAttributes.class);
        builder.Timestamp(new Timestamp(att.creationTime().toMillis()));
      } catch (Exception e) {
        LOG.error("can't get file attribute caused by {}", e.getCause());
      }
    }
    return builder.build();
  }
}
