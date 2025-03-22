package org.jboss.msc.service;
import org.jboss.msc.value.ImmediateValue;
import org.jboss.msc.value.Value;

/**
 * A service is a thing which can be started and stopped.  A service may be started or stopped from any thread.  In
 * general, injections will always happen from the same thread that will call {@code start()}, and uninjections will
 * always happen from the same thread that had called {@code stop()}.  However no other guarantees are made with respect
 * to locking or thread safety; a robust service implementation should always take care to protect any mutable state
 * appropriately.
 * <p>
 * The value type specified by this service is used by default by consumers of this service, and should represent the
 * public interface of this service, which may or may not be the same as the implementing type of this service.
 * <p>
 * When writing MSC service implementations, your {@link #start(StartContext)} and {@link #stop(StopContext)}
 * methods must never block.  This means these methods must not:
 * <ul>
 * <li>Use network connections</li>
 * <li>Wait for network connections</li>
 * <li>Sleep</li>
 * <li>Wait on a condition</li>
 * <li>Wait on a count down latch</li>
 * <li>Call any method which may do any of the above</li>
 * <li>Wait for termination of a thread pool or other service</li>
 * <li>Wait for another service to change state</li>
 * </ul>
 *
 * If your service start/stop does any of these things, you must use the asynchronous start/stop mechanism
 * ({@link LifecycleContext#asynchronous()}) and do one of the following:
 *
 * <ul>
 * <li>Initiate your task in start()/stop(), and utilize a callback (NIO, ThreadPoolExecutor.terminated(), etc.) to call
 * {@link LifecycleContext#complete()} when your start/stop completes instead of blocking</li>
 * <li>Delegate your blocking task to a thread pool ({@code Executor}) which calls {@link LifecycleContext#complete()}
 * when done</li>
 * <li>Use proper dependencies instead of explicitly waiting for services in your start/stop</li>
 * </ul>
 * <p>
 * Note that using {@link LifecycleContext#execute(Runnable)} to execute the blocking task is also not permissible.
 *
 * @param <T> the type of value that this service provides; may be {@link Void}
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 * @author <a href="mailto:ropalka@redhat.com">Richard Opalka</a>
 * @deprecated use {@link org.jboss.msc.Service} instead.
 * This class will be removed in a future release.
 */
@Deprecated public interface Service<T extends java.lang.Object> extends org.jboss.msc.Service, Value<T> {
  /**
     * A simple null service which performs no start or stop action.
     */
  Service<Void> NULL = NullService.INSTANCE;

  /**
     * A value which resolves to the {@link #NULL null service}.
     */
  Value<Service<Void>> NULL_VALUE = new ImmediateValue<>(NULL);
}