package tech.tablesaw.aggregate;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.ColumnType;

/**
 * A partial implementation of aggregate functions to summarize over a boolean column
 */
<<<<<<< /usr/src/app/output/lwhite1/tablesaw/436f7cfe5ecfe763e7a45ce6745f1d1c52133d22/core/src/main/java/tech/tablesaw/aggregate/BooleanAggregateFunction.java/left.java
public abstract class BooleanAggregateFunction<C extends Column<?>> extends AggregateFunction<Boolean, C> {
||||||| /usr/src/app/output/lwhite1/tablesaw/436f7cfe5ecfe763e7a45ce6745f1d1c52133d22/core/src/main/java/tech/tablesaw/aggregate/BooleanAggregateFunction.java/base.java
public abstract class BooleanAggregateFunction<C extends Column<?>> extends AggregateFunction<Boolean, Column> {
=======
public abstract class BooleanAggregateFunction<C extends Column<?>> extends AggregateFunction<Boolean, BooleanColumn> {
>>>>>>> /usr/src/app/output/lwhite1/tablesaw/436f7cfe5ecfe763e7a45ce6745f1d1c52133d22/core/src/main/java/tech/tablesaw/aggregate/BooleanAggregateFunction.java/right.java

    public BooleanAggregateFunction(String name) {
        super(name);
    }

<<<<<<< /usr/src/app/output/lwhite1/tablesaw/436f7cfe5ecfe763e7a45ce6745f1d1c52133d22/core/src/main/java/tech/tablesaw/aggregate/BooleanAggregateFunction.java/left.java
||||||| /usr/src/app/output/lwhite1/tablesaw/436f7cfe5ecfe763e7a45ce6745f1d1c52133d22/core/src/main/java/tech/tablesaw/aggregate/BooleanAggregateFunction.java/base.java
    abstract public Boolean summarize(Column column);

=======
    abstract public Boolean summarize(BooleanColumn column);

>>>>>>> /usr/src/app/output/lwhite1/tablesaw/436f7cfe5ecfe763e7a45ce6745f1d1c52133d22/core/src/main/java/tech/tablesaw/aggregate/BooleanAggregateFunction.java/right.java
    @Override
    public boolean isCompatableColumn(ColumnType type) {
        return type == ColumnType.BOOLEAN;
    }

    @Override
    public ColumnType returnType() {
        return ColumnType.BOOLEAN;
    }
}
