package net.openhft.chronicle.threads;
import net.openhft.chronicle.core.annotation.HotMethod;
import net.openhft.chronicle.threads.api.EventHandler;
import net.openhft.chronicle.threads.api.EventLoop;
import net.openhft.chronicle.threads.api.InvalidEventHandlerException;
import org.jetbrains.annotations.NotNull;
import javax.xml.ws.WebServiceException;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by peter.lawrey on 22/01/15.
 */
public class MonitorEventLoop implements EventLoop, Runnable, Closeable {
  final ExecutorService service = Executors.newSingleThreadExecutor(new NamedThreadFactory("event-loop-monitor", true));

  private final EventLoop parent;

  private final List<EventHandler> handlers = new ArrayList<>();

  private final Pauser pauser;

  private volatile boolean running = true;

  public MonitorEventLoop(EventLoop parent, Pauser pauser) {
    this.parent = parent;
    this.pauser = pauser;
  }

  public void start() {
    running = true;
    service.submit(this);
  }

  @Override public void unpause() {
    pauser.unpause();
  }

  public void stop() {
    running = false;
  }

  public void addHandler(@NotNull EventHandler handler) {
    synchronized (handlers) {
      handlers.add(handler);
      handler.eventLoop(parent);
    }
  }

  @Override @HotMethod public void run() {
    try {
      while (running) {
        boolean busy;
        synchronized (handlers) {
          busy = runHandlers();
        }
        if (busy) {
          pauser.reset();
        } else {
          pauser.pause();
        }
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  @HotMethod private boolean runHandlers() {
    boolean busy = false;
    for (int i = 0; i < handlers.size(); i++) {
      EventHandler handler = handlers.get(i);
      if (handler == null) {
        continue;
      }
      try {
        busy |= handler.action();
      } catch (InvalidEventHandlerException e) {
        handlers.remove(i--);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return busy;
  }

  @Override public void close() throws WebServiceException {
    service.shutdown();
    try {
      if (!service.awaitTermination(100, TimeUnit.MILLISECONDS)) {
        service.shutdownNow();
      }
    } catch (InterruptedException e) {
      service.shutdownNow();
    }
  }
}