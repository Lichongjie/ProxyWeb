package web;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import org.apache.ibatis.annotations.Param;
import sql.bean.FileInfoBean;
import sql.bean.MetricSumInfoBean;
import sql.sqlService.DataBaseException;
import sql.sqlService.FileInfoSqlService;
import sql.sqlService.MetricInfoSqlService;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Path("/")
public class WebResource {
  private ExecutorService queryThreadPool = Executors.newFixedThreadPool(10);
  private ExecutorService TRANSCODE_SERVICE = Executors.newFixedThreadPool(10);
  private ExecutorService ARCHIVE_SERVICE = Executors.newFixedThreadPool(10);

  private static final String DOWNLOAD_URL;
  private static final String USERNAME;
  private static final String PASSWD;
  private static final int HISTORU_INIT_NUM;
  private static final int FILEINFO_INIT_NUM;

  static {
    Configuration conf = Configuration.INSTANCE;

    String baseUrl = conf.getString(Constants.STORAGE_SERVICE_URL);
    USERNAME = conf.getString(Constants.USERNAME);
    PASSWD = conf.getString(Constants.PASSWORD);
    if (!baseUrl.endsWith("/")) {
      baseUrl += "/";
    }
    DOWNLOAD_URL = baseUrl + "download";
    HISTORU_INIT_NUM = conf.getInt(Constants.HISTORU_INIT_NUM);
    FILEINFO_INIT_NUM = conf.getInt(Constants.FILEINFO_INIT_NUM);
  }


  //private HashMap<String, List<List<MetricSumInfoBean>>>

  @GET
  @Path("hello")
  public Response test() {
    return Response.ok().entity("ccccc").build();
  }

  @GET
  @Path("MetricInfo")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getMetricInfo() {
    try {
      MetricSumInfoBean dayInfoRes = MetricInfoSqlService.selectMetricInfoByDate();
      MetricSumInfoBean allInfoRes = MetricInfoSqlService.selectMetricInfo();
      JSONObject res = new JSONObject();

      res.put("dayInfo", dayInfoRes);
      res.put("allInfo", allInfoRes);
      return Response.ok().entity(res.toJSONString()).build();
    } catch (Exception e) {
      return generateErrorResponse(new DataBaseException(e.getMessage()));
    }
  }


  @GET
  @Path("ServerInfo")
  @Produces(MediaType.APPLICATION_JSON)
  public String getServerMetricInfo() {

    return null;
  }

  @POST
  @Path("history")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getHistoryInfo(@Context HttpServletRequest request,
                                 @FormParam("beginDate") String beginDate,
                                 @FormParam("endDate") String endDate) {
    //HistoryResult
    try {
      java.util.Date begin = MetricInfoSqlService.MetricInfoDateFormat.parse(beginDate);
      java.util.Date end = MetricInfoSqlService.MetricInfoDateFormat.parse(endDate);

      List<MetricSumInfoBean> res = MetricInfoSqlService.selectHistoryMetricInfo(
              new Date(begin.getTime()), new Date((end.getTime())));
      JSONObject result = generateJsonResult(res);

      return Response.ok().entity(result.toJSONString()).build();
    } catch (Exception e) {
      return generateErrorResponse(e);
    }

  }

  @GET
  @Path("initHistoryInfo")
  @Produces(MediaType.APPLICATION_JSON)
  public Response initHistoryInfo() {
    try {
      List<MetricSumInfoBean> res = MetricInfoSqlService.initHistoryInfo(HISTORU_INIT_NUM);
      JSONObject result = generateJsonResult(res);
      return Response.ok().entity(result.toJSONString()).build();
    } catch (Exception e) {
      return generateErrorResponse(e);
    }
  }

  @GET
  @Path("getToTranscode")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getTotranscodeFiles() {
    try {
      List<FileInfoBean> res = FileInfoSqlService.selectTranscodeFailedFile();
      JSONObject result = generateJsonResult(res);
      return Response.ok().entity(result.toJSONString()).build();
    } catch (Exception e) {
      return generateErrorResponse(e);
    }

  }

  @POST
  @Path("reTranscode")
  @SuppressWarnings("unchecked")
  @Produces(MediaType.APPLICATION_JSON)
  public Response reTranscode(@Param("id") String infos) {
    String[] fileInfo = infos.split(";");
    List<Future<String>> futures = new ArrayList<Future<String>>();
    HashSet<String> ids = new HashSet<String>();

    for (String info : fileInfo) {
      String id = info.split(":")[0];
      String format = info.split(":")[1];
      ids.add(id);
      futures.add(TRANSCODE_SERVICE.submit(new ReTranscoder(id, format)));
    }
    for (Future<String> future : futures) {
      try {
        String res = future.get();
        if (res.length() > 0) {
          ids.remove(res);
        }
      } catch (Exception e) {
        //LOG.error("Failed to reTranscode {}", id);
      }
    }
    if (ids.isEmpty()) {
      return Response.ok().entity("all files successfully retranscoded").build();
    } else {
      return generateErrorResponse(new Exception("Failed ids: " +
              generateJsonResult(ids).toJSONString()));
    }
  }

