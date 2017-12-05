package web;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Param;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticArgumentBinding;
import sql.bean.FileInfoBean;
import sql.bean.MetricInfoBean;
import sql.bean.MetricSumInfoBean;
import sql.sqlService.DataBaseException;
import sql.sqlService.FileInfoSqlService;
import sql.sqlService.MetricInfoSqlService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.core.Response;
import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Path("/")
public class WebResource {
  private ExecutorService queryThreadPool = Executors.newFixedThreadPool(10);

  @GET
  @Path("hello")
  public Response test(){
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
  public Response getHistoryInfo(@FormParam("beginDate") String beginDate,
                               @FormParam("endDate") String endDate
                                     ) {
    try {
      java.util.Date begin = MetricInfoSqlService.MetricInfoDateFormat.parse(beginDate);
      java.util.Date end = MetricInfoSqlService.MetricInfoDateFormat.parse(endDate);
      System.out.println(beginDate+":" + endDate);

      List<MetricSumInfoBean> res = MetricInfoSqlService.selectHistoryMetricInfo(
              new Date(begin.getTime()), new Date((end.getTime())));
      JSONObject result = generateJsonResult(res);

      return Response.ok().entity(result.toJSONString()).build();
    } catch (Exception e){
      return generateErrorResponse(e);
    }

  }

  @GET
  @Path("getToTranscode")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getTotranscodeFiles() {
    try {
      System.out.print("test");
      List<FileInfoBean> res = FileInfoSqlService.selectTranscodeFailedFile();
      JSONObject result = generateJsonResult(res);

      return Response.ok().entity(result.toJSONString()).build();
    } catch (Exception e) {
      return generateErrorResponse(e);
    }

  }

  @POST
  @Path("fileInfo")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getFileInfo(@FormParam("fileId") String fileId) {
    System.out.println(fileId);
    String[] fileIds = fileId.split(";");
    if(fileIds.length == 0) {
      generateErrorResponse(new NoContentException("no files to show"));
    }
    List<Future<FileInfoBean>> futures = new ArrayList<Future<FileInfoBean>>();
    List<FileInfoBean> res = new ArrayList<FileInfoBean>();
    for(final String id : fileIds) {
      futures.add(queryThreadPool.submit(new Callable<FileInfoBean>() {
          public FileInfoBean call() throws Exception {
            return  FileInfoSqlService.selectFileInfoByFileId(id);
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

  @POST
  @Path("archiveQuery")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getUnArchiveFile() {
    try {
      List<FileInfoBean> res = FileInfoSqlService.selectUnArchiveFiles();
      JSONObject result = generateJsonResult(res);
      return Response.ok().entity(result.toJSONString()).build();
    } catch (Exception e) {
      return generateErrorResponse(e);
    }
  }



  private JSONObject generateJsonResult(List<?> l) {
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

