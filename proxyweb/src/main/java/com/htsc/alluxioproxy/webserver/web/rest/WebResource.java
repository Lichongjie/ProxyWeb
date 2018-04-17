package com.htsc.alluxioproxy.webserver.web.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.htsc.alluxioproxy.webserver.sql.bean.FileInfoBean;
import com.htsc.alluxioproxy.webserver.sql.bean.MetricSumInfoBean;
import com.htsc.alluxioproxy.webserver.sql.bean.UserBean;
import com.htsc.alluxioproxy.webserver.sql.sqlService.*;
import com.htsc.alluxioproxy.webserver.utils.Constants;
import com.htsc.alluxioproxy.webserver.utils.Configuration;
import com.htsc.alluxioproxy.webserver.utils.PathUtils;
import com.htsc.alluxioproxy.webserver.web.WebServer;
import com.htsc.alluxioproxy.webserver.web.request.ArchiveRequest;
import com.htsc.alluxioproxy.webserver.web.request.DeleteRequest;
import com.htsc.alluxioproxy.webserver.web.request.ReTranscodeRequest;
import com.htsc.alluxioproxy.webserver.web.request.ServerInfoRequest;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.rmi.runtime.Log;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.*;
import java.io.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Path("/")
public class WebResource {
  private static final Logger LOG = LoggerFactory.getLogger(WebResource.class);

  private ExecutorService queryThreadPool = Executors.newFixedThreadPool(10);
  private ExecutorService TRANSCODE_SERVICE = Executors.newFixedThreadPool(10);
  private ExecutorService ARCHIVE_SERVICE = Executors.newFixedThreadPool(10);

  private static final ConcurrentHashMap<String, UserSessionManager> userMap = new ConcurrentHashMap<>();
  private static final String DOWNLOAD_URL;
  private static final String SERVER_INFO_URL;
  private static final String USERNAME;
  private static final String PASSWD;
  private static final String FILE_INFO_URL;
  private static final int HISTORU_INIT_NUM;
  private static final int FILEINFO_INIT_NUM;
  private static final String DOWNLOAD_TMP_PATH;
  private static final int ARCHIVE_INIT_NUM;
  private static final int DELETE_INIT_NUM;

  private static String ARCHIVE_PATH = null;

  private static final String REALM = "proxy web authentication";
  private static final boolean FILE_INIT_ALLOW;
  private static final int FILE_DOWNLOAD_NUM;


  static {
    Configuration conf = Configuration.INSTANCE;
    String baseUrl = conf.getString(Constants.STORAGE_SERVICE_URL);
    USERNAME = conf.getString(Constants.USERNAME);
    PASSWD = conf.getString(Constants.PASSWORD);
    if (!baseUrl.endsWith("/")) {
      baseUrl += "/";
    }
    DOWNLOAD_URL = baseUrl + "download";
    SERVER_INFO_URL = baseUrl + "serverInfo";
    FILE_INFO_URL = baseUrl +"oneFileInfo";

    HISTORU_INIT_NUM = conf.getInt(Constants.HISTORU_INIT_NUM);
    FILEINFO_INIT_NUM = conf.getInt(Constants.FILEINFO_INIT_NUM);
    DOWNLOAD_TMP_PATH = conf.getString(Constants.TMP_PATH);
    ARCHIVE_INIT_NUM = conf.getInt(Constants.ARCHIVE_INIT_NUM);
    DELETE_INIT_NUM = conf.getInt(Constants.DELETE_INIT_NUM);
    FILE_INIT_ALLOW = conf.getBoolean(Constants.FILE_INIT_ALLOW);
    FILE_DOWNLOAD_NUM = conf.getInt(Constants.FILE_DOWNLOAD_NUM);
  }

  private String getUserKey(HttpServletRequest request) {
    String Agent = request.getHeader("User-Agent");
    return request.getRemoteHost() + ":" + Agent;
  }

  private UserSessionManager getUserManager(HttpServletRequest request) {
    String addr = getUserKey(request);
    if(!userMap.containsKey(addr)) {
      userMap.put(addr, new UserSessionManager());
    }
    return userMap.get(addr);
  }

