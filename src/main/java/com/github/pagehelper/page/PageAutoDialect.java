package com.github.pagehelper.page;
import com.github.pagehelper.Dialect;
import com.github.pagehelper.PageException;
import com.github.pagehelper.dialect.AbstractHelperDialect;
import com.github.pagehelper.dialect.helper.*;
import com.github.pagehelper.util.StringUtil;
import org.apache.ibatis.mapping.MappedStatement;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 基础方言信息
 *
 * @author liuzh
 */
public class PageAutoDialect {
  private static Map<String, Class<? extends Dialect>> dialectAliasMap = new HashMap<String, Class<? extends Dialect>>();

  public static void registerDialectAlias(String alias, Class<? extends Dialect> dialectClass) {
    dialectAliasMap.put(alias, dialectClass);
  }

  static {
    registerDialectAlias("hsqldb", HsqldbDialect.class);
    registerDialectAlias("h2", HsqldbDialect.class);
    registerDialectAlias("postgresql", HsqldbDialect.class);
    registerDialectAlias("phoenix", HsqldbDialect.class);
    registerDialectAlias("mysql", MySqlDialect.class);
    registerDialectAlias("mariadb", MySqlDialect.class);
    registerDialectAlias("sqlite", MySqlDialect.class);
    registerDialectAlias("herddb", HerdDBDialect.class);
    registerDialectAlias("oracle", OracleDialect.class);
    registerDialectAlias("oracle9i", Oracle9iDialect.class);
    registerDialectAlias("db2", Db2Dialect.class);
    registerDialectAlias("informix", InformixDialect.class);
    registerDialectAlias("informix-sqli", InformixDialect.class);
    registerDialectAlias("sqlserver", SqlServerDialect.class);
    registerDialectAlias("sqlserver2012", SqlServer2012Dialect.class);
    registerDialectAlias("derby", SqlServer2012Dialect.class);
    registerDialectAlias("dm", OracleDialect.class);
    registerDialectAlias("edb", OracleDialect.class);
    registerDialectAlias("oscar", OscarDialect.class);
    registerDialectAlias("clickhouse", MySqlDialect.class);
    registerDialectAlias("highgo", HsqldbDialect.class);
    registerDialectAlias("xugu", HsqldbDialect.class);
  }

  private boolean autoDialect = true;

  private boolean closeConn = true;

  private Properties properties;

  private Map<String, AbstractHelperDialect> urlDialectMap = new ConcurrentHashMap<String, AbstractHelperDialect>();

  private ReentrantLock lock = new ReentrantLock();

  private AbstractHelperDialect delegate;

  private ThreadLocal<AbstractHelperDialect> dialectThreadLocal = new ThreadLocal<AbstractHelperDialect>();

  public void initDelegateDialect(MappedStatement ms) {
    if (delegate == null) {
      if (autoDialect) {
        this.delegate = getDialect(ms);
      } else {
        dialectThreadLocal.set(getDialect(ms));
      }
    }
  }

  public AbstractHelperDialect getDelegate() {
    if (delegate != null) {
      return delegate;
    }
    return dialectThreadLocal.get();
  }

  public void clearDelegate() {
    dialectThreadLocal.remove();
  }

  private String fromJdbcUrl(String jdbcUrl) {
    final String url = jdbcUrl.toLowerCase();
    for (String dialect : dialectAliasMap.keySet()) {
      if (url.contains(":" + dialect.toLowerCase() + ":")) {
        return dialect;
      }
    }
    return null;
  }

  /**
     * 反射类
     *
     * @param className
     * @return
     * @throws Exception
     */
  private Class resloveDialectClass(String className) throws Exception {
    if (dialectAliasMap.containsKey(className.toLowerCase())) {
      return dialectAliasMap.get(className.toLowerCase());
    } else {
      return Class.forName(className);
    }
  }

  /**
     * 初始化 helper
     *
     * @param dialectClass
     * @param properties
     */
  private AbstractHelperDialect initDialect(String dialectClass, Properties properties) {
    AbstractHelperDialect dialect;
    if (StringUtil.isEmpty(dialectClass)) {
      throw new PageException("\u4f7f\u7528 PageHelper \u5206\u9875\u63d2\u4ef6\u65f6\uff0c\u5fc5\u987b\u8bbe\u7f6e helper \u5c5e\u6027");
    }
    try {
      Class sqlDialectClass = resloveDialectClass(dialectClass);
      if (AbstractHelperDialect.class.isAssignableFrom(sqlDialectClass)) {
        dialect = (AbstractHelperDialect) sqlDialectClass.newInstance();
      } else {
        throw new PageException("\u4f7f\u7528 PageHelper \u65f6\uff0c\u65b9\u8a00\u5fc5\u987b\u662f\u5b9e\u73b0 " + AbstractHelperDialect.class.getCanonicalName() + " \u63a5\u53e3\u7684\u5b9e\u73b0\u7c7b!");
      }
    } catch (Exception e) {
      throw new PageException("\u521d\u59cb\u5316 helper [" + dialectClass + "]\u65f6\u51fa\u9519:" + e.getMessage(), e);
    }
    dialect.setProperties(properties);
    return dialect;
  }

