// Copyright (C) 2011 - Will Glozer.  All rights reserved.

package com.lambdaworks.redis.protocol;

/**
 * Keyword modifiers for redis commands.
 * 
 * @author Will Glozer
 */
public enum CommandKeyword implements ProtocolKeyword {
    ADDR, ADDSLOTS, AFTER, AGGREGATE, ALPHA, AND, ASK, ASC, ASYNC, BEFORE, BY, CHANNELS, COUNT, COUNTKEYSINSLOT, DELSLOTS, DESC, SOFT, HARD, ENCODING,

    FAILOVER, FORGET, FLUSH, FORCE, FLUSHSLOTS, GETNAME, GETKEYSINSLOT, HTSTATS, ID, IDLETIME, KILL, KEYSLOT, LEN, LIMIT, LIST, LOAD, MATCH,

    MAX, MEET, MIN, MOVED, NO, NODE, NODES, NOSAVE, NOT, NUMSUB, NUMPAT, ONE, OR, PAUSE, REFCOUNT, REMOVE, RELOAD, REPLICATE, RESET,

<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/protocol/CommandKeyword.java/left.java
    REWRITE, RESETSTAT, SAVECONFIG, SETNAME, SETSLOT, SLOTS, STABLE, MIGRATING, IMPORTING, SKIPME, SLAVES, STORE, SUM, SEGFAULT, WEIGHTS,
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/protocol/CommandKeyword.java/base.java
    REWRITE, RESETSTAT, SETNAME, SETSLOT, SLOTS, MIGRATING, IMPORTING, SKIPME, SLAVES, STORE, SUM, SEGFAULT, WEIGHTS,
=======
    RESETSTAT, RESTART, REWRITE, SAVECONFIG, SDSLEN, SETNAME, SETSLOT, SLOTS, STABLE, MIGRATING, IMPORTING, SKIPME, SLAVES, STORE, SUM, SEGFAULT, WEIGHTS,
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/protocol/CommandKeyword.java/right.java

    WITHSCORES, XOR;

    public final byte[] bytes;

    private CommandKeyword() {
        bytes = name().getBytes(LettuceCharsets.ASCII);
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }
}
