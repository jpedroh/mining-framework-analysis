package org.agilewiki.pactor;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import org.agilewiki.pactor.impl.MailboxImpl;
import org.agilewiki.pactor.impl.MessageQueue;
import org.slf4j.Logger;
import org.agilewiki.pactor.impl.MessageQueueFactory;
import org.slf4j.LoggerFactory;
import org.agilewiki.pactor.impl.MessageQueueFactoryImpl;

public final class MailboxFactory {
  private final ExecutorService executorService;

  private final static 
<<<<<<< /usr/src/app/output/laforge49/jactor2/820eaa1534e1a211c83c3d3c0f949da64b736903/src/main/java/org/agilewiki/pactor/MailboxFactory.java/left.java
  MessageQueueFactory
=======
  Logger
>>>>>>> /usr/src/app/output/laforge49/jactor2/820eaa1534e1a211c83c3d3c0f949da64b736903/src/main/java/org/agilewiki/pactor/MailboxFactory.java/right.java
   
<<<<<<< /usr/src/app/output/laforge49/jactor2/820eaa1534e1a211c83c3d3c0f949da64b736903/src/main/java/org/agilewiki/pactor/MailboxFactory.java/left.java
  messageQueueFactory
=======
  LOG = LoggerFactory.getLogger(MailboxFactory.class)
>>>>>>> /usr/src/app/output/laforge49/jactor2/820eaa1534e1a211c83c3d3c0f949da64b736903/src/main/java/org/agilewiki/pactor/MailboxFactory.java/right.java
  ;



  /** Must also be thread-safe. */
  private final List<AutoCloseable> closables = new Vector<AutoCloseable>();

  private final AtomicBoolean shuttingDown = new AtomicBoolean();

  public MailboxFactory() {
    this(null, null);
  }

  public MailboxFactory(final ExecutorService executorService, final MessageQueueFactory messageQueueFactory) {
    this.executorService = (executorService == null) ? Executors.newCachedThreadPool() : executorService;
    this.messageQueueFactory = (messageQueueFactory == null) ? MessageQueueFactoryImpl.INTANCE : messageQueueFactory;
  }

  public Mailbox createMailbox() {
    return new MailboxImpl(this, messageQueueFactory.createMessageQueue());
  }

  public Mailbox createMailbox(final MessageQueue messageQueue) {
    return new MailboxImpl(this, messageQueue);
  }

  public void submit(final Runnable task) throws Exception {
    try {
      executorService.submit(task);
    } catch (final Exception e) {
      if (!isShuttingDown()) {
        throw e;
      } else {
        LOG.warn("Unable to process the request, possible mailbox shutdown had been called in the application");
      }
    } catch (final Error e) {
      if (!isShuttingDown()) {
        throw e;
      }
    }
  }

  public void addAutoClosable(final AutoCloseable closeable) {
    if (!isShuttingDown()) {
      closables.add(closeable);
    } else {
      throw new IllegalStateException("Shuting down ...");
    }
  }

  public void shutdown() {
    if (shuttingDown.compareAndSet(false, true)) {
      executorService.shutdownNow();
      final Iterator<AutoCloseable> it = closables.iterator();
      while (it.hasNext()) {
        try {
          it.next().close();
        } catch (final Throwable t) {
          t.printStackTrace();
        }
      }
    }
  }

  public boolean isShuttingDown() {
    return shuttingDown.get();
  }
}