  @POST
  @Path("login")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response login(@Context HttpServletRequest request,
                        @FormParam("userName") String userName,
                        @FormParam("password") String password) {

    UserSessionManager manager = new UserSessionManager();
    String key = getUserKey(request);
    userMap.put(key, manager);
    LOG.info("login {} {} {}", userName, password, key);
    try {
      UserBean bean = UserSqlService.getUser(userName);
      String defaultPassword = bean.getPassword();
      if(!password.equals(defaultPassword)){
        LOG.error("wrong password");
        throw new AuthenticationException("");
      }
      UserSessionManager.UserLevel userLevel;
      if(userName.equals("admin")) {
        userLevel = UserSessionManager.UserLevel.ADMIN;
      } else {
        userLevel = UserSessionManager.UserLevel.NORMAL;
      }
      manager.addUserLevelInfo(getUserKey(request), userLevel);
      JSONObject result = new JSONObject();
      result.put("level", userName);
      return Response.ok().entity(result.toJSONString()).build();
    } catch (Exception e) {
      LOG.error("failed to login");
      return generateErrorResponse(e);
    }
  }

  @POST
  @Path("authentication")
  @Produces(MediaType.APPLICATION_JSON)
  public Response authentication(@Context HttpServletRequest request,
                                 String pageNm) {

    String page = pageNm.split("=")[1];
    String addr = request.getRemoteHost() + ":" + request.getHeader("User-Agent");
    JSONObject result = new JSONObject();
    if(!userMap.containsKey(addr)) {
      result.put("result", "false");
    } else {
      UserSessionManager userManager = userMap.get(addr);
      if(userManager.authentic(page, addr)) {

        if(userManager.loginTimeOutCheck()) {
          result.put("result", "timeout");
          userMap.remove(addr);
        } else {
          if(page.equals("index")){
            if(userManager.USER_SESSION.get(addr) == UserSessionManager.UserLevel.ADMIN){
              result.put("result", "admin");
            } else {
              result.put("result", "download");
            }
          } else {
            result.put("result", "true");
          }
        }
      } else {
        result.put("result", "false");
      }
    }
    return Response.ok().entity(result.toJSONString()).build();
  }

  @GET
  @Path("logOut")
  public Response logOut(@Context HttpServletRequest request) {
    String addr = request.getRemoteHost() + ":" + request.getHeader("User-Agent");
    userMap.remove(addr);
    return Response.ok().build();
  }


  @GET
  @Path("MetricInfo")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getMetricInfo() {
    try {
      MetricSumInfoBean dayInfoRes = MetricInfoSqlService.selectMetricInfoByDate();
      MetricSumInfoBean allInfoRes = MetricInfoSqlService.selectMetricInfo();
      BigDecimal archiveNum = FileInfoSqlService.archiveNum();

      JSONObject res = new JSONObject();

      res.put("dayInfo", dayInfoRes);
      res.put("allInfo", allInfoRes);
      res.put("archiveNum",archiveNum.toString());
      return Response.ok().entity(res.toJSONString()).build();
    } catch (Exception e) {
      LOG.error("get Metric Info failed");
      e.printStackTrace();
      LOG.error("reason: {}", e.getMessage() + e.getCause());
      return generateErrorResponse(new DataBaseException(e.getMessage()));
    }
  }
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
      LOG.info("test line1 {}", line);

      capacity = line.split("\\s+")[1];
      used = line.split("\\s+")[2];
      notUse = line.split("\\s+")[3];
      LOG.info("test {}, {}, {}", capacity, used, notUse);
      in.close();
      pro.destroy();

      command = "df -a " + UFS_DIR;
      pro2 = r.exec(command);
      in = new BufferedReader(new InputStreamReader(pro2.getInputStream()));
      line = null;
      while((tmpline = in.readLine()) != null) {
        line = tmpline;
      }
      LOG.info("test line {}", line);
      usedBar = line.split("\\s+")[2];
      notUsedBar = line.split("\\s+")[3];
      LOG.info("test1 {}, {}", usedBar, notUsedBar);

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


  @GET
  @Path("serverInfo")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getServerMetricInfo() {
    /*
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(SERVER_INFO_URL);
    Response response =  target.request().get();
    */

    List<String> info = getDiskCapacity();
    JSONObject res = new JSONObject();
    res.put("id", "1");
    res.put("capacity", info.get(0));
    res.put("used", info.get(1));
    res.put("notUsed", info.get(2));
    res.put("name", "main" + "1");
    res.put("usedBar", Long.parseLong(info.get(3)));
    res.put("notUsedBar", Long.parseLong(info.get(4)));
    res.put("archivePath", ARCHIVE_PATH);
    if(ARCHIVE_PATH == null) {
      ARCHIVE_PATH =(String)res.get("archivePath");
    }
    LOG.info("json info {}", res);

    return Response.ok().entity(res.toJSONString()).build();
  }

