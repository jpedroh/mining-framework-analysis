package org.jfaster.mango.plugin.stats;
import org.jfaster.mango.annotation.DB;
import org.jfaster.mango.annotation.Sharding;
import org.jfaster.mango.sharding.NotUseTableShardingStrategy;
import org.jfaster.mango.stat.OperatorStat;
import org.jfaster.mango.util.Joiner;
import org.jfaster.mango.util.Strings;
import org.jfaster.mango.util.ToStringHelper;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author ash
 */
public class ExtendStat {
  private OperatorStat operatorStat;

  private Method method;

  public ExtendStat(OperatorStat operatorStat) {
    this.operatorStat = operatorStat;
    this.method = operatorStat.getMethod();
  }

  public String getSimpleClassName() {
    return operatorStat.getDaoClass().getSimpleName();
  }

  public String getSimpleMethodName() {
    return method.getName() + "(" + method.getParameterTypes().length + ")";
  }

  public String getSql() {
    String sql = 
<<<<<<< /usr/src/app/output/jfaster/mango/6c28ffc629752987623e939e30b2ae66e1952a2f/src/main/java/org/jfaster/mango/plugin/stats/ExtendStat.java/left.java
    Joiner.on(' ').join(Arrays.asList(method.getAnnotation(SQL.class).value()))
=======
    operatorStat.getSql()
>>>>>>> /usr/src/app/output/jfaster/mango/6c28ffc629752987623e939e30b2ae66e1952a2f/src/main/java/org/jfaster/mango/plugin/stats/ExtendStat.java/right.java
    ;
    DB dbAnno = operatorStat.getDaoClass().getAnnotation(DB.class);
    String table = dbAnno.table();
    if (Strings.isNotEmpty(table)) {
      Sharding shardingAnno = method.getAnnotation(Sharding.class);
      if (shardingAnno == null) {
        shardingAnno = operatorStat.getDaoClass().getAnnotation(Sharding.class);
      }
      if (shardingAnno != null && !NotUseTableShardingStrategy.class.equals(shardingAnno.tableShardingStrategy())) {
        table = table + "_#";
      }
      sql = sql.replaceAll("#table", table);
    }
    return sql;
  }

  public List<String> getStrParameterTypes() {
    List<String> r = new ArrayList<String>();
    for (Type type : method.getGenericParameterTypes()) {
      r.add(ToStringHelper.toString(type));
    }
    return r;
  }

  public String getType() {
    return operatorStat.getOperatorType().name().toLowerCase();
  }
}