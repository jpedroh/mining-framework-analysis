package com.jcabi.jdbc;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Outcome that returns first column.
 *
 * <p>Use it when you need the first column:
 *
 * <pre> Collection&lgt;Long&gt; salaries = new JdbcSession(source)
 *   .sql("SELECT salary FROM user")
 *   .select(new ColumnOutcome&lt;Long&gt;(Long.class));</pre>
 *
 * <p>Supported types are: {@link String}, {@link Long}, {@link Boolean},
 * {@link Byte}, {@link Date}, and {@link Utc}.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.13
 */
@Immutable @ToString @EqualsAndHashCode(of = "type") public final class ColumnOutcome<T extends java.lang.Object> implements Outcome<Collection<T>> {
  /**
     * The type name.
     */
  private final transient String type;

  /**
     * Public ctor.
     * @param tpe The type to convert to
     */
  public ColumnOutcome(@NotNull(message = "type can\'t be NULL") final Class<T> tpe) {
    if (tpe.equals(String.class) || tpe.equals(Long.class) || tpe.equals(Boolean.class) || tpe.equals(Byte.class) || tpe.equals(Date.class) || tpe.equals(Utc.class) || byte[].
<<<<<<< /usr/src/app/output/jcabi/jcabi-jdbc/40e95d792465ff63e698273a475a8b256cf11a5b/src/main/java/com/jcabi/jdbc/ColumnOutcome.java/left.java
    class.equals(tpe)
=======
    equals(byte[].class)
>>>>>>> /usr/src/app/output/jcabi/jcabi-jdbc/40e95d792465ff63e698273a475a8b256cf11a5b/src/main/java/com/jcabi/jdbc/ColumnOutcome.java/right.java
    ) {
      this.type = tpe.getName();
    } else {
      throw new IllegalArgumentException(String.format("type %s is not supported", tpe.getName()));
    }
  }

  @Override @Loggable(value = Loggable.DEBUG) public Collection<T> handle(final ResultSet rset, final Statement stmt) throws SQLException {
    final Collection<T> result = new LinkedList<T>();
    while (rset.next()) {
      result.add(this.fetch(rset));
    }
    return result;
  }

  /**
     * Fetch the value from result set.
     * @param rset Result set
     * @return The result
     * @throws SQLException If some error inside
     */
  @SuppressWarnings(value = { "unchecked" }) private T fetch(final ResultSet rset) throws SQLException {
    final Object result;
    Class<T> tpe;
    try {
      tpe = (Class<T>) Class.forName(this.type);
      if (tpe.equals(String.class)) {
        result = rset.getString(1);
      } else {
        if (tpe.equals(Long.class)) {
          result = rset.getLong(1);
        } else {
          if (tpe.equals(Boolean.class)) {
            result = rset.getBoolean(1);
          } else {
            if (tpe.equals(Byte.class)) {
              result = rset.getByte(1);
            } else {
              if (tpe.equals(Date.class)) {
                result = rset.getDate(1);
              } else {
                if (tpe.equals(Utc.class)) {
                  result = new Utc(Utc.getTimestamp(rset, 1));
                } else {
                  if (
<<<<<<< /usr/src/app/output/jcabi/jcabi-jdbc/40e95d792465ff63e698273a475a8b256cf11a5b/src/main/java/com/jcabi/jdbc/ColumnOutcome.java/left.java
                  byte[].class.equals(tpe)
=======
                  tpe.equals(byte[].class)
>>>>>>> /usr/src/app/output/jcabi/jcabi-jdbc/40e95d792465ff63e698273a475a8b256cf11a5b/src/main/java/com/jcabi/jdbc/ColumnOutcome.java/right.java
                  ) {
                    result = rset.getBytes(1);
                  } else {
                    throw new IllegalStateException(String.format("type %s is not allowed", tpe.getName()));
                  }
                }
              }
            }
          }
        }
      }
    } catch (final ClassNotFoundException ex) {
      throw new IllegalArgumentException(String.format("Unknown type: %s", this.type), ex);
    }
    return tpe.cast(result);
  }
}