  @POST
  @Path("history")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response getHistoryInfo(@Context HttpServletRequest request,
                                 @FormParam("beginDate") String beginDate,
                                 @FormParam("endDate") String endDate) {
    String addr = request.getRemoteHost() + ":" + request.getRemotePort();
    LOG.info("start to get metric history info, begin date is {}, end date is {}", beginDate,
        endDate);
    UserSessionManager userManager = getUserManager(request);

    userManager.clearHistoryInfo(addr);

    try {
      java.util.Date begin = MetricInfoSqlService.MetricInfoDateFormat.parse(beginDate);
      java.util.Date end = MetricInfoSqlService.MetricInfoDateFormat.parse(endDate);

      if(begin.after(end)) {
        throw new Exception("date internal is invalid");
      }

      List<MetricSumInfoBean> res = MetricInfoSqlService.selectHistoryMetricInfo(
              new Date(begin.getTime()), new Date((end.getTime())));
      userManager.addHistoryInfo(addr, res);
      JSONObject result = generateJsonResult(res);

      return Response.ok().entity(result.toJSONString()).build();
    } catch (Exception e) {
      LOG.error("get metric history info failed");
      return generateErrorResponse(e);
    }
  }

  @GET
  @Path("initHistoryInfo")
  @Produces(MediaType.APPLICATION_JSON)
  public Response initHistoryInfo(@Context HttpServletRequest request) {
    LOG.info("start to init history info");
    String addr = request.getRemoteHost() + ":" + request.getRemotePort();
    UserSessionManager userManager = getUserManager(request);

    userManager.clearHistoryInfo(addr);
    try {
      List<MetricSumInfoBean> res = MetricInfoSqlService.initHistoryInfo(HISTORU_INIT_NUM);
      userManager.addHistoryInfo(addr, res);
      JSONObject result = generateJsonResult(res);
      return Response.ok().entity(result.toJSONString()).build();
    } catch (Exception e) {
      LOG.error("failed to get history init info");
      return generateErrorResponse(e);
    }
  }

  @POST
  @Path("jumpHistoryInfo")
  @Produces(MediaType.APPLICATION_JSON)
  public Response jumpHistoryInfo(@Context HttpServletRequest request, String indexNum) {
    String index = indexNum.split("=")[1];
    String addr = request.getRemoteHost() + ":" + request.getRemotePort();
    UserSessionManager userManager = getUserManager(request);
    UserSessionManager.HistroyInfoBean bean = userManager.getHistoryInfo(addr);
    return jumpFileInfo(index, bean);
  }

  @GET
  @Path("initTranscodeInfo")
  @Produces(MediaType.APPLICATION_JSON)
  public Response initTranscodeInfo(@Context HttpServletRequest request) {
    LOG.info("start to get files  transcode init info");
    String addr = request.getRemoteHost() + ":" + request.getRemotePort();
    UserSessionManager userManager = getUserManager(request);

    userManager.clearTranscodeInfo(addr);
    try {
      List<FileInfoBean> res = FileInfoSqlService.initTranscodeInfo();
      JSONObject result = generateJsonResult(res);
      userManager.addTranscodeInfo(addr, res);
      return Response.ok().entity(result.toJSONString()).build();
    } catch (Exception e) {
      LOG.error("failed to get files transcode init info");
      return generateErrorResponse(e);
    }
  }

  @POST
  @Path("transcodeQuery")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response transcodeQuery(@Context HttpServletRequest request,
                                   @FormParam("beginDate") String beginDate,
                                   @FormParam("endDate") String endDate) {
    beginDate += " 00:00:00";
    endDate += " 23:59:59";
    LOG.info("start to query transcode file, begin date is {}, end date is {}",
        beginDate, endDate);
    String addr = request.getRemoteHost() + ":" + request.getRemotePort();
    UserSessionManager userManager = getUserManager(request);

    userManager.clearTranscodeInfo(addr);

    try {
      java.util.Date begin = FileInfoSqlService.fileInfoDateFormat.parse(beginDate);
      java.util.Date end = FileInfoSqlService.fileInfoDateFormat.parse(endDate);
      if (begin.compareTo(end) > 0) {
        throw new Exception("date interval wrong");
      }
      List<FileInfoBean> res = FileInfoSqlService.selectTranscodeFiles(
          new Timestamp(begin.getTime()), new Timestamp(end.getTime()));
      JSONObject result = generateJsonResult(res);
      userManager.addTranscodeInfo(addr, res);
      return Response.ok().entity(result.toJSONString()).build();
    } catch (Exception e) {
      LOG.error("failed to query transcode info");
      return generateErrorResponse(e);
    }
  }

