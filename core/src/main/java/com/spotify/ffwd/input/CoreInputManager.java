package com.spotify.ffwd.input;
import java.util.ArrayList;
import java.util.List;
import lombok.ToString;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.spotify.ffwd.debug.DebugServer;
import com.spotify.ffwd.model.Event;
import com.spotify.ffwd.model.Metric;
import com.spotify.ffwd.output.OutputManager;
import eu.toolchain.async.AsyncFramework;
import eu.toolchain.async.AsyncFuture;

/**
 * Responsible for receiving, logging and transforming the event.
 *
 * @author udoprog
 */
@ToString(of = { "sources" }) public class CoreInputManager implements InputManager {
  private static final String DEBUG_ID = "core.input";

  @Inject private List<PluginSource> sources;

  @Inject private AsyncFramework async;

  @Inject private OutputManager output;

  @Inject private DebugServer debug;

  @Override public void init() {
    for (final PluginSource s : sources) {
      s.init();
    }
  }

  @Override public void receiveEvent(Event event) {
    debug.inspectEvent(DEBUG_ID, event);
    output.sendEvent(event);
  }

  @Override public void receiveMetric(Metric metric) {
    debug.inspectMetric(DEBUG_ID, metric);
    output.sendMetric(metric);
  }

  @Override public AsyncFuture<Void> start() {
    final ArrayList<AsyncFuture<Void>> futures = Lists.newArrayList();
    for (final PluginSource s : sources) {
      futures.add(s.start());
    }
    return async.collectAndDiscard(futures);
  }

  @Override public AsyncFuture<Void> stop() {
    final ArrayList<AsyncFuture<Void>> futures = Lists.newArrayList();
    for (final PluginSource s : sources) {
      futures.add(s.stop());
    }
    return async.collectAndDiscard(futures);
  }
}