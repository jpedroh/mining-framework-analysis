package org.jooq.impl;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.Character.isJavaIdentifierPart;
import static org.jooq.SQLDialect.CUBRID;
import static org.jooq.SQLDialect.DERBY;
import static org.jooq.SQLDialect.FIREBIRD;
import static org.jooq.SQLDialect.HSQLDB;
import static org.jooq.SQLDialect.MARIADB;
import static org.jooq.SQLDialect.MYSQL;
import static org.jooq.SQLDialect.POSTGRES;
import static org.jooq.SQLDialect.SQLITE;
import static org.jooq.conf.BackslashEscaping.DEFAULT;
import static org.jooq.conf.BackslashEscaping.ON;
import static org.jooq.conf.ParamType.INLINED;
import static org.jooq.conf.ParamType.NAMED;
import static org.jooq.conf.ParamType.NAMED_OR_INLINED;
import static org.jooq.conf.SettingsTools.getBackslashEscaping;
import static org.jooq.conf.SettingsTools.reflectionCaching;
import static org.jooq.conf.SettingsTools.updatablePrimaryKeys;
import static org.jooq.conf.ThrowExceptions.THROW_FIRST;
import static org.jooq.conf.ThrowExceptions.THROW_NONE;
import static org.jooq.impl.DDLStatementType.ALTER_INDEX;
import static org.jooq.impl.DDLStatementType.ALTER_SEQUENCE;
import static org.jooq.impl.DDLStatementType.ALTER_TABLE;
import static org.jooq.impl.DDLStatementType.ALTER_VIEW;
import static org.jooq.impl.DDLStatementType.CREATE_INDEX;
import static org.jooq.impl.DDLStatementType.CREATE_SCHEMA;
import static org.jooq.impl.DDLStatementType.CREATE_SEQUENCE;
import static org.jooq.impl.DDLStatementType.CREATE_TABLE;
import static org.jooq.impl.DDLStatementType.CREATE_VIEW;
import static org.jooq.impl.DDLStatementType.DROP_INDEX;
import static org.jooq.impl.DDLStatementType.DROP_SCHEMA;
import static org.jooq.impl.DDLStatementType.DROP_SEQUENCE;
import static org.jooq.impl.DDLStatementType.DROP_TABLE;
import static org.jooq.impl.DDLStatementType.DROP_VIEW;
import static org.jooq.impl.DSL.concat;
import static org.jooq.impl.DSL.escape;
import static org.jooq.impl.DSL.getDataType;
import static org.jooq.impl.DSL.keyword;
import static org.jooq.impl.DSL.name;
import static org.jooq.impl.DSL.nullSafe;
import static org.jooq.impl.DSL.val;
import static org.jooq.impl.DefaultExecuteContext.localConnection;
import static org.jooq.impl.Identifiers.QUOTES;
import static org.jooq.impl.Identifiers.QUOTE_END_DELIMITER;
import static org.jooq.impl.Identifiers.QUOTE_END_DELIMITER_ESCAPED;
import static org.jooq.impl.Identifiers.QUOTE_START_DELIMITER;
import static org.jooq.impl.Keywords.K_AS;
import static org.jooq.impl.Keywords.K_ATOMIC;
import static org.jooq.impl.Keywords.K_AUTOINCREMENT;
import static org.jooq.impl.Keywords.K_AUTO_INCREMENT;
import static org.jooq.impl.Keywords.K_BEGIN;
import static org.jooq.impl.Keywords.K_BEGIN_CATCH;
import static org.jooq.impl.Keywords.K_BEGIN_TRY;
import static org.jooq.impl.Keywords.K_COLLATE;
import static org.jooq.impl.Keywords.K_DECLARE;
import static org.jooq.impl.Keywords.K_DEFAULT;
import static org.jooq.impl.Keywords.K_DO;
import static org.jooq.impl.Keywords.K_ELSE;
import static org.jooq.impl.Keywords.K_ELSIF;
import static org.jooq.impl.Keywords.K_END;
import static org.jooq.impl.Keywords.K_END_CATCH;
import static org.jooq.impl.Keywords.K_END_IF;
import static org.jooq.impl.Keywords.K_END_TRY;
import static org.jooq.impl.Keywords.K_ENUM;
import static org.jooq.impl.Keywords.K_EXCEPTION;
import static org.jooq.impl.Keywords.K_EXEC;
import static org.jooq.impl.Keywords.K_EXECUTE_BLOCK;
import static org.jooq.impl.Keywords.K_EXECUTE_IMMEDIATE;
import static org.jooq.impl.Keywords.K_EXECUTE_STATEMENT;
import static org.jooq.impl.Keywords.K_GENERATED_BY_DEFAULT_AS_IDENTITY;
import static org.jooq.impl.Keywords.K_IDENTITY;
import static org.jooq.impl.Keywords.K_IF;
import static org.jooq.impl.Keywords.K_INT;
import static org.jooq.impl.Keywords.K_LIKE;
import static org.jooq.impl.Keywords.K_NOT;
import static org.jooq.impl.Keywords.K_NOT_NULL;
import static org.jooq.impl.Keywords.K_NULL;
import static org.jooq.impl.Keywords.K_NVARCHAR;
import static org.jooq.impl.Keywords.K_PRIMARY_KEY;
import static org.jooq.impl.Keywords.K_RAISE;
import static org.jooq.impl.Keywords.K_RAISERROR;
import static org.jooq.impl.Keywords.K_SERIAL;
import static org.jooq.impl.Keywords.K_SERIAL8;
import static org.jooq.impl.Keywords.K_START_WITH;
import static org.jooq.impl.Keywords.K_THEN;
import static org.jooq.impl.Keywords.K_THROW;
import static org.jooq.impl.Keywords.K_WHEN;
import static org.jooq.impl.SQLDataType.VARCHAR;
import static org.jooq.impl.Tools.DataCacheKey.DATA_REFLECTION_CACHE_GET_ANNOTATED_GETTER;
import static org.jooq.impl.Tools.DataCacheKey.DATA_REFLECTION_CACHE_GET_ANNOTATED_MEMBERS;
import static org.jooq.impl.Tools.DataCacheKey.DATA_REFLECTION_CACHE_GET_ANNOTATED_SETTERS;
import static org.jooq.impl.Tools.DataCacheKey.DATA_REFLECTION_CACHE_GET_MATCHING_GETTER;
import static org.jooq.impl.Tools.DataCacheKey.DATA_REFLECTION_CACHE_GET_MATCHING_MEMBERS;
import static org.jooq.impl.Tools.DataCacheKey.DATA_REFLECTION_CACHE_GET_MATCHING_SETTERS;
import static org.jooq.impl.Tools.DataCacheKey.DATA_REFLECTION_CACHE_HAS_COLUMN_ANNOTATIONS;
import static org.jooq.impl.Tools.DataKey.DATA_BLOCK_NESTING;
import static org.jooq.tools.reflect.Reflect.accessible;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinPool.ManagedBlocker;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.jooq.Attachable;
import org.jooq.BindContext;
import org.jooq.Catalog;
import org.jooq.Clause;
import org.jooq.CommonTableExpression;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.EnumType;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.OrderField;
import org.jooq.Param;
import org.jooq.Query;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.RecordType;
import org.jooq.RenderContext;
import org.jooq.RenderContext.CastMode;
import org.jooq.Result;
import org.jooq.ResultOrRows;
import org.jooq.Results;
import org.jooq.Row;
import org.jooq.RowN;
import org.jooq.SQLDialect;
import org.jooq.Schema;
import org.jooq.Select;
import org.jooq.SortField;
import org.jooq.Table;
import org.jooq.TableRecord;
import org.jooq.UDT;
import org.jooq.UDTRecord;
import org.jooq.UpdatableRecord;
import org.jooq.conf.BackslashEscaping;
import org.jooq.conf.Settings;
import org.jooq.conf.ThrowExceptions;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.MappingException;
import org.jooq.exception.NoDataFoundException;
import org.jooq.exception.TooManyRowsException;
import org.jooq.impl.ResultsImpl.ResultOrRowsImpl;
import org.jooq.impl.Tools.Cache.CachedOperation;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.StringUtils;
import org.jooq.tools.jdbc.JDBCUtils;
import org.jooq.tools.reflect.Reflect;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.jooq.types.UShort;

/**
 * General internal jOOQ utilities
 *
 * @author Lukas Eder
 */
final class Tools {
  static final JooqLogger log = JooqLogger.getLogger(Tools.class);

  static final byte[] EMPTY_BYTE = {  };

  static final Class<?>[] EMPTY_CLASS = {  };

  static final Clause[] EMPTY_CLAUSE = {  };

  static final Collection<?>[] EMPTY_COLLECTION = {  };

  static final CommonTableExpression<?>[] EMPTY_COMMON_TABLE_EXPRESSION = {  };

  static final ExecuteListener[] EMPTY_EXECUTE_LISTENER = {  };

  static final Field<?>[] EMPTY_FIELD = {  };

  static final int[] EMPTY_INT = {  };

  static final Name[] EMPTY_NAME = {  };

  static final Param<?>[] EMPTY_PARAM = {  };

  static final Query[] EMPTY_QUERY = {  };

  static final QueryPart[] EMPTY_QUERYPART = {  };

  static final Record[] EMPTY_RECORD = {  };

  static final RowN[] EMPTY_ROWN = {  };

  static final SortField<?>[] EMPTY_SORTFIELD = {  };

  static final String[] EMPTY_STRING = {  };

  static final Table<?>[] EMPTY_TABLE = {  };

  static final TableRecord<?>[] EMPTY_TABLE_RECORD = {  };

  static final UpdatableRecord<?>[] EMPTY_UPDATABLE_RECORD = {  };

  enum DataKey {
    DATA_OMIT_RETURNING_CLAUSE,
    DATA_ROW_VALUE_EXPRESSION_PREDICATE_SUBQUERY,
    DATA_LOCK_ROWS_FOR_UPDATE,
    DATA_COUNT_BIND_VALUES,
    DATA_FORCE_STATIC_STATEMENT,
    DATA_OMIT_CLAUSE_EVENT_EMISSION,
    DATA_WRAP_DERIVED_TABLES_IN_PARENTHESES,
    DATA_WINDOW_DEFINITIONS,
    DATA_DEFAULT_TRANSACTION_PROVIDER_AUTOCOMMIT,
    DATA_DEFAULT_TRANSACTION_PROVIDER_SAVEPOINTS,
    DATA_DEFAULT_TRANSACTION_PROVIDER_CONNECTION,
    DATA_OVERRIDE_ALIASES_IN_ORDER_BY,
    DATA_UNALIAS_ALIASES_IN_ORDER_BY,
    DATA_SELECT_INTO_TABLE,
    DATA_SELECT_NO_DATA,
    DATA_OMIT_INTO_CLAUSE,
    DATA_RENDER_TRAILING_LIMIT_IF_APPLICABLE,
    DATA_LIST_ALREADY_INDENTED,
    DATA_CONSTRAINT_REFERENCE,
    DATA_COLLECT_SEMI_ANTI_JOIN,
    DATA_COLLECTED_SEMI_ANTI_JOIN,
    DATA_INSERT_SELECT_WITHOUT_INSERT_COLUMN_LIST,
    DATA_BLOCK_NESTING
  }

  enum DataCacheKey {
    DATA_REFLECTION_CACHE_GET_ANNOTATED_GETTER("org.jooq.configuration.reflection-cache.get-annotated-getter"),
    DATA_REFLECTION_CACHE_GET_ANNOTATED_MEMBERS("org.jooq.configuration.reflection-cache.get-annotated-members"),
    DATA_REFLECTION_CACHE_GET_ANNOTATED_SETTERS("org.jooq.configuration.reflection-cache.get-annotated-setters"),
    DATA_REFLECTION_CACHE_GET_MATCHING_GETTER("org.jooq.configuration.reflection-cache.get-matching-getter"),
    DATA_REFLECTION_CACHE_GET_MATCHING_MEMBERS("org.jooq.configuration.reflection-cache.get-matching-members"),
    DATA_REFLECTION_CACHE_GET_MATCHING_SETTERS("org.jooq.configuration.reflection-cache.get-matching-setters"),
    DATA_REFLECTION_CACHE_HAS_COLUMN_ANNOTATIONS("org.jooq.configuration.reflection-cache.has-column-annotations"),
    DATA_CACHE_RECORD_MAPPERS("org.jooq.configuration.cache.record-mappers")
    ;

    final String key;

    private DataCacheKey(String key) {
      this.key = key;
    }
  }

  /**
     * The default escape character for <code>[a] LIKE [b] ESCAPE [...]</code>
     * clauses.
     */
  static final char ESCAPE = '!';