  @POST
  @Path("fileInfo")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getFileInfo(@FormParam("fileId") String fileId) {
    String[] fileIds = fileId.split(";");

    if (fileIds.length == 0) {
      generateErrorResponse(new NoContentException("no files to show"));
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
      try {
        res.add(future.get());
      } catch (Exception e) {
        return generateErrorResponse(e);
      }
    }
    JSONObject result = generateJsonResult(res);
    return Response.ok().entity(result.toJSONString()).build();
  }

  @GET
  @Path("initFileInfo")
  @Produces(MediaType.APPLICATION_JSON)
  public Response initFileInfo() {
    try {
      List<FileInfoBean> res = FileInfoSqlService.initFileInfo(FILEINFO_INIT_NUM);
      JSONObject result = generateJsonResult(res);

      return Response.ok().entity(result.toJSONString()).build();
    } catch (Exception e) {
      return generateErrorResponse(e);
    }
  }

  @POST
  @Path("download")
  public static Response download(@FormParam("id") String fileid) {
    Form form = new Form();

    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(DOWNLOAD_URL);

    form.param("username", USERNAME);
    form.param("password", PASSWD);
    form.param("path", fileid);

    return target.request().post(Entity.form(form));
  }


  @POST
  @Path("archiveQuery")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getUnArchiveFile(@FormParam("beginDate") String beginDate,
                                   @FormParam("endDate") String endDate) {
    beginDate += " 00:00:00";
    endDate += " 23:59:59";

    try {
      java.util.Date begin = FileInfoSqlService.fileInfoDateFormat.parse(beginDate);
      java.util.Date end = FileInfoSqlService.fileInfoDateFormat.parse(endDate);
      if (begin.compareTo(end) > 0) {
        throw new Exception("date interval wrong");
      }
      List<FileInfoBean> res = FileInfoSqlService.selectUnArchiveFiles(
              new Timestamp(begin.getTime()), new Timestamp(end.getTime()));
      JSONObject result = generateJsonResult(res);
      return Response.ok().entity(result.toJSONString()).build();
    } catch (Exception e) {
      return generateErrorResponse(e);
    }
  }

  @POST
  @Path("archive")
  @SuppressWarnings("unchecked")
  @Produces(MediaType.APPLICATION_JSON)
  public Response archive(@FormParam("id") String fileid) {
    String[] fileids = fileid.split(";");
    List<Future<String>> futures = new ArrayList<>();
    HashSet<String> ids = new HashSet<>();

    for (String id : fileids) {
      futures.add(ARCHIVE_SERVICE.submit(new Archiver(id)));
    }
    for (Future<String> future : futures) {
      try {
        String res = future.get();
        if (res.length() > 0) {
          ids.remove(res);
        }
      } catch (Exception e) {
        //LOG.error("Failed to reTranscode {}", id);
      }
    }
    if (ids.isEmpty()) {
      return Response.ok().entity("all files successfully archived").build();
    } else {
      return generateErrorResponse(new Exception("Failed ids: " +
              generateJsonResult(ids).toJSONString()));
    }
  }

  @POST
  @Path("deleteQuery")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getNeedDeleteFile(@FormParam("beginDate") String beginDate,
                                    @FormParam("endDate") String endDate) {
    beginDate += " 00:00:00";
    endDate += " 23:59:59";

    try {
      java.util.Date begin = FileInfoSqlService.fileInfoDateFormat.parse(beginDate);
      java.util.Date end = FileInfoSqlService.fileInfoDateFormat.parse(endDate);
      if (begin.compareTo(end) > 0) {
        throw new Exception("date interval wrong");
      }
      List<FileInfoBean> res = FileInfoSqlService.selectNeedDeleteFiles(
              new Timestamp(begin.getTime()), new Timestamp(end.getTime()));
      JSONObject result = generateJsonResult(res);
      return Response.ok().entity(result.toJSONString()).build();
    } catch (Exception e) {
      return generateErrorResponse(e);
    }
  }

  @POST
  @Path("delete")
  @SuppressWarnings("unchecked")
  @Produces(MediaType.APPLICATION_JSON)
  public Response delete(@FormParam("id") String fileid) {
    String[] fileids = fileid.split(";");
    List<Future<String>> futures = new ArrayList<>();
    HashSet<String> ids = new HashSet<>();

    for (String id : fileids) {

      futures.add(TRANSCODE_SERVICE.submit(new Deleter(id)));
    }
    for (Future<String> future : futures) {
      try {
        String res = future.get();
        if (res.length() > 0) {
          ids.remove(res);
        }
      } catch (Exception e) {
        //LOG.error("Failed to reTranscode {}", id);
      }
    }
    if (ids.isEmpty()) {
      return Response.ok().entity("all files successfully archived").build();
    } else {
      return generateErrorResponse(new Exception("Failed ids: " +
          generateJsonResult(ids).toJSONString()));
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
    } else {
      return Response
              .status(Response.Status.INTERNAL_SERVER_ERROR)
              .type(MediaType.TEXT_PLAIN)
              .entity(t.getMessage())
              .build();
    }
  }
}

