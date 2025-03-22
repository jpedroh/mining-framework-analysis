package com.github.pagehelper.parser;
import com.github.pagehelper.JSqlParser;
import com.github.pagehelper.PageException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import java.util.*;

/**
 * 将sqlserver查询语句转换为分页语句<br>
 * 注意事项：<br>
 * <ol>
 * <li>请先保证你的SQL可以执行</li>
 * <li>sql中最好直接包含order by，可以自动从sql提取</li>
 * <li>如果没有order by，可以通过入参提供，但是需要自己保证正确</li>
 * <li>如果sql有order by，可以通过orderby参数覆盖sql中的order by</li>
 * <li>order by的列名不能使用别名</li>
 * <li>表和列使用别名的时候不要使用单引号(')</li>
 * </ol>
 * 该类设计为一个独立的工具类，依赖jsqlparser,可以独立使用
 *
 * @author liuzh
 */
public class SqlServerParser {
  public static final String START_ROW = String.valueOf(Long.MIN_VALUE);

  public static final String PAGE_SIZE = String.valueOf(Long.MAX_VALUE);

  protected static final String WRAP_TABLE = "WRAP_OUTER_TABLE";

  protected static final String PAGE_TABLE_NAME = "PAGE_TABLE_ALIAS";

  public static final Alias PAGE_TABLE_ALIAS = new Alias(PAGE_TABLE_NAME);

  protected static final String PAGE_ROW_NUMBER = "PAGE_ROW_NUMBER";

  protected static final Column PAGE_ROW_NUMBER_COLUMN = new Column(PAGE_ROW_NUMBER);

  protected static final Top TOP100_PERCENT;

  protected static final String PAGE_COLUMN_ALIAS_PREFIX = "ROW_ALIAS_";

  private final JSqlParser jSqlParser;

  static {
    TOP100_PERCENT = new Top();
    TOP100_PERCENT.setExpression(new LongValue(100));
    TOP100_PERCENT.setPercentage(true);
  }

  public SqlServerParser() {
    this.jSqlParser = JSqlParser.DEFAULT;
  }

  public SqlServerParser(JSqlParser jSqlParser) {
    this.jSqlParser = jSqlParser;
  }

  /**
     * 转换为分页语句
     *
     * @param sql
     * @return
     */
  public String convertToPageSql(String sql) {
    return convertToPageSql(sql, null, null);
  }

  /**
     * 转换为分页语句
     *
     * @param sql
     * @param offset
     * @param limit
     * @return
     */
  public String convertToPageSql(String sql, Integer offset, Integer limit) {
    Statement stmt;
    try {
      stmt = jSqlParser.parse(sql, (parser) -> parser.withSquareBracketQuotation(true));
    } catch (Throwable e) {
      throw new PageException("\u4e0d\u652f\u6301\u8be5SQL\u8f6c\u6362\u4e3a\u5206\u9875\u67e5\u8be2!", e);
    }
    if (!(stmt instanceof Select)) {
      throw new PageException("\u5206\u9875\u8bed\u53e5\u5fc5\u987b\u662fSelect\u67e5\u8be2!");
    }
    Select pageSelect = getPageSelect((Select) stmt);
    String pageSql = pageSelect.toString();
    if (offset != null) {
      pageSql = pageSql.replace(START_ROW, String.valueOf(offset));
    }
    if (limit != null) {
      pageSql = pageSql.replace(PAGE_SIZE, String.valueOf(limit));
    }
    return pageSql;
  }