  /**
     * Indicating whether JPA (<code>javax.persistence</code>) is on the
     * classpath.
     */
  private static Boolean isJPAAvailable;

  /**
     * [#3696] The maximum number of consumed exceptions in
     * {@link #consumeExceptions(Configuration, PreparedStatement, SQLException)}
     * helps prevent infinite loops and {@link OutOfMemoryError}.
     */
  private static int maxConsumedExceptions = 256;

  private static int maxConsumedResults = 65536;

  /**
     * A pattern for the dash line syntax
     */
  private static final Pattern DASH_PATTERN = Pattern.compile("(-+)");

  /**
     * A pattern for the pipe line syntax
     */
  private static final Pattern PIPE_PATTERN = Pattern.compile("(?<=\\|)([^|]+)(?=\\|)");

  /**
     * A pattern for the dash line syntax
     */
  private static final Pattern PLUS_PATTERN = Pattern.compile("\\+(-+)(?=\\+)");

  /**
     * All characters that are matched by Java's interpretation of \s.
     * <p>
     * For a more accurate set of whitespaces, refer to
     * http://stackoverflow.com/a/4731164/521799. In the event of SQL
     * processing, it is probably safe to ignore most of those alternative
     * Unicode whitespaces.
     */
  private static final String WHITESPACE = " \t\n\u000b\f\r";

  /**
     * Acceptable prefixes for JDBC escape syntax.
     */
  private static final String[] JDBC_ESCAPE_PREFIXES = { "{fn ", "{d ", "{t ", "{ts " };

  /**
     * "Suffixes" that are placed behind a "?" character to form an operator,
     * rather than a JDBC bind variable. This is particularly useful to prevent
     * parsing PostgreSQL operators as bind variables, as can be seen here:
     * <a href=
     * "https://www.postgresql.org/docs/9.5/static/functions-json.html">https://www.postgresql.org/docs/current/static/functions-json.html</a>,
     * <a href=
     * "https://www.postgresql.org/docs/current/static/ltree.html">https://www.postgresql.org/docs/current/static/ltree.html</a>,
     * <a href=
     * "https://www.postgresql.org/docs/current/static/functions-geometry.html">https://www.postgresql.org/docs/current/static/functions-geometry.html</a>.
     * <p>
     * [#5307] Known PostgreSQL JSON operators:
     * <ul>
     * <li>?|</li>
     * <li>?&</li>
     * </ul>
     * <p>
     * [#7035] Known PostgreSQL LTREE operators:
     * <ul>
     * <li>? (we cannot handle this one)</li>
     * <li>?@&gt;</li>
     * <li>?&lt;@</li>
     * <li>?~</li>
     * <li>?@</li>
     * </ul>
     * <p>
     * [#7037] Known PostgreSQL Geometry operators:
     * <ul>
     * <li>?#</li>
     * <li>?-</li>
     * <li>?|</li>
     * </ul>
     */
  private static final String[] NON_BIND_VARIABLE_SUFFIXES = { "?", "|", "&", "@", "<", "~", "#", "-" };

  /**
     * All hexadecimal digits accessible through array index, e.g.
     * <code>HEX_DIGITS[15] == 'f'</code>.
     */
  private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();

  private static final EnumSet<SQLDialect> REQUIRES_BACKSLASH_ESCAPING = EnumSet.of(MARIADB, MYSQL);

  private static final EnumSet<SQLDialect> NO_SUPPORT_NULL = EnumSet.of(DERBY, FIREBIRD);

  private static final EnumSet<SQLDialect> DEFAULT_BEFORE_NULL = EnumSet.of(FIREBIRD, HSQLDB);

  private static final EnumSet<SQLDialect> SUPPORT_MYSQL_SYNTAX = EnumSet.of(MARIADB, MYSQL);

  /**
     * Turn a {@link Result} into a list of {@link Row}
     */
  static final List<Row> rows(Result<?> result) {
    List<Row> rows = new ArrayList<Row>();
    for (Record record : result) {
      rows.add(record.valuesRow());
    }
    return rows;
  }

  /**
     * Create a new record
     */
  static final <R extends Record> RecordDelegate<R> newRecord(boolean fetched, Class<R> type) {
    return newRecord(fetched, type, null);
  }

  /**
     * Create a new record
     */
  static final <R extends Record> RecordDelegate<R> newRecord(boolean fetched, Class<R> type, Field<?>[] fields) {
    return newRecord(fetched, type, fields, null);
  }

  /**
     * Create a new record
     */
  static final <R extends Record> RecordDelegate<R> newRecord(boolean fetched, Table<R> type) {
    return newRecord(fetched, type, null);
  }

  /**
     * Create a new record
     */
  @SuppressWarnings(value = { "unchecked" }) static final <R extends Record> RecordDelegate<R> newRecord(boolean fetched, Table<R> type, Configuration configuration) {
    return (RecordDelegate<R>) newRecord(fetched, type.getRecordType(), type.fields(), configuration);
  }

  /**
     * Create a new UDT record
     */
  static final <R extends UDTRecord<R>> RecordDelegate<R> newRecord(boolean fetched, UDT<R> type) {
    return newRecord(fetched, type, null);
  }

  /**
     * Create a new UDT record
     */
  static final <R extends UDTRecord<R>> RecordDelegate<R> newRecord(boolean fetched, UDT<R> type, Configuration configuration) {
    return newRecord(fetched, type.getRecordType(), type.fields(), configuration);
  }

  /**
     * Create a new record.
     */
  static final <R extends Record> RecordDelegate<R> newRecord(boolean fetched, Class<R> type, Field<?>[] fields, Configuration configuration) {
    return newRecord(fetched, recordFactory(type, fields), configuration);
  }

  /**
     * Create a new record.
     */
  static final <R extends Record> RecordDelegate<R> newRecord(boolean fetched, RecordFactory<R> factory, Configuration configuration) {
    try {
      R record = factory.newInstance();
      if (record instanceof AbstractRecord) {
        ((AbstractRecord) record).fetched = fetched;
      }
      return new RecordDelegate<R>(configuration, record);
    } catch (Exception e) {
      throw new IllegalStateException("Could not construct new record", e);
    }
  }

  /**
     * Create a new record factory.
     */
  @SuppressWarnings(value = { "unchecked", "rawtypes" }) static final <R extends Record> RecordFactory<R> recordFactory(final Class<R> type, final Field<?>[] fields) {
    if (type == RecordImpl.class || type == Record.class) {
      final RowImpl row = new RowImpl(fields);
      return new RecordFactory<R>() {
        @Override public R newInstance() {
          return (R) new RecordImpl(row);
        }
      };
    } else {
      try {
        final Constructor<R> constructor = Reflect.accessible(type.getDeclaredConstructor());
        return new RecordFactory<R>() {
          @Override public R newInstance() {
            try {
              return constructor.newInstance();
            } catch (Exception e) {
              throw new IllegalStateException("Could not construct new record", e);
            }
          }
        };
      } catch (Exception e) {
        throw new IllegalStateException("Could not construct new record", e);
      }
    }
  }

  /**
     * [#2700] [#3582] If a POJO attribute is NULL, but the column is NOT NULL
     * then we should let the database apply DEFAULT values
     */
  static final void resetChangedOnNotNull(Record record) {
    int size = record.size();
    for (int i = 0; i < size; i++) {
      if (record.get(i) == null) {
        if (!record.field(i).getDataType().nullable()) {
          record.changed(i, false);
        }
      }
    }
  }

  /**
     * Extract the configuration from an attachable.
     */
  static final Configuration getConfiguration(Attachable attachable) {
    return attachable.configuration();
  }

  /**
     * Get an attachable's configuration or a new {@link DefaultConfiguration}
     * if <code>null</code>.
     */
  static final Configuration configuration(Attachable attachable) {
    return configuration(attachable.configuration());
  }

  /**
     * Get a configuration or a new {@link DefaultConfiguration} if
     * <code>null</code>.
     */
  static final Configuration configuration(Configuration configuration) {
    return configuration != null ? configuration : new DefaultConfiguration();
  }

  /**
     * Get a configuration's settings or default settings if the configuration
     * is <code>null</code>.
     */
  static final Settings settings(Attachable attachable) {
    return configuration(attachable).settings();
  }

  /**
     * Get a configuration's settings or default settings if the configuration
     * is <code>null</code>.
     */
  static final Settings settings(Configuration configuration) {
    return configuration(configuration).settings();
  }

  static final boolean attachRecords(Configuration configuration) {
    if (configuration != null) {
      Settings settings = configuration.settings();
      if (settings != null) {
        return !FALSE.equals(settings.isAttachRecords());
      }
    }
    return true;
  }

  static final Field<?>[] fieldArray(Collection<? extends Field<?>> fields) {
    return fields == null ? null : fields.toArray(EMPTY_FIELD);
  }

  /**
     * Useful conversion method
     */
  static final Class<?>[] types(Field<?>[] fields) {
    return types(dataTypes(fields));
  }

  /**
     * Useful conversion method
     */
  static final Class<?>[] types(DataType<?>[] types) {
    if (types == null) {
      return null;
    }
    Class<?>[] result = new Class<?>[types.length];
    for (int i = 0; i < types.length; i++) {
      if (types[i] != null) {
        result[i] = types[i].getType();
      } else {
        result[i] = Object.class;
      }
    }
    return result;
  }

  /**
     * Useful conversion method
     */
  static final Class<?>[] types(Object[] values) {
    if (values == null) {
      return null;
    }
    Class<?>[] result = new Class<?>[values.length];
    for (int i = 0; i < values.length; i++) {
      if (values[i] instanceof Field<?>) {
        result[i] = ((Field<?>) values[i]).getType();
      } else {
        if (values[i] != null) {
          result[i] = values[i].getClass();
        } else {
          result[i] = Object.class;
        }
      }
    }
    return result;
  }

  /**
     * Useful conversion method
     */
  static final DataType<?>[] dataTypes(Field<?>[] fields) {
    if (fields == null) {
      return null;
    }
    DataType<?>[] result = new DataType<?>[fields.length];
    for (int i = 0; i < fields.length; i++) {
      if (fields[i] != null) {
        result[i] = fields[i].getDataType();
      } else {
        result[i] = getDataType(Object.class);
      }
    }
    return result;
  }

  /**
     * Useful conversion method
     */
  static final DataType<?>[] dataTypes(Class<?>[] types) {
    if (types == null) {
      return null;
    }
    DataType<?>[] result = new DataType<?>[types.length];
    for (int i = 0; i < types.length; i++) {
      if (types[i] != null) {
        result[i] = getDataType(types[i]);
      } else {
        result[i] = getDataType(Object.class);
      }
    }
    return result;
  }

  /**
     * Useful conversion method
     */
  static final DataType<?>[] dataTypes(Object[] values) {
    return dataTypes(types(values));
  }

  static final <T extends java.lang.Object> SortField<T> sortField(OrderField<T> field) {
    if (field instanceof SortField) {
      return (SortField<T>) field;
    } else {
      if (field instanceof Field) {
        return ((Field<T>) field).sortDefault();
      } else {
        throw new IllegalArgumentException("Field not supported : " + field);
      }
    }
  }

  static final SortField<?>[] sortFields(OrderField<?>[] fields) {
    if (fields == null) {
      return null;
    }
    if (fields instanceof SortField<?>[]) {
      return (SortField<?>[]) fields;
    }
    SortField<?>[] result = new SortField[fields.length];
    for (int i = 0; i < fields.length; i++) {
      result[i] = sortField(fields[i]);
    }
    return result;
  }

  static final List<SortField<?>> sortFields(Collection<? extends OrderField<?>> fields) {
    if (fields == null) {
      return null;
    }
    int size = fields.size();
    List<SortField<?>> result = new ArrayList<SortField<?>>(size);
    for (OrderField<?> field : fields) {
      result.add(sortField(field));
    }
    return result;
  }

  static final Name[] fieldNames(int length) {
    Name[] result = new Name[length];
    for (int i = 0; i < length; i++) {
      result[i] = name("v" + i);
    }
    return result;
  }

  static final String[] fieldNameStrings(int length) {
    String[] result = new String[length];
    for (int i = 0; i < length; i++) {
      result[i] = "v" + i;
    }
    return result;
  }

  static final Name[] fieldNames(Field<?>[] fields) {
    if (fields == null) {
      return null;
    }
    Name[] result = new Name[fields.length];
    for (int i = 0; i < fields.length; i++) {
      result[i] = fields[i].getUnqualifiedName();
    }
    return result;
  }

  static final String[] fieldNameStrings(Field<?>[] fields) {
    if (fields == null) {
      return null;
    }
    String[] result = new String[fields.length];
    for (int i = 0; i < fields.length; i++) {
      result[i] = fields[i].getName();
    }
    return result;
  }

