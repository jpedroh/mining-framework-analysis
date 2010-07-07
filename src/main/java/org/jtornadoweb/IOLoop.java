package org.jtornadoweb;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class IOLoop {

	private class EventHandlerTask implements Runnable {

		private final EventHandler handler;
		private final SelectionKey key;

		public EventHandlerTask(EventHandler ev, SelectionKey key) {
			this.handler = ev;
			this.key = key;
		}

		@Override
		public void run() {
			try {
				handler.handleEvents(key);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// key.cancel();
			}
		}

	}

	private HashSet<SelectionKey> handlers;

	// private Map<SelectionKey, EventHandler> handlers;

	public static interface EventHandler {
		public void handleEvents(SelectionKey key) throws Exception;
	}

	private final Selector selector;
	private final ExecutorService pool;

	public IOLoop(ExecutorService pool) throws Exception {
		this.pool = pool;
		this.selector = Selector.open();
		// this.handlers = new HashMap<SelectionKey, EventHandler>();
		this.handlers = new HashSet<SelectionKey>();

	}

	public void start() throws Exception {

		while (true) {

			selector.select(1);

			for (SelectionKey key : selector.selectedKeys()) {

				selector.selectedKeys().remove(key);

				if (!key.isAcceptable())
					this.removeHandler(key);
				// ((EventHandler) key.attachment()).handleEvents(key);

				EventHandlerTask task = new EventHandlerTask(
						(EventHandler) key.attachment(), key);
				pool.execute(task);

			}
		}
	}

	public void removeHandler(SelectionKey key) {
		key.cancel();
		this.handlers.remove(key);
	}

	public void addHandler(AbstractSelectableChannel channel,
			EventHandler eventHandler, int opts) throws Exception {
		channel.configureBlocking(false);
		channel.register(selector, opts, eventHandler);
		// handlers.put(channel.keyFor(selector), eventHandler);
		handlers.add(channel.keyFor(selector));
	}
}
