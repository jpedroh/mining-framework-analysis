package com.github.pagehelper.dialect;
import com.github.pagehelper.Dialect;
import com.github.pagehelper.parser.CountSqlParser;
import com.github.pagehelper.util.MetaObjectUtil;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.RowBounds;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 针对 PageHelper 的实现
 *
 * @author liuzh
 * @since 2016-12-04 14:32
 */
public abstract class AbstractDialect implements Dialect {
  protected CountSqlParser countSqlParser = new CountSqlParser();

  @Override public String getCountSql(MappedStatement ms, BoundSql boundSql, Object parameterObject, RowBounds rowBounds, CacheKey countKey) {
    return countSqlParser.getSmartCountSql(boundSql.getSql());
  }


<<<<<<< /usr/src/app/output/pagehelper/mybatis-pagehelper/964a5ebebae2922f93d80ac9403670905686c88c/src/main/java/com/github/pagehelper/dialect/AbstractDialect.java/left.java
  @Override public void afterCount(long count, Object parameterObject, RowBounds rowBounds) {
    Page page = SqlUtil.getLocalPage();
    page.setTotal(count);
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.



<<<<<<< /usr/src/app/output/pagehelper/mybatis-pagehelper/964a5ebebae2922f93d80ac9403670905686c88c/src/main/java/com/github/pagehelper/dialect/AbstractDialect.java/left.java
  @Override public Object processParameterObject(MappedStatement ms, Object parameterObject, BoundSql boundSql, CacheKey pageKey) {
    Page page = SqlUtil.getLocalPage();
    if (page.isOrderByOnly()) {
      return parameterObject;
    }
    Map<String, Object> paramMap = null;
    if (parameterObject == null) {
      paramMap = new HashMap<String, Object>();
    } else {
      if (parameterObject instanceof Map) {
        paramMap = new HashMap<String, Object>();
        paramMap.putAll((Map) parameterObject);
      } else {
        paramMap = new HashMap<String, Object>();
        boolean hasTypeHandler = ms.getConfiguration().getTypeHandlerRegistry().hasTypeHandler(parameterObject.getClass());
        MetaObject metaObject = MetaObjectUtil.forObject(parameterObject);
        if (!hasTypeHandler) {
          for (String name : metaObject.getGetterNames()) {
            paramMap.put(name, metaObject.getValue(name));
          }
        }
        if (boundSql.getParameterMappings() != null && boundSql.getParameterMappings().size() > 0) {
          for (ParameterMapping parameterMapping : boundSql.getParameterMappings()) {
            String name = parameterMapping.getProperty();
            if (!name.equals(PAGEPARAMETER_FIRST) && !name.equals(PAGEPARAMETER_SECOND) && paramMap.get(name) == null) {
              if (hasTypeHandler || parameterMapping.getJavaType().equals(parameterObject.getClass())) {
                paramMap.put(name, parameterObject);
                break;
              }
            }
          }
        }
      }
    }
    return processPageParameter(ms, paramMap, page, boundSql, pageKey);
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  /**
     * 处理分页参数
     *
     * @param ms
     * @param paramMap
     * @param page
     * @param boundSql
     * @param pageKey
     * @return
     */
  public abstract Object processPageParameter(MappedStatement ms, Map<String, Object> paramMap, Page page, BoundSql boundSql, CacheKey pageKey);
}