  static final Field<?>[] fields(int length) {
    return fields(length, SQLDataType.OTHER);
  }

  @SuppressWarnings(value = { "unchecked" }) static final <T extends java.lang.Object> Field<T>[] fields(int length, DataType<T> type) {
    Field<T>[] result = new Field[length];
    Name[] names = fieldNames(length);
    for (int i = 0; i < length; i++) {
      result[i] = DSL.field(name(names[i]), type);
    }
    return result;
  }

  static final Field<?>[] aliasedFields(Field<?>[] fields, Name[] aliases) {
    if (fields == null) {
      return null;
    }
    Field<?>[] result = new Field[fields.length];
    for (int i = 0; i < fields.length; i++) {
      result[i] = fields[i].as(aliases[i]);
    }
    return result;
  }

  static final Field<?>[] fieldsByName(Collection<String> fieldNames) {
    return fieldsByName(null, fieldNames.toArray(EMPTY_STRING));
  }

  static final Field<?>[] fieldsByName(String[] fieldNames) {
    return fieldsByName(null, fieldNames);
  }

  static final Field<?>[] fieldsByName(String tableName, Collection<String> fieldNames) {
    return fieldsByName(tableName, fieldNames.toArray(EMPTY_STRING));
  }

  static final Field<?>[] fieldsByName(Name tableName, Name[] fieldNames) {
    if (fieldNames == null) {
      return null;
    }
    Field<?>[] result = new Field[fieldNames.length];
    if (tableName == null) {
      for (int i = 0; i < fieldNames.length; i++) {
        result[i] = DSL.field(fieldNames[i]);
      }
    } else {
      for (int i = 0; i < fieldNames.length; i++) {
        result[i] = DSL.field(name(tableName, fieldNames[i]));
      }
    }
    return result;
  }

  static final Field<?>[] fieldsByName(String tableName, String[] fieldNames) {
    if (fieldNames == null) {
      return null;
    }
    Field<?>[] result = new Field[fieldNames.length];
    if (StringUtils.isEmpty(tableName)) {
      for (int i = 0; i < fieldNames.length; i++) {
        result[i] = DSL.field(name(fieldNames[i]));
      }
    } else {
      for (int i = 0; i < fieldNames.length; i++) {
        result[i] = DSL.field(name(tableName, fieldNames[i]));
      }
    }
    return result;
  }

  static final Field<?>[] fieldsByName(Name[] names) {
    if (names == null) {
      return null;
    }
    Field<?>[] result = new Field[names.length];
    for (int i = 0; i < names.length; i++) {
      result[i] = DSL.field(names[i]);
    }
    return result;
  }

  static final Name[] names(String[] names) {
    if (names == null) {
      return null;
    }
    Name[] result = new Name[names.length];
    for (int i = 0; i < names.length; i++) {
      result[i] = DSL.name(names[i]);
    }
    return result;
  }

  private static final IllegalArgumentException fieldExpected(Object value) {
    return new IllegalArgumentException("Cannot interpret argument of type " + value.getClass() + " as a Field: " + value);
  }

  /**
     * Be sure that a given object is a field.
     *
     * @param value The argument object
     * @return The argument object itself, if it is a {@link Field}, or a bind
     *         value created from the argument object.
     */
  @SuppressWarnings(value = { "unchecked" }) static final <T extends java.lang.Object> Field<T> field(T value) {
    if (value instanceof Field<?>) {
      return (Field<T>) value;
    } else {
      if (value instanceof Select && ((Select<?>) value).getSelect().size() == 1) {
        return DSL.field((Select<Record1<T>>) value);
      } else {
        if (value instanceof QueryPart) {
          throw fieldExpected(value);
        } else {
          return val(value);
        }
      }
    }
  }

  static final Param<Byte> field(byte value) {
    return val((Object) value, SQLDataType.TINYINT);
  }

  static final Param<Byte> field(Byte value) {
    return val((Object) value, SQLDataType.TINYINT);
  }

  static final Param<UByte> field(UByte value) {
    return val((Object) value, SQLDataType.TINYINTUNSIGNED);
  }

  static final Param<Short> field(short value) {
    return val((Object) value, SQLDataType.SMALLINT);
  }

  static final Param<Short> field(Short value) {
    return val((Object) value, SQLDataType.SMALLINT);
  }

  static final Param<UShort> field(UShort value) {
    return val((Object) value, SQLDataType.SMALLINTUNSIGNED);
  }

  static final Param<Integer> field(int value) {
    return val((Object) value, SQLDataType.INTEGER);
  }

  static final Param<Integer> field(Integer value) {
    return val((Object) value, SQLDataType.INTEGER);
  }

  static final Param<UInteger> field(UInteger value) {
    return val((Object) value, SQLDataType.INTEGERUNSIGNED);
  }

  static final Param<Long> field(long value) {
    return val((Object) value, SQLDataType.BIGINT);
  }

  static final Param<Long> field(Long value) {
    return val((Object) value, SQLDataType.BIGINT);
  }

  static final Param<ULong> field(ULong value) {
    return val((Object) value, SQLDataType.BIGINTUNSIGNED);
  }

  static final Param<Float> field(float value) {
    return val((Object) value, SQLDataType.REAL);
  }

  static final Param<Float> field(Float value) {
    return val((Object) value, SQLDataType.REAL);
  }

  static final Param<Double> field(double value) {
    return val((Object) value, SQLDataType.DOUBLE);
  }

  static final Param<Double> field(Double value) {
    return val((Object) value, SQLDataType.DOUBLE);
  }

  static final Param<Boolean> field(boolean value) {
    return val((Object) value, SQLDataType.BOOLEAN);
  }

  static final Param<Boolean> field(Boolean value) {
    return val((Object) value, SQLDataType.BOOLEAN);
  }

  static final Param<BigDecimal> field(BigDecimal value) {
    return val((Object) value, SQLDataType.DECIMAL);
  }

  static final Param<BigInteger> field(BigInteger value) {
    return val((Object) value, SQLDataType.DECIMAL_INTEGER);
  }

  static final Param<byte[]> field(byte[] value) {
    return val((Object) value, SQLDataType.VARBINARY);
  }

  static final Param<String> field(String value) {
    return val((Object) value, SQLDataType.VARCHAR);
  }

  static final Param<Date> field(Date value) {
    return val((Object) value, SQLDataType.DATE);
  }

  static final Param<Time> field(Time value) {
    return val((Object) value, SQLDataType.TIME);
  }

  static final Param<Timestamp> field(Timestamp value) {
    return val((Object) value, SQLDataType.TIMESTAMP);
  }

  static final Param<LocalDate> field(LocalDate value) {
    return val((Object) value, SQLDataType.LOCALDATE);
  }

  static final Param<LocalTime> field(LocalTime value) {
    return val((Object) value, SQLDataType.LOCALTIME);
  }

  static final Param<LocalDateTime> field(LocalDateTime value) {
    return val((Object) value, SQLDataType.LOCALDATETIME);
  }

  static final Param<OffsetTime> field(OffsetTime value) {
    return val((Object) value, SQLDataType.OFFSETTIME);
  }

  static final Param<OffsetDateTime> field(OffsetDateTime value) {
    return val((Object) value, SQLDataType.OFFSETDATETIME);
  }

  static final Param<UUID> field(UUID value) {
    return val((Object) value, SQLDataType.UUID);
  }

  /**
     * @deprecated - This method is probably called by mistake (ambiguous static import).
     */
  @Deprecated static final Field<Object> field(Name name) {
    return DSL.field(name);
  }

  /**
     * Be sure that a given object is a field.
     *
     * @param value The argument object
     * @param field The field to take the bind value type from
     * @return The argument object itself, if it is a {@link Field}, or a bind
     *         value created from the argument object.
     */
  @SuppressWarnings(value = { "unchecked" }) static final <T extends java.lang.Object> Field<T> field(Object value, Field<T> field) {
    if (value instanceof Field<?>) {
      return (Field<T>) value;
    } else {
      if (value instanceof QueryPart) {
        throw fieldExpected(value);
      } else {
        return val(value, field);
      }
    }
  }

  /**
     * Be sure that a given object is a field.
     *
     * @param value The argument object
     * @param type The type to take the bind value type from
     * @return The argument object itself, if it is a {@link Field}, or a bind
     *         value created from the argument object.
     */
  @SuppressWarnings(value = { "unchecked" }) static final <T extends java.lang.Object> Field<T> field(Object value, Class<T> type) {
    if (value instanceof Field<?>) {
      return (Field<T>) value;
    } else {
      if (value instanceof QueryPart) {
        throw fieldExpected(value);
      } else {
        return val(value, type);
      }
    }
  }

  /**
     * Be sure that a given object is a field.
     *
     * @param value The argument object
     * @param type The type to take the bind value type from
     * @return The argument object itself, if it is a {@link Field}, or a bind
     *         value created from the argument object.
     */
  @SuppressWarnings(value = { "unchecked" }) static final <T extends java.lang.Object> Field<T> field(Object value, DataType<T> type) {
    if (value instanceof Field<?>) {
      return (Field<T>) value;
    } else {
      if (value instanceof QueryPart) {
        throw fieldExpected(value);
      } else {
        return val(value, type);
      }
    }
  }

  /**
     * Be sure that a given set of objects are fields.
     *
     * @param values The argument objects
     * @return The argument objects themselves, if they are {@link Field}s, or a bind
     *         values created from the argument objects.
     */
  static final <T extends java.lang.Object> List<Field<T>> fields(T[] values) {
    List<Field<T>> result = new ArrayList<Field<T>>();
    if (values != null) {
      for (T value : values) {
        result.add(field(value));
      }
    }
    return result;
  }

  /**
     * Be sure that a given set of objects are fields.
     *
     * @param values The argument objects
     * @param field The field to take the bind value types from
     * @return The argument objects themselves, if they are {@link Field}s, or a bind
     *         values created from the argument objects.
     */
  static final List<Field<?>> fields(Object[] values, Field<?> field) {
    List<Field<?>> result = new ArrayList<Field<?>>();
    if (values != null && field != null) {
      for (int i = 0; i < values.length; i++) {
        result.add(field(values[i], field));
      }
    }
    return result;
  }

  /**
     * Be sure that a given set of objects are fields.
     *
     * @param values The argument objects
     * @param fields The fields to take the bind value types from
     * @return The argument objects themselves, if they are {@link Field}s, or a bind
     *         values created from the argument objects.
     */
  static final List<Field<?>> fields(Object[] values, Field<?>[] fields) {
    List<Field<?>> result = new ArrayList<Field<?>>();
    if (values != null && fields != null) {
      for (int i = 0; i < values.length && i < fields.length; i++) {
        result.add(field(values[i], fields[i]));
      }
    }
    return result;
  }

  /**
     * Be sure that a given set of objects are fields.
     *
     * @param values The argument objects
     * @param type The type to take the bind value types from
     * @return The argument objects themselves, if they are {@link Field}s, or a bind
     *         values created from the argument objects.
     */
  static final List<Field<?>> fields(Object[] values, Class<?> type) {
    List<Field<?>> result = new ArrayList<Field<?>>();
    if (values != null && type != null) {
      for (int i = 0; i < values.length; i++) {
        result.add(field(values[i], type));
      }
    }
    return result;
  }

  /**
     * Be sure that a given set of objects are fields.
     *
     * @param values The argument objects
     * @param types The types to take the bind value types from
     * @return The argument objects themselves, if they are {@link Field}s, or a bind
     *         values created from the argument objects.
     */
  static final List<Field<?>> fields(Object[] values, Class<?>[] types) {
    List<Field<?>> result = new ArrayList<Field<?>>();
    if (values != null && types != null) {
      for (int i = 0; i < values.length && i < types.length; i++) {
        result.add(field(values[i], types[i]));
      }
    }
    return result;
  }

  /**
     * Be sure that a given set of objects are fields.
     *
     * @param values The argument objects
     * @param type The type to take the bind value types from
     * @return The argument objects themselves, if they are {@link Field}s, or a bind
     *         values created from the argument objects.
     */
  static final List<Field<?>> fields(Object[] values, DataType<?> type) {
    List<Field<?>> result = new ArrayList<Field<?>>();
    if (values != null && type != null) {
      for (int i = 0; i < values.length; i++) {
        result.add(field(values[i], type));
      }
    }
    return result;
  }

