package com.datastax.driver.core;
import com.datastax.driver.core.exceptions.DriverInternalError;

public enum ConsistencyLevel {
  ANY(0),
  ONE(1),
  TWO(2),
  THREE(3),
  QUORUM(4),
  ALL(5),
  LOCAL_QUORUM(6),
  EACH_QUORUM(7),
  SERIAL(8),
  LOCAL_SERIAL(9),
  LOCAL_ONE(10)
  ;

  final int code;

  private static final ConsistencyLevel[] codeIdx;

  static {
    int maxCode = -1;
    for (ConsistencyLevel cl : ConsistencyLevel.values()) {
      maxCode = Math.max(maxCode, cl.code);
    }
    codeIdx = new ConsistencyLevel[maxCode + 1];
    for (ConsistencyLevel cl : ConsistencyLevel.values()) {
      if (codeIdx[cl.code] != null) {
        throw new IllegalStateException("Duplicate code");
      }
      codeIdx[cl.code] = cl;
    }
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  static ConsistencyLevel from(org.apache.cassandra.db.ConsistencyLevel cl) {
    switch (cl) {
      case ANY:
      return ANY;
      case ONE:
      return ONE;
      case TWO:
      return TWO;
      case THREE:
      return THREE;
      case QUORUM:
      return QUORUM;
      case ALL:
      return ALL;
      case LOCAL_QUORUM:
      return LOCAL_QUORUM;
      case EACH_QUORUM:
      return EACH_QUORUM;
      case LOCAL_ONE:
      return LOCAL_ONE;
    }
    throw new AssertionError();
  }
>>>>>>> /usr/src/app/output/datastax/java-driver/3705c952425480778ddd51751be4db54cf75bc81/driver-core/src/main/java/com/datastax/driver/core/ConsistencyLevel.java/right.java


  private ConsistencyLevel(int code) {
    this.code = code;
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  static org.apache.cassandra.db.ConsistencyLevel toCassandraCL(ConsistencyLevel cl) {
    if (cl == null) {
      return org.apache.cassandra.db.ConsistencyLevel.ONE;
    }
    switch (cl) {
      case ANY:
      return org.apache.cassandra.db.ConsistencyLevel.ANY;
      case ONE:
      return org.apache.cassandra.db.ConsistencyLevel.ONE;
      case TWO:
      return org.apache.cassandra.db.ConsistencyLevel.TWO;
      case THREE:
      return org.apache.cassandra.db.ConsistencyLevel.THREE;
      case QUORUM:
      return org.apache.cassandra.db.ConsistencyLevel.QUORUM;
      case ALL:
      return org.apache.cassandra.db.ConsistencyLevel.ALL;
      case LOCAL_QUORUM:
      return org.apache.cassandra.db.ConsistencyLevel.LOCAL_QUORUM;
      case EACH_QUORUM:
      return org.apache.cassandra.db.ConsistencyLevel.EACH_QUORUM;
      case LOCAL_ONE:
      return org.apache.cassandra.db.ConsistencyLevel.LOCAL_ONE;
    }
    throw new AssertionError();
  }
>>>>>>> /usr/src/app/output/datastax/java-driver/3705c952425480778ddd51751be4db54cf75bc81/driver-core/src/main/java/com/datastax/driver/core/ConsistencyLevel.java/right.java


  static ConsistencyLevel fromCode(int code) {
    if (code < 0 || code >= codeIdx.length) {
      throw new DriverInternalError(String.format("Unknown code %d for a consistency level", code));
    }
    return codeIdx[code];
  }
}