  /**
     * 获取url
     *
     * @param dataSource
     * @return
     */
  private String getUrl(DataSource dataSource) {
    Connection conn = null;
    try {
      conn = dataSource.getConnection();
      return conn.getMetaData().getURL();
    } catch (SQLException e) {
      throw new PageException(e);
    } finally {
      if (conn != null) {
        try {
          if (closeConn) {
            conn.close();
          }
        } catch (SQLException e) {
        }
      }
    }
  }

  /**
     * 根据 jdbcUrl 获取数据库方言
     *
     * @param ms
     * @return
     */
  private AbstractHelperDialect getDialect(MappedStatement ms) {
    DataSource dataSource = ms.getConfiguration().getEnvironment().getDataSource();
    String url = getUrl(dataSource);
    if (urlDialectMap.containsKey(url)) {
      return urlDialectMap.get(url);
    }
    try {
      lock.lock();
      if (urlDialectMap.containsKey(url)) {
        return urlDialectMap.get(url);
      }
      if (StringUtil.isEmpty(url)) {
        throw new PageException("\u65e0\u6cd5\u81ea\u52a8\u83b7\u53d6jdbcUrl\uff0c\u8bf7\u5728\u5206\u9875\u63d2\u4ef6\u4e2d\u914d\u7f6edialect\u53c2\u6570!");
      }
      String dialectStr = fromJdbcUrl(url);
      if (dialectStr == null) {
        throw new PageException("\u65e0\u6cd5\u81ea\u52a8\u83b7\u53d6\u6570\u636e\u5e93\u7c7b\u578b\uff0c\u8bf7\u901a\u8fc7 helperDialect \u53c2\u6570\u6307\u5b9a!");
      }
      AbstractHelperDialect dialect = initDialect(dialectStr, properties);
      urlDialectMap.put(url, dialect);
      return dialect;
    }  finally {
      lock.unlock();
    }
  }

  public void setProperties(Properties properties) {
    String closeConn = properties.getProperty("closeConn");
    if (StringUtil.isNotEmpty(closeConn)) {
      this.closeConn = Boolean.parseBoolean(closeConn);
    }
    String useSqlserver2012 = properties.getProperty("useSqlserver2012");
    if (StringUtil.isNotEmpty(useSqlserver2012) && Boolean.parseBoolean(useSqlserver2012)) {
      registerDialectAlias("sqlserver", SqlServer2012Dialect.class);
      registerDialectAlias("sqlserver2008", SqlServerDialect.class);
    }
    String dialectAlias = properties.getProperty("dialectAlias");
    if (StringUtil.isNotEmpty(dialectAlias)) {
      String[] alias = dialectAlias.split(";");
      for (int i = 0; i < alias.length; i++) {
        String[] kv = alias[i].split("=");
        if (kv.length != 2) {
          throw new IllegalArgumentException("dialectAlias \u53c2\u6570\u914d\u7f6e\u9519\u8bef\uff0c" + "\u8bf7\u6309\u7167 alias1=xx.dialectClass;alias2=dialectClass2 \u7684\u5f62\u5f0f\u8fdb\u884c\u914d\u7f6e!");
        }
        for (int j = 0; j < kv.length; j++) {
          try {
            Class<? extends Dialect> diallectClass = (Class<? extends Dialect>) Class.forName(kv[1]);
            registerDialectAlias(kv[0], diallectClass);
          } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("\u8bf7\u786e\u4fdd dialectAlias \u914d\u7f6e\u7684 Dialect \u5b9e\u73b0\u7c7b\u5b58\u5728!", e);
          }
        }
      }
    }
    String dialect = properties.getProperty("helperDialect");
    String runtimeDialect = properties.getProperty("autoRuntimeDialect");
    if (StringUtil.isNotEmpty(runtimeDialect) && "TRUE".equalsIgnoreCase(runtimeDialect)) {
      this.autoDialect = false;
      this.properties = properties;
    } else {
      if (StringUtil.isEmpty(dialect)) {
        autoDialect = true;
        this.properties = properties;
      } else {
        autoDialect = false;
        this.delegate = initDialect(dialect, properties);
      }
    }
  }
}