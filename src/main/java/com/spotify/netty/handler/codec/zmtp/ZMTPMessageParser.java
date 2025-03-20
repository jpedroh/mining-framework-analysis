package com.spotify.netty.handler.codec.zmtp;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import static com.spotify.netty.handler.codec.zmtp.ZMTPUtils.MORE_FLAG;
import static java.nio.ByteOrder.BIG_ENDIAN;
import static java.lang.Math.min;
import static org.jboss.netty.buffer.ChannelBuffers.swapLong;

/**
 * Decodes ZMTP messages from a channel buffer, reading and accumulating frame by frame, keeping
 * state and updating buffer reader indices as necessary.
 */
public class ZMTPMessageParser {
  private static final byte LONG_FLAG = 0x02;

  private final boolean enveloped;

  private final long sizeLimit;

  private final int version;

  private List<ZMTPFrame> envelope;

  private List<ZMTPFrame> content;

  private List<ZMTPFrame> part;

  private boolean hasMore;

  private long size;

  private int frameSize;

  private int frameRemaining;

  private boolean headerParsed;

  public ZMTPMessageParser(final boolean enveloped, final long sizeLimit, int version) {
    this.enveloped = enveloped;
    this.sizeLimit = sizeLimit;
    this.version = version;
    reset();
  }

  /**
     * Parses as many whole frames from the buffer as possible, until the final frame is encountered.
     * If the message was completed, it returns the frames of the message. Otherwise it returns null
     * to indicate that more data is needed.
     * <p/>
     * <p> Oversized messages will be truncated by discarding frames that would make the message size
     * exceeed the specified size limit.
     *
     * @param buffer Buffer with data
     * @return A {@link ZMTPMessage} if it was completely parsed, otherwise null.
     */
  public ZMTPParsedMessage parse(final ByteBuf buffer) throws ZMTPMessageParsingException {
    if (isOversized(size)) {
      return discardFrames(buffer);
    }
    while (buffer.readableBytes() > 0) {
      buffer.markReaderIndex();
      final boolean parsedHeader = parseZMTPHeader(buffer);
      if (!parsedHeader) {
        buffer.resetReaderIndex();
        return null;
      }
      if (isOversized(size + frameSize)) {
        buffer.resetReaderIndex();
        return discardFrames(buffer);
      }
      if (frameSize > buffer.readableBytes()) {
        buffer.resetReaderIndex();
        return null;
      }
      size += frameSize;
      final ZMTPFrame frame = ZMTPFrame.read(buffer, frameSize);
      if (!frame.hasData() && part == envelope) {
        part = content;
      } else {
        part.add(frame);
      }
      if (!hasMore) {
        return finish(false);
      }
    }
    return null;
  }

  /**
     * Check if the message is too large and frames should be discarded.
     */
  private boolean isOversized(final long size) {
    return size > sizeLimit;
  }

  /**
     * Create a message from the parsed frames and reset the parser.
     */
  private ZMTPParsedMessage finish(final boolean truncated) {
    final ZMTPMessage message = new ZMTPMessage(envelope, content);
    final ZMTPParsedMessage parsedMessage = new ZMTPParsedMessage(truncated, size, message);
    reset();
    return parsedMessage;
  }

  /**
     * Reset parser in preparation for the next message.
     */
  private void reset() {
    envelope = new ArrayList<ZMTPFrame>(3);
    content = new ArrayList<ZMTPFrame>(3);
    part = enveloped ? envelope : content;
    hasMore = true;
    size = 0;
  }

  /**
     * Discard frames for current message.
     *
     * @return A truncated message if done discarding, null if not yet done.
     */
  private ZMTPParsedMessage discardFrames(final ByteBuf buffer) throws ZMTPMessageParsingException {
    while (buffer.readableBytes() > 0) {
      if (!headerParsed) {
        buffer.markReaderIndex();
        headerParsed = parseZMTPHeader(buffer);
        if (!headerParsed) {
          buffer.resetReaderIndex();
          return null;
        }
        size += frameSize;
        frameRemaining = frameSize;
      }
      final int discardBytes = min(frameRemaining, buffer.readableBytes());
      frameRemaining -= discardBytes;
      buffer.skipBytes(discardBytes);
      final boolean done = frameRemaining == 0;
      if (done) {
        headerParsed = false;
      }
      if (done && !hasMore) {
        return finish(true);
      }
    }
    return null;
  }


<<<<<<< /usr/src/app/output/spotify/netty-zmtp/8d02651ebcbfc80ea17699b5547176248b2635c7/src/main/java/com/spotify/netty/handler/codec/zmtp/ZMTPMessageParser.java/left.java
  /**
     * Parse a frame header.
     */
  private boolean parseFrameHeader(final ByteBuf buffer) throws ZMTPMessageParsingException {
    final long len = ZMTPUtils.decodeLength(buffer);
    if (len > Integer.MAX_VALUE) {
      throw new ZMTPMessageParsingException("Received too large frame: " + len);
    }
    if (len == -1) {
      return false;
    }
    if (len == 0) {
      throw new ZMTPMessageParsingException("Received frame with zero length");
    }
    if (buffer.readableBytes() < 1) {
      return false;
    }
    frameSize = (int) len - 1;
    hasMore = (buffer.readByte() & MORE_FLAG) == MORE_FLAG;
    return true;
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  private boolean parseZMTPHeader(final ChannelBuffer buffer) throws ZMTPMessageParsingException {
    return version == 1 ? parseZMTP1Header(buffer) : parseZMTP2Header(buffer);
  }

  /**
   * Parse a frame header.
   */
  private boolean parseZMTP1Header(final ChannelBuffer buffer) throws ZMTPMessageParsingException {
    final long len = ZMTPUtils.decodeLength(buffer);
    if (len > Integer.MAX_VALUE) {
      throw new ZMTPMessageParsingException("Received too large frame: " + len);
    }
    if (len == -1) {
      return false;
    }
    if (len == 0) {
      throw new ZMTPMessageParsingException("Received frame with zero length");
    }
    if (buffer.readableBytes() < 1) {
      return false;
    }
    frameSize = (int) len - 1;
    hasMore = (buffer.readByte() & MORE_FLAG) == MORE_FLAG;
    return true;
  }

  private boolean parseZMTP2Header(ChannelBuffer buffer) throws ZMTPMessageParsingException {
    if (buffer.readableBytes() < 2) {
      return false;
    }
    int flags = buffer.readByte();
    hasMore = (flags & MORE_FLAG) == MORE_FLAG;
    if ((flags & LONG_FLAG) != LONG_FLAG) {
      frameSize = buffer.readByte() & 0xff;
      return true;
    }
    if (buffer.readableBytes() < 8) {
      return false;
    }
    long len;
    if (buffer.order() == BIG_ENDIAN) {
      len = buffer.readLong();
    } else {
      len = swapLong(buffer.readLong());
    }
    if (len > Integer.MAX_VALUE) {
      throw new ZMTPMessageParsingException("Received too large frame: " + len);
    }
    frameSize = (int) len;
    return true;
  }
}