  @GET
  @Path("getToTranscode")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getTotranscodeFiles(@Context HttpServletRequest request) {
    LOG.info("start to get toTranscode files");
    String addr = request.getRemoteHost() + ":" + request.getRemotePort();
    UserSessionManager userManager = getUserManager(request);

    userManager.clearTranscodeInfo(addr);

    try {
      List<FileInfoBean> res = FileInfoSqlService.selectNeedTranscodeFiles();
      userManager.addTranscodeInfo(addr, res);
      JSONObject result = generateJsonResult(res);
      return Response.ok().entity(result.toJSONString()).build();
    } catch (Exception e) {
      LOG.error("get totranscode files failed");
      return generateErrorResponse(e);
    }
  }

  @POST
  @Path("jumpTranscodeInfo")
  @Produces(MediaType.APPLICATION_JSON)
  public Response jumpTranscodeInfo(@Context HttpServletRequest request,String indexNum) {
    String index = indexNum.split("=")[1];
    String addr = request.getRemoteHost() + ":" + request.getRemotePort();
    UserSessionManager userManager = getUserManager(request);

    UserSessionManager.FileInfoPageBean bean = userManager.getTranscodeInfo(addr);
    return jumpFileInfo(index, bean);
  }

  @POST
  @Path("reTranscode")
  @SuppressWarnings("unchecked")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response reTranscode(@Context HttpServletRequest request,String infos) {
    String[] fileInfo = infos.split(";");
    List<Future<String>> futures = new ArrayList<Future<String>>();
    HashMap<String, FileInfoBean> resMap = new HashMap<>();
    LOG.info("start to retranscode file {}",fileInfo);
    try {
      String addr = request.getRemoteHost() + ":" + request.getRemotePort();
      UserSessionManager userManager = getUserManager(request);

      UserSessionManager.FileInfoPageBean pagebean = userManager.getTranscodeInfo(addr);
      List<FileInfoBean> allFileInfo = pagebean.getResult();
      for(FileInfoBean bean : allFileInfo) {
        resMap.put(bean.getFileId(), bean);
      }
      HashSet<String> neededFiles = new HashSet<>();
      for (String info : fileInfo) {
        String id = info.split(":")[0];
        String format = info.split(":")[1];
        neededFiles.add(id);
        futures.add(TRANSCODE_SERVICE.submit(new ReTranscodeRequest(id, format)));
      }
      for (Future<String> future : futures) {
          String resId = future.get();
          if (resId.length() > 0) {
            resMap.remove(resId);
            neededFiles.remove(resId);
          }
      }

      List<FileInfoBean> resList = new ArrayList<>();
      for(Map.Entry<String, FileInfoBean> entry : resMap.entrySet()) {
        resList.add(entry.getValue());
      }

      userManager.addTranscodeInfo(addr, resList);
      JSONObject lastObj = new JSONObject();
      JSONArray jj = new JSONArray();
      jj.addAll(resList);
      JSONArray jj2 = new JSONArray();
      jj2.addAll(neededFiles);
      lastObj.put("root", jj);
      lastObj.put("failed",jj2);

      return Response.ok().type(MediaType.APPLICATION_JSON).entity(lastObj.toJSONString())
          .build();
    } catch (Exception e) {
      return generateErrorResponse(e);
    }
  }

  private List<FileInfoBean> selectFiles(String fileId) throws Exception{
    String[] fileIds = fileId.split(";");
    if (fileIds.length == 0) {
      throw new NoContentException("no files to show");
    }
    List<Future<FileInfoBean>> futures = new ArrayList<Future<FileInfoBean>>();
    List<FileInfoBean> res = new ArrayList<FileInfoBean>();
    for (final String id : fileIds) {
      futures.add(queryThreadPool.submit(new Callable<FileInfoBean>() {
        public FileInfoBean call() throws Exception {
          return FileInfoSqlService.selectFileInfoByFileId(id);
        }
      }));
    }
    for (Future<FileInfoBean> future : futures) {
      res.add(future.get());
    }
    return res;
  }

