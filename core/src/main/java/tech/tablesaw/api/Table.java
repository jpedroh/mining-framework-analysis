package tech.tablesaw.api;
import static java.util.stream.Collectors.toList;
import static tech.tablesaw.aggregate.AggregateFunctions.countMissing;
import static tech.tablesaw.selection.Selection.selectNRowsAtRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntComparator;
import tech.tablesaw.aggregate.AggregateFunction;
import tech.tablesaw.aggregate.CrossTab;
import tech.tablesaw.aggregate.PivotTable;
import tech.tablesaw.aggregate.Summarizer;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.DataFrameReader;
import tech.tablesaw.io.DataFrameWriter;
import tech.tablesaw.io.DataReader;
import tech.tablesaw.io.DataWriter;
import tech.tablesaw.io.ReaderRegistry;
import tech.tablesaw.io.WriterRegistry;
import tech.tablesaw.joining.DataFrameJoiner;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;
import tech.tablesaw.sorting.Sort;
import tech.tablesaw.sorting.SortUtils;
import tech.tablesaw.sorting.comparators.IntComparatorChain;
import tech.tablesaw.table.Relation;
import tech.tablesaw.table.Rows;
import tech.tablesaw.table.StandardTableSliceGroup;
import tech.tablesaw.table.TableSliceGroup;
import java.util.stream.Stream;
import com.google.common.collect.Streams;

public class Table extends Relation implements Iterable<Row> {
  public static final ReaderRegistry defaultReaderRegistry = new ReaderRegistry();

  public static final WriterRegistry defaultWriterRegistry = new WriterRegistry();

  static {
    autoRegisterReadersAndWriters();
  }

  private final List<Column<?>> columnList = new ArrayList<>();

  private String name;

  private Table() {
  }

  private Table(String name) {
    this.name = name;
  }

  protected Table(Column<?>... columns) {
    this(null, columns);
  }

  protected Table(String name, Column<?>... columns) {
    this(name);
    for (final Column<?> column : columns) {
      this.addColumns(column);
    }
  }

  private static void autoRegisterReadersAndWriters() {
    try (ScanResult scanResult = new ClassGraph().enableAllInfo().whitelistPackages("tech.tablesaw.io").scan()) {
      List<String> classes = new ArrayList<>();
      classes.addAll(scanResult.getClassesImplementing(DataWriter.class.getName()).getNames());
      classes.addAll(scanResult.getClassesImplementing(DataReader.class.getName()).getNames());
      for (String clazz : classes) {
        try {
          Class.forName(clazz);
        } catch (ClassNotFoundException e) {
          new IllegalStateException(e);
        }
      }
    }
  }

  public static Table create() {
    return new Table();
  }

  public static Table create(String tableName) {
    return new Table(tableName);
  }

  public static Table create(Column<?>... columns) {
    return new Table(columns);
  }

  public static Table create(String name, Column<?>... columns) {
    return new Table(name, columns);
  }

  private static Sort first(String columnName, Sort.Order order) {
    return Sort.on(columnName, order);
  }

  private static Sort getSort(String... columnNames) {
    Sort key = null;
    for (String s : columnNames) {
      if (key == null) {
        key = first(s, Sort.Order.DESCEND);
      } else {
        key.next(s, Sort.Order.DESCEND);
      }
    }
    return key;
  }

  public static DataFrameReader read() {
    return new DataFrameReader(defaultReaderRegistry);
  }

  public DataFrameWriter write() {
    return new DataFrameWriter(defaultWriterRegistry, this);
  }

  @Override public Table addColumns(final Column<?>... cols) {
    for (final Column<?> c : cols) {
      validateColumn(c);
      columnList.add(c);
    }
    return this;
  }

  private void validateColumn(final Column<?> newColumn) {
    Preconditions.checkNotNull(newColumn, "Attempted to add a null to the columns in table " + name);
    List<String> stringList = new ArrayList<>();
    for (String name : columnNames()) {
      stringList.add(name.toLowerCase());
    }
    if (stringList.contains(newColumn.name().toLowerCase())) {
      String message = String.format("Cannot add column with duplicate name %s to table %s", newColumn, name);
      throw new IllegalArgumentException(message);
    }
    checkColumnSize(newColumn);
  }

