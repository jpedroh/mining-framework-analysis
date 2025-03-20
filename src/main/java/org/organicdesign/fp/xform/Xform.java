package org.organicdesign.fp.xform;
import org.organicdesign.fp.collections.UnmodIterable;
import org.organicdesign.fp.collections.UnmodIterator;
import org.organicdesign.fp.function.Fn1;
import org.organicdesign.fp.function.Fn2;
import org.organicdesign.fp.oneOf.Or;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 An immutable description of operations to be performed (a transformation, transform, or x-form).
 When fold() (or another terminating function) is called, the Xform definition is "compiled" into
 a one-time mutable transformation which is then carried out.  This allows certain performance
 shortcuts (such as doing a drop with index addition instead of iteration) and also hides the
 mutability otherwise inherent in a transformation.

 Xform is an abstract class.  Most of the methods on Xform produce immutable descriptions of actions
 to take at a later time.  These are represented by ___Desc classes.  When fold() is called
 (or any of the helper methods that wrap it), that produces a result by first stringing together
 a bunch of Operations (____Op classes) and then "running" them.  This is analogous to compiling
 a program and running it.  The ____Desc classes are like the immutable source, the ____Op classes
 like the op-codes it's compiled into.

 Special thanks to Nathan Williams for pointing me toward separating the mutation from the
 description of a transformation.  Also to Paul Phillips (@extempore2) whose lectures provided
 an outline for what was ideal and also what was important.  All errors are my own.
 -Glen 2015-08-30
 */
public abstract class Xform<A extends java.lang.Object> implements UnmodIterable<A> {
  enum OpStrategy {
    HANDLE_INTERNALLY,
    ASK_SUPPLIER,
    CANNOT_HANDLE
  }

  private static final Object TERMINATE = new Object();

  @SuppressWarnings(value = { "unchecked" }) private A terminate() {
    return (A) TERMINATE;
  }

  static abstract class Operation {
    Fn1<Object, Boolean> filter = null;

    Fn1 map = null;

    Fn1<Object, Iterable> flatMap = null;

    /**
         Drops as many items as the source can handle.
         @param num the number of items to drop
         @return  whether the source can handle the take, or pass-through (ask-supplier), or can't
         do either.
         */
    public Or<Long, OpStrategy> drop(long num) {
      return (num < 1) ? Or.good(0L) : Or.bad(OpStrategy.CANNOT_HANDLE);
    }

    /**
         Takes as many items as the source can handle.
         @param num the number of items to take.
         @return whether the source can handle the take, or pass-through (ask-supplier), or can't
         do either.
         */
    public OpStrategy take(long num) {
      return OpStrategy.CANNOT_HANDLE;
    }

    private static class DropOp extends Operation {
      private long leftToDrop;

      DropOp(long drop) {
        leftToDrop = drop;
        filter = (o) -> {
          if (leftToDrop > 0) {
            leftToDrop = leftToDrop - 1;
            return Boolean.FALSE;
          }
          return Boolean.TRUE;
        };
      }

      @Override public Or<Long, OpStrategy> drop(long num) {
        leftToDrop = leftToDrop + num;
        return Or.good(num);
      }
    }

    private static class FilterOp extends Operation {
      FilterOp(Fn1<Object, Boolean> func) {
        filter = func;
      }
    }

    private static class MapOp extends Operation {
      MapOp(Fn1 func) {
        map = func;
      }

      @Override public Or<Long, OpStrategy> drop(long num) {
        return Or.bad(OpStrategy.ASK_SUPPLIER);
      }

      @Override public OpStrategy take(long num) {
        return OpStrategy.ASK_SUPPLIER;
      }
    }

    private static class FlatMapOp extends Operation {
      FlatMapOp(Fn1<Object, Iterable> func) {
        flatMap = func;
      }
    }

    private static class TakeOp extends Operation {
      private long numToTake;

      TakeOp(long take) {
        numToTake = take;
        map = (a) -> {
          if (numToTake > 0) {
            numToTake = numToTake - 1;
            return a;
          }
          return TERMINATE;
        };
      }

      @Override public OpStrategy take(long num) {
        if (num < numToTake) {
          numToTake = num;
        }
        return OpStrategy.HANDLE_INTERNALLY;
      }
    }
  }

