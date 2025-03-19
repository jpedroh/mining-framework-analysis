package org.restheart.db;
import com.mongodb.DBCursor;

/**
 *
 * @author Andrea Di Cesare <andrea@softinstigate.com>
 */
public class SkippedDBCursor {
  private final DBCursor cursor;

  private final int alreadySkipped;

  public SkippedDBCursor(DBCursor cursor, int alreadySkipped) {
    this.cursor = cursor;
    this.alreadySkipped = alreadySkipped;
  }

  /**
     * @return the alreadySkipped
     */
  public int getAlreadySkipped() {
    return alreadySkipped;
  }

  /**
     * @return the cursor
     */
  public DBCursor getCursor() {
    return cursor;
  }
}