  private void checkColumnSize(Column<?> newColumn) {
    if (columnCount() != 0) {
      Preconditions.checkArgument(newColumn.size() == rowCount(), "Column " + newColumn.name() + " does not have the same number of rows as the other columns in the table.");
    }
  }

  public Table insertColumn(int index, Column<?> column) {
    validateColumn(column);
    columnList.add(index, column);
    return this;
  }

  public Table replaceColumn(final int colIndex, final Column<?> newColumn) {
    removeColumns(column(colIndex));
    return insertColumn(colIndex, newColumn);
  }

  public Table replaceColumn(final String columnName, final Column<?> newColumn) {
    int colIndex = columnIndex(columnName);
    return replaceColumn(colIndex, newColumn);
  }

  @Override public Table setName(String name) {
    this.name = name;
    return this;
  }

  @Override public Column<?> column(int columnIndex) {
    return columnList.get(columnIndex);
  }

  @Override public int columnCount() {
    return columnList.size();
  }

  @Override public int rowCount() {
    int result = 0;
    if (!columnList.isEmpty()) {
      result = columnList.get(0).size();
    }
    return result;
  }

  @Override public List<Column<?>> columns() {
    return columnList;
  }

  public Column<?>[] columnArray() {
    return columnList.toArray(new Column<?>[columnCount()]);
  }

  public List<CategoricalColumn<?>> categoricalColumns(String... columnNames) {
    List<CategoricalColumn<?>> columns = new ArrayList<>();
    for (String columnName : columnNames) {
      columns.add(categoricalColumn(columnName));
    }
    return columns;
  }

  public int columnIndex(String columnName) {
    int columnIndex = -1;
    for (int i = 0; i < columnList.size(); i++) {
      if (columnList.get(i).name().equalsIgnoreCase(columnName)) {
        columnIndex = i;
        break;
      }
    }
    if (columnIndex == -1) {
      throw new IllegalArgumentException(String.format("Column %s is not present in table %s", columnName, name));
    }
    return columnIndex;
  }

  public int columnIndex(Column<?> column) {
    int columnIndex = -1;
    for (int i = 0; i < columnList.size(); i++) {
      if (columnList.get(i).equals(column)) {
        columnIndex = i;
        break;
      }
    }
    if (columnIndex == -1) {
      throw new IllegalArgumentException(String.format("Column %s is not present in table %s", column.name(), name));
    }
    return columnIndex;
  }

  @Override public String name() {
    return name;
  }

  public List<String> columnNames() {
    return columnList.stream().map(Column::name).collect(toList());
  }

  public Table copy() {
    Table copy = new Table(name);
    for (Column<?> column : columnList) {
      copy.addColumns(column.emptyCopy(rowCount()));
    }
    int[] rows = new int[rowCount()];
    for (int i = 0; i < rowCount(); i++) {
      rows[i] = i;
    }
    Rows.copyRowsToTable(rows, this, copy);
    return copy;
  }

  public Table emptyCopy() {
    Table copy = new Table(name);
    for (Column<?> column : columnList) {
      copy.addColumns(column.emptyCopy());
    }
    return copy;
  }

  public Table emptyCopy(int rowSize) {
    Table copy = new Table(name);
    for (Column<?> column : columnList) {
      copy.addColumns(column.emptyCopy(rowSize));
    }
    return copy;
  }

  public Table[] sampleSplit(double table1Proportion) {
    Table[] tables = new Table[2];
    int table1Count = (int) Math.round(rowCount() * table1Proportion);
    Selection table2Selection = new BitmapBackedSelection();
    for (int i = 0; i < rowCount(); i++) {
      table2Selection.add(i);
    }
    Selection table1Selection = new BitmapBackedSelection();
    Selection table1Records = selectNRowsAtRandom(table1Count, rowCount());
    for (int table1Record : table1Records) {
      table1Selection.add(table1Record);
    }
    table2Selection.andNot(table1Selection);
    tables[0] = where(table1Selection);
    tables[1] = where(table2Selection);
    return tables;
  }