  /**
     * Be sure that a given set of objects are fields.
     *
     * @param values The argument objects
     * @param types The types to take the bind value types from
     * @return The argument objects themselves, if they are {@link Field}s, or a bind
     *         values created from the argument objects.
     */
  static final List<Field<?>> fields(Object[] values, DataType<?>[] types) {
    List<Field<?>> result = new ArrayList<Field<?>>();
    if (values != null && types != null) {
      for (int i = 0; i < values.length && i < types.length; i++) {
        result.add(field(values[i], types[i]));
      }
    }
    return result;
  }

  static final <T extends java.lang.Object> List<Field<T>> inline(T[] values) {
    List<Field<T>> result = new ArrayList<Field<T>>();
    if (values != null) {
      for (T value : values) {
        result.add(DSL.inline(value));
      }
    }
    return result;
  }

  /**
     * A utility method that fails with an exception if
     * {@link Row#indexOf(Field)} doesn't return any index.
     */
  static final int indexOrFail(Row row, Field<?> field) {
    int result = row.indexOf(field);
    if (result < 0) {
      throw new IllegalArgumentException("Field (" + field + ") is not contained in Row " + row);
    }
    return result;
  }

  /**
     * A utility method that fails with an exception if
     * {@link Row#indexOf(String)} doesn't return any index.
     */
  static final int indexOrFail(Row row, String fieldName) {
    int result = row.indexOf(fieldName);
    if (result < 0) {
      throw new IllegalArgumentException("Field (" + fieldName + ") is not contained in Row " + row);
    }
    return result;
  }

  /**
     * A utility method that fails with an exception if
     * {@link Row#indexOf(Name)} doesn't return any index.
     */
  static final int indexOrFail(Row row, Name fieldName) {
    int result = row.indexOf(fieldName);
    if (result < 0) {
      throw new IllegalArgumentException("Field (" + fieldName + ") is not contained in Row " + row);
    }
    return result;
  }

  /**
     * A utility method that fails with an exception if
     * {@link RecordType#indexOf(Field)} doesn't return any index.
     */
  static final int indexOrFail(RecordType<?> row, Field<?> field) {
    int result = row.indexOf(field);
    if (result < 0) {
      throw new IllegalArgumentException("Field (" + field + ") is not contained in RecordType " + row);
    }
    return result;
  }

  /**
     * A utility method that fails with an exception if
     * {@link RecordType#indexOf(String)} doesn't return any index.
     */
  static final int indexOrFail(RecordType<?> row, String fieldName) {
    int result = row.indexOf(fieldName);
    if (result < 0) {
      throw new IllegalArgumentException("Field (" + fieldName + ") is not contained in RecordType " + row);
    }
    return result;
  }

  /**
     * A utility method that fails with an exception if
     * {@link RecordType#indexOf(Name)} doesn't return any index.
     */
  static final int indexOrFail(RecordType<?> row, Name fieldName) {
    int result = row.indexOf(fieldName);
    if (result < 0) {
      throw new IllegalArgumentException("Field (" + fieldName + ") is not contained in RecordType " + row);
    }
    return result;
  }

  /**
     * Create a new array
     */
  @SafeVarargs static final <T extends java.lang.Object> T[] array(T... array) {
    return array;
  }

  /**
     * Use this rather than {@link Arrays#asList(Object...)} for
     * <code>null</code>-safety
     */
  @SafeVarargs static final <T extends java.lang.Object> List<T> list(T... array) {
    return array == null ? Collections.<T>emptyList() : Arrays.asList(array);
  }

  /**
     * Turn a {@link Record} into a {@link Map}
     */
  static final Map<Field<?>, Object> mapOfChangedValues(Record record) {
    Map<Field<?>, Object> result = new LinkedHashMap<Field<?>, Object>();
    int size = record.size();
    for (int i = 0; i < size; i++) {
      if (record.changed(i)) {
        result.put(record.field(i), record.get(i));
      }
    }
    return result;
  }

  /**
     * Extract the first item from an iterable or <code>null</code>, if there is
     * no such item, or if iterable itself is <code>null</code>
     */
  static final <T extends java.lang.Object> T first(Iterable<? extends T> iterable) {
    if (iterable == null) {
      return null;
    } else {
      Iterator<? extends T> iterator = iterable.iterator();
      if (iterator.hasNext()) {
        return iterator.next();
      } else {
        return null;
      }
    }
  }

  /**
     * Get the only element from a list or <code>null</code>, or throw an
     * exception
     *
     * @param list The list
     * @return The only element from the list or <code>null</code>
     * @throws TooManyRowsException Thrown if the list contains more than one
     *             element
     */
  static final <R extends Record> R filterOne(List<R> list) throws TooManyRowsException {
    int size = list.size();
    if (size == 0) {
      return null;
    } else {
      if (size == 1) {
        return list.get(0);
      } else {
        throw new TooManyRowsException("Too many rows selected : " + size);
      }
    }
  }

  /**
     * Get the only element from a cursor or <code>null</code>, or throw an
     * exception.
     * <p>
     * [#2373] This method will always close the argument cursor, as it is
     * supposed to be completely consumed by this method.
     *
     * @param cursor The cursor
     * @return The only element from the cursor or <code>null</code>
     * @throws TooManyRowsException Thrown if the cursor returns more than one
     *             element
     */
  static final <R extends Record> R fetchOne(Cursor<R> cursor) throws TooManyRowsException {
    try {
      Result<R> result = cursor.fetchNext(2);
      int size = result.size();
      if (size == 0) {
        return null;
      } else {
        if (size == 1) {
          return result.get(0);
        } else {
          throw new TooManyRowsException("Cursor returned more than one result");
        }
      }
    }  finally {
      cursor.close();
    }
  }

  /**
     * Get the only element from a cursor, or throw an exception.
     * <p>
     * [#2373] This method will always close the argument cursor, as it is
     * supposed to be completely consumed by this method.
     *
     * @param cursor The cursor
     * @return The only element from the cursor
     * @throws NoDataFoundException Thrown if the cursor did not return any rows
     * @throws TooManyRowsException Thrown if the cursor returns more than one
     *             element
     */
  static final <R extends Record> R fetchSingle(Cursor<R> cursor) throws NoDataFoundException, TooManyRowsException {
    try {
      Result<R> result = cursor.fetchNext(2);
      int size = result.size();
      if (size == 0) {
        throw new NoDataFoundException("Cursor returned no rows");
      } else {
        if (size == 1) {
          return result.get(0);
        } else {
          throw new TooManyRowsException("Cursor returned more than one result");
        }
      }
    }  finally {
      cursor.close();
    }
  }

  /**
     * Visit each query part from a collection, given a context.
     */
  static final <C extends Context<? super C>> C visitAll(C ctx, Collection<? extends QueryPart> parts) {
    if (parts != null) {
      for (QueryPart part : parts) {
        ctx.visit(part);
      }
    }
    return ctx;
  }

  /**
     * Visit each query part from an array, given a context.
     */
  static final <C extends Context<? super C>> C visitAll(C ctx, QueryPart[] parts) {
    if (parts != null) {
      for (QueryPart part : parts) {
        ctx.visit(part);
      }
    }
    return ctx;
  }

