package com.addthis.meshy.service.stream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import com.addthis.basis.util.Parameter;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Counter;
import com.yammer.metrics.core.Meter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamService {
  protected static final Logger log = LoggerFactory.getLogger(StreamService.class);

  public static final String ERROR_EXCEED_OPEN = "Exceeded Max Open Files";

  public static final String ERROR_CHANNEL_LOST = "Channel Connection Lost";

  public static final int STREAM_BYTE_OVERHEAD = 1;

  static final int MODE_START = 0;

  static final int MODE_MORE = 1;

  static final int MODE_FAIL = 2;

  static final int MODE_CLOSE = 3;

  static final int MODE_START_2 = 4;

  static final byte[] FAIL_BYTES = new byte[0];

  static final boolean DIRECT_COPY = Parameter.boolValue("meshy.copy.direct", true);

  static final boolean LOG_DROP_MORE = Parameter.boolValue("meshy.log.dropmore", false);

  static final long READ_WAIT = Parameter.longValue("meshy.read.wait", 10);

  static final Counter openStreams = Metrics.newCounter(StreamService.class, "openStreams");

  static final Meter newStreamMeter = Metrics.newMeter(StreamService.class, "newStreams", "newStreams", TimeUnit.SECONDS);

  static final AtomicInteger newOpenStreams = new AtomicInteger(0);

  static final AtomicInteger closedStreams = new AtomicInteger(0);

  static final AtomicInteger readBytes = new AtomicInteger(0);

  static final AtomicInteger seqReads = new AtomicInteger(0);

  static final AtomicInteger totalReads = new AtomicInteger(0);

  static final AtomicInteger readWaitTime = new AtomicInteger(0);

  static final AtomicInteger sendWaiting = new AtomicInteger(0);

  static final AtomicInteger sleeps = new AtomicInteger(0);
}