  /**
     * 获取一个外层包装的TOP查询
     *
     * @param select
     * @return
     */
  protected Select getPageSelect(Select select) {
    SelectBody selectBody = select.getSelectBody();
    if (selectBody instanceof SetOperationList) {
      selectBody = wrapSetOperationList((SetOperationList) selectBody);
    }
    if (((PlainSelect) selectBody).getTop() != null) {
      throw new PageException("\u88ab\u5206\u9875\u7684\u8bed\u53e5\u5df2\u7ecf\u5305\u542b\u4e86Top\uff0c\u4e0d\u80fd\u518d\u901a\u8fc7\u5206\u9875\u63d2\u4ef6\u8fdb\u884c\u5206\u9875\u67e5\u8be2!");
    }
    List<SelectItem> selectItems = getSelectItems((PlainSelect) selectBody);
    List<SelectItem> autoItems = new ArrayList<SelectItem>();
    SelectItem orderByColumn = addRowNumber((PlainSelect) selectBody, autoItems);
    ((PlainSelect) selectBody).addSelectItems(autoItems.toArray(new SelectItem[autoItems.size()]));
    processSelectBody(selectBody, 0);
    PlainSelect innerSelectBody = new PlainSelect();
    innerSelectBody.addSelectItems(orderByColumn);
    innerSelectBody.addSelectItems(selectItems.toArray(new SelectItem[selectItems.size()]));
    SubSelect fromInnerItem = new SubSelect();
    fromInnerItem.setSelectBody(selectBody);
    fromInnerItem.setAlias(PAGE_TABLE_ALIAS);
    innerSelectBody.setFromItem(fromInnerItem);
    Select newSelect = new Select();
    PlainSelect newSelectBody = new PlainSelect();
    Top top = new Top();
    top.setExpression(new LongValue(Long.MAX_VALUE));
    newSelectBody.setTop(top);
    List<OrderByElement> orderByElements = new ArrayList<OrderByElement>();
    OrderByElement orderByElement = new OrderByElement();
    orderByElement.setExpression(PAGE_ROW_NUMBER_COLUMN);
    orderByElements.add(orderByElement);
    newSelectBody.setOrderByElements(orderByElements);
    GreaterThan greaterThan = new GreaterThan();
    greaterThan.setLeftExpression(PAGE_ROW_NUMBER_COLUMN);
    greaterThan.setRightExpression(new LongValue(Long.MIN_VALUE));
    newSelectBody.setWhere(greaterThan);
    newSelectBody.setSelectItems(selectItems);
    SubSelect fromItem = new SubSelect();
    fromItem.setSelectBody(innerSelectBody);
    fromItem.setAlias(PAGE_TABLE_ALIAS);
    newSelectBody.setFromItem(fromItem);
    newSelect.setSelectBody(newSelectBody);
    if (isNotEmptyList(select.getWithItemsList())) {
      newSelect.setWithItemsList(select.getWithItemsList());
    }
    return newSelect;
  }

  /**
     * 包装SetOperationList
     *
     * @param setOperationList
     * @return
     */
  protected SelectBody wrapSetOperationList(SetOperationList setOperationList) {
    SelectBody setSelectBody = setOperationList.getSelects().get(setOperationList.getSelects().size() - 1);
    if (!(setSelectBody instanceof PlainSelect)) {
      throw new PageException("\u76ee\u524d\u65e0\u6cd5\u5904\u7406\u8be5SQL\uff0c\u60a8\u53ef\u4ee5\u5c06\u8be5SQL\u53d1\u9001\u7ed9abel533@gmail.com\u534f\u52a9\u4f5c\u8005\u89e3\u51b3!");
    }
    PlainSelect plainSelect = (PlainSelect) setSelectBody;
    PlainSelect selectBody = new PlainSelect();
    List<SelectItem> selectItems = getSelectItems(plainSelect);
    selectBody.setSelectItems(selectItems);
    SubSelect fromItem = new SubSelect();
    fromItem.setSelectBody(setOperationList);
    fromItem.setAlias(new Alias(WRAP_TABLE));
    selectBody.setFromItem(fromItem);
    if (isNotEmptyList(plainSelect.getOrderByElements())) {
      selectBody.setOrderByElements(plainSelect.getOrderByElements());
      plainSelect.setOrderByElements(null);
    }
    return selectBody;
  }

  /**
     * 获取查询列
     *
     * @param plainSelect
     * @return
     */
  protected List<SelectItem> getSelectItems(PlainSelect plainSelect) {
    List<SelectItem> selectItems = new ArrayList<SelectItem>();
    for (SelectItem selectItem : plainSelect.getSelectItems()) {
      if (selectItem instanceof SelectExpressionItem) {
        SelectExpressionItem selectExpressionItem = (SelectExpressionItem) selectItem;
        if (selectExpressionItem.getAlias() != null) {
          Column column = new Column(selectExpressionItem.getAlias().getName());
          SelectExpressionItem expressionItem = new SelectExpressionItem(column);
          selectItems.add(expressionItem);
        } else {
          if (selectExpressionItem.getExpression() instanceof Column) {
            Column column = (Column) selectExpressionItem.getExpression();
            SelectExpressionItem item = null;
            if (column.getTable() != null) {
              Column newColumn = new Column(column.getColumnName());
              item = new SelectExpressionItem(newColumn);
              selectItems.add(item);
            } else {
              selectItems.add(selectItem);
            }
          } else {
            selectItems.add(selectItem);
          }
        }
      } else {
        if (selectItem instanceof AllTableColumns) {
          selectItems.add(new AllColumns());
        } else {
          selectItems.add(selectItem);
        }
      }
    }
    for (SelectItem selectItem : selectItems) {
      if (selectItem instanceof AllColumns) {
        return Collections.singletonList(selectItem);
      }
    }
    return selectItems;
  }