  protected static class RunList implements Iterable {
    Iterable source;

    List<Operation> list = new ArrayList<>();

    RunList prev = null;

    private RunList(RunList prv, Iterable src) {
      prev = prv;
      source = src;
    }

    public static RunList of(RunList prv, Iterable src) {
      return new RunList(prv, src);
    }

    Operation[] opArray() {
      return list.toArray(new Operation[list.size()]);
    }

    @Override public Iterator iterator() {
      return source.iterator();
    }
  }

  private static class AppendOp extends RunList {
    private AppendOp(RunList prv, Iterable src) {
      super(prv, src);
    }

    @Override public Iterator iterator() {
      @SuppressWarnings(value = { "Convert2Lambda" }) ArrayList prevSrc = _fold(prev, prev.opArray(), 0, new ArrayList(), new Fn2<ArrayList, Object, ArrayList>() {
        @SuppressWarnings(value = { "unchecked" }) @Override public ArrayList applyEx(ArrayList res, Object item) throws Exception {
          res.add(item);
          return res;
        }
      });
      return new Iterator() {
        Iterator innerIter = prevSrc.iterator();

        boolean usingPrevSrc = true;

        /** {@inheritDoc} */
        @Override public boolean hasNext() {
          if (innerIter.hasNext()) {
            return true;
          } else {
            if (usingPrevSrc) {
              usingPrevSrc = false;
              innerIter = source.iterator();
            }
          }
          return innerIter.hasNext();
        }

        @Override public Object next() {
          return innerIter.next();
        }
      };
    }
  }

  private static class AppendIterDesc<T extends java.lang.Object> extends Xform<T> {
    final Xform<T> src;

    AppendIterDesc(Xform<T> prev, Xform<T> s) {
      super(prev);
      src = s;
    }

    @SuppressWarnings(value = { "unchecked" }) @Override protected RunList toRunList() {
      return new AppendOp(prevOp.toRunList(), src);
    }
  }

  private static class DropDesc<T extends java.lang.Object> extends Xform<T> {
    private final long dropAmt;

    DropDesc(Xform<T> prev, long d) {
      super(prev);
      dropAmt = d;
    }

    @SuppressWarnings(value = { "unchecked" }) @Override protected RunList toRunList() {
      RunList ret = prevOp.toRunList();
      int i = ret.list.size() - 1;
      Or<Long, OpStrategy> earlierDs;
      for ( ; i >= 0; i--) {
        Operation op = ret.list.get(i);
        earlierDs = op.drop(dropAmt);
        if (earlierDs.isBad() && (earlierDs.bad() == OpStrategy.CANNOT_HANDLE)) {
          break;
        } else {
          if (earlierDs.isGood()) {
            return ret;
          }
        }
      }
      ret.list.add(new Operation.DropOp(dropAmt));
      return ret;
    }
  }

  private static class DropWhileDesc<T extends java.lang.Object> extends Xform<T> {
    final Fn1<? super T, Boolean> f;

    DropWhileDesc(Xform<T> prev, Fn1<? super T, Boolean> func) {
      super(prev);
      f = func;
    }

    @SuppressWarnings(value = { "unchecked" }) @Override protected RunList toRunList() {
      RunList ret = prevOp.toRunList();
      ret.list.add(new Operation.FilterOp(new Fn1<Object, Boolean>() {
        private boolean active = true;

        @Override public Boolean applyEx(Object o) throws Exception {
          if (!active) {
            return true;
          }
          boolean ret = !((Fn1<Object, Boolean>) f).apply(o);
          if (ret) {
            active = false;
          }
          return ret;
        }
      }));
      return ret;
    }
  }

  private static class FilterDesc<T extends java.lang.Object> extends Xform<T> {
    final Fn1<? super T, Boolean> f;

    FilterDesc(Xform<T> prev, Fn1<? super T, Boolean> func) {
      super(prev);
      f = func;
    }

    @SuppressWarnings(value = { "unchecked" }) @Override protected RunList toRunList() {
      RunList ret = prevOp.toRunList();
      ret.list.add(new Operation.FilterOp((Fn1<Object, Boolean>) f));
      return ret;
    }
  }

