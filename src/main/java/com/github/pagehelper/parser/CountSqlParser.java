package com.github.pagehelper.parser;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import java.util.ArrayList;
import java.util.List;

/**
 * sql解析类，提供更智能的count查询sql
 *
 * @author liuzh
 */
public class CountSqlParser {
  public static final String KEEP_ORDERBY = "/*keep orderby*/";

  private static final List<SelectItem> COUNT_ITEM;

  private static final Alias TABLE_ALIAS;

  static {
    COUNT_ITEM = new ArrayList<SelectItem>();
    COUNT_ITEM.add(new SelectExpressionItem(new Column("count(0)")));
    TABLE_ALIAS = new Alias("table_count");
    TABLE_ALIAS.setUseAs(false);
  }

  public void isSupportedSql(String sql) {
    if (sql.trim().toUpperCase().endsWith("FOR UPDATE")) {
      throw new RuntimeException("\u5206\u9875\u63d2\u4ef6\u4e0d\u652f\u6301\u5305\u542bfor update\u7684sql");
    }
  }

  /**
     * 获取智能的countSql
     *
     * @param sql
     * @return
     */
  public String getSmartCountSql(String sql) {
    isSupportedSql(sql);
    Statement stmt = null;
    if (sql.indexOf(KEEP_ORDERBY) >= 0) {
      return getSimpleCountSql(sql);
    }
    try {
      stmt = CCJSqlParserUtil.parse(sql);
    } catch (Throwable e) {
      return getSimpleCountSql(sql);
    }
    Select select = (Select) stmt;
    SelectBody selectBody = select.getSelectBody();
    try {
      processSelectBody(selectBody);
    } catch (Exception e) {
      return getSimpleCountSql(sql);
    }
    processWithItemsList(select.getWithItemsList());
    sqlToCount(select);
    String result = select.toString();
    return result;
  }

  /**
     * 获取普通的Count-sql
     *
     * @param sql 原查询sql
     * @return 返回count查询sql
     */
  public String getSimpleCountSql(final String sql) {
    isSupportedSql(sql);
    StringBuilder stringBuilder = new StringBuilder(sql.length() + 40);
    stringBuilder.append("select count(0) from (");
    stringBuilder.append(sql);
    stringBuilder.append(") tmp_count");
    return stringBuilder.toString();
  }

  /**
     * 将sql转换为count查询
     *
     * @param select
     */
  public void sqlToCount(Select select) {
    SelectBody selectBody = select.getSelectBody();
    if (selectBody instanceof PlainSelect && isSimpleCount((PlainSelect) selectBody)) {
      ((PlainSelect) selectBody).setSelectItems(COUNT_ITEM);
    } else {
      PlainSelect plainSelect = new PlainSelect();
      SubSelect subSelect = new SubSelect();
      subSelect.setSelectBody(selectBody);
      subSelect.setAlias(TABLE_ALIAS);
      plainSelect.setFromItem(subSelect);
      plainSelect.setSelectItems(COUNT_ITEM);
      select.setSelectBody(plainSelect);
    }
  }

  /**
     * 是否可以用简单的count查询方式
     *
     * @param select
     * @return
     */
  public boolean isSimpleCount(PlainSelect select) {
    if (select.getGroupByColumnReferences() != null) {
      return false;
    }
    if (select.getDistinct() != null) {
      return false;
    }
    for (SelectItem item : select.getSelectItems()) {
      if (item.toString().contains("?")) {
        return false;
      }
      if (item instanceof SelectExpressionItem) {
        if (((SelectExpressionItem) item).getExpression() instanceof Function) {
          return false;
        }
      }
    }
    return true;
  }

  /**
     * 处理selectBody去除Order by
     *
     * @param selectBody
     */
  public void processSelectBody(SelectBody selectBody) {
    if (selectBody instanceof PlainSelect) {
      processPlainSelect((PlainSelect) selectBody);
    } else {
      if (selectBody instanceof WithItem) {
        WithItem withItem = (WithItem) selectBody;
        if (withItem.getSelectBody() != null) {
          processSelectBody(withItem.getSelectBody());
        }
      } else {
        SetOperationList operationList = (SetOperationList) selectBody;
        if (operationList.getSelects() != null && operationList.getSelects().size() > 0) {
          List<SelectBody> plainSelects = operationList.getSelects();
          for (SelectBody plainSelect : plainSelects) {
            processSelectBody(plainSelect);
          }
        }
        if (!orderByHashParameters(operationList.getOrderByElements())) {
          operationList.setOrderByElements(null);
        }
      }
    }
  }

  /**
     * 处理PlainSelect类型的selectBody
     *
     * @param plainSelect
     */
  public void processPlainSelect(PlainSelect plainSelect) {

<<<<<<< /usr/src/app/output/pagehelper/mybatis-pagehelper/964a5ebebae2922f93d80ac9403670905686c88c/src/main/java/com/github/pagehelper/parser/CountSqlParser.java/left.java
    if (plainSelect.getGroupByColumnReferences() != null && plainSelect.getGroupByColumnReferences().size() > 0) {
    }
=======
>>>>>>> Unknown file: This is a bug in JDime.

    if (!orderByHashParameters(plainSelect.getOrderByElements())) {
      plainSelect.setOrderByElements(null);
    }
    if (plainSelect.getFromItem() != null) {
      processFromItem(plainSelect.getFromItem());
    }
    if (plainSelect.getJoins() != null && plainSelect.getJoins().size() > 0) {
      List<Join> joins = plainSelect.getJoins();
      for (Join join : joins) {
        if (join.getRightItem() != null) {
          processFromItem(join.getRightItem());
        }
      }
    }
  }

  /**
     * 处理WithItem
     *
     * @param withItemsList
     */
  public void processWithItemsList(List<WithItem> withItemsList) {
    if (withItemsList != null && withItemsList.size() > 0) {
      for (WithItem item : withItemsList) {
        processSelectBody(item.getSelectBody());
      }
    }
  }

  /**
     * 处理子查询
     *
     * @param fromItem
     */
  public void processFromItem(FromItem fromItem) {
    if (fromItem instanceof SubJoin) {
      SubJoin subJoin = (SubJoin) fromItem;
      if (subJoin.getJoin() != null) {
        if (subJoin.getJoin().getRightItem() != null) {
          processFromItem(subJoin.getJoin().getRightItem());
        }
      }
      if (subJoin.getLeft() != null) {
        processFromItem(subJoin.getLeft());
      }
    } else {
      if (fromItem instanceof SubSelect) {
        SubSelect subSelect = (SubSelect) fromItem;
        if (subSelect.getSelectBody() != null) {
          processSelectBody(subSelect.getSelectBody());
        }
      } else {
        if (fromItem instanceof ValuesList) {
        } else {
          if (fromItem instanceof LateralSubSelect) {
            LateralSubSelect lateralSubSelect = (LateralSubSelect) fromItem;
            if (lateralSubSelect.getSubSelect() != null) {
              SubSelect subSelect = lateralSubSelect.getSubSelect();
              if (subSelect.getSelectBody() != null) {
                processSelectBody(subSelect.getSelectBody());
              }
            }
          }
        }
      }
    }
  }

  /**
     * 判断Orderby是否包含参数，有参数的不能去
     *
     * @param orderByElements
     * @return
     */
  public boolean orderByHashParameters(List<OrderByElement> orderByElements) {
    if (orderByElements == null) {
      return false;
    }
    for (OrderByElement orderByElement : orderByElements) {
      if (orderByElement.toString().contains("?")) {
        return true;
      }
    }
    return false;
  }
}