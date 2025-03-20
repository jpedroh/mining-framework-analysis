package com.jcabi.aspects.aj;
import com.jcabi.aspects.Cacheable;
import com.jcabi.aspects.Loggable;
import com.jcabi.log.Logger;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * Cache method results.
 *
 * <p>It is an AspectJ aspect and you are not supposed to use it directly. It
 * is instantiated by AspectJ runtime framework when your code is annotated
 * with {@link Cacheable} annotation.
 *
 * <p>The class is thread-safe.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.8
 */
@Aspect @SuppressWarnings(value = { "PMD.TooManyMethods" }) public final class MethodCacher {
  /**
     * Calling tunnels.
     * @checkstyle LineLength (2 lines)
     */
  private final transient ConcurrentMap<MethodCacher.Key, MethodCacher.Tunnel> tunnels = new ConcurrentHashMap<MethodCacher.Key, MethodCacher.Tunnel>(0);

  /**
     * Service that cleans cache.
     */
  private final transient BlockingQueue<Key> updatekeys = new LinkedBlockingQueue<MethodCacher.Key>();

  /**
     * Public ctor.
     */
  public MethodCacher() {
    new UpdateMethodCacher(this.tunnels, this.updatekeys).start();
  }

  /**
     * Call the method or fetch from cache.
     *
     * <p>Try NOT to change the signature of this method, in order to keep
     * it backward compatible.
     *
     * @param point Joint point
     * @return The result of call
     * @throws Throwable If something goes wrong inside
     * @checkstyle IllegalThrows (4 lines)
     */
  @Around(value = "execution(* *(..)) && @annotation(com.jcabi.aspects.Cacheable)") public Object cache(final ProceedingJoinPoint point) throws Throwable {
    final MethodCacher.Key key = new MethodCacher.Key(point);
    MethodCacher.Tunnel tunnel;
    final Method method = MethodSignature.class.cast(point.getSignature()).getMethod();
    final Cacheable annot = method.getAnnotation(Cacheable.class);
    synchronized (this.tunnels) {
      for (final Class<?> before : annot.before()) {
        final boolean flag = Boolean.class.cast(before.getMethod("flushBefore").invoke(method.getClass()));
        if (flag) {
          this.preflush(point);
        }
      }
      tunnel = this.tunnels.get(key);
      if (this.isCreateTunnel(tunnel)) {
        tunnel = new MethodCacher.Tunnel(point, key, annot.asyncUpdate());
        this.tunnels.put(key, tunnel);
      }
      if (tunnel.expired() && tunnel.asyncUpdate()) {
        this.updatekeys.offer(key);
      }
      for (final Class<?> after : annot.after()) {
        final boolean flag = Boolean.class.cast(after.getMethod("flushAfter").invoke(method.getClass()));
        if (flag) {
          this.postflush(point);
        }
      }
    }
    return tunnel.through();
  }

  /**
     * Flush cache.
     * @param point Join point
     * @return Value of the method
     * @since 0.7.14
     * @deprecated Since 0.7.17, and preflush() should be used
     * @throws Throwable If something goes wrong inside
     * @checkstyle IllegalThrows (3 lines)
     */
  @Deprecated public Object flush(final ProceedingJoinPoint point) throws Throwable {
    this.preflush(point);
    return point.proceed();
  }

  /**
     * Flush cache.
     *
     * <p>Try NOT to change the signature of this method, in order to keep
     * it backward compatible.
     *
     * @param point Joint point
     * @since 0.7.14
     */
  @Before(value = "execution(* *(..))" + " && (@annotation(com.jcabi.aspects.Cacheable.Flush)" + " || @annotation(com.jcabi.aspects.Cacheable.FlushBefore))") public void preflush(final JoinPoint point) {
    this.flush(point, "before the call");
  }

  /**
     * Flush cache after method execution.
     *
     * <p>Try NOT to change the signature of this method, in order to keep
     * it backward compatible.
     *
     * @param point Joint point
     * @since 0.7.18
     */
  @After(value = "execution(* *(..))" + " && @annotation(com.jcabi.aspects.Cacheable.FlushAfter)") public void postflush(final JoinPoint point) {
    this.flush(point, "after the call");
  }

  /**
     * Flush cache.
     * @param point Joint point
     * @param when When it happens
     * @since 0.7.18
     */
  private void flush(final JoinPoint point, final String when) {
    synchronized (this.tunnels) {
      for (final MethodCacher.Key key : this.tunnels.keySet()) {
        if (!key.sameTarget(point)) {
          continue;
        }
        final MethodCacher.Tunnel removed = this.tunnels.remove(key);
        final Method method = MethodSignature.class.cast(point.getSignature()).getMethod();
        if (LogHelper.enabled(key.getLevel(), method.getDeclaringClass())) {
          LogHelper.log(key.getLevel(), method.getDeclaringClass(), "%s: %s:%s removed from cache %s", Mnemos.toText(method, point.getArgs(), true, false), key, removed, when);
        }
      }
    }
  }

  /**
     * Whether create a new Tunnel.
     * @param tunnel MethodCacher.Tunnel
     * @return Boolean
     */
  private boolean isCreateTunnel(final MethodCacher.Tunnel tunnel) {
    return tunnel == null || (tunnel.expired() && !tunnel.asyncUpdate());
  }

  protected static final class Tunnel {
    /**
         * Proceeding join point.
         */
    private final transient ProceedingJoinPoint point;

    /**
         * Key related to this tunnel.
         */
    private final transient MethodCacher.Key key;

    /**
         * Whether asynchronous update.
         */
    private final transient boolean async;

