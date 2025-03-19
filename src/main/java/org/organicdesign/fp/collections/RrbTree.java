package org.organicdesign.fp.collections;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import org.organicdesign.fp.indent.Indented;
import org.organicdesign.fp.tuple.Tuple2;
import org.organicdesign.fp.tuple.Tuple4;
import static org.organicdesign.fp.collections.Cowry.*;
import static org.organicdesign.fp.indent.IndentUtils.arrayString;
import static org.organicdesign.fp.indent.IndentUtils.indentSpace;

/**
 <p>An RRB Tree is an immutable List (like Clojure's PersistentVector) that also supports random inserts, deletes,
 and can be split and joined back together in logarithmic time.
 This is based on the paper, "RRB-Trees: Efficient Immutable Vectors" by Phil Bagwell and
 Tiark Rompf, with the following differences:</p>

 <ul>
 <li>The Relaxed nodes can be sized between n/3 and 2n/3 (Bagwell/Rompf specify n and n-1)</li>
 <li>The Join operation sticks the shorter tree unaltered into the larger tree (except for very
 small trees which just get concatenated).</li>
 </ul>

 <p>Details were filled in from the Cormen, Leiserson, Rivest & Stein Algorithms book entry
 on B-Trees.  Also with an awareness of the Clojure PersistentVector by Rich Hickey.  All errors
 are by Glen Peterson.</p>

 <h4>History (what little I know):</h4>
 1972: B-Tree: Rudolf Bayer and Ed McCreight<br>
 1998: Purely Functional Data Structures: Chris Okasaki<br>
 2007: Clojure's Persistent Vector (and HashMap) implementations: Rich Hickey<br>
 2012: RRB-Tree: Phil Bagwell and Tiark Rompf<br>

 <p>Compared to other collections (timings summary from 2017-06-11):</p>

 <ul>
 <li>append() - {@link ImRrbt} varies between 90% and 100% of the speed of {@link PersistentVector} (biggest difference above 100K).
 {@link MutRrbt} varies between 45% and 80% of the speed of
 {@link PersistentVector.MutVector} (biggest difference from 100 to 1M).</li>
 <li>get() - varies between 50% and 150% of the speed of PersistentVector (PV wins above 1K) if you build RRB using append().
 If you build rrb using random inserts (worst case), it goes from 90% at 10 items down to 15% of the speed of the PV at 1M items.</li>
 <li>iterate() - is about the same speed as PersistentVector</li>
 <li>insert(0, item) - beats ArrayList above 1K items (worse than ArrayList below 100 items).</li>
 <li>insert(random, item) - beats ArrayList above 10K items (worse than ArrayList until then).</li>
 <li>O(log n) split(), join(), and remove() (not timed yet).</li>
 </ul>

 <p>Latest detailed timing results are
 <a target="_blank" href="https://docs.google.com/spreadsheets/d/1D0bjfsHpmK7aJzyE2WwArlioI6w69YhZ2f-x0yM6_Z0/edit?usp=sharing">here</a>.</p>
 */
@SuppressWarnings(value = { "WeakerAccess" }) public abstract class RrbTree<E extends java.lang.Object> implements BaseList<E>, Indented {

<<<<<<< /usr/src/app/output/glenkpeterson/j-sicle/bf4de20006b5405bf4c84c50a037ef0b28a060ab/src/main/java/org/organicdesign/fp/collections/RrbTree.java/left.java
  public static class MutRrbt<E extends java.lang.Object> extends RrbTree<E> implements MutList<E> {
    private E[] focus;

    private int focusStartIndex;

    private int focusLength;

    private Node<E> root;

    private int size;

    MutRrbt(E[] f, int fi, int fl, Node<E> r, int s) {
      focus = f;
      focusStartIndex = fi;
      focusLength = fl;
      root = r;
      size = s;
    }

    /** {@inheritDoc} */
    @SuppressWarnings(value = { "unchecked" }) @Override public MutRrbt<E> append(E val) {
      if ((focusLength >= STRICT_NODE_LENGTH) || ((focusLength > 0) && (focusStartIndex < (size - focusLength)))) {
        root = root.pushFocus(focusStartIndex, arrayCopy(focus, focusLength, null));
        focus = (E[]) new Object[STRICT_NODE_LENGTH];
        focus[0] = val;
        focusStartIndex = size;
        focusLength = 1;
        size++;
        return this;
      }
      if (focus.length <= focusLength) {
        focus = arrayCopy(focus, STRICT_NODE_LENGTH, null);
      }
      focus[focusLength] = val;
      focusLength++;
      size++;
      return this;
    }

    /** {@inheritDoc} */
    @Override public MutRrbt<E> concat(Iterable<? extends E> es) {
      return (MutRrbt<E>) MutList.super.concat(es);
    }

    void debugValidate() {
      if (focusLength > STRICT_NODE_LENGTH) {
        throw new IllegalStateException("focus len:" + focusLength + " gt STRICT_NODE_LENGTH:" + STRICT_NODE_LENGTH + "\n" + this.indentedStr(0));
      }
      int sz = root.debugValidate();
      if (sz != size - focusLength) {
        throw new IllegalStateException("Size incorrect.  Root size: " + root.size() + " RrbSize: " + size + " focusLen: " + focusLength + "\n" + this.indentedStr(0));
      }
      if ((focusStartIndex < 0) || (focusStartIndex > size)) {
        throw new IllegalStateException("focusStartIndex out of bounds!\n" + this.indentedStr(0));
      }
      if (!root.equals(eliminateUnnecessaryAncestors(root))) {
        throw new IllegalStateException("Unnecessary ancestors!\n" + this.indentedStr(0));
      }
    }

    /** {@inheritDoc} */
    @Override public E get(int i) {
      if ((i < 0) || (i > size)) {
        throw new IndexOutOfBoundsException("Index: " + i + " size: " + size);
      }
      if (i >= focusStartIndex) {
        int focusOffset = i - focusStartIndex;
        if (focusOffset < focusLength) {
          return focus[focusOffset];
        }
        i -= focusLength;
      }
      return root.get(i);
    }

    /** {@inheritDoc} */
    @Override public ImRrbt<E> immutable() {
      return new ImRrbt<>(arrayCopy(focus, focusLength, null), focusStartIndex, root, size);
    }

    /** {@inheritDoc} */
    @Override public String indentedStr(int indent) {
      return "RrbTree(size=" + size + " fsi=" + focusStartIndex + " focus=" + arrayString(focus) + "\n" + indentSpace(indent + 8) + "root=" + (root == null ? "null" : root.indentedStr(indent + 13)) + ")";
    }

    /** {@inheritDoc} */
    @Override public MutRrbt<E> insert(int idx, E element) {
      if (focusLength >= STRICT_NODE_LENGTH) {
        root = root.pushFocus(focusStartIndex, arrayCopy(focus, focusLength, null));
        focus = singleElementArray(element);
        focusStartIndex = idx;
        focusLength = 1;
        size++;
        return this;
      }
      if (focusLength == 0) {
        focus = singleElementArray(element);
        focusStartIndex = idx;
        focusLength = 1;
        size++;
        return this;
      }
      int diff = idx - focusStartIndex;
      if ((diff >= 0) && (diff <= focusLength)) {
        if (focus.length <= focusLength) {
          int newLen = (focusLength >= HALF_STRICT_NODE_LENGTH) ? STRICT_NODE_LENGTH : focusLength << 1;
          focus = arrayCopy(focus, newLen, null);
        }
        int numItemsToShift = focusLength - diff;
        if (numItemsToShift > 0) {
          System.arraycopy(focus, diff, focus, diff + 1, numItemsToShift);
        }
        focus[diff] = element;
        focusLength++;
        size++;
        return this;
      }
      if (focusLength > 0) {
        root = root.pushFocus(focusStartIndex, arrayCopy(focus, focusLength, null));
      }
      focus = singleElementArray(element);
      focusStartIndex = idx;
      focusLength = 1;
      size++;
      return this;
    }

    /** {@inheritDoc} */
    @Override public UnmodSortedIterator<E> iterator() {
      return new Iter(pushFocus());
    }

    /** {@inheritDoc} */
    @Override Node<E> pushFocus() {
      return (focusLength == 0) ? root : root.pushFocus(focusStartIndex, arrayCopy(focus, focusLength, null));
    }

    /** {@inheritDoc} */
    @Override public String toString() {
      return UnmodIterable.toString("MutRrbt", this);
    }