  /**
     * Render and bind a list of {@link QueryPart} to plain SQL
     * <p>
     * This will perform two actions:
     * <ul>
     * <li>When {@link RenderContext} is provided, it will render plain SQL to
     * the context, substituting {numbered placeholders} and bind values if
     * {@link RenderContext#inline()} is set</li>
     * <li>When {@link BindContext} is provided, it will bind the list of
     * {@link QueryPart} according to the {numbered placeholders} and bind
     * values in the sql string</li>
     * </ul>
     */
  @SuppressWarnings(value = { "null" }) static final void renderAndBind(Context<?> ctx, String sql, List<QueryPart> substitutes) {
    RenderContext render = (RenderContext) ((ctx instanceof RenderContext) ? ctx : null);
    BindContext bind = (BindContext) ((ctx instanceof BindContext) ? ctx : null);
    int substituteIndex = 0;
    char[] sqlChars = sql.toCharArray();
    if (render == null) {
      render = new DefaultRenderContext(bind.configuration());
    }
    SQLDialect dialect = render.dialect();
    SQLDialect family = dialect.family();
    boolean mysql = SUPPORT_MYSQL_SYNTAX.contains(family);
    String[][] quotes = QUOTES.get(family);
    boolean needsBackslashEscaping = needsBackslashEscaping(ctx.configuration());
    characterLoop:
    for (int i = 0; i < sqlChars.length; i++) {
      if (peek(sqlChars, i, "--") || (mysql && peek(sqlChars, i, "#"))) {
        for ( ; i < sqlChars.length && sqlChars[i] != '\r' && sqlChars[i] != '\n'; render.sql(sqlChars[i++])) {
          ;
        }
        if (i < sqlChars.length) {
          render.sql(sqlChars[i]);
        }
      } else {
        if (peek(sqlChars, i, "/*")) {
          for ( ; !peek(sqlChars, i, "*/"); render.sql(sqlChars[i++])) {
            ;
          }
          render.sql(sqlChars[i++]);
          render.sql(sqlChars[i]);
        } else {
          if (sqlChars[i] == '\'') {
            render.sql(sqlChars[i++]);
            for ( ; ; ) {
              if (sqlChars[i] == '\\' && needsBackslashEscaping) {
                render.sql(sqlChars[i++]);
              } else {
                if (peek(sqlChars, i, "\'\'")) {
                  render.sql(sqlChars[i++]);
                } else {
                  if (peek(sqlChars, i, "\'")) {
                    break;
                  }
                }
              }
              render.sql(sqlChars[i++]);
            }
            render.sql(sqlChars[i]);
          } else {
            if ((sqlChars[i] == 'e' || sqlChars[i] == 'E') && ctx.family() == POSTGRES && i + 1 < sqlChars.length && sqlChars[i + 1] == '\'') {
              render.sql(sqlChars[i++]);
              render.sql(sqlChars[i++]);
              for ( ; ; ) {
                if (sqlChars[i] == '\\') {
                  render.sql(sqlChars[i++]);
                } else {
                  if (peek(sqlChars, i, "\'\'")) {
                    render.sql(sqlChars[i++]);
                  } else {
                    if (peek(sqlChars, i, "\'")) {
                      break;
                    }
                  }
                }
                render.sql(sqlChars[i++]);
              }
              render.sql(sqlChars[i]);
            } else {
              if (peekAny(sqlChars, i, quotes[QUOTE_START_DELIMITER])) {
                int delimiter = 0;
                for (int d = 0; d < quotes[QUOTE_START_DELIMITER].length; d++) {
                  if (peek(sqlChars, i, quotes[QUOTE_START_DELIMITER][d])) {
                    delimiter = d;
                    break;
                  }
                }
                for (int d = 0; d < quotes[QUOTE_START_DELIMITER][delimiter].length(); d++) {
                  render.sql(sqlChars[i++]);
                }
                for ( ; ; ) {
                  if (peek(sqlChars, i, quotes[QUOTE_END_DELIMITER_ESCAPED][delimiter])) {
                    for (int d = 0; d < quotes[QUOTE_END_DELIMITER_ESCAPED][delimiter].length(); d++) {
                      render.sql(sqlChars[i++]);
                    }
                  } else {
                    if (peek(sqlChars, i, quotes[QUOTE_END_DELIMITER][delimiter])) {
                      break;
                    }
                  }
                  render.sql(sqlChars[i++]);
                }
                for (int d = 0; d < quotes[QUOTE_END_DELIMITER][delimiter].length(); d++) {
                  if (d > 0) {
                    i++;
                  }
                  render.sql(sqlChars[i]);
                }
              } else {
                if (substituteIndex < substitutes.size() && ((sqlChars[i] == '?') || (sqlChars[i] == ':' && i + 1 < sqlChars.length && isJavaIdentifierPart(sqlChars[i + 1]) && (i - 1 < 0 || sqlChars[i - 1] != ':')))) {
                  if (sqlChars[i] == '?' && i + 1 < sqlChars.length) {
                    for (String suffix : NON_BIND_VARIABLE_SUFFIXES) {
                      if (peek(sqlChars, i + 1, suffix)) {
                        for (int j = i; i - j <= suffix.length(); i++) {
                          render.sql(sqlChars[i]);
                        }
                        render.sql(sqlChars[i]);
                        continue characterLoop;
                      }
                    }
                  }
                  if (sqlChars[i] == ':') {
                    while (i + 1 < sqlChars.length && isJavaIdentifierPart(sqlChars[i + 1])) {
                      i++;
                    }
                  }
                  QueryPart substitute = substitutes.get(substituteIndex++);
                  if (render.paramType() == INLINED || render.paramType() == NAMED || render.paramType() == NAMED_OR_INLINED) {
                    render.visit(substitute);
                  } else {
                    CastMode previous = render.castMode();
                    render.castMode(CastMode.NEVER).visit(substitute).castMode(previous);
                  }
                  if (bind != null) {
                    bind.visit(substitute);
                  }
                } else {
                  if (sqlChars[i] == '{') {
                    if (peekAny(sqlChars, i, JDBC_ESCAPE_PREFIXES, true)) {
                      render.sql(sqlChars[i]);
                    } else {
                      int start = ++i;
                      for ( ; i < sqlChars.length && sqlChars[i] != '}'; i++) {
                        ;
                      }
                      int end = i;
                      String token = sql.substring(start, end);
                      try {
                        QueryPart substitute = substitutes.get(Integer.valueOf(token));
                        render.visit(substitute);
                        if (bind != null) {
                          bind.visit(substitute);
                        }
                      } catch (NumberFormatException e) {
                        render.visit(DSL.keyword(token));
                      }
                    }
                  } else {
                    render.sql(sqlChars[i]);
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  /**
     * Whether backslash escaping is needed in inlined string literals.
     */
  static final boolean needsBackslashEscaping(Configuration configuration) {
    BackslashEscaping escaping = getBackslashEscaping(configuration.settings());
    return escaping == ON || (escaping == DEFAULT && REQUIRES_BACKSLASH_ESCAPING.contains(configuration.family()));
  }

  /**
     * Peek for a string at a given <code>index</code> of a <code>char[]</code>
     *
     * @param sqlChars The char array to peek into
     * @param index The index within the char array to peek for a string
     * @param peek The string to peek for
     */
  static final boolean peek(char[] sqlChars, int index, String peek) {
    return peek(sqlChars, index, peek, false);
  }

  /**
     * Peek for a string at a given <code>index</code> of a <code>char[]</code>
     *
     * @param sqlChars The char array to peek into
     * @param index The index within the char array to peek for a string
     * @param peek The string to peek for
     * @param anyWhitespace A whitespace character in <code>peekAny</code>
     *            represents "any" whitespace character as defined in
     *            {@link #WHITESPACE}, or in Java Regex "\s".
     */
  static final boolean peek(char[] sqlChars, int index, String peek, boolean anyWhitespace) {
    char[] peekArray = peek.toCharArray();
    peekArrayLoop:
    for (int i = 0; i < peekArray.length; i++) {
      if (index + i >= sqlChars.length) {
        return false;
      }
      if (sqlChars[index + i] != peekArray[i]) {
        if (anyWhitespace && peekArray[i] == ' ') {
          for (int j = 0; j < WHITESPACE.length(); j++) {
            if (sqlChars[index + i] == WHITESPACE.charAt(j)) {
              continue peekArrayLoop;
            }
          }
        }
        return false;
      }
    }
    return true;
  }

  /**
     * Peek for several strings at a given <code>index</code> of a <code>char[]</code>
     *
     * @param sqlChars The char array to peek into
     * @param index The index within the char array to peek for a string
     * @param peekAny The strings to peek for
     */
  static final boolean peekAny(char[] sqlChars, int index, String[] peekAny) {
    return peekAny(sqlChars, index, peekAny, false);
  }

  /**
     * Peek for several strings at a given <code>index</code> of a
     * <code>char[]</code>
     *
     * @param sqlChars The char array to peek into
     * @param index The index within the char array to peek for a string
     * @param peekAny The strings to peek for
     * @param anyWhitespace A whitespace character in <code>peekAny</code>
     *            represents "any" whitespace character as defined in
     *            {@link #WHITESPACE}, or in Java Regex "\s".
     */
  static final boolean peekAny(char[] sqlChars, int index, String[] peekAny, boolean anyWhitespace) {
    for (String peek : peekAny) {
      if (peek(sqlChars, index, peek, anyWhitespace)) {
        return true;
      }
    }
    return false;
  }

  /**
     * Create {@link QueryPart} objects from bind values or substitutes
     */
  static final List<QueryPart> queryParts(Object... substitutes) {
    if (substitutes == null) {
      return queryParts(new Object[] { null });
    } else {
      List<QueryPart> result = new ArrayList<QueryPart>();
      for (Object substitute : substitutes) {
        if (substitute instanceof QueryPart) {
          result.add((QueryPart) substitute);
        } else {
          @SuppressWarnings(value = { "unchecked" }) Class<Object> type = (Class<Object>) (substitute != null ? substitute.getClass() : Object.class);
          result.add(new Val<Object>(substitute, DSL.getDataType(type)));
        }
      }
      return result;
    }
  }

  /**
     * Render a list of names of the <code>NamedQueryParts</code> contained in
     * this list.
     */
  static final void fieldNames(Context<?> context, Fields<?> fields) {
    fieldNames(context, list(fields.fields));
  }

  /**
     * Render a list of names of the <code>NamedQueryParts</code> contained in
     * this list.
     */
  static final void fieldNames(Context<?> context, Field<?>... fields) {
    fieldNames(context, list(fields));
  }

  /**
     * Render a list of names of the <code>NamedQueryParts</code> contained in
     * this list.
     */
  static final void fieldNames(Context<?> context, Collection<? extends Field<?>> list) {
    String separator = "";
    for (Field<?> field : list) {
      context.sql(separator).visit(field.getUnqualifiedName());
      separator = ", ";
    }
  }

  /**
     * Render a list of names of the <code>NamedQueryParts</code> contained in
     * this list.
     */
  static final void tableNames(Context<?> context, Table<?>... list) {
    tableNames(context, list(list));
  }

  /**
     * Render a list of names of the <code>NamedQueryParts</code> contained in
     * this list.
     */
  static final void tableNames(Context<?> context, Collection<? extends Table<?>> list) {
    String separator = "";
    for (Table<?> table : list) {
      context.sql(separator).visit(table.getUnqualifiedName());
      separator = ", ";
    }
  }

  @SuppressWarnings(value = { "unchecked" }) static final <T extends java.lang.Object> T[] combine(T[] array, T value) {
    T[] result = (T[]) java.lang.reflect.Array.newInstance(array.getClass().getComponentType(), array.length + 1);
    System.arraycopy(array, 0, result, 0, array.length);
    result[array.length] = value;
    return result;
  }

  /**
     * Combine a field with an array of fields
     */
  static final Field<?>[] combine(Field<?> field, Field<?>... fields) {
    if (fields == null) {
      return new Field[] { field };
    } else {
      Field<?>[] result = new Field<?>[fields.length + 1];
      result[0] = field;
      System.arraycopy(fields, 0, result, 1, fields.length);
      return result;
    }
  }

  /**
     * Combine a field with an array of fields
     */
  static final Field<?>[] combine(Field<?> field1, Field<?> field2, Field<?>... fields) {
    if (fields == null) {
      return new Field[] { field1, field2 };
    } else {
      Field<?>[] result = new Field<?>[fields.length + 2];
      result[0] = field1;
      result[1] = field2;
      System.arraycopy(fields, 0, result, 2, fields.length);
      return result;
    }
  }

  /**
     * Combine a field with an array of fields
     */
  static final Field<?>[] combine(Field<?> field1, Field<?> field2, Field<?> field3, Field<?>... fields) {
    if (fields == null) {
      return new Field[] { field1, field2, field3 };
    } else {
      Field<?>[] result = new Field<?>[fields.length + 3];
      result[0] = field1;
      result[1] = field2;
      result[2] = field3;
      System.arraycopy(fields, 0, result, 3, fields.length);
      return result;
    }
  }

  /**
     * Translate a {@link SQLException} to a {@link DataAccessException}
     */
  static final DataAccessException translate(String sql, SQLException e) {
    String message = "SQL [" + sql + "]; " + e.getMessage();
    return new DataAccessException(message, e);
  }

  /**
     * Safely close a statement
     */
  static final void safeClose(ExecuteListener listener, ExecuteContext ctx) {
    safeClose(listener, ctx, false);
  }

  /**
     * Safely close a statement
     */
  static final void safeClose(ExecuteListener listener, ExecuteContext ctx, boolean keepStatement) {
    safeClose(listener, ctx, keepStatement, true);
  }

  /**
     * Safely close a statement
     */
  static final void safeClose(ExecuteListener listener, ExecuteContext ctx, boolean keepStatement, boolean keepResultSet) {
    JDBCUtils.safeClose(ctx.resultSet());
    ctx.resultSet(null);
    PreparedStatement statement = ctx.statement();
    if (statement != null) {
      consumeWarnings(ctx, listener);
    }
    if (!keepStatement) {
      if (statement != null) {
        JDBCUtils.safeClose(statement);
        ctx.statement(null);
      } else {
        Connection connection = localConnection();
        if (connection != null) {
          ctx.configuration().connectionProvider().release(connection);
        }
      }
    }
    if (keepResultSet) {
      listener.end(ctx);
    }
    DefaultExecuteContext.clean();
  }

  /**
     * Type-safely copy a value from one record to another
     */
  static final <T extends java.lang.Object> void setValue(Record target, Field<T> targetField, Record source, Field<?> sourceField) {
    setValue(target, targetField, source.get(sourceField));
  }

  /**
     * Type-safely set a value to a record
     */
  static final <T extends java.lang.Object> void setValue(Record target, Field<T> targetField, Object value) {
    target.set(targetField, targetField.getDataType().convert(value));
  }

  /**
     * [#2591] Type-safely copy a value from one record to another, preserving flags.
     */
  static final <T extends java.lang.Object> void copyValue(AbstractRecord target, Field<T> targetField, Record source, Field<?> sourceField) {
    DataType<T> targetType = targetField.getDataType();
    int targetIndex = indexOrFail(target.fieldsRow(), targetField);
    int sourceIndex = indexOrFail(source.fieldsRow(), sourceField);
    target.values[targetIndex] = targetType.convert(source.get(sourceIndex));
    target.originals[targetIndex] = targetType.convert(source.original(sourceIndex));
    target.changed.set(targetIndex, source.changed(sourceIndex));
  }

  /**
     * Map a {@link Catalog} according to the configured {@link org.jooq.SchemaMapping}
     */
  @SuppressWarnings(value = { "deprecation" }) static final Catalog getMappedCatalog(Configuration configuration, Catalog catalog) {
    if (configuration != null) {
      org.jooq.SchemaMapping mapping = configuration.schemaMapping();
      if (mapping != null) {
        return mapping.map(catalog);
      }
    }
    return catalog;
  }

  /**
     * Map a {@link Schema} according to the configured {@link org.jooq.SchemaMapping}
     */
  @SuppressWarnings(value = { "deprecation" }) static final Schema getMappedSchema(Configuration configuration, Schema schema) {
    if (configuration != null) {
      org.jooq.SchemaMapping mapping = configuration.schemaMapping();
      if (mapping != null) {
        return mapping.map(schema);
      }
    }
    return schema;
  }

  /**
     * Map a {@link Table} according to the configured {@link org.jooq.SchemaMapping}
     */
  @SuppressWarnings(value = { "deprecation" }) static final <R extends Record> Table<R> getMappedTable(Configuration configuration, Table<R> table) {
    if (configuration != null) {
      org.jooq.SchemaMapping mapping = configuration.schemaMapping();
      if (mapping != null) {
        return mapping.map(table);
      }
    }
    return table;
  }

  /**
     * Map an {@link ArrayRecord} according to the configured {@link org.jooq.SchemaMapping}
     */
  @SuppressWarnings(value = { "unchecked" }) static final String getMappedUDTName(Configuration configuration, Class<? extends UDTRecord<?>> type) {
    return getMappedUDTName(configuration, Tools.newRecord(false, (Class<UDTRecord<?>>) type).<RuntimeException>operate(null));
  }

  /**
     * Map an {@link ArrayRecord} according to the configured {@link org.jooq.SchemaMapping}
     */
  static final String getMappedUDTName(Configuration configuration, UDTRecord<?> record) {
    UDT<?> udt = record.getUDT();
    Schema mapped = getMappedSchema(configuration, udt.getSchema());
    StringBuilder sb = new StringBuilder();
    if (mapped != null) {
      sb.append(mapped.getName()).append('.');
    }
    sb.append(record.getUDT().getName());
    return sb.toString();
  }

  private static final DSLContext CTX = DSL.using(new DefaultConfiguration());

  /**
     * Return a non-negative hash code for a {@link QueryPart}, taking into
     * account FindBugs' <code>RV_ABSOLUTE_VALUE_OF_HASHCODE</code> pattern
     */
  static final int hash(QueryPart part) {
    return 0x7FFFFFF & CTX.render(part).hashCode();
  }

  /**
     * Utility method to escape strings or "toString" other objects
     */
  static final Field<String> escapeForLike(Object value) {
    return escapeForLike(value, new DefaultConfiguration());
  }

  /**
     * Utility method to escape strings or "toString" other objects
     */
  static final Field<String> escapeForLike(Object value, Configuration configuration) {
    if (value != null && value.getClass() == String.class) {
      {
        return val(escape("" + value, ESCAPE));
      }
    } else {
      return val("" + value);
    }
  }

  /**
     * Utility method to escape string fields, or cast other fields
     */
  static final Field<String> escapeForLike(Field<?> field) {
    return escapeForLike(field, new DefaultConfiguration());
  }

  /**
     * Utility method to escape string fields, or cast other fields
     */
  @SuppressWarnings(value = { "unchecked" }) static final Field<String> escapeForLike(Field<?> field, Configuration configuration) {
    if (nullSafe(field).getDataType().isString()) {
      {
        return escape((Field<String>) field, ESCAPE);
      }
    } else {
      return field.cast(String.class);
    }
  }

  /**
     * Utility method to check whether a field is a {@link Param}
     */
  static final boolean isVal(Field<?> field) {
    return field instanceof Param;
  }

  /**
     * Utility method to extract a value from a field
     */
  static final <T extends java.lang.Object> T extractVal(Field<T> field) {
    if (isVal(field)) {
      return ((Param<T>) field).getValue();
    } else {
      return null;
    }
  }

  /**
     * Add primary key conditions to a query
     */
  @SuppressWarnings(value = { "deprecation" }) static final void addConditions(org.jooq.ConditionProvider query, Record record, Field<?>... keys) {
    for (Field<?> field : keys) {
      addCondition(query, record, field);
    }
  }

  /**
     * Add a field condition to a query
     */
  @SuppressWarnings(value = { "deprecation" }) static final <T extends java.lang.Object> void addCondition(org.jooq.ConditionProvider provider, Record record, Field<T> field) {
    if (updatablePrimaryKeys(settings(record))) {
      provider.addConditions(condition(field, record.original(field)));
    } else {
      provider.addConditions(condition(field, record.get(field)));
    }
  }

  /**
     * Create a <code>null</code>-safe condition.
     */
  static final <T extends java.lang.Object> Condition condition(Field<T> field, T value) {
    return (value == null) ? field.isNull() : field.eq(value);
  }

  static class ThreadGuard {
    static enum Guard {
      RECORD_TOSTRING
      ;

      ThreadLocal<Object> tl = new ThreadLocal<Object>();
    }

    static interface GuardedOperation<V extends java.lang.Object> {
      /**
             * This callback is executed only once on the current stack.
             */
      V unguarded();

      /**
             * This callback is executed if {@link #unguarded()} has already been executed on the current stack.
             */
      V guarded();
    }

    abstract static class AbstractGuardedOperation<V extends java.lang.Object> implements GuardedOperation<V> {
      @Override public V guarded() {
        return null;
      }
    }

    /**
         * Run an operation using a guard.
         */
    static final <V extends java.lang.Object> V run(Guard guard, GuardedOperation<V> operation) {
      boolean unguarded = (guard.tl.get() == null);
      if (unguarded) {
        guard.tl.set(Guard.class);
      }
      try {
        if (unguarded) {
          return operation.unguarded();
        } else {
          return operation.guarded();
        }
      }  finally {
        if (unguarded) {
          guard.tl.remove();
        }
      }
    }
  }

  static class Cache {
    static interface CachedOperation<V extends java.lang.Object> {
      /**
             * An expensive operation.
             */
      V call();
    }

    /**
         * Run a {@link CachedOperation} in the context of a
         * {@link Configuration}.
         *
         * @param configuration The configuration that may cache the outcome of
         *            the {@link CachedOperation}.
         * @param operation The expensive operation.
         * @param type The cache type to be used.
         * @param keys The cache keys.
         * @return The cached value or the outcome of the cached operation.
         */
    @SuppressWarnings(value = { "unchecked" }) static final <V extends java.lang.Object> V run(Configuration configuration, CachedOperation<V> operation, DataCacheKey type, Object key) {
      if (configuration == null) {
        configuration = new DefaultConfiguration();
      }
      if (!reflectionCaching(configuration.settings())) {
        return operation.call();
      }
      Map<Object, Object> cache = (Map<Object, Object>) configuration.data(type);
      if (cache == null) {
        synchronized (type) {
          cache = (Map<Object, Object>) configuration.data(type);
          if (cache == null) {
            cache = new ConcurrentHashMap<Object, Object>();
            configuration.data(type, cache);
          }
        }
      }
      Object result = cache.get(key);
      if (result == null) {
        synchronized (cache) {
          result = cache.get(key);
          if (result == null) {
            result = operation.call();
            cache.put(key, result == null ? NULL : result);
          }
        }
      }
      return (V) (result == NULL ? null : result);
    }

    /**
         * A <code>null</code> placeholder to be put in {@link ConcurrentHashMap}.
         */
    private static final Object NULL = new Object();

    /**
         * Create a single-value or multi-value key for caching.
         */
    static final Object key(Object key1, Object key2) {
      return new Key2(key1, key2);
    }

    private static class Key2 implements Serializable {
      /**
             * Generated UID.
             */
      private static final long serialVersionUID = 5822370287443922993L;

      private final Object key1;

      private final Object key2;

      Key2(Object key1, Object key2) {
        this.key1 = key1;
        this.key2 = key2;
      }

      @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key1 == null) ? 0 : key1.hashCode());
        result = prime * result + ((key2 == null) ? 0 : key2.hashCode());
        return result;
      }

      @Override public boolean equals(Object obj) {
        if (this == obj) {
          return true;
        }
        if (obj == null) {
          return false;
        }
        if (getClass() != obj.getClass()) {
          return false;
        }
        Key2 other = (Key2) obj;
        if (key1 == null) {
          if (other.key1 != null) {
            return false;
          }
        } else {
          if (!key1.equals(other.key1)) {
            return false;
          }
        }
        if (key2 == null) {
          if (other.key2 != null) {
            return false;
          }
        } else {
          if (!key2.equals(other.key2)) {
            return false;
          }
        }
        return true;
      }

      @Override public String toString() {
        return "[" + key1 + ", " + key2 + "]";
      }
    }
  }

  /**
     * Check if JPA classes can be loaded. This is only done once per JVM!
     */
  private static final boolean isJPAAvailable() {
    if (isJPAAvailable == null) {
      try {
        Class.forName(Column.class.getName());
        isJPAAvailable = true;
      } catch (Throwable e) {
        isJPAAvailable = false;
      }
    }
    return isJPAAvailable;
  }

  /**
     * Check whether <code>type</code> has any {@link Column} annotated members
     * or methods
     */
  static final boolean hasColumnAnnotations(final Configuration configuration, final Class<?> type) {
    return Cache.run(configuration, new CachedOperation<Boolean>() {
      @Override public Boolean call() {
        if (!isJPAAvailable()) {
          return false;
        }
        if (type.getAnnotation(Entity.class) != null) {
          return true;
        }
        if (type.getAnnotation(javax.persistence.Table.class) != null) {
          return true;
        }
        for (java.lang.reflect.Field member : getInstanceMembers(type)) {
          if (member.getAnnotation(Column.class) != null) {
            return true;
          }
          if (member.getAnnotation(Id.class) != null) {
            return true;
          }
        }
        for (Method method : getInstanceMethods(type)) {
          if (method.getAnnotation(Column.class) != null) {
            return true;
          }
        }
        return false;
      }
    }, DATA_REFLECTION_CACHE_HAS_COLUMN_ANNOTATIONS, type);
  }

  /**
     * Get all members annotated with a given column name
     */
  static final List<java.lang.reflect.Field> getAnnotatedMembers(final Configuration configuration, final Class<?> type, final String name) {
    return Cache.run(configuration, new CachedOperation<List<java.lang.reflect.Field>>() {
      @Override public List<java.lang.reflect.Field> call() {
        List<java.lang.reflect.Field> result = new ArrayList<java.lang.reflect.Field>();
        for (java.lang.reflect.Field member : getInstanceMembers(type)) {
          Column column = member.getAnnotation(Column.class);
          if (column != null) {
            if (namesMatch(name, column.name())) {
              result.add(accessible(member));
            }
          } else {
            Id id = member.getAnnotation(Id.class);
            if (id != null) {
              if (namesMatch(name, member.getName())) {
                result.add(accessible(member));
              }
            }
          }
        }
        return result;
      }
    }, DATA_REFLECTION_CACHE_GET_ANNOTATED_MEMBERS, Cache.key(type, name));
  }

  private static final boolean namesMatch(String name, String annotation) {
    return annotation.startsWith("\"") ? ('\"' + name + '\"').equals(annotation) : name.equalsIgnoreCase(annotation);
  }

  /**
     * Get all members matching a given column name
     */
  static final List<java.lang.reflect.Field> getMatchingMembers(final Configuration configuration, final Class<?> type, final String name) {
    return Cache.run(configuration, new CachedOperation<List<java.lang.reflect.Field>>() {
      @Override public List<java.lang.reflect.Field> call() {
        List<java.lang.reflect.Field> result = new ArrayList<java.lang.reflect.Field>();
        String camelCaseLC = StringUtils.toCamelCaseLC(name);
        for (java.lang.reflect.Field member : getInstanceMembers(type)) {
          if (name.equals(member.getName())) {
            result.add(accessible(member));
          } else {
            if (camelCaseLC.equals(member.getName())) {
              result.add(accessible(member));
            }
          }
        }
        return result;
      }
    }, DATA_REFLECTION_CACHE_GET_MATCHING_MEMBERS, Cache.key(type, name));
  }

  /**
     * Get all setter methods annotated with a given column name
     */
  static final List<Method> getAnnotatedSetters(final Configuration configuration, final Class<?> type, final String name) {
    return Cache.run(configuration, new CachedOperation<List<Method>>() {
      @Override public List<Method> call() {
        List<Method> result = new ArrayList<Method>();
        for (Method method : getInstanceMethods(type)) {
          Column column = method.getAnnotation(Column.class);
          if (column != null && namesMatch(name, column.name())) {
            if (method.getParameterTypes().length == 1) {
              result.add(accessible(method));
            } else {
              if (method.getParameterTypes().length == 0) {
                String m = method.getName();
                String suffix = m.startsWith("get") ? m.substring(3) : m.startsWith("is") ? m.substring(2) : null;
                if (suffix != null) {
                  try {
                    Method setter = type.getMethod("set" + suffix, method.getReturnType());
                    if (setter.getAnnotation(Column.class) == null) {
                      result.add(accessible(setter));
                    }
                  } catch (NoSuchMethodException ignore) {
                  }
                }
              }
            }
          }
        }
        return result;
      }
    }, DATA_REFLECTION_CACHE_GET_ANNOTATED_SETTERS, Cache.key(type, name));
  }

  /**
     * Get the first getter method annotated with a given column name
     */
  static final Method getAnnotatedGetter(final Configuration configuration, final Class<?> type, final String name) {
    return Cache.run(configuration, new CachedOperation<Method>() {
      @Override public Method call() {
        for (Method method : getInstanceMethods(type)) {
          Column column = method.getAnnotation(Column.class);
          if (column != null && namesMatch(name, column.name())) {
            if (method.getParameterTypes().length == 0) {
              return accessible(method);
            } else {
              if (method.getParameterTypes().length == 1) {
                String m = method.getName();
                if (m.startsWith("set")) {
                  try {
                    Method getter = type.getMethod("get" + m.substring(3));
                    if (getter.getAnnotation(Column.class) == null) {
                      return accessible(getter);
                    }
                  } catch (NoSuchMethodException ignore) {
                  }
                  try {
                    Method getter = type.getMethod("is" + m.substring(3));
                    if (getter.getAnnotation(Column.class) == null) {
                      return accessible(getter);
                    }
                  } catch (NoSuchMethodException ignore) {
                  }
                }
              }
            }
          }
        }
        return null;
      }
    }, DATA_REFLECTION_CACHE_GET_ANNOTATED_GETTER, Cache.key(type, name));
  }

  /**
     * Get all setter methods matching a given column name
     */
  static final List<Method> getMatchingSetters(final Configuration configuration, final Class<?> type, final String name) {
    return Cache.run(configuration, new CachedOperation<List<Method>>() {
      @Override public List<Method> call() {
        List<Method> result = new ArrayList<Method>();
        String camelCase = StringUtils.toCamelCase(name);
        String camelCaseLC = StringUtils.toLC(camelCase);
        for (Method method : getInstanceMethods(type)) {
          Class<?>[] parameterTypes = method.getParameterTypes();
          if (parameterTypes.length == 1) {
            if (name.equals(method.getName())) {
              result.add(accessible(method));
            } else {
              if (camelCaseLC.equals(method.getName())) {
                result.add(accessible(method));
              } else {
                if (("set" + name).equals(method.getName())) {
                  result.add(accessible(method));
                } else {
                  if (("set" + camelCase).equals(method.getName())) {
                    result.add(accessible(method));
                  }
                }
              }
            }
          }
        }
        return result;
      }
    }, DATA_REFLECTION_CACHE_GET_MATCHING_SETTERS, Cache.key(type, name));
  }

  /**
     * Get the first getter method matching a given column name
     */
  static final Method getMatchingGetter(final Configuration configuration, final Class<?> type, final String name) {
    return Cache.run(configuration, new CachedOperation<Method>() {
      @Override public Method call() {
        String camelCase = StringUtils.toCamelCase(name);
        String camelCaseLC = StringUtils.toLC(camelCase);
        for (Method method : getInstanceMethods(type)) {
          if (method.getParameterTypes().length == 0) {
            if (name.equals(method.getName())) {
              return accessible(method);
            } else {
              if (camelCaseLC.equals(method.getName())) {
                return accessible(method);
              } else {
                if (("get" + name).equals(method.getName())) {
                  return accessible(method);
                } else {
                  if (("get" + camelCase).equals(method.getName())) {
                    return accessible(method);
                  } else {
                    if (("is" + name).equals(method.getName())) {
                      return accessible(method);
                    } else {
                      if (("is" + camelCase).equals(method.getName())) {
                        return accessible(method);
                      }
                    }
                  }
                }
              }
            }
          }
        }
        return null;
      }
    }, DATA_REFLECTION_CACHE_GET_MATCHING_GETTER, Cache.key(type, name));
  }

  private static final List<Method> getInstanceMethods(Class<?> type) {
    List<Method> result = new ArrayList<Method>();
    for (Method method : type.getMethods()) {
      if ((method.getModifiers() & Modifier.STATIC) == 0) {
        result.add(method);
      }
    }
    return result;
  }

  private static final List<java.lang.reflect.Field> getInstanceMembers(Class<?> type) {
    List<java.lang.reflect.Field> result = new ArrayList<java.lang.reflect.Field>();
    for (java.lang.reflect.Field field : type.getFields()) {
      if ((field.getModifiers() & Modifier.STATIC) == 0) {
        result.add(field);
      }
    }
    do {
      for (java.lang.reflect.Field field : type.getDeclaredFields()) {
        if ((field.getModifiers() & Modifier.STATIC) == 0) {
          result.add(field);
        }
      }
      type = type.getSuperclass();
    } while(type != null);
    return result;
  }

  /**
     * Get a property name associated with a getter/setter method name.
     */
  static final String getPropertyName(String methodName) {
    String name = methodName;
    if (name.startsWith("is") && name.length() > 2) {
      name = name.substring(2, 3).toLowerCase() + name.substring(3);
    } else {
      if (name.startsWith("get") && name.length() > 3) {
        name = name.substring(3, 4).toLowerCase() + name.substring(4);
      } else {
        if (name.startsWith("set") && name.length() > 3) {
          name = name.substring(3, 4).toLowerCase() + name.substring(4);
        }
      }
    }
    return name;
  }

  /**
     * [#3011] [#3054] [#6390] [#6413] Consume additional exceptions if there
     * are any and append them to the <code>previous</code> exception's
     * {@link SQLException#getNextException()} list.
     */
  static final void consumeExceptions(Configuration configuration, PreparedStatement stmt, SQLException previous) {
    ThrowExceptions exceptions = configuration.settings().getThrowExceptions();
    if (exceptions == THROW_FIRST) {
      return;
    }
  }

  /**
     * [#3076] Consume warnings from a {@link Statement} and notify listeners.
     */
  static final void consumeWarnings(ExecuteContext ctx, ExecuteListener listener) {
    if (!Boolean.FALSE.equals(ctx.settings().isFetchWarnings())) {
      try {
        ctx.sqlWarning(ctx.statement().getWarnings());
      } catch (SQLException e) {
        ctx.sqlWarning(new SQLWarning("Could not fetch SQLWarning", e));
      }
    }
    if (ctx.sqlWarning() != null) {
      listener.warning(ctx);
    }
  }

  /**
     * [#5666] Handle the complexity of each dialect's understanding of
     * correctly calling {@link Statement#execute()}.
     */
  static final SQLException executeStatementAndGetFirstResultSet(ExecuteContext ctx, int skipUpdateCounts) throws SQLException {
    PreparedStatement stmt = ctx.statement();
    try {
      if (stmt.execute()) {
        ctx.resultSet(stmt.getResultSet());
      } else {
        ctx.resultSet(null);
        ctx.rows(stmt.getUpdateCount());
      }
      return null;
    } catch (SQLException e) {
      if (ctx.settings().getThrowExceptions() != THROW_NONE) {
        consumeExceptions(ctx.configuration(), ctx.statement(), e);
        throw e;
      } else {
        return e;
      }
    }
  }

  /**
     * [#3681] Consume all {@link ResultSet}s from a JDBC {@link Statement}.
     */
  static final void consumeResultSets(ExecuteContext ctx, ExecuteListener listener, Results results, Intern intern, SQLException prev) throws SQLException {
    boolean anyResults = false;
    int i = 0;
    int rows = (ctx.resultSet() == null) ? ctx.rows() : 0;
    for (i = 0; i < maxConsumedResults; i++) {
      try {
        if (ctx.resultSet() != null) {
          anyResults = true;
          Field<?>[] fields = new MetaDataFieldProvider(ctx.configuration(), ctx.resultSet().getMetaData()).getFields();
          Cursor<Record> c = new CursorImpl<Record>(ctx, listener, fields, intern != null ? intern.internIndexes(fields) : null, true, false);
          results.resultsOrRows().add(new ResultOrRowsImpl(c.fetch()));
        } else {
          if (prev == null) {
            if (rows != -1) {
              results.resultsOrRows().add(new ResultOrRowsImpl(rows));
            } else {
              break;
            }
          }
        }
        if (ctx.statement().getMoreResults()) {
          ctx.resultSet(ctx.statement().getResultSet());
        } else {
          rows = ctx.statement().getUpdateCount();
          ctx.rows(rows);
          if (rows != -1) {
            ctx.resultSet(null);
          } else {
            break;
          }
        }
        prev = null;
      } catch (SQLException e) {
        prev = e;
        if (ctx.settings().getThrowExceptions() == THROW_NONE) {
          ctx.sqlException(e);
          results.resultsOrRows().add(new ResultOrRowsImpl(Tools.translate(ctx.sql(), e)));
        } else {
          consumeExceptions(ctx.configuration(), ctx.statement(), e);
          throw e;
        }
      }
    }
    if (i == maxConsumedResults) {
      log.warn("Maximum consumed results reached: " + maxConsumedResults + ". This is probably a bug. Please report to https://github.com/jOOQ/jOOQ/issues/new");
    }
    if (anyResults && ctx.family() != CUBRID) {
      ctx.statement().getMoreResults(Statement.CLOSE_ALL_RESULTS);
    }
    if (ctx.settings().getThrowExceptions() == THROW_NONE) {
      SQLException s1 = null;
      for (ResultOrRows r : results.resultsOrRows()) {
        DataAccessException d = r.exception();
        if (d != null && d.getCause() instanceof SQLException) {
          SQLException s2 = (SQLException) d.getCause();
          if (s1 != null) {
            s1.setNextException(s2);
          }
          s1 = s2;
        }
      }
    }
  }

  private static final Pattern NEW_LINES = Pattern.compile("[\\r\\n]+");

  static final List<String[]> parseTXT(String string, String nullLiteral) {
    String[] strings = NEW_LINES.split(string);
    if (strings.length < 2) {
      throw new DataAccessException("String must contain at least two lines");
    }
    boolean formattedJOOQ = (string.charAt(0) == '+');
    boolean formattedOracle = (string.charAt(0) == '-');
    if (formattedJOOQ) {
      return parseTXTLines(nullLiteral, strings, PLUS_PATTERN, 0, 1, 3, strings.length - 1);
    } else {
      if (formattedOracle) {
        return parseTXTLines(nullLiteral, strings, PIPE_PATTERN, 1, 1, 3, strings.length - 1);
      } else {
        return parseTXTLines(nullLiteral, strings, DASH_PATTERN, 1, 0, 2, strings.length);
      }
    }
  }

  private static final List<String[]> parseTXTLines(String nullLiteral, String[] strings, Pattern pattern, int matchLine, int headerLine, int dataLineStart, int dataLineEnd) {
    List<int[]> positions = new ArrayList<int[]>();
    Matcher m = pattern.matcher(strings[matchLine]);
    while (m.find()) {
      positions.add(new int[] { m.start(1), m.end(1) });
    }
    List<String[]> result = new ArrayList<String[]>();
    parseTXTLine(positions, result, strings[headerLine], nullLiteral);
    for (int j = dataLineStart; j < dataLineEnd; j++) {
      parseTXTLine(positions, result, strings[j], nullLiteral);
    }
    return result;
  }

  private static final void parseTXTLine(List<int[]> positions, List<String[]> result, String string, String nullLiteral) {
    String[] fields = new String[positions.size()];
    result.add(fields);
    int length = string.length();
    for (int i = 0; i < fields.length; i++) {
      int[] position = positions.get(i);
      if (position[0] < length) {
        fields[i] = string.substring(position[0], Math.min(position[1], length)).trim();
      } else {
        fields[i] = null;
      }
      if (StringUtils.equals(fields[i], nullLiteral)) {
        fields[i] = null;
      }
    }
  }

  private static final Pattern P_PARSE_HTML_ROW = Pattern.compile("<tr>(.*?)</tr>");

  private static final Pattern P_PARSE_HTML_COL_HEAD = Pattern.compile("<th>(.*?)</th>");

  private static final Pattern P_PARSE_HTML_COL_BODY = Pattern.compile("<td>(.*?)</td>");

  static final List<String[]> parseHTML(String string) {
    List<String[]> result = new ArrayList<String[]>();
    Matcher mRow = P_PARSE_HTML_ROW.matcher(string);
    while (mRow.find()) {
      String row = mRow.group(1);
      List<String> col = new ArrayList<String>();
      if (result.isEmpty()) {
        Matcher mColHead = P_PARSE_HTML_COL_HEAD.matcher(row);
        while (mColHead.find()) {
          col.add(mColHead.group(1));
        }
      }
      if (col.isEmpty()) {
        Matcher mColBody = P_PARSE_HTML_COL_BODY.matcher(row);
        while (mColBody.find()) {
          col.add(mColBody.group(1));
        }
        if (result.isEmpty()) {
          result.add(fieldNameStrings(col.size()));
        }
      }
      result.add(col.toArray(EMPTY_STRING));
    }
    return result;
  }

  /**
     * Generate the <code>BEGIN</code> part of an anonymous procedural block.
     */
  static final void begin(Context<?> ctx) {
    switch (ctx.family()) {
      case FIREBIRD:
      {
        ctx.visit(K_EXECUTE_BLOCK).formatSeparator().visit(K_AS).formatSeparator().visit(K_BEGIN).formatIndentStart().formatSeparator();
        break;
      }
      case MARIADB:
      {
        ctx.visit(K_BEGIN).sql(' ').visit(K_NOT).sql(' ').visit(K_ATOMIC).formatIndentStart().formatSeparator();
        break;
      }
      case POSTGRES:
      {
        if (increment(ctx.data(), DATA_BLOCK_NESTING)) {
          ctx.visit(K_DO).sql(" $$").formatSeparator();
        }
        ctx.visit(K_BEGIN).formatIndentStart().formatSeparator();
        break;
      }
    }
  }

  /**
     * Generate the <code>END</code> part of an anonymous procedural block.
     */
  static final void end(Context<?> ctx) {
    switch (ctx.family()) {
      case FIREBIRD:
      case MARIADB:
      {
        ctx.formatIndentEnd().formatSeparator().visit(K_END);
        break;
      }
      case POSTGRES:
      {
        ctx.formatIndentEnd().formatSeparator().visit(K_END);
        if (decrement(ctx.data(), DATA_BLOCK_NESTING)) {
          ctx.sql(" $$");
        }
        break;
      }
    }
  }

  /**
     * Wrap a statement in an <code>EXECUTE IMMEDIATE</code> statement.
     */
  static final void beginExecuteImmediate(Context<?> ctx) {
    switch (ctx.family()) {
      case FIREBIRD:
      {
        ctx.visit(K_EXECUTE_STATEMENT).sql(" \'").stringLiteral(true).formatIndentStart().formatSeparator();
        break;
      }
    }
  }

  /**
     * Wrap a statement in an <code>EXECUTE IMMEDIATE</code> statement.
     */
  static final void endExecuteImmediate(Context<?> ctx) {
    ctx.formatIndentEnd().formatSeparator().stringLiteral(false).sql("\';");
  }

  /**
     * Wrap a <code>DROP .. IF EXISTS</code> statement with
     * <code>BEGIN EXECUTE IMMEDIATE '...' EXCEPTION WHEN ... END;</code>, if
     * <code>IF EXISTS</code> is not supported.
     */
  static final void beginTryCatch(Context<?> ctx, DDLStatementType type) {
    beginTryCatch(ctx, type, null, null);
  }

  static final void beginTryCatch(Context<?> ctx, DDLStatementType type, Boolean container, Boolean element) {
    switch (ctx.family()) {
      case FIREBIRD:
      {
        begin(ctx);
        beginExecuteImmediate(ctx);
        break;
      }
      case MARIADB:
      {
        List<String> sqlstates = new ArrayList<String>();
        sqlstates.add("42S02");
        begin(ctx);
        for (String sqlstate : sqlstates) {
          ctx.visit(keyword("declare continue handler for sqlstate")).sql(' ').visit(DSL.inline(sqlstate)).sql(' ').visit(K_BEGIN).sql(' ').visit(K_END).sql(';').formatSeparator();
        }
        break;
      }
      default:
      break;
    }
  }

  /**
     * Wrap a <code>DROP .. IF EXISTS</code> statement with
     * <code>BEGIN EXECUTE IMMEDIATE '...' EXCEPTION WHEN ... END;</code>, if
     * <code>IF EXISTS</code> is not supported.
     */
  static final void endTryCatch(Context<?> ctx, DDLStatementType type) {
    endTryCatch(ctx, type, null, null);
  }

  static final void endTryCatch(Context<?> ctx, DDLStatementType type, Boolean container, Boolean element) {
    switch (ctx.family()) {
      case FIREBIRD:
      {
        endExecuteImmediate(ctx);
        ctx.formatSeparator().visit(K_WHEN).sql(" sqlcode -607 ").visit(K_DO).formatIndentStart().formatSeparator().visit(K_BEGIN).sql(' ').visit(K_END).formatIndentEnd();
        end(ctx);
        break;
      }
      case MARIADB:
      {
        ctx.sql(';');
        end(ctx);
        break;
      }
      default:
      break;
    }
  }

  static final void toSQLDDLTypeDeclarationForAddition(Context<?> ctx, DataType<?> type) {
    toSQLDDLTypeDeclaration(ctx, type);
    toSQLDDLTypeDeclarationIdentityBeforeNull(ctx, type);
    if (DEFAULT_BEFORE_NULL.contains(ctx.family())) {
      toSQLDDLTypeDeclarationDefault(ctx, type);
    }
    if (!type.nullable()) {
      ctx.sql(' ').visit(K_NOT_NULL);
    } else {
      if (!NO_SUPPORT_NULL.contains(ctx.family())) {
        ctx.sql(' ').visit(K_NULL);
      }
    }
    if (!DEFAULT_BEFORE_NULL.contains(ctx.family())) {
      toSQLDDLTypeDeclarationDefault(ctx, type);
    }
    toSQLDDLTypeDeclarationIdentityAfterNull(ctx, type);
  }

  /**
     * If a type is an identity type, some dialects require the relevant
     * keywords before the [ NOT ] NULL constraint.
     */
  static final void toSQLDDLTypeDeclarationIdentityBeforeNull(Context<?> ctx, DataType<?> type) {
    if (type.identity()) {
      switch (ctx.family()) {
        case CUBRID:
        ctx.sql(' ').visit(K_AUTO_INCREMENT);
        break;
        case DERBY:
        ctx.sql(' ').visit(K_GENERATED_BY_DEFAULT_AS_IDENTITY);
        break;
        case HSQLDB:
        ctx.sql(' ').visit(K_GENERATED_BY_DEFAULT_AS_IDENTITY).sql('(').visit(K_START_WITH).sql(" 1)");
        break;
        case SQLITE:
        ctx.sql(' ').visit(K_PRIMARY_KEY).sql(' ').visit(K_AUTOINCREMENT);
        break;
      }
    }
  }

  /**
     * If a type is an identity type, some dialects require the relevant
     * keywords after the [ NOT ] NULL constraint.
     */
  static final void toSQLDDLTypeDeclarationIdentityAfterNull(Context<?> ctx, DataType<?> type) {
    if (type.identity()) {
      switch (ctx.family()) {
        case H2:
        case MARIADB:
        case MYSQL:
        ctx.sql(' ').visit(K_AUTO_INCREMENT);
        break;
      }
    }
  }

  private static final void toSQLDDLTypeDeclarationDefault(Context<?> ctx, DataType<?> type) {
    if (type.defaulted()) {
      ctx.sql(' ').visit(K_DEFAULT).sql(' ').visit(type.defaultValue());
    }
  }

  static final void toSQLDDLTypeDeclaration(Context<?> ctx, DataType<?> type) {
    String typeName = type.getTypeName(ctx.configuration());
    if (type.identity()) {
      switch (ctx.family()) {
        case POSTGRES:
        ctx.visit(type.getType() == Long.class ? K_SERIAL8 : K_SERIAL);
        return;
      }
    }
    if (EnumType.class.isAssignableFrom(type.getType())) {
      switch (ctx.family()) {
        case MARIADB:
        case MYSQL:
        {
          ctx.visit(K_ENUM).sql('(');
          Object[] enums = type.getType().getEnumConstants();
          if (enums == null) {
            throw new IllegalStateException("EnumType must be a Java enum");
          }
          String separator = "";
          for (Object e : enums) {
            ctx.sql(separator).visit(DSL.inline(((EnumType) e).getLiteral()));
            separator = ", ";
          }
          ctx.sql(')');
          return;
        }
      }
    }
    if (type.getType() == UUID.class) {
      switch (ctx.family()) {
        case MARIADB:
        case MYSQL:
        {
          toSQLDDLTypeDeclaration(ctx, VARCHAR(36));
          return;
        }
      }
    }
    if (type.hasLength()) {
      if (type.isBinary() && ctx.family() == POSTGRES) {
        ctx.sql(typeName);
      } else {
        if (type.length() > 0) {
          ctx.sql(typeName).sql('(').sql(type.length()).sql(')');
        } else {
          String castTypeName = type.getCastTypeName(ctx.configuration());
          if (!typeName.equals(castTypeName)) {
            ctx.sql(castTypeName);
          } else {
            ctx.sql(typeName);
          }
        }
      }
    } else {
      if (type.hasPrecision() && type.precision() > 0) {
        if (type.hasScale()) {
          ctx.sql(typeName).sql('(').sql(type.precision()).sql(", ").sql(type.scale()).sql(')');
        } else {
          ctx.sql(typeName).sql('(').sql(type.precision()).sql(')');
        }
      } else {
        if (type.identity() && ctx.family() == SQLITE && type.isNumeric()) {
          ctx.sql("integer");
        } else {
          ctx.sql(typeName);
        }
      }
    }
    if (type.collation() != null) {
      ctx.sql(' ').visit(K_COLLATE).sql(' ').visit(type.collation());
    }
  }

  static <T extends java.lang.Object> Supplier<T> blocking(Supplier<T> supplier) {
    return blocking(supplier, false);
  }

  static <T extends java.lang.Object> Supplier<T> blocking(Supplier<T> supplier, boolean threadLocal) {
    return threadLocal ? supplier : new Supplier<T>() {
      volatile T asyncResult;

      @Override public T get() {
        try {
          ForkJoinPool.managedBlock(new ManagedBlocker() {
            @Override public boolean block() {
              asyncResult = supplier.get();
              return true;
            }

            @Override public boolean isReleasable() {
              return asyncResult != null;
            }
          });
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        return asyncResult;
      }
    };
  }

  static <E extends EnumType> EnumType[] enums(Class<? extends E> type) {
    if (Enum.class.isAssignableFrom(type)) {
      return type.getEnumConstants();
    } else {
      try {
        Class<?> companionClass = Thread.currentThread().getContextClassLoader().loadClass(type.getName() + "$");
        java.lang.reflect.Field module = companionClass.getField("MODULE$");
        Object companion = module.get(companionClass);
        return (EnumType[]) companionClass.getMethod("values").invoke(companion);
      } catch (Exception e) {
        throw new MappingException("Error while looking up Scala enum", e);
      }
    }
  }

  /**
     * Whether a Java type is suitable for {@link Types#TIME}.
     */
  static final boolean isTime(Class<?> t) {
    return t == Time.class || t == LocalTime.class;
  }

  /**
     * Whether a Java type is suitable for {@link Types#TIMESTAMP}.
     */
  static final boolean isTimestamp(Class<?> t) {
    return t == Timestamp.class || t == LocalDateTime.class;
  }

  /**
     * Whether a Java type is suitable for {@link Types#DATE}.
     */
  static final boolean isDate(Class<?> t) {
    return t == Date.class || t == LocalDate.class;
  }

  static final boolean hasAmbiguousNames(Collection<? extends Field<?>> fields) {
    if (fields == null) {
      return false;
    }
    Set<String> names = new HashSet<String>();
    for (Field<?> field : fields) {
      if (!names.add(field.getName())) {
        return true;
      }
    }
    return false;
  }

  static final <T extends java.lang.Object> Field<T> qualify(Field<T> field, Table<?> table) {
    Field<T> result = table.field(field);
    if (result != null) {
      return result;
    }
    Name[] part = table.getQualifiedName().parts();
    Name[] name = new Name[part.length + 1];
    System.arraycopy(part, 0, name, 0, part.length);
    name[part.length] = field.getUnqualifiedName();
    return DSL.field(DSL.name(name), field.getDataType());
  }

  static final <R extends Record> Table<R> aliased(Table<R> table) {
    if (table instanceof TableImpl) {
      return ((TableImpl<R>) table).getAliasedTable();
    } else {
      if (table instanceof TableAlias) {
        return ((TableAlias<R>) table).getAliasedTable();
      } else {
        return null;
      }
    }
  }

  static final <R extends Record> Alias<Table<R>> alias(Table<R> table) {
    if (table instanceof TableImpl) {
      return ((TableImpl<R>) table).alias;
    } else {
      if (table instanceof TableAlias) {
        return ((TableAlias<R>) table).alias;
      } else {
        return null;
      }
    }
  }

  /**
     * Increment a counter and return true if the counter was zero prior to
     * incrementing.
     */
  static final boolean increment(Map<Object, Object> data, DataKey key) {
    boolean result = true;
    Integer updateCounts = (Integer) data.get(key);
    if (updateCounts == null) {
      updateCounts = 0;
    } else {
      result = false;
    }
    data.put(key, updateCounts + 1);
    return result;
  }

  /**
     * Decrement a counter and return true if the counter is zero after
     * decrementing.
     */
  static final boolean decrement(Map<Object, Object> data, DataKey key) {
    boolean result = false;
    Integer updateCounts = (Integer) data.get(key);
    if (updateCounts == null || updateCounts == 0) {
      throw new IllegalStateException("Unmatching increment / decrement on key: " + key);
    } else {
      if (updateCounts == 1) {
        result = true;
      }
    }
    data.put(key, updateCounts - 1);
    return result;
  }

  static Field<?> tableField(Table<?> table, Object field) {
    if (field instanceof Field<?>) {
      return (Field<?>) field;
    } else {
      if (field instanceof Name) {
        return table.field((Name) field);
      } else {
        if (field instanceof String) {
          return table.field((String) field);
        } else {
          throw new IllegalArgumentException("Field type not supported: " + field);
        }
      }
    }
  }

  /**
     * Convert a byte array to a hex encoded string.
     *
     * @param value the byte array
     * @param len the number of bytes to encode
     * @return the hex encoded string
     */
  static final String convertBytesToHex(byte[] value, int len) {
    char[] buff = new char[len + len];
    char[] hex = HEX_DIGITS;
    for (int i = 0; i < len; i++) {
      int c = value[i] & 0xff;
      buff[i + i] = hex[c >> 4];
      buff[i + i + 1] = hex[c & 0xf];
    }
    return new String(buff);
  }

  /**
     * Convert a byte array to a hex encoded string.
     *
     * @param value the byte array
     * @return the hex encoded string
     */
  static final String convertBytesToHex(byte[] value) {
    return convertBytesToHex(value, value.length);
  }

  static final boolean isNotEmpty(Collection<?> collection) {
    return collection != null && !collection.isEmpty();
  }

  static final boolean isEmpty(Collection<?> collection) {
    return collection == null || collection.isEmpty();
  }

  static final boolean isNotEmpty(Object[] array) {
    return array != null && array.length > 0;
  }

  static final boolean isEmpty(Object[] array) {
    return array == null || array.length == 0;
  }
}