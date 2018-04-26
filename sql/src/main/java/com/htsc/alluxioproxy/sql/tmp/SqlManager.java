package com.htsc.alluxioproxy.sql.tmp;

import com.htsc.alluxioproxy.sql.SqlTmp;
import com.htsc.alluxioproxy.sql.service.DataBaseException;
import com.htsc.alluxioproxy.sql.service.DatabaseService;
import com.htsc.alluxioproxy.sql.service.FileInfoSqlDatabaseService;
import com.htsc.alluxioproxy.sql.service.MetricInfoSqlDatabaseService;
import com.htsc.alluxioproxy.sql.util.Configuration;
import com.htsc.alluxioproxy.sql.util.Constants;
import com.htsc.alluxioproxy.sql.util.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.channels.FileLock;

/**
 * Created by lenovo on 2017/12/14.
 */
public enum SqlManager {
  INSTANCE;
  private static final String DATABASE_BACKUP_DIR =
          Configuration.INSTANCE.getString(Constants.DATABASE_BACKUP_DIR);
  private static final String TMP_FILE_NAME =
          Configuration.INSTANCE.getString(Constants.TMP_FILE_NAME);
  private static final Logger LOG = LoggerFactory.getLogger(SqlManager.class);
  private static final Configuration CONF = Configuration.INSTANCE;
  private static final long SQL_EXECUTE_INTERNAL = CONF.getLong(Constants.SQL_EXECUTE_INTERNAL);
  private File mTmpFile = null;
  private final Object TMP_LOCK = new Object();
  private static boolean mIsRecover = true;


  public static int getServerId() {
    return CONF.getInt(Constants.SERVER_ID);
  }

  /**
   * Start Sql operation.
   */
  public void start() {
    //Thread thread = new Thread(new SqlExecutor());
    //thread.start();
    new SqlExecutor().run();
  }

  /**
   * Thread class to execute sql.sql operation.
   */
  private class SqlExecutor implements Runnable {
    @Override
    public void run() {
      while (true) {
        try {
          executeSql();
        } catch (Exception e) {
          LOG.error("sql exec failed");
          e.printStackTrace();
        }  finally {
          try {
            Thread.sleep(SQL_EXECUTE_INTERNAL);
          } catch (InterruptedException e) {
            LOG.error("SqlExecutor thread was interrupted");
            return;
          }
        }
      }
    }
  }

  public void createTable() {
    openDatabaseSession();
    try {
      MetricInfoSqlDatabaseService.createTable();
      MetricInfoSqlDatabaseService.addId();
      FileInfoSqlDatabaseService.createTable();
    } catch(Exception e) {
      LOG.warn("failed to create table");
    } finally {
      DatabaseService.close();
    }
  }

  private void wrongFileLengthCheck(File f) throws Exception {
    LOG.info("check file {} ", f.getName());
    if(!f.exists()){
      return;
    } if(f.exists() && f.length() == 0) {
      if(!f.delete()) {
        LOG.error("can't delete empty backupFile");
        System.gc();
        f.delete();
      }
    }
  }

  public void openDatabaseSession() {
    DatabaseService.openSession(false);
    FileInfoSqlDatabaseService.open();
    MetricInfoSqlDatabaseService.open();
  }

  private InputStream read(File f) throws Exception {
    RandomAccessFile r = null;
    FileLock lock = null;
    try {
      r = new RandomAccessFile(f, "rw");
      lock = r.getChannel().lock();

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      int j;
      while((j = r.read()) != -1) {
        out.write(j);
      }
      out.close();
      byte [] b = out.toByteArray();
      return new ByteArrayInputStream(b);
    } finally {
      if(lock!= null) {
        lock.release();
      }
      if(r != null) {
        r.close();
      }
    }
  }

  private boolean backupFileCheck(String filePath) throws Exception{
		File backUpFile = new File(filePath);
		if(!backUpFile.exists() || backUpFile.length() == 0) {
			mIsRecover = false;
			String tmpPath = PathUtils.concatPath(DATABASE_BACKUP_DIR, TMP_FILE_NAME +
				getServerId());
			mTmpFile = new File(tmpPath);
			if(!mTmpFile.exists() || mTmpFile.length() == 0 ){
				return true;
			}
			synchronized (TMP_LOCK) {
				wrongFileLengthCheck(backUpFile);
				if (!mTmpFile.renameTo(backUpFile)) {
					LOG.error("can't rename tmp file");
					throw new RuntimeException();
				}
			}
			LOG.info("execute sql, create tmp file {} ", backUpFile.getName());
		} else {
			if(mIsRecover) {
				LOG.info("recover sql from server shut down");
				mIsRecover = false;
			} else {
				LOG.info("recover sql from sql.sql exec failed");
			}
		}
		return false;
	}

	private void executeSql0(InputStream mTmpFileInputStream) throws Exception {
		SqlTmp info;
		ObjectInputStream ois = new ObjectInputStream(mTmpFileInputStream);
		LOG.info("test2");
		info = (SqlTmp) ois.readObject();
		LOG.info("test3");
		String operation = info.getOperationName();
		String serviceName = info.getServiceName();
		Serializable bean = info.getBean();
		boolean isupdate = info.isUpdate();
		String id = info.getID();
		Class service = Class.forName("com.htsc.alluxioproxy.sql.service." + serviceName);
		if (!isupdate) {
			Method oper = service.getDeclaredMethod(operation, Serializable.class);
			oper.invoke(null, bean);
		} else {
			Method oper = service.getDeclaredMethod(operation, String.class);
			oper.invoke(null, id);
		}
		LOG.info("sql exec from object succeed");
	}

  /**
   *  execute the sql operation in the tmp file.
   *
   * @throws Exception if error happened when executing sql.sql operation or reading tmp file
   */
  @SuppressWarnings("unchecked")
  private void executeSql() throws Exception {
    String filePath = PathUtils.concatPath(DATABASE_BACKUP_DIR, TMP_FILE_NAME+
        getServerId()+".backup");
		if(backupFileCheck(filePath)) {
			return;
		}
		boolean isCommited = false;
		File sqlFile = new File(filePath);
    openDatabaseSession();
    InputStream mTmpFileInputStream = read(sqlFile);
    try {
      while(mTmpFileInputStream.available() > 0) {
      	LOG.info("test1");
				executeSql0(mTmpFileInputStream);
				LOG.info("test2");
      }
      DatabaseService.commit();
      isCommited = true;
      while (!sqlFile.delete()) {
        LOG.error("can't delete tmp backup file {}", sqlFile.getName());
        System.gc();
        sqlFile.delete();
        Thread.sleep(1);
      }
      LOG.info("execute sql finished, delete tmp file {} ", sqlFile.getName());
    } catch (Throwable e) {
      if(e instanceof DataBaseException) {
        LOG.error("sql.sql execute failed, reason {}", e.getMessage());
      }
      else if (e instanceof IOException){
        LOG.error("IO exception {}, {}", e.getClass(), e.getMessage());
      }
      else {
        LOG.error("unKnownError :{}", e.getMessage() + e.getStackTrace() + e.getCause());
      }
      if(isCommited) {
        LOG.info("roll back");
        DatabaseService.rollback();
      }
    } finally {
      DatabaseService.close();
      wrongFileLengthCheck(sqlFile);
    }
  }
}