  /**
     * 获取 ROW_NUMBER() 列
     *
     * @param plainSelect 原查询
     * @param autoItems   自动生成的查询列
     * @return ROW_NUMBER() 列
     */
  protected SelectItem addRowNumber(PlainSelect plainSelect, List<SelectItem> autoItems) {
    StringBuilder orderByBuilder = new StringBuilder();
    orderByBuilder.append("ROW_NUMBER() OVER (");
    if (isNotEmptyList(plainSelect.getOrderByElements())) {
      orderByBuilder.append(PlainSelect.orderByToString(getOrderByElements(plainSelect, autoItems)).substring(1));
      plainSelect.setOrderByElements(null);
    } else {
      orderByBuilder.append("ORDER BY RAND()");
    }
    orderByBuilder.append(") ");
    orderByBuilder.append(PAGE_ROW_NUMBER);
    return new SelectExpressionItem(new Column(orderByBuilder.toString()));
  }

  /**
     * 处理selectBody去除Order by
     *
     * @param selectBody
     */
  protected void processSelectBody(SelectBody selectBody, int level) {
    if (selectBody != null) {
      if (selectBody instanceof PlainSelect) {
        processPlainSelect((PlainSelect) selectBody, level + 1);
      } else {
        if (selectBody instanceof WithItem) {
          WithItem withItem = (WithItem) selectBody;
          if (withItem.getSubSelect() != null) {
            processSelectBody(withItem.getSubSelect().getSelectBody(), level + 1);
          }
        } else {
          SetOperationList operationList = (SetOperationList) selectBody;
          if (operationList.getSelects() != null && operationList.getSelects().size() > 0) {
            List<SelectBody> plainSelects = operationList.getSelects();
            for (SelectBody plainSelect : plainSelects) {
              processSelectBody(plainSelect, level + 1);
            }
          }
        }
      }
    }
  }

  /**
     * 处理PlainSelect类型的selectBody
     *
     * @param plainSelect
     */
  protected void processPlainSelect(PlainSelect plainSelect, int level) {
    if (level > 1) {
      if (isNotEmptyList(plainSelect.getOrderByElements())) {
        if (plainSelect.getTop() == null) {
          plainSelect.setTop(TOP100_PERCENT);
        }
      }
    }
    if (plainSelect.getFromItem() != null) {
      processFromItem(plainSelect.getFromItem(), level + 1);
    }
    if (plainSelect.getJoins() != null && plainSelect.getJoins().size() > 0) {
      List<Join> joins = plainSelect.getJoins();
      for (Join join : joins) {
        if (join.getRightItem() != null) {
          processFromItem(join.getRightItem(), level + 1);
        }
      }
    }
  }

  /**
     * 处理子查询
     *
     * @param fromItem
     */
  protected void processFromItem(FromItem fromItem, int level) {
    if (fromItem instanceof SubJoin) {
      SubJoin subJoin = (SubJoin) fromItem;
      if (subJoin.getJoinList() != null && subJoin.getJoinList().size() > 0) {
        for (Join join : subJoin.getJoinList()) {
          if (join.getRightItem() != null) {
            processFromItem(join.getRightItem(), level + 1);
          }
        }
      }
      if (subJoin.getLeft() != null) {
        processFromItem(subJoin.getLeft(), level + 1);
      }
    } else {
      if (fromItem instanceof SubSelect) {
        SubSelect subSelect = (SubSelect) fromItem;
        if (subSelect.getSelectBody() != null) {
          processSelectBody(subSelect.getSelectBody(), level + 1);
        }
      } else {
        if (fromItem instanceof ValuesList) {
        } else {
          if (fromItem instanceof LateralSubSelect) {
            LateralSubSelect lateralSubSelect = (LateralSubSelect) fromItem;
            if (lateralSubSelect.getSubSelect() != null) {
              SubSelect subSelect = lateralSubSelect.getSubSelect();
              if (subSelect.getSelectBody() != null) {
                processSelectBody(subSelect.getSelectBody(), level + 1);
              }
            }
          }
        }
      }
    }
  }

