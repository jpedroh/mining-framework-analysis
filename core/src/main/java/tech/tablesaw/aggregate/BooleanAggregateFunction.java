package tech.tablesaw.aggregate;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.ColumnType;


/**
 * A partial implementation of aggregate functions to summarize over a boolean column
 */
public abstract class BooleanAggregateFunction<C extends Column<?>> extends AggregateFunction<Boolean, C> {
    public BooleanAggregateFunction(String name) {
        super(name);
    }

    @Override
    public boolean isCompatableColumn(ColumnType type) {
        return type == ColumnType.BOOLEAN;
    }

    @Override
    public ColumnType returnType() {
        return ColumnType.BOOLEAN;
    }
}