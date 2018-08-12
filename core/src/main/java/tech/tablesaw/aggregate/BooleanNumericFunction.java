package tech.tablesaw.aggregate;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.ColumnType;

abstract class BooleanNumericFunction extends AggregateFunction<Double, BooleanColumn> {

    public BooleanNumericFunction(String functionName) {
        super(functionName);
    }

    @Override
    abstract public Double summarize(BooleanColumn column);

    @Override
    public boolean isCompatableColumn(ColumnType type) {
        return type.equals(ColumnType.BOOLEAN);
    }

    @Override
    public ColumnType returnType() {
        return null;
    }
}