  /**
     * List不空
     *
     * @param list
     * @return
     */
  public boolean isNotEmptyList(List<?> list) {
    if (list == null || list.size() == 0) {
      return false;
    }
    return true;
  }

  /**
     * 复制 OrderByElement
     *
     * @param orig  原 OrderByElement
     * @param alias 新 OrderByElement 的排序要素
     * @return 复制的新 OrderByElement
     */
  protected OrderByElement cloneOrderByElement(OrderByElement orig, String alias) {
    return cloneOrderByElement(orig, new Column(alias));
  }

  /**
     * 复制 OrderByElement
     *
     * @param orig       原 OrderByElement
     * @param expression 新 OrderByElement 的排序要素
     * @return 复制的新 OrderByElement
     */
  protected OrderByElement cloneOrderByElement(OrderByElement orig, Expression expression) {
    OrderByElement element = new OrderByElement();
    element.setAsc(orig.isAsc());
    element.setAscDescPresent(orig.isAscDescPresent());
    element.setNullOrdering(orig.getNullOrdering());
    element.setExpression(expression);
    return element;
  }

  /**
     * 获取新的排序列表
     *
     * @param plainSelect 原始查询
     * @param autoItems   生成的新查询要素
     * @return 新的排序列表
     */
  protected List<OrderByElement> getOrderByElements(PlainSelect plainSelect, List<SelectItem> autoItems) {
    List<OrderByElement> orderByElements = plainSelect.getOrderByElements();
    ListIterator<OrderByElement> iterator = orderByElements.listIterator();
    OrderByElement orderByElement;
    Map<String, SelectExpressionItem> selectMap = new HashMap<String, SelectExpressionItem>();
    Set<String> aliases = new HashSet<String>();
    boolean allColumns = false;
    Set<String> allColumnsTables = new HashSet<String>();
    for (SelectItem item : plainSelect.getSelectItems()) {
      if (item instanceof SelectExpressionItem) {
        SelectExpressionItem expItem = (SelectExpressionItem) item;
        selectMap.put(expItem.getExpression().toString(), expItem);
        Alias alias = expItem.getAlias();
        if (alias != null) {
          aliases.add(alias.getName());
        }
      } else {
        if (item instanceof AllColumns) {
          allColumns = true;
        } else {
          if (item instanceof AllTableColumns) {
            allColumnsTables.add(((AllTableColumns) item).getTable().getName());
          }
        }
      }
    }
    int aliasNo = 1;
    while (iterator.hasNext()) {
      orderByElement = iterator.next();
      Expression expression = orderByElement.getExpression();
      SelectExpressionItem selectExpressionItem = selectMap.get(expression.toString());
      if (selectExpressionItem != null) {
        Alias alias = selectExpressionItem.getAlias();
        if (alias != null) {
          iterator.set(cloneOrderByElement(orderByElement, alias.getName()));
        } else {
          if (expression instanceof Column) {
            ((Column) expression).setTable(null);
          } else {
            throw new PageException("\u5217 \"" + expression + "\" \u9700\u8981\u5b9a\u4e49\u522b\u540d");
          }
        }
      } else {
        if (expression instanceof Column) {
          Table table = ((Column) expression).getTable();
          if (table == null) {
            if (allColumns || (allColumnsTables.size() == 1 && plainSelect.getJoins() == null) || aliases.contains(((Column) expression).getColumnName())) {
              continue;
            }
          } else {
            String tableName = table.getName();
            if (allColumns || allColumnsTables.contains(tableName)) {
              ((Column) expression).setTable(null);
              continue;
            }
          }
        }
        String aliasName = PAGE_COLUMN_ALIAS_PREFIX + aliasNo++;
        SelectExpressionItem item = new SelectExpressionItem();
        item.setExpression(expression);
        item.setAlias(new Alias(aliasName));
        autoItems.add(item);
        iterator.set(cloneOrderByElement(orderByElement, aliasName));
      }
    }
    return orderByElements;
  }
}