  public Table[] stratifiedSampleSplit(CategoricalColumn column, double table1Proportion) {
    Preconditions.checkArgument(containsColumn(column), "The categorical column must be part of the table, you can create a string column and add it to this table before sampling.");
    final Table first = emptyCopy();
    final Table second = emptyCopy();
    splitOn(column).asTableList().forEach((tab) -> {
      Table[] splits = tab.sampleSplit(table1Proportion);
      first.append(splits[0]);
      second.append(splits[1]);
    });
    return new Table[] { first, second };
  }

  public Table sampleX(double proportion) {
    Preconditions.checkArgument(proportion <= 1 && proportion >= 0, "The sample proportion must be between 0 and 1");
    int tableSize = (int) Math.round(rowCount() * proportion);
    return where(selectNRowsAtRandom(tableSize, rowCount()));
  }

  public Table sampleN(int nRows) {
    Preconditions.checkArgument(nRows > 0 && nRows < rowCount(), "The number of rows sampled must be greater than 0 and less than the number of rows in the table.");
    return where(selectNRowsAtRandom(nRows, rowCount()));
  }

  @Override public void clear() {
    columnList.forEach(Column::clear);
  }

  public Table first(int nRows) {
    int newRowCount = Math.min(nRows, rowCount());
    return inRange(0, newRowCount);
  }

  public Table last(int nRows) {
    int newRowCount = Math.min(nRows, rowCount());
    return inRange(rowCount() - newRowCount, rowCount());
  }

  public Table sortOn(int... columnIndexes) {
    List<String> names = new ArrayList<>();
    for (int i : columnIndexes) {
      if (i >= 0) {
        names.add(columnList.get(i).name());
      } else {
        names.add("-" + columnList.get(-i).name());
      }
    }
    return sortOn(names.toArray(new String[names.size()]));
  }

  public Table sortOn(String... columnNames) {
    Sort key = null;
    List<String> names = columnNames().stream().map(String::toUpperCase).collect(toList());
    for (String columnName : columnNames) {
      Sort.Order order = Sort.Order.ASCEND;
      if (!names.contains(columnName.toUpperCase())) {
        String prefix = columnName.substring(0, 1);
        columnName = columnName.substring(1, columnName.length());
        order = getOrder(prefix);
      }
      if (key == null) {
        key = first(columnName, order);
      } else {
        key.next(columnName, order);
      }
    }
    return sortOn(key);
  }

  private Sort.Order getOrder(String prefix) {
    Sort.Order order;
    switch (prefix) {
      case "+":
      order = Sort.Order.ASCEND;
      break;
      case "-":
      order = Sort.Order.DESCEND;
      break;
      default:
      throw new IllegalStateException("Column prefix: " + prefix + " is unknown.");
    }
    return order;
  }

  public Table sortAscendingOn(String... columnNames) {
    return this.sortOn(columnNames);
  }

  public Table sortDescendingOn(String... columnNames) {
    Sort key = getSort(columnNames);
    return sortOn(key);
  }

  public Table sortOn(Sort key) {
    Preconditions.checkArgument(!key.isEmpty());
    if (key.size() == 1) {
      IntComparator comparator = SortUtils.getComparator(this, key);
      return sortOn(comparator);
    }
    IntComparatorChain chain = SortUtils.getChain(this, key);
    return sortOn(chain);
  }

  private Table sortOn(IntComparator rowComparator) {
    Table newTable = emptyCopy(rowCount());
    int[] newRows = rows();
    IntArrays.parallelQuickSort(newRows, rowComparator);
    Rows.copyRowsToTable(newRows, this, newTable);
    return newTable;
  }

  public Table sortOn(Comparator<Row> rowComparator) {
    Row row1 = new Row(this);
    Row row2 = new Row(this);
    return sortOn((IntComparator) (k1, k2) -> {
      row1.at(k1);
      row2.at(k2);
      return rowComparator.compare(row1, row2);
    });
  }

  private int[] rows() {
    int[] rowIndexes = new int[rowCount()];
    for (int i = 0; i < rowCount(); i++) {
      rowIndexes[i] = i;
    }
    return rowIndexes;
  }

