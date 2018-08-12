package tech.tablesaw.aggregate;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.StringColumn;

/**
 * A partial implementation of aggregate functions to summarize over a date column
 */
public abstract class StringFunction extends AggregateFunction<String, StringColumn> {

    public StringFunction(String name) {
        super(name);
    }

    abstract public String summarize(StringColumn column);

    @Override
    public boolean isCompatableColumn(ColumnType type) {
        return type.equals(ColumnType.STRING);
    }

    @Override
    public ColumnType returnType() {
        return ColumnType.STRING;
    }
}
