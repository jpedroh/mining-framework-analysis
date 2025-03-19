package tech.tablesaw.aggregate;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.ColumnType;


<<<<<<< /usr/src/app/output/lwhite1/tablesaw/436f7cfe5ecfe763e7a45ce6745f1d1c52133d22/core/src/main/java/tech/tablesaw/aggregate/BooleanAggregateFunction.java/left.java
/**
 * A partial implementation of aggregate functions to summarize over a boolean column
 */
public abstract class BooleanAggregateFunction<C extends Column<?>> extends AggregateFunction<Boolean, C> {
  public BooleanAggregateFunction(String name) {
    super(name);
  }

  @Override public boolean isCompatableColumn(ColumnType type) {
    return false;
  }

  @Override public ColumnType returnType() {
    return null;
  }
}
=======
/**
 * A partial implementation of aggregate functions to summarize over a boolean column
 */
public abstract class BooleanAggregateFunction extends AggregateFunction<Boolean, BooleanColumn> {
  public BooleanAggregateFunction(String name) {
    super(name);
  }

  abstract public Boolean summarize(BooleanColumn column);

  @Override public boolean isCompatableColumn(ColumnType type) {
    return type == ColumnType.BOOLEAN;
  }

  @Override public ColumnType returnType() {
    return ColumnType.BOOLEAN;
  }
}
>>>>>>> /usr/src/app/output/lwhite1/tablesaw/436f7cfe5ecfe763e7a45ce6745f1d1c52133d22/core/src/main/java/tech/tablesaw/aggregate/BooleanAggregateFunction.java/right.java