  public void addRow(int rowIndex, Table sourceTable) {
    for (int i = 0; i < columnCount(); i++) {
      column(i).appendObj(sourceTable.column(i).get(rowIndex));
    }
  }

  public void addRow(Row row) {
    for (int i = 0; i < row.columnCount(); i++) {
      column(i).appendObj(row.getObject(i));
    }
  }

  public Row row(int rowIndex) {
    Row row = new Row(Table.this);
    row.at(rowIndex);
    return row;
  }

  public Table rows(int... rowNumbers) {
    Preconditions.checkArgument(Ints.max(rowNumbers) <= rowCount());
    return where(Selection.with(rowNumbers));
  }

  public Table dropRows(int... rowNumbers) {
    Preconditions.checkArgument(Ints.max(rowNumbers) <= rowCount());
    Selection selection = Selection.withRange(0, rowCount()).andNot(Selection.with(rowNumbers));
    return where(selection);
  }

  public Table inRange(int rowStart, int rowEnd) {
    Preconditions.checkArgument(rowEnd <= rowCount());
    return where(Selection.withRange(rowStart, rowEnd));
  }

  public Table dropRange(int rowStart, int rowEnd) {
    Preconditions.checkArgument(rowEnd <= rowCount());
    return where(Selection.withoutRange(0, rowCount(), rowStart, rowEnd));
  }

  public Table where(Selection selection) {
    Table newTable = this.emptyCopy(selection.size());
    Rows.copyRowsToTable(selection, this, newTable);
    return newTable;
  }

  public Table dropWhere(Selection selection) {
    Selection opposite = new BitmapBackedSelection();
    opposite.addRange(0, rowCount());
    opposite.andNot(selection);
    Table newTable = this.emptyCopy(opposite.size());
    Rows.copyRowsToTable(opposite, this, newTable);
    return newTable;
  }

  public Table pivot(CategoricalColumn<?> column1, CategoricalColumn<?> column2, NumberColumn<?> column3, AggregateFunction<?, ?> aggregateFunction) {
    return PivotTable.pivot(this, column1, column2, column3, aggregateFunction);
  }

  public Table pivot(String column1Name, String column2Name, String column3Name, AggregateFunction<?, ?> aggregateFunction) {
    return pivot(categoricalColumn(column1Name), categoricalColumn(column2Name), numberColumn(column3Name), aggregateFunction);
  }

  public TableSliceGroup splitOn(String... columns) {
    return splitOn(categoricalColumns(columns).toArray(new CategoricalColumn<?>[columns.length]));
  }

  public TableSliceGroup splitOn(CategoricalColumn<?>... columns) {
    return StandardTableSliceGroup.create(this, columns);
  }

  public Table structure() {
    Table t = new Table("Structure of " + name());
    IntColumn index = IntColumn.indexColumn("Index", columnCount(), 0);
    StringColumn columnName = StringColumn.create("Column Name", columnCount());
    StringColumn columnType = StringColumn.create("Column Type", columnCount());
    t.addColumns(index);
    t.addColumns(columnName);
    t.addColumns(columnType);
    for (int i = 0; i < columnCount(); i++) {
      Column<?> column = columnList.get(i);
      columnType.set(i, column.type().name());
      columnName.set(i, columnNames().get(i));
    }
    return t;
  }

  public Table dropDuplicateRows() {
    Table sorted = this.sortOn(columnNames().toArray(new String[columns().size()]));
    Table temp = emptyCopy();
    for (int row = 0; row < rowCount(); row++) {
      if (temp.isEmpty() || !Rows.compareRows(row, sorted, temp)) {
        Rows.appendRowToTable(row, sorted, temp);
      }
    }
    return temp;
  }

  public Table dropRowsWithMissingValues() {
    Selection missing = new BitmapBackedSelection();
    for (int row = 0; row < rowCount(); row++) {
      for (int col = 0; col < columnCount(); col++) {
        Column<?> c = column(col);
        if (c.isMissing(row)) {
          missing.add(row);
          break;
        }
      }
    }
    Selection notMissing = Selection.withRange(0, rowCount());
    notMissing.andNot(missing);
    Table temp = emptyCopy(notMissing.size());
    Rows.copyRowsToTable(notMissing, this, temp);
    return temp;
  }

