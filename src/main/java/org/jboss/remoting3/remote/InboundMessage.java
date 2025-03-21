package org.jboss.remoting3.remote;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.jboss.remoting3.MessageCancelledException;
import org.jboss.remoting3.MessageInputStream;
import org.jboss.remoting3._private.IntIndexer;
import org.xnio.Pooled;
import org.xnio.streams.BufferPipeInputStream;
import static org.jboss.remoting3.remote.RemoteLogger.log;
import static java.lang.Thread.holdsLock;
import static org.xnio.IoUtils.safeClose;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
final class InboundMessage {
  final short messageId;

  final RemoteConnectionChannel channel;

  int inboundWindow;

  boolean streamClosed;

  boolean closeSent;

  boolean eofReceived;

  boolean cancelled;

  long remaining;

  static final IntIndexer<InboundMessage> INDEXER = new IntIndexer<InboundMessage>() {
    public int getKey(final InboundMessage argument) {
      return argument.messageId & 0xffff;
    }

    public boolean equals(final InboundMessage argument, final int index) {
      return (argument.messageId & 0xffff) == index;
    }
  };

  InboundMessage(final short messageId, final RemoteConnectionChannel channel, int inboundWindow, final long maxInboundMessageSize) {
    this.messageId = messageId;
    this.channel = channel;
    this.inboundWindow = inboundWindow;
    remaining = maxInboundMessageSize;
  }

  final BufferPipeInputStream inputStream = new BufferPipeInputStream(new BufferPipeInputStream.InputHandler() {
    public void acknowledge(final Pooled<ByteBuffer> acked) throws IOException {
      doAcknowledge(acked);
    }

    public void close() throws IOException {
      doClose();
    }
  });

  void terminate() {
    synchronized (inputStream) {
      safeClose(inputStream);
    }
  }

  private void doClose() {
    assert holdsLock(inputStream);
    if (streamClosed) {
      return;
    }
    streamClosed = true;
    doSendCloseMessage();
  }

  private void doSendCloseMessage() {
    assert holdsLock(inputStream);
    if (closeSent || !channel.getConnectionHandler().isMessageClose()) {
      return;
    }
    Pooled<ByteBuffer> pooled = allocate(Protocol.MESSAGE_CLOSE);
    boolean ok = false;
    try {
      ByteBuffer buffer = pooled.getResource();
      buffer.flip();
      channel.getRemoteConnection().send(pooled);
      ok = true;
      closeSent = true;
    }  finally {
      if (!ok) {
        pooled.free();
      }
    }
  }

  private void doAcknowledge(final Pooled<ByteBuffer> acked) {
    assert holdsLock(inputStream);
    if (eofReceived) {
      return;
    }
    final boolean badMsgSize = channel.getConnectionHandler().isFaultyMessageSize();
    int consumed = acked.getResource().position();
    if (!badMsgSize) {
      consumed -= 8;
    }
    inboundWindow += consumed;
    Pooled<ByteBuffer> pooled = allocate(Protocol.MESSAGE_WINDOW_OPEN);
    boolean ok = false;
    try {
      ByteBuffer buffer = pooled.getResource();
      buffer.putInt(consumed);
      buffer.flip();
      channel.getRemoteConnection().send(pooled);
      ok = true;
    }  finally {
      if (!ok) {
        pooled.free();
      }
    }
  }

  final MessageInputStream messageInputStream = new MessageInputStream() {
    public int read() throws IOException {
      synchronized (inputStream) {
        if (cancelled) {
          throw new MessageCancelledException();
        }
        return inputStream.read();
      }
    }

    public int read(final byte[] bytes, final int offs, final int length) throws IOException {
      synchronized (inputStream) {
        if (cancelled) {
          throw new MessageCancelledException();
        }
        return inputStream.read(bytes, offs, length);
      }
    }

    public long skip(final long l) throws IOException {
      synchronized (inputStream) {
        if (cancelled) {
          throw new MessageCancelledException();
        }
        return inputStream.skip(l);
      }
    }

    public int available() throws IOException {
      synchronized (inputStream) {
        if (cancelled) {
          throw new MessageCancelledException();
        }
        return inputStream.available();
      }
    }

    public void close() throws IOException {
      synchronized (inputStream) {
        if (!streamClosed) {
          inputStream.close();
          if (cancelled) {
            throw new MessageCancelledException();
          }
        }
      }
    }
  };

  Pooled<ByteBuffer> allocate(byte protoId) {
    Pooled<ByteBuffer> pooled = channel.allocate(protoId);
    ByteBuffer buffer = pooled.getResource();
    buffer.putShort(messageId);
    return pooled;
  }

  void handleIncoming(Pooled<ByteBuffer> pooledBuffer) {
    boolean eof;
    boolean free = true;
    try {
      synchronized (inputStream) {
        ByteBuffer buffer = pooledBuffer.getResource();
        final int bufRemaining = buffer.remaining();
        if ((inboundWindow -= bufRemaining) < 0) {
          channel.getRemoteConnection().handleException(new IOException("Input overrun"));
          return;
        }
        if (log.isTraceEnabled()) {
          log.tracef("Received message (chan %08x msg %04x) (%d-%d=%d remaining)", Integer.valueOf(channel.getChannelId()), Short.valueOf(messageId), Integer.valueOf(inboundWindow + bufRemaining), Integer.valueOf(bufRemaining), Integer.valueOf(inboundWindow));
        }
        buffer.position(buffer.position() - 1);
        byte flags = buffer.get();
        eof = (flags & Protocol.MSG_FLAG_EOF) != 0;
        boolean cancelled = (flags & Protocol.MSG_FLAG_CANCELLED) != 0;
        if (bufRemaining > remaining) {
          cancelled = true;
          doClose();
        }
        if (cancelled) {
          this.cancelled = true;
          inputStream.pushException(new MessageCancelledException());
        }
        if (streamClosed) {
          if (!eof && !closeSent) {
            buffer.position(buffer.limit());
            doAcknowledge(pooledBuffer);
          }
        } else {
          if (!cancelled) {
            remaining -= bufRemaining;
            free = false;
            inputStream.push(pooledBuffer);
          }
        }
        if (eof) {
          eofReceived = true;
          if (!streamClosed) {
            inputStream.pushEof();
          }
          channel.freeInboundMessage(messageId);
          doSendCloseMessage();
        }
      }
    }  finally {
      if (free) {
        pooledBuffer.free();
      }
    }
  }

  void handleDuplicate() {
    RemoteLogger.conn.duplicateMessageId(messageId, channel.getRemoteConnection().getChannel().getPeerAddress());
    synchronized (inputStream) {
      if (!streamClosed) {
        eofReceived = true;
        closeSent = true;
        cancelled = true;
        inputStream.pushException(RemoteLogger.conn.duplicateMessageIdException());
      }
    }
  }

  void dumpState(final StringBuilder b) {
    b.append("            ").append(String.format("Inbound message ID %04x, window %d\n", messageId & 0xFFFF, inboundWindow));
    b.append("            ").append("* flags: ");
    if (cancelled) {
      b.append("cancelled ");
    }
    if (closeSent) {
      b.append("close-sent ");
    }
    if (streamClosed) {
      b.append("stream-closed ");
    }
    if (eofReceived) {
      b.append("eof-received ");
    }
    b.append('\n');
  }
}