  @POST
  @Path("fileInfo")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response getFileInfo(@Context HttpServletRequest request,
                              @FormParam("fileId") String fileId) {
    LOG.info("start to get files info {}", fileId);
    String addr = request.getRemoteHost() + ":" + request.getRemotePort();
    try {
      List<FileInfoBean> res = new ArrayList<>();
      //JSONObject result = generateJsonResult(res);

      //mUserManager.addDownloadInfo(addr, res);
      String[] fileIds = fileId.split(";");
      JSONObject result = new JSONObject();
      JSONArray jj = new JSONArray();
      for(String id : fileIds) {
        FileInfoBean bean = null;
        Response response;
        try {
          bean = FileInfoSqlService.selectFileInfoByFileId(id.trim());
          jj.add(bean);
        } catch (Exception e) {
        }
        if (bean == null) {
          Client client = ClientBuilder.newClient();
          response = client.target(FILE_INFO_URL).request().post(Entity.form
              (new Form().param("fileId", id)));
          String entity = response.readEntity(String.class);
          bean = JSON.parseObject(entity,FileInfoBean.class);
          jj.add(bean);
        }
      }
      result.put("root", jj);

      return Response.ok().type(MediaType.APPLICATION_JSON).entity(result.toJSONString())
          .build();
    } catch (Exception e) {
      LOG.info("{}", e.getMessage());
      return generateErrorResponse(e);
    }
  }

  @GET
  @Path("initFileInfo")
  @Produces(MediaType.APPLICATION_JSON)
  public Response initFileInfo(@Context HttpServletRequest request) {
    LOG.info("start to get files init info");
    String addr = request.getRemoteHost() + ":" + request.getRemotePort();
    UserSessionManager userManager = getUserManager(request);

    userManager.clearDownloadInfo(addr);
    try {
      if(FILE_INIT_ALLOW) {
        LOG.info("init test begin " );
        List<FileInfoBean> res = FileInfoSqlService.initFileInfo(FILEINFO_INIT_NUM);
        LOG.info("init test {}", res);
        JSONObject result = generateJsonResult(res);
        userManager.addDownloadInfo(addr, res);
        return Response.ok().entity(result.toJSONString()).build();
      } else {
        return Response.ok().entity(generateJsonResult(null).toJSONString()).build();
      }
    } catch (Exception e) {
      LOG.error("failed to get files init info, {}" , e.getMessage());
      e.printStackTrace();
      return generateErrorResponse(e);
    }
  }


  @POST
  @Path("jumpDownloadInfo")
  @Produces(MediaType.APPLICATION_JSON)
  public Response jumpDownloadInfo(@Context HttpServletRequest request,String indexNum) {
    String index = indexNum.split("=")[1];
    String addr = request.getRemoteHost() + ":" + request.getRemotePort();
    UserSessionManager userManager = getUserManager(request);

    UserSessionManager.FileInfoPageBean bean = userManager.getDownloadInfo(addr);
    return jumpFileInfo(index, bean);
  }

  /*
  @GET
  @Path("download")
  public static Response download(@Param("id") String fileid) {
    LOG.info("start to download file, id is {}", fileid);

    Form form = new Form();

    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(DOWNLOAD_URL);

    form.param("username", USERNAME);
    form.param("password", PASSWD);
    form.param("path", fileid);

    return target.request().post(Entity.form(form));
  }*/

  public static File downloadOneFile(String fileId) throws IOException {
    Form form = new Form();

    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(DOWNLOAD_URL);

    form.param("username", USERNAME);
    form.param("password", PASSWD);
    form.param("path", fileId);

    String[] fileNames = fileId.split("/");

    Response output = target.request().post(Entity.form(form));
    InputStream in = new BufferedInputStream(output.readEntity(InputStream.class));
    File tmpFile = new File(PathUtils.concatPath(DOWNLOAD_TMP_PATH, fileNames[fileNames.length-1]));
    if(!tmpFile.getParentFile().exists()) {
      PathUtils.mkdirs(tmpFile.getParentFile());
    }
    try (OutputStream out = new BufferedOutputStream(new FileOutputStream(tmpFile))) {
      byte[] buf = new byte[1024];
      int read;
      while ((read = in.read(buf)) != -1) {
        out.write(buf, 0, read);
      }
    }
    in.close();
    return tmpFile;
  }

  private void deleteFile(List<File> files) {
    for(File file : files) {
      File needDeleteFile = file.getParentFile();
      if(!file.delete()) {
        LOG.error("Failed to delete the archived file {}", file.getName());
      }
      // delete empty directory
      while((needDeleteFile.listFiles() == null || needDeleteFile.listFiles().length == 0) &&
          !needDeleteFile.getAbsolutePath().equals(DOWNLOAD_TMP_PATH) ) {
        File tmp = needDeleteFile.getParentFile();
        if(needDeleteFile.delete()) {
          needDeleteFile = tmp;
        } else {
          LOG.error("failed to delete empty directory {}" + needDeleteFile.getAbsolutePath());
        }
      }
    }
  }