  public Table select(Column<?>... columns) {
    return new Table(this.name, columns);
  }

  public Table select(String... columnNames) {
    return Table.create(this.name, columns(columnNames).toArray(new Column<?>[0]));
  }

  @Override public Table removeColumns(Column<?>... columns) {
    columnList.removeAll(Arrays.asList(columns));
    return this;
  }

  public Table removeColumnsWithMissingValues() {
    removeColumns(columnList.stream().filter((x) -> x.countMissing() > 0).toArray(Column<?>[]::new));
    return this;
  }

  public Table retainColumns(Column<?>... columns) {
    List<Column<?>> retained = Arrays.asList(columns);
    columnList.clear();
    columnList.addAll(retained);
    return this;
  }

  public Table retainColumns(String... columnNames) {
    List<Column<?>> retained = columns(columnNames);
    columnList.clear();
    columnList.addAll(retained);
    return this;
  }

  @SuppressWarnings(value = { "rawtypes", "unchecked" }) public Table append(Table tableToAppend) {
    for (final Column column : columnList) {
      final Column columnToAppend = tableToAppend.column(column.name());
      column.append(columnToAppend);
    }
    return this;
  }

  public Table concat(Table tableToConcatenate) {
    Preconditions.checkArgument(tableToConcatenate.rowCount() == this.rowCount(), "Both tables must have the same number of rows to concatenate them.");
    for (Column<?> column : tableToConcatenate.columns()) {
      this.addColumns(column);
    }
    return this;
  }

  public Summarizer summarize(String columName, AggregateFunction<?, ?>... functions) {
    return summarize(column(columName), functions);
  }

  public Summarizer summarize(List<String> columnNames, AggregateFunction<?, ?>... functions) {
    return new Summarizer(this, columnNames, functions);
  }

  public Summarizer summarize(String numericColumn1Name, String numericColumn2Name, AggregateFunction<?, ?>... functions) {
    return summarize(column(numericColumn1Name), column(numericColumn2Name), functions);
  }

  public Summarizer summarize(String col1Name, String col2Name, String col3Name, AggregateFunction<?, ?>... functions) {
    return summarize(column(col1Name), column(col2Name), column(col3Name), functions);
  }

  public Summarizer summarize(String col1Name, String col2Name, String col3Name, String col4Name, AggregateFunction<?, ?>... functions) {
    return summarize(column(col1Name), column(col2Name), column(col3Name), column(col4Name), functions);
  }

  public Summarizer summarize(Column<?> numberColumn, AggregateFunction<?, ?>... function) {
    return new Summarizer(this, numberColumn, function);
  }

  public Summarizer summarize(Column<?> column1, Column<?> column2, AggregateFunction<?, ?>... function) {
    return new Summarizer(this, column1, column2, function);
  }

  public Summarizer summarize(Column<?> column1, Column<?> column2, Column<?> column3, AggregateFunction<?, ?>... function) {
    return new Summarizer(this, column1, column2, column3, function);
  }

  public Summarizer summarize(Column<?> column1, Column<?> column2, Column<?> column3, Column<?> column4, AggregateFunction<?, ?>... function) {
    return new Summarizer(this, column1, column2, column3, column4, function);
  }

  public Table xTabCounts(String column1Name, String column2Name) {
    return CrossTab.counts(this, categoricalColumn(column1Name), categoricalColumn(column2Name));
  }

  public Table xTabRowPercents(String column1Name, String column2Name) {
    return CrossTab.rowPercents(this, column1Name, column2Name);
  }

  public Table xTabColumnPercents(String column1Name, String column2Name) {
    return CrossTab.columnPercents(this, column1Name, column2Name);
  }

  public Table xTabTablePercents(String column1Name, String column2Name) {
    return CrossTab.tablePercents(this, column1Name, column2Name);
  }

  public Table xTabPercents(String column1Name) {
    return CrossTab.percents(this, column1Name);
  }

  public Table xTabCounts(String column1Name) {
    return CrossTab.counts(this, column1Name);
  }

  public Table countBy(CategoricalColumn<?> groupingColumn) {
    return groupingColumn.countByCategory();
  }