    /**
         Joins the given tree to the right side of this tree (or this to the left side of that one) in
         something like O(log n) time.
         */
    @SuppressWarnings(value = { "unchecked" }) public RrbTree<E> join(RrbTree<E> that) {
      if (that.size() < MAX_NODE_LENGTH) {
        return concat(that);
      }
      if (this.size < MAX_NODE_LENGTH) {
        for (int i = 0; i < size; i++) {
          that = that.insert(i, this.get(i));
        }
        return that;
      }
      Node<E> leftRoot = pushFocus();
      Node<E> rightRoot = that.pushFocus();
      boolean leftIntoRight = leftRoot.height() < rightRoot.height();
      Node<E> taller = leftIntoRight ? rightRoot : leftRoot;
      Node<E> shorter = leftIntoRight ? leftRoot : rightRoot;
      Node<E> n = taller;
      int descentDepth = taller.height() - shorter.height();
      Node<E>[] ancestors = genericNodeArray(descentDepth);
      int i = 0;
      for ( ; i < ancestors.length; i++) {
        ancestors[i] = n;
        n = n.endChild(leftIntoRight);
      }
      i--;
      if (n.thisNodeHasRelaxedCapacity(shorter.numChildren())) {
        Node<E>[] kids;
        if (shorter instanceof Strict) {
          kids = ((Strict) shorter).nodes;
        } else {
          if (shorter instanceof Relaxed) {
            kids = ((Relaxed) shorter).nodes;
          } else {
            throw new IllegalStateException("Expected a strict or relaxed, but found " + shorter.getClass());
          }
        }
        n = n.addEndChildren(leftIntoRight, kids);
      }
      if (i >= 0) {
        n = ancestors[i];
        i--;
      }
      while (!n.thisNodeHasRelaxedCapacity(1) && (i >= 0)) {
        n = ancestors[i];
        i--;
        shorter = addAncestor(shorter);
        if (leftIntoRight) {
          leftRoot = shorter;
        } else {
          rightRoot = shorter;
        }
      }
      if (shorter.height() == (n.height() - 1)) {
        n = n.addEndChild(leftIntoRight, shorter);
      } else {
        if (i < 0) {
          @SuppressWarnings(value = { "unchecked" }) Node<E>[] newRootArray = new Node[] { leftRoot, rightRoot };
          int leftSize = leftRoot.size();
          Node<E> newRoot = new Relaxed<>(new int[] { leftSize, leftSize + rightRoot.size() }, newRootArray);
          return new MutRrbt<>(emptyArray(), 0, 0, newRoot, newRoot.size());
        } else {
          throw new IllegalStateException("How did we get here?");
        }
      }
      while (i >= 0) {
        Node<E> anc = ancestors[i];
        Relaxed<E> rel = (anc instanceof Strict) ? ((Strict) anc).relax() : (Relaxed<E>) anc;
        int repIdx = leftIntoRight ? 0 : rel.numChildren() - 1;
        n = Relaxed.replaceInRelaxedAt(rel.cumulativeSizes, rel.nodes, n, repIdx, n.size() - rel.nodes[repIdx].size());
        i--;
      }
      return new MutRrbt<>(emptyArray(), 0, 0, n, n.size());
    }

    /** {@inheritDoc} */
    @Override public MutRrbt<E> replace(int index, E item) {
      if ((index < 0) || (index > size)) {
        throw new IndexOutOfBoundsException("Index: " + index + " size: " + size);
      }
      if (index >= focusStartIndex) {
        int focusOffset = index - focusStartIndex;
        if (focusOffset < focusLength) {
          focus[focusOffset] = item;
          return this;
        }
        index -= focusLength;
      }
      root = root.replace(index, item);
      return this;
    }

    /** {@inheritDoc} */
    public MutRrbt<E> without(int index) {
      return (MutRrbt<E>) super.without(index);
    }

    @Override public int size() {
      return size;
    }

    /**
         Divides this RRB-Tree such that every index less-than the given index ends up in the
         left-hand tree and the indexed item and all subsequent ones end up in the right-hand tree.

         @param splitIndex the split point (excluded from the left-tree, included in the right one)
         @return two new sub-trees as determined by the split point.  If the point is 0 or
         this.size() one tree will be empty (but never null).
         */
    public Tuple2<MutRrbt<E>, MutRrbt<E>> split(int splitIndex) {
      if ((splitIndex < 1) || (splitIndex > size)) {
        throw new IndexOutOfBoundsException("Constraint violation failed: 1 <= splitIndex <= size");
      }
      Node<E> newRoot = pushFocus();
      SplitNode<E> split = newRoot.splitAt(splitIndex);
      E[] lFocus = split.leftFocus();
      Node<E> left = eliminateUnnecessaryAncestors(split.left());
      E[] rFocus = split.rightFocus();
      Node<E> right = eliminateUnnecessaryAncestors(split.right());
      return Tuple2.of(new MutRrbt<>(lFocus, left.size(), lFocus.length, left, left.size() + lFocus.length), new MutRrbt<>(rFocus, 0, rFocus.length, right, right.size() + rFocus.length));
    }
  }
=======
  public static class MutableRrbt<E extends java.lang.Object> extends RrbTree<E> implements MutableList<E> {
    private E[] focus;

    private int focusStartIndex;

    private int focusLength;

    private Node<E> root;

    private int size;

    MutableRrbt(E[] f, int fi, int fl, Node<E> r, int s) {
      focus = f;
      focusStartIndex = fi;
      focusLength = fl;
      root = r;
      size = s;
    }

    /** {@inheritDoc} */
    @SuppressWarnings(value = { "unchecked" }) @Override public MutableRrbt<E> append(E val) {
      if ((focusLength >= STRICT_NODE_LENGTH) || ((focusLength > 0) && (focusStartIndex < (size - focusLength)))) {
        root = root.pushFocus(focusStartIndex, arrayCopy(focus, focusLength, null));
        focus = (E[]) new Object[STRICT_NODE_LENGTH];
        focus[0] = val;
        focusStartIndex = size;
        focusLength = 1;
        size++;
        return this;
      }
      if (focus.length <= focusLength) {
        focus = arrayCopy(focus, STRICT_NODE_LENGTH, null);
      }
      focus[focusLength] = val;
      focusLength++;
      size++;
      return this;
    }

    /** {@inheritDoc} */
    @Override public MutableRrbt<E> concat(Iterable<? extends E> es) {
      return (MutableRrbt<E>) MutableList.super.concat(es);
    }

    void debugValidate() {
      if (focusLength > STRICT_NODE_LENGTH) {
        throw new IllegalStateException("focus len:" + focusLength + " gt STRICT_NODE_LENGTH:" + STRICT_NODE_LENGTH + "\n" + this.indentedStr(0));
      }
      int sz = root.debugValidate();
      if (sz != size - focusLength) {
        throw new IllegalStateException("Size incorrect.  Root size: " + root.size() + " RrbSize: " + size + " focusLen: " + focusLength + "\n" + this.indentedStr(0));
      }
      if ((focusStartIndex < 0) || (focusStartIndex > size)) {
        throw new IllegalStateException("focusStartIndex out of bounds!\n" + this.indentedStr(0));
      }
      if (!root.equals(eliminateUnnecessaryAncestors(root))) {
        throw new IllegalStateException("Unnecessary ancestors!\n" + this.indentedStr(0));
      }
    }

    /** {@inheritDoc} */
    @Override public E get(int i) {
      if ((i < 0) || (i > size)) {
        throw new IndexOutOfBoundsException("Index: " + i + " size: " + size);
      }
      if (i >= focusStartIndex) {
        int focusOffset = i - focusStartIndex;
        if (focusOffset < focusLength) {
          return focus[focusOffset];
        }
        i -= focusLength;
      }
      return root.get(i);
    }

    /** {@inheritDoc} */
    @Override public ImRrbt<E> immutable() {
      return new ImRrbt<>(arrayCopy(focus, focusLength, null), focusStartIndex, root, size);
    }

    /** {@inheritDoc} */
    @Override public String indentedStr(int indent) {
      return "RrbTree(size=" + size + " fsi=" + focusStartIndex + " focus=" + arrayString(focus) + "\n" + indentSpace(indent + 8) + "root=" + (root == null ? "null" : root.indentedStr(indent + 13)) + ")";
    }

    /** {@inheritDoc} */
    @Override public MutableRrbt<E> insert(int idx, E element) {
      if (focusLength >= STRICT_NODE_LENGTH) {
        root = root.pushFocus(focusStartIndex, arrayCopy(focus, focusLength, null));
        focus = singleElementArray(element);
        focusStartIndex = idx;
        focusLength = 1;
        size++;
        return this;
      }
      if (focusLength == 0) {
        focus = singleElementArray(element);
        focusStartIndex = idx;
        focusLength = 1;
        size++;
        return this;
      }
      int diff = idx - focusStartIndex;
      if ((diff >= 0) && (diff <= focusLength)) {
        if (focus.length <= focusLength) {
          int newLen = (focusLength >= HALF_STRICT_NODE_LENGTH) ? STRICT_NODE_LENGTH : focusLength << 1;
          focus = arrayCopy(focus, newLen, null);
        }
        int numItemsToShift = focusLength - diff;
        if (numItemsToShift > 0) {
          System.arraycopy(focus, diff, focus, diff + 1, numItemsToShift);
        }
        focus[diff] = element;
        focusLength++;
        size++;
        return this;
      }
      if (focusLength > 0) {
        root = root.pushFocus(focusStartIndex, arrayCopy(focus, focusLength, null));
      }
      focus = singleElementArray(element);
      focusStartIndex = idx;
      focusLength = 1;
      size++;
      return this;
    }

    /** {@inheritDoc} */
    @Override public UnmodSortedIterator<E> iterator() {
      return new Iter(pushFocus());
    }

    /** {@inheritDoc} */
    @Override Node<E> pushFocus() {
      return (focusLength == 0) ? root : root.pushFocus(focusStartIndex, arrayCopy(focus, focusLength, null));
    }

    /** {@inheritDoc} */
    @Override public String toString() {
      return UnmodIterable.toString("MutableRrbt", this);
    }

