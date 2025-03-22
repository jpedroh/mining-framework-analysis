package com.github.pagehelper.dialect.helper;
import com.github.pagehelper.Page;
import java.util.Map;
import com.github.pagehelper.dialect.AbstractHelperDialect;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * @author liuzh
 */
public class OracleDialect extends AbstractHelperDialect {
  @Override public Object processPageParameter(MappedStatement ms, Map<String, Object> paramMap, Page page, BoundSql boundSql, CacheKey pageKey) {
    paramMap.put(PAGEPARAMETER_FIRST, page.getEndRow());
    paramMap.put(PAGEPARAMETER_SECOND, page.getStartRow());
    pageKey.update(page.getEndRow());
    pageKey.update(page.getStartRow());
    handleParameter(boundSql, ms);
    return paramMap;
  }

  @Override public String getPageSql(String sql, Page page, CacheKey pageKey) {
    StringBuilder sqlBuilder = new StringBuilder(sql.length() + 120);
    sqlBuilder.append("SELECT * FROM ( ");
    sqlBuilder.append(" SELECT TMP_PAGE.*, ROWNUM ROW_ID FROM ( ");
    sqlBuilder.append(sql);
    sqlBuilder.append(" ) TMP_PAGE)");
    sqlBuilder.append(" WHERE ROW_ID <= ? AND ROW_ID > ?");
    return sqlBuilder.toString();
  }
}