  private void zipFile(List<File> files, String baseName, ZipOutputStream zos) throws IOException {
    for (File f : files) {
      zos.putNextEntry(new ZipEntry(baseName + f.getName()));
      FileInputStream fis = new FileInputStream(f);
      byte[] buffer = new byte[1024];
      int r;
      while ((r = fis.read(buffer)) != -1) {
        zos.write(buffer, 0, r);
      }
      fis.close();
    }
  }

  @POST
  @Path("downloadCheck")
  @Produces(MediaType.APPLICATION_JSON)
  public Response downloadCheck(String num) {
    String fileNum = num.split("=")[1];
    int nums = Integer.parseInt(fileNum);
    JSONObject result = new JSONObject();
    if(nums > FILE_DOWNLOAD_NUM) {
      result.put("result", "false");
    } else {
      result.put("result", "true");
    }
    return Response.ok().entity(result.toJSONString()).build();
  }

  @POST
  @Path("download")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response download(@FormParam("id") String fileid) {
    LOG.info("start to download file, id is {}", fileid);
    String[] fileIds = fileid.split(";");

    List<File> result =  new ArrayList<>();
    List<String> failedId = new ArrayList<>();
    for(String id : fileIds) {
      try {
        result.add(downloadOneFile(id));
      } catch (IOException e) {
        LOG.error("failed to download file {}, reason {}", id, e.getMessage());
        failedId.add(id);
      }
    }
    if(!failedId.isEmpty()) {
      String errorMsg = "can't download " + failedId.toString();
      deleteFile(result);
      return generateErrorResponse(new Exception(errorMsg));
    }
    String currentTime = FileInfoSqlService.fileInfoDateFormat.format(new java.util.Date());
    String baseName = "downloadFile" + currentTime +  ".zip";
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      ZipOutputStream zipOut = new ZipOutputStream(out);
      zipFile(result, baseName, zipOut);
      zipOut.close();
      deleteFile(result);
      ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
      BufferedInputStream resIn = new BufferedInputStream(in);
      return  Response
          .ok(resIn)
          .type(MediaType.APPLICATION_OCTET_STREAM)
          .header("content-disposition", "attachment; filename = " + baseName)
          .build();
    } catch (Exception e) {
      return generateErrorResponse(e);
    }
  }

  @POST
  @Path("archiveQuery")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response getUnArchiveFile(@Context HttpServletRequest request,
                                   @FormParam("beginDate") String beginDate,
                                   @FormParam("endDate") String endDate) {
    beginDate += " 00:00:00";
    endDate += " 23:59:59";
    LOG.info("start to query unArchived file, begin date is {}, end date is {}",
        beginDate, endDate);
    String addr = request.getRemoteHost() + ":" + request.getRemotePort();
    UserSessionManager userManager = getUserManager(request);

    userManager.clearArchiveInfo(addr);

    try {
      java.util.Date begin = FileInfoSqlService.fileInfoDateFormat.parse(beginDate);
      java.util.Date end = FileInfoSqlService.fileInfoDateFormat.parse(endDate);
      if (begin.compareTo(end) > 0) {
        throw new Exception("date interval wrong");
      }
      List<FileInfoBean> res = FileInfoSqlService.selectUnArchiveFiles(
              new Timestamp(begin.getTime()), new Timestamp(end.getTime()));
      JSONObject result = generateJsonResult(res);
      userManager.addArchiveInfo(addr, res);
      result.put("path", ARCHIVE_PATH);
      return Response.ok().entity(result.toJSONString()).build();
    } catch (Exception e) {
      LOG.error("failed to query unArchived info");
      return generateErrorResponse(e);
    }
  }

  @POST
  @Path("jumpArchiveInfo")
  @Produces(MediaType.APPLICATION_JSON)
  public Response jumpArchiveInfo(@Context HttpServletRequest request,String indexNum) {
    String index = indexNum.split("=")[1];
    String addr = request.getRemoteHost() + ":" + request.getRemotePort();
    UserSessionManager userManager = getUserManager(request);

    UserSessionManager.FileInfoPageBean bean = userManager.getArchiveInfo(addr);
    return jumpFileInfo(index, bean);
  }

  @GET
  @Path("initArchiveInfo")
  @Produces(MediaType.APPLICATION_JSON)
  public Response initArchive(@Context HttpServletRequest request) {
    LOG.info("start to get files archive init info");
    String addr = request.getRemoteHost() + ":" + request.getRemotePort();
    UserSessionManager userManager = getUserManager(request);

    userManager.clearArchiveInfo(addr);
    try {
      List<FileInfoBean> res = FileInfoSqlService.initArchiveInfo(ARCHIVE_INIT_NUM);
      JSONObject result = generateJsonResult(res);
      userManager.addArchiveInfo(addr, res);
      result.put("path", ARCHIVE_PATH);
      return Response.ok().entity(result.toJSONString()).build();
    } catch (Exception e) {
      LOG.error("failed to get files init info");
      return generateErrorResponse(e);
    }
  }

  @POST
  @Path("archive")
  @SuppressWarnings("unchecked")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response archive(@Context HttpServletRequest request, String fileid) {
    String[] fileids = fileid.split(";");
    LOG.info("start to archive files {}", fileid);
    List<Future<String>> futures = new ArrayList<>();
    HashSet<String> ids = new HashSet<>();

    HashMap<String, FileInfoBean> resMap = new HashMap<>();
    try {
      String addr = request.getRemoteHost() + ":" + request.getRemotePort();
      UserSessionManager userManager = getUserManager(request);

      UserSessionManager.FileInfoPageBean pagebean = userManager.getArchiveInfo(addr);
      List<FileInfoBean> allFileInfo = pagebean.getResult();
      for(FileInfoBean bean : allFileInfo) {
        resMap.put(bean.getFileId(), bean);
      }
      HashSet<String> neededFiles = new HashSet<>();
      for (String id : fileids) {
        neededFiles.add(id);
        futures.add(ARCHIVE_SERVICE.submit(new ArchiveRequest(id)));
      }
      for (Future<String> future : futures) {
        String resId = future.get();
        if (resId.length() > 0) {
          resMap.remove(resId);
          neededFiles.remove(resId);
        }
      }

      List<FileInfoBean> resList = new ArrayList<>();
      for(Map.Entry<String, FileInfoBean> entry : resMap.entrySet()) {
        resList.add(entry.getValue());
      }

      userManager.addArchiveInfo(addr, resList);
      JSONObject lastObj = new JSONObject();
      JSONArray jj = new JSONArray();
      jj.addAll(resList);
      JSONArray jj2 = new JSONArray();
      jj2.addAll(neededFiles);
      lastObj.put("root", jj);
      lastObj.put("failed",jj2);
      lastObj.put("path", ARCHIVE_PATH);
      LOG.info("result {}", lastObj);


      return Response.ok().type(MediaType.APPLICATION_JSON).entity(lastObj.toJSONString())
          .build();
    } catch (Exception e) {
      return generateErrorResponse(e);
    }
  }

  @POST
  @Path("deleteQuery")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response getNeedDeleteFile(@Context HttpServletRequest request,
                                    @FormParam("beginDate") String beginDate,
                                    @FormParam("endDate") String endDate) {
    beginDate += " 00:00:00";
    endDate += " 23:59:59";
    String addr = request.getRemoteHost() + ":" + request.getRemotePort();
    UserSessionManager userManager = getUserManager(request);

    userManager.clearDeleteInfo(addr);
    LOG.info("start to query delete file info, begin date is {}, end date is {}", beginDate,
        endDate);
    try {
      java.util.Date begin = FileInfoSqlService.fileInfoDateFormat.parse(beginDate);
      java.util.Date end = FileInfoSqlService.fileInfoDateFormat.parse(endDate);
      if (begin.compareTo(end) > 0) {
        throw new Exception("date interval wrong");
      }
      List<FileInfoBean> res = FileInfoSqlService.selectNeedDeleteFiles(
              new Timestamp(begin.getTime()), new Timestamp(end.getTime()));
      JSONObject result = generateJsonResult(res);
      userManager.addDeleteInfo(addr, res);
      return Response.ok().entity(result.toJSONString()).build();
    } catch (Exception e) {
      LOG.error("failed to query delete file info");
      return generateErrorResponse(e);
    }
  }

  @GET
  @Path("initDeleteInfo")
  @Produces(MediaType.APPLICATION_JSON)
  public Response initDeleteInfo(@Context HttpServletRequest request) {
    LOG.info("start to get files delete init info");
    String addr = request.getRemoteHost() + ":" + request.getRemotePort();
    UserSessionManager userManager = getUserManager(request);

    userManager.clearDeleteInfo(addr);
    try {
      List<FileInfoBean> res = FileInfoSqlService.initDeleteInfo(DELETE_INIT_NUM);
      JSONObject result = generateJsonResult(res);
      userManager.addDeleteInfo(addr, res);
      return Response.ok().entity(result.toJSONString()).build();
    } catch (Exception e) {
      LOG.error("failed to get files init info");
      return generateErrorResponse(e);
    }
  }

  @POST
  @Path("jumpDeleteInfo")
  @Produces(MediaType.APPLICATION_JSON)
  public Response jumpDeleteInfo(@Context HttpServletRequest request,String indexNum) {
    String index = indexNum.split("=")[1];
    String addr = request.getRemoteHost() + ":" + request.getRemotePort();
    UserSessionManager userManager = getUserManager(request);

    UserSessionManager.FileInfoPageBean bean = userManager.getDeleteInfo(addr);
    return jumpFileInfo(index, bean);
  }

  @SuppressWarnings("unchecked")
  private Response jumpFileInfo(String index, UserSessionManager.InfoBean bean) {
    List<?> res = null;
    if(index.equals("0")) {
      res = bean.getForwardPage();
    }
    else if(index.equals("-1")) {
      res = bean.getBackwordPage();
    } else {
      res = bean.getResultByIndex(Integer.parseInt(index));
    }
    JSONObject result = generateJsonResult(res);
    result.put("path", ARCHIVE_PATH);
    return Response.ok().entity(result.toJSONString()).build();
  }

  @POST
  @Path("delete")
  @SuppressWarnings("unchecked")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response delete(@Context HttpServletRequest request,String fileid) {
    String[] fileids = fileid.split(";");
    LOG.info("start to delete files {}", fileid);
    List<Future<String>> futures = new ArrayList<>();
    HashSet<String> ids = new HashSet<>();

    HashMap<String, FileInfoBean> resMap = new HashMap<>();
    try {
      String addr = request.getRemoteHost() + ":" + request.getRemotePort();
      UserSessionManager userManager = getUserManager(request);

      UserSessionManager.FileInfoPageBean pagebean = userManager.getDeleteInfo(addr);
      List<FileInfoBean> allFileInfo = pagebean.getResult();
      for(FileInfoBean bean : allFileInfo) {
        resMap.put(bean.getFileId(), bean);
      }
      HashSet<String> neededFiles = new HashSet<>();
      for (String id : fileids) {
        neededFiles.add(id);
        futures.add(ARCHIVE_SERVICE.submit(new DeleteRequest(id)));
      }
      for (Future<String> future : futures) {
        String resId = future.get();
        if (resId.length() > 0) {
          resMap.remove(resId);
          neededFiles.remove(resId);
        }
      }

      List<FileInfoBean> resList = new ArrayList<>();
      for(Map.Entry<String, FileInfoBean> entry : resMap.entrySet()) {
        resList.add(entry.getValue());
      }

      userManager.addDeleteInfo(addr, resList);
      JSONObject lastObj = new JSONObject();
      JSONArray jj = new JSONArray();
      jj.addAll(resList);
      JSONArray jj2 = new JSONArray();
      jj2.addAll(neededFiles);
      lastObj.put("root", jj);
      lastObj.put("failed",jj2);
      LOG.info("result {}", lastObj);

      return Response.ok().type(MediaType.APPLICATION_JSON).entity(lastObj.toJSONString())
          .build();
    } catch (Exception e) {
      return generateErrorResponse(e);
    }
  }


  private JSONObject generateJsonResult(Collection<?> l) {
    JSONObject result = new JSONObject();
    JSONArray jj = new JSONArray();
    jj.addAll(l);
    result.put("root", jj);
    return result;
  }

  private Response generateErrorResponse(Throwable t) {
    if (t instanceof DataBaseException) {
      return Response
              .status(Response.Status.INTERNAL_SERVER_ERROR)
              .type(MediaType.TEXT_PLAIN)
              .entity(t.getMessage())
              .build();
    } else if (t instanceof AuthenticationException) {
      return Response
          .status(Response.Status.PROXY_AUTHENTICATION_REQUIRED)
          .header("WWW-Authenticate", "Basic realm=\"" + REALM + "\"")
          .type(MediaType.TEXT_PLAIN)
          .entity(t.getMessage())
          .build();
    } else if (t instanceof AuthorizationException) {
      return Response
          .status(Response.Status.UNAUTHORIZED)
          .type(MediaType.TEXT_PLAIN)
          .entity(t.getMessage())
          .build();
    }

    else {
      return Response
              .status(Response.Status.INTERNAL_SERVER_ERROR)
              .type(MediaType.TEXT_PLAIN)
              .entity(t.getMessage())
              .build();
    }
  }
}