  private static class MapDesc<T extends java.lang.Object, U extends java.lang.Object> extends Xform<U> {
    final Fn1<? super T, ? extends U> f;

    MapDesc(Xform<T> prev, Fn1<? super T, ? extends U> func) {
      super(prev);
      f = func;
    }

    @SuppressWarnings(value = { "unchecked" }) @Override protected RunList toRunList() {
      RunList ret = prevOp.toRunList();
      ret.list.add(new Operation.MapOp(f));
      return ret;
    }
  }

  private static class FlatMapDesc<T extends java.lang.Object, U extends java.lang.Object> extends Xform<U> {
    final Fn1<? super T, Iterable<U>> f;

    FlatMapDesc(Xform<T> prev, Fn1<? super T, Iterable<U>> func) {
      super(prev);
      f = func;
    }

    @SuppressWarnings(value = { "unchecked" }) @Override protected RunList toRunList() {
      RunList ret = prevOp.toRunList();
      ret.list.add(new Operation.FlatMapOp((Fn1) f));
      return ret;
    }
  }

  private static class TakeDesc<T extends java.lang.Object> extends Xform<T> {
    private final long take;

    TakeDesc(Xform<T> prev, long t) {
      super(prev);
      take = t;
    }

    @SuppressWarnings(value = { "unchecked" }) @Override protected RunList toRunList() {
      RunList ret = prevOp.toRunList();
      int i = ret.list.size() - 1;
      OpStrategy earlierTs;
      for ( ; i >= 0; i--) {
        Operation op = ret.list.get(i);
        earlierTs = op.take(take);
        if (earlierTs == OpStrategy.CANNOT_HANDLE) {
          break;
        } else {
          if (earlierTs == OpStrategy.HANDLE_INTERNALLY) {
            return ret;
          }
        }
      }
      ret.list.add(new Operation.TakeOp(take));
      return ret;
    }
  }

  static class SourceProviderIterableDesc<T extends java.lang.Object> extends Xform<T> {
    private final Iterable<? extends T> list;

    SourceProviderIterableDesc(Iterable<? extends T> l) {
      super(null);
      list = l;
    }

    @Override protected RunList toRunList() {
      return RunList.of(null, list);
    }

    @Override public int hashCode() {
      return UnmodIterable.hash(this);
    }

    @Override public boolean equals(Object other) {
      if (this == other) {
        return true;
      }
      if (!(other instanceof SourceProviderIterableDesc)) {
        return false;
      }
      return Objects.equals(this.list, ((SourceProviderIterableDesc) other).list);
    }
  }

  public static final Xform EMPTY = new SourceProviderIterableDesc<>(Collections.emptyList());

  @SuppressWarnings(value = { "unchecked" }) public static <T extends java.lang.Object> Xform<T> empty() {
    return (Xform<T>) EMPTY;
  }

  /** Static factory methods */
  public static <T extends java.lang.Object> Xform<T> of(Iterable<? extends T> list) {
    if (list == null) {
      return empty();
    }
    return new SourceProviderIterableDesc<>(list);
  }

  /** This is the previous operation or source. */
  final Xform prevOp;

  Xform(Xform pre) {
    prevOp = pre;
  }

  /**
     @param reducer combines each value in the list with the result so far.  The result so far is the first argument.
     the current value to combine with it is the second argument.  The return type is the same as the result so far.
     Fn2&lt;? super U,? super T,U&gt;
     */
  @SuppressWarnings(value = { "unchecked" }) private static <H extends java.lang.Object> H _fold(Iterable source, Operation[] ops, int opIdx, H ident, Fn2 reducer) {
    Object ret = ident;
    sourceLoop:
    for (Object o : source) {
      for (int j = opIdx; j < ops.length; j++) {
        Operation op = ops[j];
        if ((op.filter != null) && !op.filter.apply(o)) {
          continue sourceLoop;
        }
        if (op.map != null) {
          o = op.map.apply(o);
          if (o == TERMINATE) {
            return (H) ret;
          }
        } else {
          if (op.flatMap != null) {
            ret = _fold(op.flatMap.apply(o), ops, j + 1, (H) ret, reducer);
            continue sourceLoop;
          }
        }
      }
      ret = reducer.apply(ret, o);
    }
    return (H) ret;
  }

