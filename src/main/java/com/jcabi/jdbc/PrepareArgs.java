package com.jcabi.jdbc;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;

/**
 * Prepare arguments.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.13
 */
final class PrepareArgs implements Preparation {
  /**
     * Arguments.
     */
  private final transient Collection<Object> args;

  /**
     * Ctor.
     * @param arguments Arguments
     */
  PrepareArgs(final Collection<Object> arguments) {
    this.args = Collections.unmodifiableCollection(arguments);
  }

  @Override public void prepare(final PreparedStatement stmt) throws SQLException {
    int pos = 1;
    for (final Object arg : this.args) {
      if (arg == null) {
        stmt.setString(pos, null);
      } else {
        if (arg instanceof Long) {
          stmt.setLong(pos, Long.class.cast(arg));
        } else {
          if (arg instanceof Boolean) {
            stmt.setBoolean(pos, Boolean.class.cast(arg));
          } else {
            if (arg instanceof Date) {
              stmt.setDate(pos, Date.class.cast(arg));
            } else {
              if (arg instanceof Integer) {
                stmt.setInt(pos, Integer.class.cast(arg));
              } else {
                if (arg instanceof Utc) {
                  Utc.class.cast(arg).setTimestamp(stmt, pos);
                } else {
                  if (arg instanceof byte[]) {
                    stmt.setBytes(pos, 
<<<<<<< /usr/src/app/output/jcabi/jcabi-jdbc/40e95d792465ff63e698273a475a8b256cf11a5b/src/main/java/com/jcabi/jdbc/PrepareArgs.java/left.java
                    byte[].class.cast(arg)
=======
                    (byte[]) arg
>>>>>>> /usr/src/app/output/jcabi/jcabi-jdbc/40e95d792465ff63e698273a475a8b256cf11a5b/src/main/java/com/jcabi/jdbc/PrepareArgs.java/right.java
                    );
                  } else {
                    stmt.setString(pos, arg.toString());
                  }
                }
              }
            }
          }
        }
      }
      ++pos;
    }
  }
}