  public Table missingValueCounts() {
    return summarize(columnNames(), countMissing).apply();
  }

  @Override public Iterator<Row> iterator() {
    return new Iterator<Row>() {
      final private Row row = new Row(Table.this);

      @Override public Row next() {
        return row.next();
      }

      @Override public boolean hasNext() {
        return row.hasNext();
      }
    };
  }

  public void doWithRows(Consumer<Row> doable) {
    stream().forEach(doable);
  }

  public boolean detect(Predicate<Row> predicate) {
    return stream().anyMatch(predicate);
  }

  public void stepWithRows(Consumer<Row[]> rowConsumer, int n) {
    if (isEmpty()) {
      return;
    }
    Row[] rows = new Row[n];
    for (int i = 0; i < n; i++) {
      rows[i] = new Row(this);
    }
    int max = rowCount() / n;
    for (int i = 0; i < max; i++) {
      for (int r = 1; r <= n; r++) {
        int row = i * n + r - 1;
        rows[r - 1].at(row);
      }
      rowConsumer.accept(rows);
    }
  }

  public void doWithRows(Pairs pairs) {
    if (isEmpty()) {
      return;
    }
    Row row1 = new Row(this);
    Row row2 = new Row(this);
    int max = rowCount();
    for (int i = 1; i < max; i++) {
      row1.at(i - 1);
      row2.at(i);
      pairs.doWithPair(row1, row2);
    }
  }

  public void doWithRowPairs(Consumer<RowPair> pairConsumer) {
    if (isEmpty()) {
      return;
    }
    Row row1 = new Row(this);
    Row row2 = new Row(this);
    RowPair pair = new RowPair(row1, row2);
    int max = rowCount();
    for (int i = 1; i < max; i++) {
      row1.at(i - 1);
      row2.at(i);
      pairConsumer.accept(pair);
    }
  }

  public void rollWithRows(Consumer<Row[]> rowConsumer, int n) {
    if (isEmpty()) {
      return;
    }
    Row[] rows = new Row[n];
    for (int i = 0; i < n; i++) {
      rows[i] = new Row(this);
    }
    int max = rowCount() - (n - 2);
    for (int i = 1; i < max; i++) {
      for (int r = 0; r < n; r++) {
        rows[r].at(i + r - 1);
      }
      rowConsumer.accept(rows);
    }
  }

  public static class RowPair {
    private final Row first;

    private final Row second;

    public RowPair(Row first, Row second) {
      this.first = first;
      this.second = second;
    }

    public Row getFirst() {
      return first;
    }

    public Row getSecond() {
      return second;
    }
  }

  interface Pairs {
    void doWithPair(Row row1, Row row2);

    default Object getResult() {
      throw new UnsupportedOperationException("This Pairs function returns no results");
    }
  }

  public Table[] stratifiedSampleSplit(CategoricalColumn<?> column, double table1Proportion) {
    Preconditions.checkArgument(containsColumn(column), "The categorical column must be part of the table, you can create a string column and add it to this table before sampling.");
    final Table first = emptyCopy();
    final Table second = emptyCopy();
    splitOn(column).asTableList().forEach((tab) -> {
      Table[] splits = tab.sampleSplit(table1Proportion);
      first.append(splits[0]);
      second.append(splits[1]);
    });
    return new Table[] { first, second };
  }

  public Table inRange(int rowCount) {
    Preconditions.checkArgument(rowCount <= rowCount());
    int rowStart = rowCount >= 0 ? 0 : rowCount() + rowCount;
    int rowEnd = rowCount >= 0 ? rowCount : rowCount();
    return where(Selection.withRange(rowStart, rowEnd));
  }

  public Table dropRange(int rowCount) {
    Preconditions.checkArgument(rowCount <= rowCount());
    int rowStart = rowCount >= 0 ? rowCount : 0;
    int rowEnd = rowCount >= 0 ? rowCount() : rowCount() + rowCount;
    return where(Selection.withRange(rowStart, rowEnd));
  }

  public DataFrameJoiner joinOn(String... columnNames) {
    return new DataFrameJoiner(this, columnNames);
  }

  public Stream<Row> stream() {
    return Streams.stream(iterator());
  }
}