  @Override public UnmodIterator<A> iterator() {
    return toMutList().iterator();
  }

  @Override public Xform<A> concat(Iterable<? extends A> list) {
    if (list == null) {
      throw new IllegalArgumentException("Can\'t concat a null iterable");
    }
    return new AppendIterDesc<>(this, new SourceProviderIterableDesc<>(list));
  }

  @Override public Xform<A> precat(Iterable<? extends A> list) {
    if (list == null) {
      throw new IllegalArgumentException("Can\'t precat a null iterable");
    }
    return new AppendIterDesc<>(of(list), this);
  }

  /** The number of items to drop from the beginning of the output. */
  @Override public Xform<A> drop(long n) {
    if (n < 0) {
      throw new IllegalArgumentException("Can\'t drop less than zero items.");
    }
    return new DropDesc<>(this, n);
  }

  /** The number of items to drop from the beginning of the output. */
  @Override public Xform<A> dropWhile(Fn1<? super A, Boolean> predicate) {
    if (predicate == null) {
      throw new IllegalArgumentException("Can\'t dropWhile without a function.");
    }
    return new DropWhileDesc<>(this, predicate);
  }

  /** Provides a way to collect the results of the transformation. */
  @Override public <B extends java.lang.Object> B fold(B ident, Fn2<? super B, ? super A, B> reducer) {
    if (reducer == null) {
      throw new IllegalArgumentException("Can\'t fold with a null reduction function.");
    }
    RunList runList = toRunList();
    return _fold(runList, runList.opArray(), 0, ident, reducer);
  }

  /**
     Thit implementation should be correct, but could be slow in the case where previous operations
     are slow and the terminateWhen operation is fast and terminates early.  It actually renders
     items to a mutable List, then runs through the list performing the requested reduction,
     checking for early termination on the result.  If you can to a takeWhile() or take() earlier
     in the transform chain instead of doing it here, always do that.  If you really need early
     termination based on the *result* of a fold, and the operations are expensive or the input
     is huge, try using a View instead.  If you don't care about those things, then this method is
     perfect for you.

     {@inheritDoc}
     */
  @Override public <G extends java.lang.Object, B extends java.lang.Object> Or<G, B> foldUntil(G accum, Fn2<? super G, ? super A, B> terminator, Fn2<? super G, ? super A, G> reducer) {
    if (terminator == null) {
      return Or.good(fold(accum, reducer));
    }
    if (reducer == null) {
      throw new IllegalArgumentException("Can\'t fold with a null reduction function.");
    }
    List<A> as = this.toMutList();
    for (A a : as) {
      B term = terminator.apply(accum, a);
      if (term != null) {
        return Or.bad(term);
      }
      accum = reducer.apply(accum, a);
    }
    return Or.good(accum);
  }

  @Override public Xform<A> filter(Fn1<? super A, Boolean> f) {
    if (f == null) {
      throw new IllegalArgumentException("Can\'t filter with a null function.");
    }
    return new FilterDesc<>(this, f);
  }

  @Override public <B extends java.lang.Object> Xform<B> flatMap(Fn1<? super A, Iterable<B>> f) {
    if (f == null) {
      throw new IllegalArgumentException("Can\'t flatmap with a null function.");
    }
    return new FlatMapDesc<>(this, f);
  }

  @Override public <B extends java.lang.Object> Xform<B> map(Fn1<? super A, ? extends B> f) {
    if (f == null) {
      throw new IllegalArgumentException("Can\'t map with a null function.");
    }
    return new MapDesc<>(this, f);
  }

  protected abstract RunList toRunList();

  @Override public Xform<A> take(long numItems) {
    if (numItems < 0) {
      throw new IllegalArgumentException("Num items must be >= 0");
    }
    return new TakeDesc<>(this, numItems);
  }

  @Override public Xform<A> takeWhile(Fn1<? super A, Boolean> f) {
    if (f == null) {
      throw new IllegalArgumentException("Can\'t takeWhile with a null function.");
    }
    return new MapDesc<>(this, (a) -> f.apply(a) ? a : terminate());
  }
}