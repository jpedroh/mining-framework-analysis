package org.jeromq;
import java.io.IOException;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jeromq.ZMQ.PollItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
    The ZLoop class provides an event-driven reactor pattern. The reactor
    handles zmq.PollItem items (pollers or writers, sockets or fds), and
    once-off or repeated timers. Its resolution is 1 msec. It uses a tickless
    timer to reduce CPU interrupts in inactive processes.
 */
public class ZLoop {
  private static Logger LOG = LoggerFactory.getLogger(ZLoop.class);

  private static ThreadLocal<Boolean> initialized = new ThreadLocal<Boolean>();

  private static ZLoop instance = null;

  public static interface IZLoopHandler {
    public int handle(ZLoop loop, PollItem item, Object arg);
  }

  private class SPoller {
    PollItem item;

    IZLoopHandler handler;

    Object arg;

    int errors;

    protected SPoller(PollItem item, IZLoopHandler handler, Object arg) {
      this.item = item;
      this.handler = handler;
      this.arg = arg;
      errors = 0;
    }
  }



  private class STimer {
    int delay;

    int times;

    IZLoopHandler handler;

    Object arg;

    long when;

    public STimer(int delay, int times, IZLoopHandler handler, Object arg) {
      this.delay = delay;
      this.times = times;
      this.handler = handler;
      this.arg = arg;
      this.when = -1;
    }
  }

  private final List<SPoller> pollers;

  private final List<STimer> timers;

  private int poll_size;

  private zmq.PollItem[] pollset;

  private SPoller[] pollact;

  private boolean dirty;

  private boolean verbose;

  private final List<Object> zombies;

  private final List<STimer> newTimers;

  private ZLoop() {
    pollers = new ArrayList<SPoller>();
    timers = new ArrayList<STimer>();
    zombies = new ArrayList<Object>();
    newTimers = new ArrayList<STimer>();
  }

  public static ZLoop instance() {
    if (initialized.get() == null) {
      synchronized (initialized) {
        if (instance == null) {
          instance = new ZLoop();
        }
        initialized.set(Boolean.TRUE);
      }
    }
    return instance;
  }

  public void destory() {
  }

  private void rebuild() {
    pollset = null;
    pollact = null;
    poll_size = pollers.size();
    pollset = new zmq.PollItem[poll_size];
    pollact = new SPoller[poll_size];
    int item_nbr = 0;
    for (SPoller poller : pollers) {
      pollset[item_nbr] = poller.item.base();
      pollact[item_nbr] = poller;
      item_nbr++;
    }
    dirty = false;
  }

  private long ticklessTimer() {
    long tickless = System.currentTimeMillis() + 1000 * 3600;
    for (STimer timer : timers) {
      if (timer.when == -1) {
        timer.when = timer.delay + System.currentTimeMillis();
      }
      if (tickless > timer.when) {
        tickless = timer.when;
      }
    }
    long timeout = tickless - System.currentTimeMillis();
    if (timeout < 0) {
      timeout = 0;
    }
    if (verbose) {
      LOG.info("I: zloop: polling for {} msec", timeout);
    }
    return timeout;
  }

  public int poller(PollItem item_, IZLoopHandler handler, Object arg) {
    zmq.PollItem item = item_.base();
    if (item.getChannel() == null) {
      return -1;
    }
    SPoller poller = new SPoller(item_, handler, arg);
    pollers.add(poller);
    dirty = true;
    if (verbose) {
      LOG.info("I: zloop: register {} poller ({}, {})", new Object[] { item.getSocket() != null ? item.getSocket().typeString() : "FD", item.getSocket(), item.getChannel() });
    }
    return 0;
  }

  public void pollerEnd(PollItem item_) {
    zmq.PollItem item = item_.base();
    assert (item.getChannel() != null);
    Iterator<SPoller> it = pollers.iterator();
    while (it.hasNext()) {
      SPoller p = it.next();
      if (item.getChannel() == p.item.getChannel()) {
        it.remove();
        dirty = true;
      }
    }
    if (verbose) {
      LOG.info("I: zloop: cancel {} poller ({}, {})", new Object[] { item.getSocket() != null ? item.getSocket().typeString() : "FD", item.getSocket(), item.getChannel() });
    }
  }

  public int timer(int delay, int times, IZLoopHandler handler, Object arg) {
    STimer timer = new STimer(delay, times, handler, arg);
    newTimers.add(timer);
    if (verbose) {
      LOG.info("I: zloop: register timer delay={} times={}", delay, times);
    }
    return 0;
  }

  public int timerEnd(Object arg) {
    assert (arg != null);
    zombies.add(arg);
    if (verbose) {
      LOG.info("I: zloop: cancel timer");
    }
    return 0;
  }

  public void verbose(boolean verbose) {
    this.verbose = verbose;
  }

  public int start() {
    int rc = 0;
    timers.addAll(newTimers);
    newTimers.clear();
    for (STimer timer : timers) {
      timer.when = timer.delay + System.currentTimeMillis();
    }
    Selector selector;
    try {
      selector = Selector.open();
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
      return -1;
    }
    while (!Thread.currentThread().isInterrupted()) {
      if (dirty) {
        rebuild();
      }
      long wait = ticklessTimer();
      rc = zmq.ZMQ.zmq_poll(selector, pollset, wait);
      if (rc == -1) {
        if (verbose) {
          LOG.info("I: zloop: interrupted ({}) - {}", rc, zmq.ZError.errno());
        }
        rc = 0;
        break;
      }
      Iterator<STimer> it = timers.iterator();
      while (it.hasNext()) {
        STimer timer = it.next();
        if (System.currentTimeMillis() >= timer.when && timer.when != -1) {
          if (verbose) {
            LOG.info("I: zloop: call timer handler");
          }
          rc = timer.handler.handle(this, null, timer.arg);
          if (rc == -1) {
            break;
          }
          if (timer.times != 0 && --timer.times == 0) {
            it.remove();
          } else {
            timer.when = timer.delay + System.currentTimeMillis();
          }
        }
      }
      if (rc == -1) {
        break;
      }
      for (int item_nbr = 0; item_nbr < poll_size; item_nbr++) {
        SPoller poller = pollact[item_nbr];
        assert (pollset[item_nbr].getSocket() == poller.item.getSocket());
        if (pollset[item_nbr].isError()) {
          if (verbose) {
            LOG.info("I: zloop: can\'t poll {} socket ({}, {}): {}", new Object[] { poller.item.getSocket() != null ? poller.item.getSocket().typeString() : "FD", poller.item.getSocket(), poller.item.getChannel(), zmq.ZError.errno() });
          }
          if (poller.errors++ > 0) {
            pollerEnd(poller.item);
          }
        } else {
          poller.errors = 0;
        }
        if (pollset[item_nbr].readyOps() > 0) {
          if (verbose) {
            LOG.info("I: zloop: call {} socket handler ({}, {})", new Object[] { poller.item.getSocket() != null ? poller.item.getSocket().typeString() : "FD", poller.item.getSocket(), poller.item.getChannel() });
          }
          rc = poller.handler.handle(this, poller.item, poller.arg);
          if (rc == -1) {
            break;
          }
        }
      }
      for (Object arg : zombies) {
        it = timers.iterator();
        while (it.hasNext()) {
          STimer timer = it.next();
          if (timer.arg == arg) {
            it.remove();
          }
        }
      }
      timers.addAll(newTimers);
      newTimers.clear();
      if (rc == -1) {
        break;
      }
    }
    try {
      selector.close();
    } catch (IOException e) {
    }
    return rc;
  }
}