    /**
         Joins the given tree to the right side of this tree (or this to the left side of that one) in
         something like O(log n) time.
         */
    @SuppressWarnings(value = { "unchecked" }) public RrbTree<E> join(RrbTree<E> that) {
      if (that.size() < MAX_NODE_LENGTH) {
        return concat(that);
      }
      if (this.size < MAX_NODE_LENGTH) {
        for (int i = 0; i < size; i++) {
          that = that.insert(i, this.get(i));
        }
        return that;
      }
      Node<E> leftRoot = pushFocus();
      Node<E> rightRoot = that.pushFocus();
      boolean leftIntoRight = leftRoot.height() < rightRoot.height();
      Node<E> taller = leftIntoRight ? rightRoot : leftRoot;
      Node<E> shorter = leftIntoRight ? leftRoot : rightRoot;
      Node<E> n = taller;
      int descentDepth = taller.height() - shorter.height();
      Node<E>[] ancestors = genericNodeArray(descentDepth);
      int i = 0;
      for ( ; i < ancestors.length; i++) {
        ancestors[i] = n;
        n = n.endChild(leftIntoRight);
      }
      i--;
      if (n.thisNodeHasRelaxedCapacity(shorter.numChildren())) {
        Node<E>[] kids;
        if (shorter instanceof Strict) {
          kids = ((Strict) shorter).nodes;
        } else {
          if (shorter instanceof Relaxed) {
            kids = ((Relaxed) shorter).nodes;
          } else {
            throw new IllegalStateException("Expected a strict or relaxed, but found " + shorter.getClass());
          }
        }
        n = n.addEndChildren(leftIntoRight, kids);
      }
      if (i >= 0) {
        n = ancestors[i];
        i--;
      }
      while (!n.thisNodeHasRelaxedCapacity(1) && (i >= 0)) {
        n = ancestors[i];
        i--;
        shorter = addAncestor(shorter);
        if (leftIntoRight) {
          leftRoot = shorter;
        } else {
          rightRoot = shorter;
        }
      }
      if (shorter.height() == (n.height() - 1)) {
        n = n.addEndChild(leftIntoRight, shorter);
      } else {
        if (i < 0) {
          @SuppressWarnings(value = { "unchecked" }) Node<E>[] newRootArray = new Node[] { leftRoot, rightRoot };
          int leftSize = leftRoot.size();
          Node<E> newRoot = new Relaxed<>(new int[] { leftSize, leftSize + rightRoot.size() }, newRootArray);
          return new MutableRrbt<>(emptyArray(), 0, 0, newRoot, newRoot.size());
        } else {
          throw new IllegalStateException("How did we get here?");
        }
      }
      while (i >= 0) {
        Node<E> anc = ancestors[i];
        Relaxed<E> rel = (anc instanceof Strict) ? ((Strict) anc).relax() : (Relaxed<E>) anc;
        int repIdx = leftIntoRight ? 0 : rel.numChildren() - 1;
        n = Relaxed.replaceInRelaxedAt(rel.cumulativeSizes, rel.nodes, n, repIdx, n.size() - rel.nodes[repIdx].size());
        i--;
      }
      return new MutableRrbt<>(emptyArray(), 0, 0, n, n.size());
    }

    /** {@inheritDoc} */
    @Override public MutableRrbt<E> replace(int index, E item) {
      if ((index < 0) || (index > size)) {
        throw new IndexOutOfBoundsException("Index: " + index + " size: " + size);
      }
      if (index >= focusStartIndex) {
        int focusOffset = index - focusStartIndex;
        if (focusOffset < focusLength) {
          focus[focusOffset] = item;
          return this;
        }
        index -= focusLength;
      }
      root = root.replace(index, item);
      return this;
    }

    /** {@inheritDoc} */
    public MutableRrbt<E> without(int index) {
      return (MutableRrbt<E>) super.without(index);
    }

    @Override public int size() {
      return size;
    }

    /**
         Divides this RRB-Tree such that every index less-than the given index ends up in the
         left-hand tree and the indexed item and all subsequent ones end up in the right-hand tree.

         @param splitIndex the split point (excluded from the left-tree, included in the right one)
         @return two new sub-trees as determined by the split point.  If the point is 0 or
         this.size() one tree will be empty (but never null).
         */
    public Tuple2<MutableRrbt<E>, MutableRrbt<E>> split(int splitIndex) {
      if (splitIndex < 1) {
        if (splitIndex == 0) {
          return Tuple2.of(emptyMutable(), this);
        } else {
          throw new IndexOutOfBoundsException("Constraint violation failed: 1 <= splitIndex <= size");
        }
      } else {
        if (splitIndex >= size) {
          if (splitIndex == size) {
            return Tuple2.of(this, emptyMutable());
          } else {
            throw new IndexOutOfBoundsException("Constraint violation failed: 1 <= splitIndex <= size");
          }
        }
      }
      Node<E> newRoot = pushFocus();
      SplitNode<E> split = newRoot.splitAt(splitIndex);
      E[] lFocus = split.leftFocus();
      Node<E> left = eliminateUnnecessaryAncestors(split.left());
      E[] rFocus = split.rightFocus();
      Node<E> right = eliminateUnnecessaryAncestors(split.right());
      return Tuple2.of(new MutableRrbt<>(lFocus, left.size(), lFocus.length, left, left.size() + lFocus.length), new MutableRrbt<>(rFocus, 0, rFocus.length, right, right.size() + rFocus.length));
    }
  }
>>>>>>> /usr/src/app/output/glenkpeterson/j-sicle/bf4de20006b5405bf4c84c50a037ef0b28a060ab/src/main/java/org/organicdesign/fp/collections/RrbTree.java/right.java


  public static class ImRrbt<E extends java.lang.Object> extends RrbTree<E> implements ImList<E>, Serializable {
    private final E[] focus;

    private final int focusStartIndex;

    private transient final Node<E> root;

    private final int size;

    ImRrbt(E[] f, int fi, Node<E> r, int s) {
      focus = f;
      focusStartIndex = fi;
      root = r;
      size = s;
    }

    private static final long serialVersionUID = 20170625165600L;

    private static class SerializationProxy<E extends java.lang.Object> implements Serializable {
      private static final long serialVersionUID = 20160904155600L;

      private final int size;

      private transient RrbTree<E> rrbTree;

      SerializationProxy(RrbTree<E> v) {
        size = v.size();
        rrbTree = v;
      }

      private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (E entry : rrbTree) {
          s.writeObject(entry);
        }
      }

      @SuppressWarnings(value = { "unchecked" }) private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        MutRrbt<E> temp = emptyMutable();
        for (int i = 0; i < size; i++) {
          temp.append((E) s.readObject());
        }
        rrbTree = temp.immutable();
      }