    /**
         * Was it already executed?
         */
    private transient boolean executed;

    /**
         * When will it expire (moment in time).
         */
    private transient long lifetime;

    /**
         * Cached value.
         */
    private transient Object cached;

    /**
         * Public ctor.
         * @param pnt Joint point
         * @param akey The key related to it
         */
    Tunnel(final ProceedingJoinPoint pnt, final MethodCacher.Key akey, final boolean asy) {
      this.point = pnt;
      this.key = akey;
      this.async = asy;
    }

    @Override public String toString() {
      return Mnemos.toText(this.cached, true, false);
    }

    /**
         * Get a new instance.
         * @return MethodCacher.Tunnel
         */
    public Tunnel copy() {
      return new Tunnel(this.point, this.key, this.async);
    }

    /**
         * Get a result through the tunnel.
         * @return The result
         * @throws Throwable If something goes wrong inside
         * @checkstyle IllegalThrows (5 lines)
         */
    @SuppressWarnings(value = { "PMD.AvoidSynchronizedAtMethodLevel" }) public synchronized Object through() throws Throwable {
      if (!this.executed) {
        final long start = System.currentTimeMillis();
        this.cached = this.point.proceed();
        final Method method = MethodSignature.class.cast(this.point.getSignature()).getMethod();
        final Cacheable annot = method.getAnnotation(Cacheable.class);
        final String suffix;
        if (annot.forever()) {
          this.lifetime = Long.MAX_VALUE;
          suffix = "valid forever";
        } else {
          if (annot.lifetime() == 0) {
            this.lifetime = 0L;
            suffix = "invalid immediately";
          } else {
            final long msec = annot.unit().toMillis((long) annot.lifetime());
            this.lifetime = start + msec;
            suffix = Logger.format("valid for %[ms]s", msec);
          }
        }
        final Class<?> type = method.getDeclaringClass();
        if (LogHelper.enabled(this.key.getLevel(), type)) {
          LogHelper.log(this.key.getLevel(), type, "%s: %s cached in %[ms]s, %s", Mnemos.toText(method, this.point.getArgs(), true, false), Mnemos.toText(this.cached, true, false), System.currentTimeMillis() - start, suffix);
        }
        this.executed = true;
      }
      return this.key.through(this.cached);
    }

    /**
         * Is it expired already?
         * @return TRUE if expired
         */
    public boolean expired() {
      return this.executed && this.lifetime < System.currentTimeMillis();
    }

    /**
         * Whether asynchronous update.
         * @return TRUE if asynchronous update
         */
    public boolean asyncUpdate() {
      return this.async;
    }
  }

  protected static final class Key {
    /**
         * When instantiated.
         */
    private final transient long start = System.currentTimeMillis();

    /**
         * How many times the key was already accessed.
         */
    private final transient AtomicInteger accessed = new AtomicInteger();

    /**
         * Method.
         */
    private final transient Method method;

    /**
         * Object callable (or class, if static method).
         */
    private final transient Object target;

    /**
         * Arguments.
         */
    private final transient Object[] arguments;

    /**
         * Log level.
         */
    private final int level;

    /**
         * Public ctor.
         * @param point Joint point
         */
    Key(final JoinPoint point) {
      this.method = MethodSignature.class.cast(point.getSignature()).getMethod();
      this.target = MethodCacher.Key.targetize(point);
      this.arguments = point.getArgs();
      if (this.method.isAnnotationPresent(Loggable.class)) {
        this.level = this.method.getAnnotation(Loggable.class).value();
      } else {
        this.level = Loggable.DEBUG;
      }
    }

    @Override public String toString() {
      return Mnemos.toText(this.method, this.arguments, true, false);
    }

    /**
         * Get log level.
         * @return Log level of current method.
         */
    public int getLevel() {
      return this.level;
    }

    @Override public int hashCode() {
      return this.method.hashCode();
    }

    @Override public boolean equals(final Object obj) {
      final boolean equals;
      if (this == obj) {
        equals = true;
      } else {
        if (obj instanceof MethodCacher.Key) {
          final MethodCacher.Key key = MethodCacher.Key.class.cast(obj);
          equals = key.method.equals(this.method) && this.target.equals(key.target) && Arrays.deepEquals(key.arguments, this.arguments);
        } else {
          equals = false;
        }
      }
      return equals;
    }

    /**
         * Send a result through, with necessary logging.
         * @param result The result to send through
         * @return The same result/object
         */
    public Object through(final Object result) {
      final int hit = this.accessed.getAndIncrement();
      final Class<?> type = this.method.getDeclaringClass();
      if (hit > 0 && LogHelper.enabled(this.level, type)) {
        LogHelper.log(this.level, type, "%s: %s from cache (hit #%d, %[ms]s old)", this, Mnemos.toText(result, true, false), hit, System.currentTimeMillis() - this.start);
      }
      return result;
    }

    /**
         * Is it related to the same target?
         * @param point Proceeding point
         * @return True if the target is the same
         */
    public boolean sameTarget(final JoinPoint point) {
      return MethodCacher.Key.targetize(point).equals(this.target);
    }

    /**
         * Calculate its target.
         * @param point Proceeding point
         * @return The target
         */
    private static Object targetize(final JoinPoint point) {
      final Object tgt;
      final Method method = MethodSignature.class.cast(point.getSignature()).getMethod();
      if (Modifier.isStatic(method.getModifiers())) {
        tgt = method.getDeclaringClass();
      } else {
        tgt = point.getTarget();
      }
      return tgt;
    }
  }
}