      private Object readResolve() {
        return rrbTree;
      }
    }

    private Object writeReplace() {
      return new SerializationProxy<>(this);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
      throw new InvalidObjectException("Proxy required");
    }

    /** {@inheritDoc} */
    @Override public ImRrbt<E> append(E val) {
      if ((focus.length >= STRICT_NODE_LENGTH) || ((focus.length > 0) && (focusStartIndex < (size - focus.length)))) {
        Node<E> newRoot = root.pushFocus(focusStartIndex, focus);
        return new ImRrbt<>(singleElementArray(val), size, newRoot, size + 1);
      }
      return new ImRrbt<>(insertIntoArrayAt(val, focus, focus.length, null), focusStartIndex, root, size + 1);
    }

    /** {@inheritDoc} */
    @Override public ImRrbt<E> concat(Iterable<? extends E> es) {
      return this.mutable().concat(es).immutable();
    }

    void debugValidate() {
      if (focus.length > STRICT_NODE_LENGTH) {
        throw new IllegalStateException("focus len:" + focus.length + " gt STRICT_NODE_LENGTH:" + STRICT_NODE_LENGTH + "\n" + this.indentedStr(0));
      }
      int sz = root.debugValidate();
      if (sz != size - focus.length) {
        throw new IllegalStateException("Size incorrect.  Root size: " + root.size() + " RrbSize: " + size + " focusLen: " + focus.length + "\n" + this.indentedStr(0));
      }
      if ((focusStartIndex < 0) || (focusStartIndex > size)) {
        throw new IllegalStateException("focusStartIndex out of bounds!\n" + this.indentedStr(0));
      }
      if (!root.equals(eliminateUnnecessaryAncestors(root))) {
        throw new IllegalStateException("Unnecessary ancestors!\n" + this.indentedStr(0));
      }
    }

    /** {@inheritDoc} */
    @Override public E get(int i) {
      if ((i < 0) || (i > size)) {
        throw new IndexOutOfBoundsException("Index: " + i + " size: " + size);
      }
      if (i >= focusStartIndex) {
        int focusOffset = i - focusStartIndex;
        if (focusOffset < focus.length) {
          return focus[focusOffset];
        }
        i -= focus.length;
      }
      return root.get(i);
    }

    /** {@inheritDoc} */
    @Override public ImRrbt<E> insert(int idx, E element) {
      if (focus.length >= STRICT_NODE_LENGTH) {
        Node<E> newRoot = root.pushFocus(focusStartIndex, focus);
        E[] newFocus = singleElementArray(element);
        return new ImRrbt<>(newFocus, idx, newRoot, size + 1);
      }
      int diff = idx - focusStartIndex;
      if ((diff >= 0) && (diff <= focus.length)) {
        E[] newFocus = insertIntoArrayAt(element, focus, diff, null);
        return new ImRrbt<>(newFocus, focusStartIndex, root, size + 1);
      }
      Node<E> newRoot = focus.length > 0 ? root.pushFocus(focusStartIndex, focus) : root;
      E[] newFocus = singleElementArray(element);
      return new ImRrbt<>(newFocus, idx, newRoot, size + 1);
    }

    /** {@inheritDoc} */
    @Override public MutRrbt<E> mutable() {
      return new MutRrbt<>(arrayCopy(focus, focus.length, null), focusStartIndex, focus.length, root, size);
    }

    /** {@inheritDoc} */
    @Override public UnmodSortedIterator<E> iterator() {
      return new Iter(pushFocus());
    }

    /** {@inheritDoc} */
    @Override Node<E> pushFocus() {
      return (focus.length == 0) ? root : root.pushFocus(focusStartIndex, focus);
    }

    /**
         Joins the given tree to the right side of this tree (or this to the left side of that one)
         in something like O(log n) time.
         */
    @SuppressWarnings(value = { "unchecked" }) public RrbTree<E> join(RrbTree<E> that) {
      if (that.size() < MAX_NODE_LENGTH) {
        return concat(that);
      }
      if (this.size < MAX_NODE_LENGTH) {
        for (int i = 0; i < size; i++) {
          that = that.insert(i, this.get(i));
        }
        return that;
      }
      Node<E> leftRoot = pushFocus();
      Node<E> rightRoot = that.pushFocus();
      boolean leftIntoRight = leftRoot.height() < rightRoot.height();
      Node<E> taller = leftIntoRight ? rightRoot : leftRoot;
      Node<E> shorter = leftIntoRight ? leftRoot : rightRoot;
      Node<E> n = taller;
      int descentDepth = taller.height() - shorter.height();
      Node<E>[] ancestors = genericNodeArray(descentDepth);
      int i = 0;
      for ( ; i < ancestors.length; i++) {
        ancestors[i] = n;
        n = n.endChild(leftIntoRight);
      }
      i--;
      if (n.thisNodeHasRelaxedCapacity(shorter.numChildren())) {
        Node<E>[] kids;
        if (shorter instanceof Strict) {
          kids = ((Strict) shorter).nodes;
        } else {
          if (shorter instanceof Relaxed) {
            kids = ((Relaxed) shorter).nodes;
          } else {
            throw new IllegalStateException("Expected a strict or relaxed, but found " + shorter.getClass());
          }
        }
        n = n.addEndChildren(leftIntoRight, kids);
      }
      if (i >= 0) {
        n = ancestors[i];
        i--;
      }
      while (!n.thisNodeHasRelaxedCapacity(1) && (i >= 0)) {
        n = ancestors[i];
        i--;
        shorter = addAncestor(shorter);
        if (leftIntoRight) {
          leftRoot = shorter;
        } else {
          rightRoot = shorter;
        }
      }
      if (shorter.height() == (n.height() - 1)) {
        n = n.addEndChild(leftIntoRight, shorter);
      } else {
        if (i < 0) {
          @SuppressWarnings(value = { "unchecked" }) Node<E>[] newRootArray = new Node[] { leftRoot, rightRoot };
          int leftSize = leftRoot.size();
          Node<E> newRoot = new Relaxed<>(new int[] { leftSize, leftSize + rightRoot.size() }, newRootArray);
          return new ImRrbt<>(emptyArray(), 0, newRoot, newRoot.size());
        } else {
          throw new IllegalStateException("How did we get here?");
        }
      }
      while (i >= 0) {
        Node<E> anc = ancestors[i];
        Relaxed<E> rel = (anc instanceof Strict) ? ((Strict) anc).relax() : (Relaxed<E>) anc;
        int repIdx = leftIntoRight ? 0 : rel.numChildren() - 1;
        n = Relaxed.replaceInRelaxedAt(rel.cumulativeSizes, rel.nodes, n, repIdx, n.size() - rel.nodes[repIdx].size());
        i--;
      }
      return new ImRrbt<>(emptyArray(), 0, n, n.size());
    }

    /** {@inheritDoc} */
    @Override public ImRrbt<E> replace(int index, E item) {
      if ((index < 0) || (index > size)) {
        throw new IndexOutOfBoundsException("Index: " + index + " size: " + size);
      }
      if (index >= focusStartIndex) {
        int focusOffset = index - focusStartIndex;
        if (focusOffset < focus.length) {
          return new ImRrbt<>(replaceInArrayAt(item, focus, focusOffset, null), focusStartIndex, root, size);
        }
        index -= focus.length;
      }
      return new ImRrbt<>(focus, focusStartIndex, root.replace(index, item), size);
    }

    /** {@inheritDoc} */
    public ImRrbt<E> without(int index) {
      return (ImRrbt<E>) super.without(index);
    }

    @Override public int size() {
      return size;
    }

    /**
         Divides this RRB-Tree such that every index less-than the given index ends up in the left-hand
         tree and the indexed item and all subsequent ones end up in the right-hand tree.

         @param splitIndex the split point (excluded from the left-tree, included in the right one)
         @return two new sub-trees as determined by the split point.  If the point is 0 or this.size()
         one tree will be empty (but never null).
         */
    public Tuple2<ImRrbt<E>, ImRrbt<E>> split(int splitIndex) {
      if (splitIndex < 1) {
        if (splitIndex == 0) {
          return Tuple2.of(empty(), this);
        } else {
          throw new IndexOutOfBoundsException("Constraint violation failed: 1 <= splitIndex <= size");
        }
      } else {
        if (splitIndex >= size) {
          if (splitIndex == size) {
            return Tuple2.of(this, empty());
          } else {
            throw new IndexOutOfBoundsException("Constraint violation failed: 1 <= splitIndex <= size");
          }
        }
      }
      Node<E> newRoot = pushFocus();
      SplitNode<E> split = newRoot.splitAt(splitIndex);
      E[] lFocus = split.leftFocus();
      Node<E> left = eliminateUnnecessaryAncestors(split.left());
      E[] rFocus = split.rightFocus();
      Node<E> right = eliminateUnnecessaryAncestors(split.right());
      return Tuple2.of(new ImRrbt<>(lFocus, left.size(), left, left.size() + lFocus.length), new ImRrbt<>(rFocus, 0, right, right.size() + rFocus.length));
    }

    /** {@inheritDoc} */
    @Override public String indentedStr(int indent) {
      return "RrbTree(size=" + size + " fsi=" + focusStartIndex + " focus=" + arrayString(focus) + "\n" + indentSpace(indent + 8) + "root=" + (root == null ? "null" : root.indentedStr(indent + 13)) + ")";
    }

    /** {@inheritDoc} */
    @Override public String toString() {
      return UnmodIterable.toString("ImRrbt", this);
    }

    private static final ImRrbt EMPTY_IM_RRBT = new ImRrbt<>(emptyArray(), 0, emptyLeaf(), 0);
  }

  /** Returns the empty, immutable RRB-Tree (there is only one) */
  @SuppressWarnings(value = { "unchecked" }) public static <T extends java.lang.Object> ImRrbt<T> empty() {
    return (ImRrbt<T>) ImRrbt.EMPTY_IM_RRBT;
  }

  /** Returns the empty, mutable RRB-Tree (there is only one) */
  @SuppressWarnings(value = { "unchecked" }) public static <T extends java.lang.Object> MutRrbt<T> emptyMutable() {
    return (MutRrbt<T>) empty().mutable();
  }

  /** {@inheritDoc} */
  @Override abstract public RrbTree<E> append(E t);

  /** Internal validation method for testing. */
  abstract void debugValidate();

  /** {@inheritDoc} */
  @Override abstract public E get(int i);

  /**
     Inserts an item in the RRB tree pushing the current element at that index and all subsequent
     elements to the right.
     @param idx the insertion point
     @param element the item to insert
     @return a new RRB-Tree with the item inserted.
     */
  @SuppressWarnings(value = { "WeakerAccess" }) public abstract RrbTree<E> insert(int idx, E element);

  /** {@inheritDoc} */
  @Override abstract public UnmodSortedIterator<E> iterator();

  /**
     Joins the given tree to the right side of this tree (or this to the left side of that one) in
     something like O(log n) time.
     */
  public abstract RrbTree<E> join(RrbTree<E> that);

  /** Internal method - do not use. */
  abstract Node<E> pushFocus();

  /** {@inheritDoc} */
  @Override abstract public RrbTree<E> replace(int index, E item);

  /** {@inheritDoc} */
  @Override abstract public int size();

  /**
     Divides this RRB-Tree such that every index less-than the given index ends up in the left-hand
     tree and the indexed item and all subsequent ones end up in the right-hand tree.

     @param splitIndex the split point (excluded from the left-tree, included in the right one)
     @return two new sub-trees as determined by the split point.  If the point is 0 or this.size()
     one tree will be empty (but never null).
     */
  abstract public Tuple2<? extends RrbTree<E>, ? extends RrbTree<E>> split(int splitIndex);

  /**
     Returns a new RrbTree minus the given item (all items to the right are shifted left one)
     This is O(log n).
     */
  public RrbTree<E> without(int index) {
    if ((index > 0) && (index < size() - 1)) {
      Tuple2<? extends RrbTree<E>, ? extends RrbTree<E>> s1 = split(index);
      Tuple2<? extends RrbTree<E>, ? extends RrbTree<E>> s2 = s1._2().split(1);
      return s1._1().join(s2._2());
    } else {
      if (index == 0) {
        return split(1)._2();
      } else {
        if (index == size() - 1) {
          return split(size() - 1)._1();
        } else {
          throw new IndexOutOfBoundsException("Failed test: 0 <= index < size");
        }
      }
    }
  }

  private static <E extends java.lang.Object> Node<E> eliminateUnnecessaryAncestors(Node<E> n) {
    while (!(n instanceof Leaf) && (n.numChildren() == 1)) {
      n = n.child(0);
    }
    return n;
  }

  @SuppressWarnings(value = { "unchecked" }) private static <E extends java.lang.Object> Node<E> addAncestor(Node<E> n) {
    return ((n instanceof Leaf) && (n.size() == STRICT_NODE_LENGTH)) ? new Strict<>(NODE_LENGTH_POW_2, n.size(), (Node<E>[]) new Node[] { n }) : (n instanceof Strict) ? new Strict<>(((Strict) n).shift + NODE_LENGTH_POW_2, n.size(), (Node<E>[]) new Node[] { n }) : new Relaxed<>(new int[] { n.size() }, (Node<E>[]) new Node[] { n });
  }

  /** {@inheritDoc} */
  @SuppressWarnings(value = { "unchecked" }) @Override public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof List)) {
      return false;
    }
    List<? extends E> that = (List<? extends E>) other;
    return (this.size() == that.size()) && UnmodSortedIterable.equal(this, UnmodSortedIterable.castFromList(that));
  }

  /** This implementation is correct and compatible with java.util.AbstractList, but O(n). */
  @Override public int hashCode() {
    int ret = 1;
    for (E item : this) {
      ret *= 31;
      if (item != null) {
        ret += item.hashCode();
      }
    }
    return ret;
  }

  /** {@inheritDoc} */
  @Override abstract public String indentedStr(int indent);

  private static final int NODE_LENGTH_POW_2 = 5;

  static final int STRICT_NODE_LENGTH = 1 << NODE_LENGTH_POW_2;

  private static final int HALF_STRICT_NODE_LENGTH = STRICT_NODE_LENGTH >> 1;

  private static final int MIN_NODE_LENGTH = (STRICT_NODE_LENGTH + 1) * 2 / 3;

  private static final int MAX_NODE_LENGTH = ((STRICT_NODE_LENGTH + 1) * 4 / 3);

  private static final Leaf EMPTY_LEAF = new Leaf<>(EMPTY_ARRAY);

  @SuppressWarnings(value = { "unchecked" }) private static <T extends java.lang.Object> Leaf<T> emptyLeaf() {
    return (Leaf<T>) EMPTY_LEAF;
  }

  private interface Node<T extends java.lang.Object> extends Indented {
    /** Returns the immediate child node at the given index. */
    Node<T> child(int childIdx);

    int debugValidate();

    /** Returns the leftMost (first) or right-most (last) child */
    Node<T> endChild(boolean leftMost);

    /** Adds a node as the first/leftmost or last/rightmost child */
    Node<T> addEndChild(boolean leftMost, Node<T> shorter);

    /** Adds kids as leftmost or rightmost of current children */
    Node<T> addEndChildren(boolean leftMost, Node<T>[] newKids);

    /** Return the item at the given index */
    T get(int i);

    /** Returns true if this strict-Radix tree can take another 32 items. */
    boolean hasStrictCapacity();

    /** Returns the maximum depth below this node.  Leaf nodes are height 1. */
    int height();

    /** Number of items stored in this node */
    int size();

    /** Can this node take the specified number of children? */
    boolean thisNodeHasRelaxedCapacity(int numItems);

    /**
         Can we put focus at the given index without reshuffling nodes?
         @param index the index we want to insert at
         @param size the number of items to insert.  Must be size < MAX_NODE_LENGTH
         @return true if we can do so without otherwise adjusting the tree.
         */
    boolean hasRelaxedCapacity(int index, int size);

    /** Returns the number of immediate children of this node, not all descendants. */
    int numChildren();

    Node<T> pushFocus(int index, T[] oldFocus);

    Node<T> replace(int idx, T t);

    SplitNode<T> splitAt(int splitIndex);
  }

  private static class SplitNode<T extends java.lang.Object> extends Tuple4<Node<T>, T[], Node<T>, T[]> implements Indented {
    /**
         Constructor.
         @param ln Left-hand whole-node
         @param lf Left-focus (leftover items from left node)
         @param rn Right-hand whole-node
         @param rf Right-focus (leftover items from right node)
         */
    SplitNode(Node<T> ln, T[] lf, Node<T> rn, T[] rf) {
      super(ln, lf, rn, rf);
    }

    public Node<T> left() {
      return _1;
    }

    public T[] leftFocus() {
      return _2;
    }

    public Node<T> right() {
      return _3;
    }

    public T[] rightFocus() {
      return _4;
    }

    public int size() {
      return _1.size() + _2.length + _3.size() + _4.length;
    }

    @Override public String indentedStr(int indent) {
      StringBuilder sB = new StringBuilder().append("SplitNode(");
      int nextIndent = indent + sB.length();
      String nextIndentStr = indentSpace(nextIndent).toString();
      return sB.append("left=").append(left().indentedStr(nextIndent + 5)).append(",\n").append(nextIndentStr).append("leftFocus=").append(arrayString(leftFocus())).append(",\n").append(nextIndentStr).append("right=").append(right().indentedStr(nextIndent + 6)).append(",\n").append(nextIndentStr).append("rightFocus=").append(arrayString(rightFocus())).append(")").toString();
    }

    @Override public String toString() {
      return indentedStr(0);
    }
  }

  private static class Leaf<T extends java.lang.Object> implements Node<T> {
    final T[] items;

    Leaf(T[] ts) {
      items = ts;
    }

    @Override public Node<T> child(int childIdx) {
      throw new UnsupportedOperationException("Don\'t call this on a leaf");
    }

    @Override public int debugValidate() {
      if (items.length == 0) {
        return 0;
      }
      if (items.length < MIN_NODE_LENGTH) {
        throw new IllegalStateException("Leaf too short!\n" + this.indentedStr(0));
      } else {
        if (items.length >= MAX_NODE_LENGTH) {
          throw new IllegalStateException("Leaf too long!\n" + this.indentedStr(0));
        }
      }
      return items.length;
    }

    /** Returns the leftMost (first) or right-most (last) child */
    @Override public Node<T> endChild(boolean leftMost) {
      throw new UnsupportedOperationException("Don\'t call this on a leaf");
    }

    /** Adds a node as the first/leftmost or last/rightmost child */
    @Override public Node<T> addEndChild(boolean leftMost, Node<T> shorter) {
      throw new UnsupportedOperationException("Don\'t call this on a leaf");
    }

    /** Adds kids as leftmost or rightmost of current children */
    @Override public Node<T> addEndChildren(boolean leftMost, Node<T>[] newKids) {
      throw new UnsupportedOperationException("Don\'t call this on a leaf");
    }

    @Override public T get(int i) {
      return items[i];
    }

    @Override public int height() {
      return 1;
    }

    @Override public int size() {
      return items.length;
    }

    @Override public boolean hasStrictCapacity() {
      return false;
    }

    @Override public boolean hasRelaxedCapacity(int index, int size) {
      return (items.length + size) < MAX_NODE_LENGTH;
    }

    @Override public SplitNode<T> splitAt(int splitIndex) {
      if (splitIndex == 0) {
        return new SplitNode<>(emptyLeaf(), emptyArray(), emptyLeaf(), items);
      }
      if (splitIndex == items.length) {
        return new SplitNode<>(emptyLeaf(), items, emptyLeaf(), emptyArray());
      }
      Tuple2<T[], T[]> split = splitArray(items, splitIndex);
      T[] splitL = split._1();
      T[] splitR = split._2();
      Leaf<T> leafL = emptyLeaf();
      Leaf<T> leafR = emptyLeaf();
      if (splitL.length > STRICT_NODE_LENGTH) {
        leafL = new Leaf<>(splitL);
        splitL = emptyArray();
      }
      if (splitR.length > STRICT_NODE_LENGTH) {
        leafR = new Leaf<>(splitR);
        splitR = emptyArray();
      }
      return new SplitNode<>(leafL, splitL, leafR, splitR);
    }

    @SuppressWarnings(value = { "unchecked" }) private Leaf<T>[] spliceAndSplit(T[] oldFocus, int splitIndex) {
      T[] newItems = spliceIntoArrayAt(oldFocus, items, splitIndex, null);
      Tuple2<T[], T[]> split = splitArray(newItems, newItems.length >> 1);
      return new Leaf[] { new Leaf<>(split._1()), new Leaf<>(split._2()) };
    }

    @Override public int numChildren() {
      return size();
    }

    @SuppressWarnings(value = { "unchecked" }) @Override public Node<T> pushFocus(int index, T[] oldFocus) {
      if (items.length == 0) {
        return new Leaf<>(oldFocus);
      }
      if ((items.length == STRICT_NODE_LENGTH) && (oldFocus.length == STRICT_NODE_LENGTH) && ((index == STRICT_NODE_LENGTH) || (index == 0))) {
        Leaf<T>[] newNodes = (index == STRICT_NODE_LENGTH) ? new Leaf[] { this, new Leaf<>(oldFocus) } : new Leaf[] { new Leaf<>(oldFocus), this };
        return new Strict<>(NODE_LENGTH_POW_2, STRICT_NODE_LENGTH << 1, newNodes);
      }
      if ((items.length + oldFocus.length) < MAX_NODE_LENGTH) {
        return new Leaf<>(spliceIntoArrayAt(oldFocus, items, index, null));
      }
      Leaf<T>[] res = spliceAndSplit(oldFocus, index);
      Leaf<T> leftLeaf = res[0];
      Leaf<T> rightLeaf = res[1];
      int leftSize = leftLeaf.size();
      return new Relaxed<>(new int[] { leftSize, leftSize + rightLeaf.size() }, res);
    }

    @Override public Node<T> replace(int idx, T t) {
      return new Leaf<>(replaceInArrayAt(t, items, idx, null));
    }

    @Override public boolean thisNodeHasRelaxedCapacity(int numItems) {
      return items.length + numItems < MAX_NODE_LENGTH;
    }

    @Override public String toString() {
      return arrayString(items);
    }

    @Override public String indentedStr(int indent) {
      return arrayString(items);
    }
  }

  private static class Strict<T extends java.lang.Object> implements Node<T> {
    final int shift;

    final int size;

    final Node<T>[] nodes;

    Strict(int sh, int sz, Node<T>[] ns) {
      shift = sh;
      size = sz;
      nodes = ns;
    }

    @Override public Node<T> child(int childIdx) {
      return nodes[childIdx];
    }

    @Override public int debugValidate() {
      if (nodes.length > STRICT_NODE_LENGTH) {
        throw new IllegalStateException("Too many child nodes!\n" + this.indentedStr(0));
      }
      int sz = 0;
      int height = height() - 1;
      int sh = shift - NODE_LENGTH_POW_2;
      for (int i = 0; i < nodes.length; i++) {
        Node<T> n = nodes[i];
        if (!(n instanceof Strict) && !(n instanceof Leaf)) {
          throw new IllegalStateException("Strict nodes can only have strict or leaf children!\n" + this.indentedStr(0));
        }
        if (n.height() != height) {
          throw new IllegalStateException("Unequal height!  My height = " + height() + "\n" + this.indentedStr(0));
        }
        if ((n instanceof Strict) && ((Strict) n).shift != sh) {
          throw new IllegalStateException("Unexpected shift difference between levels!\n" + this.indentedStr(0));
        }
        if (i < nodes.length - 1) {
          if (n.hasStrictCapacity()) {
            throw new IllegalStateException("Non-last strict node is not full!\n" + this.indentedStr(0));
          }
          if ((n.size() % STRICT_NODE_LENGTH) != 0) {
            throw new IllegalStateException("Non-last strict node has a weird size!\n" + this.indentedStr(0));
          }
        }
        if (n instanceof Strict) {
          n.debugValidate();
        }
        sz += n.size();
      }
      return sz;
    }

    /** Returns the leftMost (first) or right-most (last) child */
    @Override public Node<T> endChild(boolean leftMost) {
      return nodes[leftMost ? 0 : nodes.length - 1];
    }

    /** Adds a node as the first/leftmost or last/rightmost child */
    @SuppressWarnings(value = { "unchecked" }) @Override public Node<T> addEndChild(boolean leftMost, Node<T> shorter) {
      if (leftMost || !(shorter instanceof Strict)) {
        return relax().addEndChild(leftMost, shorter);
      }
      return new Strict<>(shift, size + shorter.size(), insertIntoArrayAt(shorter, nodes, nodes.length, Node.class));
    }

    /** Adds kids as leftmost or rightmost of current children */
    @Override public Node<T> addEndChildren(boolean leftMost, Node<T>[] newKids) {
      return relax().addEndChildren(leftMost, newKids);
    }

    @Override public int height() {
      return (shift / NODE_LENGTH_POW_2) + 1;
    }

    /**
         Returns the highest bits which we use to index into our array - the index of the immediate
         child of this node.  This is the simplicity (and
         speed) of Strict indexing.  When everything works, this can be inlined for performance.
         This could maybe yield a good guess for Relaxed nodes?

         Shifting right by a number is equivalent to dividing by: 2 raised to the power of that
         number.
         i >> n is equivalent to i / (2^n)
         */
    private int highBits(int i) {
      return i >> shift;
    }

    /**
         Returns the low bits of the index (the part Strict sub-nodes need to know about).  This
         only works because the leaf nodes are all the same size and that size is a power of 2
         (the radix).  All branch must have the same radix (branching factor or number of immediate
         sub-nodes).

         Bit shifting is faster than addition or multiplication, but perhaps more importantly, it
         means we don't have to store the sizes of the nodes which means we don't have to fetch
         those sizes from memory or use up cache space.  All of this helps make this data structure
         simple and fast.

         When everything works, this function can be inlined for performance (if that even helps).
         Contrast this with how Relaxed nodes work: they use subtraction instead!
         */
    private int lowBits(int i) {
      int shifter = -1 << shift;
      int invShifter = ~shifter;
      return i & invShifter;
    }

    @Override public T get(int i) {
      return nodes[highBits(i)].get(lowBits(i));
    }

    @Override public int size() {
      return size;
    }

    @Override public boolean hasStrictCapacity() {
      return highBits(size) != STRICT_NODE_LENGTH;
    }

    @Override public boolean hasRelaxedCapacity(int index, int size) {
      return size < MAX_NODE_LENGTH - STRICT_NODE_LENGTH;
    }

    @SuppressWarnings(value = { "unchecked" }) @Override public SplitNode<T> splitAt(int splitIndex) {
      if (splitIndex == 0) {
        return new SplitNode<>(emptyLeaf(), emptyArray(), this, emptyArray());
      }
      if (splitIndex == size) {
        return new SplitNode<>(this, emptyArray(), emptyLeaf(), emptyArray());
      }
      int subNodeIndex = highBits(splitIndex);
      Node<T> subNode = nodes[subNodeIndex];
      int subNodeAdjustedIndex = lowBits(splitIndex);
      SplitNode<T> split = subNode.splitAt(subNodeAdjustedIndex);
      final Node<T> left;
      final Node<T> splitLeft = split.left();
      if (subNodeIndex == 0) {
        left = new Strict<>(shift, splitLeft.size(), new Node[] { splitLeft });
      } else {
        boolean haveLeft = (splitLeft.size() > 0);
        int numLeftItems = subNodeIndex + (haveLeft ? 1 : 0);
        Node<T>[] leftNodes = genericNodeArray(numLeftItems);
        System.arraycopy(nodes, 0, leftNodes, 0, subNodeIndex);
        if (haveLeft) {
          leftNodes[numLeftItems - 1] = splitLeft;
        }
        int newSize = 0;
        for (Node n : leftNodes) {
          newSize += n.size();
        }
        left = new Strict<>(shift, newSize, leftNodes);
      }
      final Node<T> right = Relaxed.fixRight(nodes, split.right(), subNodeIndex);
      return new SplitNode<>(left, split.leftFocus(), right, split.rightFocus());
    }

    Relaxed<T> relax() {
      int[] newCumSizes = new int[nodes.length];
      int cumulativeSize = 0;
      int subNodeSize = nodes[0].size();
      for (int i = 0; i < nodes.length - 1; i++) {
        cumulativeSize += subNodeSize;
        newCumSizes[i] = cumulativeSize;
      }
      cumulativeSize += nodes[nodes.length - 1].size();
      newCumSizes[newCumSizes.length - 1] = cumulativeSize;
      return new Relaxed<>(newCumSizes, nodes);
    }

    @Override public int numChildren() {
      return nodes.length;
    }

    @SuppressWarnings(value = { "unchecked" }) @Override public Node<T> pushFocus(int index, T[] oldFocus) {
      int subNodeIndex = highBits(index);
      if (oldFocus.length == STRICT_NODE_LENGTH) {
        if (index == size()) {
          Node<T> lastNode = nodes[nodes.length - 1];
          if (lastNode.hasStrictCapacity()) {
            Strict<T> strict = (Strict<T>) lastNode;
            Node<T> newNode = strict.pushFocus(lowBits(index), oldFocus);
            Node<T>[] newNodes = replaceInArrayAt(newNode, nodes, nodes.length - 1, Node.class);
            return new Strict<>(shift, size + oldFocus.length, newNodes);
          }
          Node<T> newNode = new Leaf<>(oldFocus);
          int newShift = NODE_LENGTH_POW_2;
          int maxShift = (nodes.length < STRICT_NODE_LENGTH) ? shift : shift + 1;
          while (newShift < maxShift) {
            newNode = new Strict<>(newShift, oldFocus.length, singleElementArray(newNode, Node.class));
            newShift += NODE_LENGTH_POW_2;
          }
          if ((nodes.length < STRICT_NODE_LENGTH)) {
            Node<T>[] newNodes = insertIntoArrayAt(newNode, nodes, subNodeIndex, Node.class);
            return new Strict<>(shift, size + oldFocus.length, newNodes);
          } else {
            return new Strict(shift + NODE_LENGTH_POW_2, size + oldFocus.length, new Node[] { this, newNode });
          }
        } else {
          if ((shift == NODE_LENGTH_POW_2) && (lowBits(index) == 0) && (nodes.length < STRICT_NODE_LENGTH)) {
            Node<T> newNode = new Leaf<>(oldFocus);
            Node<T>[] newNodes = insertIntoArrayAt(newNode, nodes, subNodeIndex, Node.class);
            return new Strict<>(shift, size + oldFocus.length, newNodes);
          }
        }
      }
      return relax().pushFocus(index, oldFocus);
    }

    @SuppressWarnings(value = { "unchecked" }) @Override public Node<T> replace(int idx, T t) {
      int thisNodeIdx = highBits(idx);
      Node<T> newNode = nodes[thisNodeIdx].replace(lowBits(idx), t);
      return new Strict<>(shift, size, replaceInArrayAt(newNode, nodes, thisNodeIdx, Node.class));
    }

    @Override public boolean thisNodeHasRelaxedCapacity(int numNodes) {
      return nodes.length + numNodes < MAX_NODE_LENGTH;
    }

    @Override public String toString() {
      return "Strict" + shift + arrayString(nodes);
    }

    @Override public String indentedStr(int indent) {
      StringBuilder sB = new StringBuilder().append("Strict").append(shift).append("(");
      int len = sB.length();
      sB.append("size=").append(size).append("\n");
      sB.append(indentSpace(len + indent));
      return showSubNodes(sB, nodes, indent + len).append(")").toString();
    }
  }

  private static class Relaxed<T extends java.lang.Object> implements Node<T> {
    final int[] cumulativeSizes;

    final Node<T>[] nodes;

    Relaxed(int[] szs, Node<T>[] ns) {
      cumulativeSizes = szs;
      nodes = ns;
    }

    @Override public Node<T> child(int childIdx) {
      return nodes[childIdx];
    }

    @Override public int debugValidate() {
      int sz = 0;
      int height = height() - 1;
      if (nodes.length != cumulativeSizes.length) {
        throw new IllegalStateException("Unequal size of nodes and sizes!\n" + this.indentedStr(0));
      }
      for (int i = 0; i < nodes.length; i++) {
        Node<T> n = nodes[i];
        if (n.height() != height) {
          throw new IllegalStateException("Unequal height!\n" + this.indentedStr(0));
        }
        sz += n.size();
        if (sz != cumulativeSizes[i]) {
          throw new IllegalStateException("Cumulative Sizes are wrong!\n" + this.indentedStr(0));
        }
      }
      return sz;
    }

    /** Returns the leftMost (first) or right-most (last) child */
    @Override public Node<T> endChild(boolean leftMost) {
      return nodes[leftMost ? 0 : nodes.length - 1];
    }

    /** Adds a node as the first/leftmost or last/rightmost child */
    @Override public Node<T> addEndChild(boolean leftMost, Node<T> shorter) {
      return insertInRelaxedAt(cumulativeSizes, nodes, shorter, leftMost ? 0 : nodes.length);
    }

    /** Adds kids as leftmost or rightmost of current children */
    @Override public Node<T> addEndChildren(boolean leftMost, Node<T>[] newKids) {
      @SuppressWarnings(value = { "unchecked" }) Node<T>[] res = spliceIntoArrayAt(newKids, nodes, leftMost ? 0 : nodes.length, Node.class);
      return new Relaxed<>(makeSizeArray(res), res);
    }

    @Override public int height() {
      return nodes[0].height() + 1;
    }

    @Override public int size() {
      return cumulativeSizes[cumulativeSizes.length - 1];
    }

    /**
         Converts the index of an item into the index of the sub-node containing that item.
         @param treeIndex The index of the item in the tree
         @return The index of the immediate child of this node that the desired node resides in.
         */
    private int subNodeIndex(int treeIndex) {
      int guess = (cumulativeSizes.length * treeIndex) / size();
      if (guess >= cumulativeSizes.length) {
        return cumulativeSizes.length - 1;
      }
      int guessedCumSize = cumulativeSizes[guess];
      if (guessedCumSize < treeIndex) {
        while (guess < (cumulativeSizes.length - 1)) {
          guessedCumSize = cumulativeSizes[++guess];
          if (guessedCumSize >= treeIndex) {
            return (guessedCumSize == treeIndex) ? guess + 1 : guess;
          }
        }
        throw new IllegalStateException("Can we get here?  If so, how?");
      } else {
        if (guessedCumSize > (treeIndex + MIN_NODE_LENGTH)) {
          while (guess > 0) {
            int nextGuess = guess - 1;
            guessedCumSize = cumulativeSizes[nextGuess];
            if (guessedCumSize <= treeIndex) {
              return guess;
            }
            guess = nextGuess;
          }
          return guess;
        } else {
          if (guessedCumSize == treeIndex) {
            return (treeIndex == size()) ? guess : guess + 1;
          } else {
            return guess;
          }
        }
      }
    }

    /**
         Converts the index of an item into the index to pass to the sub-node containing that item.
         @param index The index of the item in the entire tree
         @param subNodeIndex the index into this node's array of sub-nodes.
         @return The index to pass to the sub-branch the item resides in
         */
    private int subNodeAdjustedIndex(int index, int subNodeIndex) {
      return (subNodeIndex == 0) ? index : index - cumulativeSizes[subNodeIndex - 1];
    }

    @Override public T get(int index) {
      int subNodeIndex = subNodeIndex(index);
      return nodes[subNodeIndex].get(subNodeAdjustedIndex(index, subNodeIndex));
    }

    @Override public boolean thisNodeHasRelaxedCapacity(int numNodes) {
      return nodes.length + numNodes < MAX_NODE_LENGTH;
    }

    @Override public boolean hasStrictCapacity() {
      throw new UnsupportedOperationException("I don\'t think this should ever be called.");
    }

    @Override public boolean hasRelaxedCapacity(int index, int size) {
      if (thisNodeHasRelaxedCapacity(1)) {
        return true;
      }
      int subNodeIndex = subNodeIndex(index);
      return nodes[subNodeIndex].hasRelaxedCapacity(subNodeAdjustedIndex(index, subNodeIndex), size);
    }

    @SuppressWarnings(value = { "unchecked" }) Relaxed<T>[] split() {
      int midpoint = nodes.length >> 1;
      Relaxed<T> left = new Relaxed<>(Arrays.copyOf(cumulativeSizes, midpoint), Arrays.copyOf(nodes, midpoint));
      int[] rightCumSizes = new int[nodes.length - midpoint];
      int leftCumSizes = cumulativeSizes[midpoint - 1];
      for (int j = 0; j < rightCumSizes.length; j++) {
        rightCumSizes[j] = cumulativeSizes[midpoint + j] - leftCumSizes;
      }
      Relaxed<T> right = new Relaxed<>(rightCumSizes, Arrays.copyOfRange(nodes, midpoint, nodes.length));
      return new Relaxed[] { left, right };
    }

    @Override public SplitNode<T> splitAt(int splitIndex) {
      int size = size();
      if (splitIndex == 0) {
        return new SplitNode<>(emptyLeaf(), emptyArray(), emptyLeaf(), emptyArray());
      }
      if (splitIndex == size) {
        return new SplitNode<>(this, emptyArray(), emptyLeaf(), emptyArray());
      }
      int subNodeIndex = subNodeIndex(splitIndex);
      Node<T> subNode = nodes[subNodeIndex];
      if ((subNodeIndex > 0) && (splitIndex == cumulativeSizes[subNodeIndex - 1])) {
        Tuple2<Node<T>[], Node<T>[]> splitNodes = splitArray(nodes, subNodeIndex);
        int[][] splitCumSizes = splitArray(cumulativeSizes, subNodeIndex);
        int[] leftCumSizes = splitCumSizes[0];
        int[] rightCumSizes = splitCumSizes[1];
        int bias = leftCumSizes[leftCumSizes.length - 1];
        for (int i = 0; i < rightCumSizes.length; i++) {
          rightCumSizes[i] = rightCumSizes[i] - bias;
        }
        Node<T> left = new Relaxed<>(leftCumSizes, splitNodes._1());
        Node<T> right = new Relaxed<>(rightCumSizes, splitNodes._2());
        return new SplitNode<>(left, emptyArray(), right, emptyArray());
      }
      int subNodeAdjustedIndex = subNodeAdjustedIndex(splitIndex, subNodeIndex);
      SplitNode<T> split = subNode.splitAt(subNodeAdjustedIndex);
      final Node<T> left;
      Node<T> splitLeft = split.left();
      if (subNodeIndex == 0) {
        left = splitLeft;
      } else {
        boolean haveLeft = (splitLeft.size() > 0);
        int numLeftItems = subNodeIndex + (haveLeft ? 1 : 0);
        int[] leftCumSizes = new int[numLeftItems];
        Node<T>[] leftNodes = genericNodeArray(numLeftItems);
        System.arraycopy(cumulativeSizes, 0, leftCumSizes, 0, numLeftItems);
        if (haveLeft) {
          int cumulativeSize = (numLeftItems > 1) ? leftCumSizes[numLeftItems - 2] : 0;
          leftCumSizes[numLeftItems - 1] = cumulativeSize + splitLeft.size();
        }
        System.arraycopy(nodes, 0, leftNodes, 0, subNodeIndex);
        if (haveLeft) {
          while (splitLeft.height() < this.height() - 1) {
            splitLeft = addAncestor(splitLeft);
          }
          leftNodes[numLeftItems - 1] = splitLeft;
        }
        left = new Relaxed<>(leftCumSizes, leftNodes);
      }
      final Node<T> right = fixRight(nodes, split.right(), subNodeIndex);
      return new SplitNode<>(left, split.leftFocus(), right, split.rightFocus());
    }

    @Override public int numChildren() {
      return nodes.length;
    }

    @SuppressWarnings(value = { "unchecked" }) @Override public Node<T> pushFocus(int index, T[] oldFocus) {
      int subNodeIndex = subNodeIndex(index);
      Node<T> subNode = nodes[subNodeIndex];
      int subNodeAdjustedIndex = subNodeAdjustedIndex(index, subNodeIndex);
      if (subNode.hasRelaxedCapacity(subNodeAdjustedIndex, oldFocus.length)) {
        Node<T> newNode = subNode.pushFocus(subNodeAdjustedIndex, oldFocus);
        return replaceInRelaxedAt(cumulativeSizes, nodes, newNode, subNodeIndex, oldFocus.length);
      }
      if (!thisNodeHasRelaxedCapacity(1)) {
        Relaxed<T>[] split = split();
        int max1 = split[0].size();
        Relaxed<T> newRelaxed = new Relaxed<>(new int[] { max1, max1 + split[1].size() }, split);
        return newRelaxed.pushFocus(index, oldFocus);
      }
      if (subNode instanceof Leaf) {
        final Node<T>[] newNodes;
        final int[] newCumSizes;
        final int numToSkip;
        if ((oldFocus.length >= MIN_NODE_LENGTH) && (subNodeAdjustedIndex == 0 || subNodeAdjustedIndex == subNode.size())) {
          Leaf<T> newNode = new Leaf<>(oldFocus);
          if (subNodeAdjustedIndex != 0) {
            subNodeIndex++;
          }
          newNodes = insertIntoArrayAt(newNode, nodes, subNodeIndex, Node.class);
          newCumSizes = new int[cumulativeSizes.length + 1];
          int cumulativeSize = 0;
          if (subNodeIndex > 0) {
            System.arraycopy(cumulativeSizes, 0, newCumSizes, 0, subNodeIndex);
            cumulativeSize = newCumSizes[subNodeIndex - 1];
          }
          newCumSizes[subNodeIndex] = cumulativeSize + oldFocus.length;
          numToSkip = 1;
        } else {
          Leaf<T>[] res = ((Leaf<T>) subNode).spliceAndSplit(oldFocus, subNodeAdjustedIndex);
          Leaf<T> leftLeaf = res[0];
          Leaf<T> rightLeaf = res[1];
          newNodes = new Node[nodes.length + 1];
          newCumSizes = new int[cumulativeSizes.length + 1];
          int leftSize = 0;
          if (subNodeIndex > 0) {
            System.arraycopy(nodes, 0, newNodes, 0, subNodeIndex);
            System.arraycopy(cumulativeSizes, 0, newCumSizes, 0, subNodeIndex);
            leftSize = cumulativeSizes[subNodeIndex - 1];
          }
          newNodes[subNodeIndex] = leftLeaf;
          newNodes[subNodeIndex + 1] = rightLeaf;
          leftSize += leftLeaf.size();
          newCumSizes[subNodeIndex] = leftSize;
          newCumSizes[subNodeIndex + 1] = leftSize + rightLeaf.size();
          if (subNodeIndex < (nodes.length - 1)) {
            System.arraycopy(nodes, subNodeIndex + 1, newNodes, subNodeIndex + 2, nodes.length - subNodeIndex - 1);
          }
          numToSkip = 2;
        }
        for (int i = subNodeIndex + numToSkip; i < newCumSizes.length; i++) {
          newCumSizes[i] = cumulativeSizes[i - 1] + oldFocus.length;
        }
        return new Relaxed<>(newCumSizes, newNodes);
      } else {
        if (subNode instanceof Strict) {
          Relaxed<T> relaxed = ((Strict) subNode).relax();
          Node<T> newNode = relaxed.pushFocus(subNodeAdjustedIndex, oldFocus);
          return replaceInRelaxedAt(cumulativeSizes, nodes, newNode, subNodeIndex, oldFocus.length);
        }
      }
      Relaxed<T>[] newSubNode = ((Relaxed<T>) subNode).split();
      Relaxed<T> node1 = newSubNode[0];
      Relaxed<T> node2 = newSubNode[1];
      Node<T>[] newNodes = genericNodeArray(nodes.length + 1);
      if (subNodeIndex > 0) {
        System.arraycopy(nodes, 0, newNodes, 0, subNodeIndex);
      }
      newNodes[subNodeIndex] = node1;
      newNodes[subNodeIndex + 1] = node2;
      if (subNodeIndex < nodes.length) {
        System.arraycopy(nodes, subNodeIndex + 1, newNodes, subNodeIndex + 2, nodes.length - subNodeIndex - 1);
      }
      int[] newCumSizes = new int[cumulativeSizes.length + 1];
      int cumulativeSize = 0;
      if (subNodeIndex > 0) {
        System.arraycopy(cumulativeSizes, 0, newCumSizes, 0, subNodeIndex);
        cumulativeSize = cumulativeSizes[subNodeIndex - 1];
      }
      for (int i = subNodeIndex; i < newCumSizes.length; i++) {
        cumulativeSize += newNodes[i].size();
        newCumSizes[i] = cumulativeSize;
      }
      Relaxed<T> newRelaxed = new Relaxed<>(newCumSizes, newNodes);
      return newRelaxed.pushFocus(index, oldFocus);
    }

    @SuppressWarnings(value = { "unchecked" }) @Override public Node<T> replace(int index, T t) {
      int subNodeIndex = subNodeIndex(index);
      Node<T> alteredNode = nodes[subNodeIndex].replace(subNodeAdjustedIndex(index, subNodeIndex), t);
      Node<T>[] newNodes = replaceInArrayAt(alteredNode, nodes, subNodeIndex, Node.class);
      return new Relaxed<>(cumulativeSizes, newNodes);
    }

    @Override public String indentedStr(int indent) {
      StringBuilder sB = new StringBuilder().append("Relaxed(");
      int nextIndent = indent + sB.length();
      sB.append("cumulativeSizes=").append(arrayString(cumulativeSizes)).append("\n").append(indentSpace(nextIndent)).append("nodes=[");
      return showSubNodes(sB, nodes, nextIndent + 7).append("])").toString();
    }

    @Override public String toString() {
      return indentedStr(0);
    }

    /**
         This asks each node what size it is, then puts the cumulative sizes into a new array.
         In theory, it might be faster to figure out what side we added/removed nodes and do
         some addition/subtraction (in the amount of the added/removed nodes).  But I want to
         optimize other things first and be sure everything is correct before experimenting with
         that.  After all, it might not even be faster!
         @param newNodes the nodes to take sizes from.
         @return An array of cumulative sizes of each node in the passed array.
         */
    private static int[] makeSizeArray(Node[] newNodes) {
      int[] newCumSizes = new int[newNodes.length];
      int cumulativeSize = 0;
      for (int i = 0; i < newCumSizes.length; i++) {
        cumulativeSize += newNodes[i].size();
        newCumSizes[i] = cumulativeSize;
      }
      return newCumSizes;
    }

    /**
         Replace a node in a relaxed node by recalculating the cumulative sizes and copying
         all sub nodes.
         @param is original cumulative sizes
         @param ns original nodes
         @param newNode replacement node
         @param subNodeIndex index to replace in this node's immediate children
         @param insertSize the difference in size between the original node and the new node.
         @return a new immutable Relaxed node with the immediate child node replaced.
         */
    static <T extends java.lang.Object> Relaxed<T> replaceInRelaxedAt(int[] is, Node<T>[] ns, Node<T> newNode, int subNodeIndex, int insertSize) {
      @SuppressWarnings(value = { "unchecked" }) Node<T>[] newNodes = replaceInArrayAt(newNode, ns, subNodeIndex, Node.class);
      int[] newCumSizes = new int[is.length];
      if (subNodeIndex > 0) {
        System.arraycopy(is, 0, newCumSizes, 0, subNodeIndex);
      }
      for (int i = subNodeIndex; i < is.length; i++) {
        newCumSizes[i] = is[i] + insertSize;
      }
      return new Relaxed<>(newCumSizes, newNodes);
    }

    /**
         Insert a node in a relaxed node by recalculating the cumulative sizes and copying
         all sub nodes.
         @param oldCumSizes original cumulative sizes
         @param ns original nodes
         @param newNode replacement node
         @param subNodeIndex index to insert in this node's immediate children
         @return a new immutable Relaxed node with the immediate child node inserted.
         */
    static <T extends java.lang.Object> Relaxed<T> insertInRelaxedAt(int[] oldCumSizes, Node<T>[] ns, Node<T> newNode, int subNodeIndex) {
      @SuppressWarnings(value = { "unchecked" }) Node<T>[] newNodes = insertIntoArrayAt(newNode, ns, subNodeIndex, Node.class);
      int oldLen = oldCumSizes.length;
      int[] newCumSizes = new int[oldLen + 1];
      if (subNodeIndex > 0) {
        System.arraycopy(oldCumSizes, 0, newCumSizes, 0, subNodeIndex);
      }
      int newNodeSize = newNode.size();
      int prevNodeTotal = (subNodeIndex == 0) ? 0 : oldCumSizes[subNodeIndex - 1];
      newCumSizes[subNodeIndex] = newNodeSize + prevNodeTotal;
      for (int i = subNodeIndex; i < oldCumSizes.length; i++) {
        newCumSizes[i + 1] = oldCumSizes[i] + newNodeSize;
      }
      return new Relaxed<>(newCumSizes, newNodes);
    }

    /**
         Fixes up nodes on the right-hand side of the split.  Might want to explain this better...

         @param origNodes the immediate children of the node we're splitting
         @param splitRight the pre-split right node
         @param subNodeIndex the index to split children at?
         @return a copy of this node with only the right-hand side of the split.
         */
    @SuppressWarnings(value = { "unchecked" }) public static <T extends java.lang.Object> Node<T> fixRight(Node<T>[] origNodes, Node<T> splitRight, int subNodeIndex) {
      Node<T> right;
      if (subNodeIndex == (origNodes.length - 1)) {
        right = new Relaxed<>(new int[] { splitRight.size() }, new Node[] { splitRight });
      } else {
        boolean haveRightSubNode = splitRight.size() > 0;
        int numRightNodes = (origNodes.length - subNodeIndex) - (haveRightSubNode ? 0 : 1);
        int[] rightCumSizes = new int[numRightNodes];
        Node<T>[] rightNodes = genericNodeArray(numRightNodes);
        int cumulativeSize = 0;
        int destCopyStartIdx = 0;
        if (haveRightSubNode) {
          System.arraycopy(origNodes, subNodeIndex + 1, rightNodes, 1, numRightNodes - 1);
          rightNodes[0] = splitRight;
          cumulativeSize = splitRight.size();
          rightCumSizes[0] = cumulativeSize;
          destCopyStartIdx = 1;
        } else {
          System.arraycopy(origNodes, subNodeIndex + 1, rightNodes, 0, numRightNodes);
        }
        for (int i = destCopyStartIdx; i < numRightNodes; i++) {
          cumulativeSize += rightNodes[i].size();
          rightCumSizes[i] = cumulativeSize;
        }
        right = new Relaxed<>(rightCumSizes, rightNodes);
      }
      return right;
    }
  }

  private static final class IdxNode<E extends java.lang.Object> {
    int idx = 0;

    final Node<E> node;

    IdxNode(Node<E> n) {
      node = n;
    }

    public boolean hasNext() {
      return idx < node.numChildren();
    }

    public Node<E> next() {
      return node.child(idx++);
    }
  }

  final class Iter implements UnmodSortedIterator<E> {
    private final IdxNode<E>[] stack;

    private int stackMaxIdx = -1;

    private E[] leafArray = emptyArray();

    private int leafArrayIdx;

    @SuppressWarnings(value = { "unchecked" }) private Iter(Node<E> root) {
      stack = (IdxNode<E>[]) new IdxNode<?>[root.height()];
      leafArray = findLeaf(root);
    }

    private E[] findLeaf(Node<E> node) {
      while (!(node instanceof Leaf)) {
        IdxNode<E> in = new IdxNode<>(node);
        stack[++stackMaxIdx] = in;
        node = in.next();
      }
      return ((Leaf<E>) node).items;
    }

    private E[] nextLeafArray() {
      while ((stackMaxIdx > -1) && !stack[stackMaxIdx].hasNext()) {
        stackMaxIdx--;
      }
      if (stackMaxIdx < 0) {
        return emptyArray();
      }
      return findLeaf(stack[stackMaxIdx].next());
    }

    @Override public boolean hasNext() {
      if (leafArrayIdx < leafArray.length) {
        return true;
      }
      leafArray = nextLeafArray();
      leafArrayIdx = 0;
      return leafArray.length > 0;
    }

    @Override public E next() {
      if (leafArrayIdx >= leafArray.length) {
        leafArray = nextLeafArray();
        leafArrayIdx = 0;
      }
      return leafArray[leafArrayIdx++];
    }
  }

  @SuppressWarnings(value = { "unchecked" }) private static <T extends java.lang.Object> Node<T>[] genericNodeArray(int size) {
    return (Node<T>[]) new Node<?>[size];
  }

  private static StringBuilder showSubNodes(StringBuilder sB, Object[] items, int nextIndent) {
    boolean isFirst = true;
    for (Object n : items) {
      if (isFirst) {
        isFirst = false;
      } else {
        if (items[0] instanceof Leaf) {
          sB.append(" ");
        } else {
          sB.append("\n").append(indentSpace(nextIndent));
        }
      }
      sB.append(((Node) n).indentedStr(nextIndent));
    }
    return sB;
  }
}