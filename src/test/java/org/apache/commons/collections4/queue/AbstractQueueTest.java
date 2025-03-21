package org.apache.commons.collections4.queue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import org.apache.commons.collections4.collection.AbstractCollectionTest;

/**
 * Abstract test class for {@link java.util.Queue} methods and contracts.
 * <p>
 * To use, simply extend this class, and implement
 * the {@link #makeObject} method.
 * <p>
 * If your {@link Queue} fails one of these tests by design,
 * you may still use this base set of cases.  Simply override the
 * test case (method) your {@link Queue} fails or override one of the
 * protected methods from AbstractCollectionTest.
 *
 * @since 4.0
 */
public abstract class AbstractQueueTest<E extends java.lang.Object> extends AbstractCollectionTest<E> {
  /**
     * JUnit constructor.
     *
     * @param testName  the test class name
     */
  public AbstractQueueTest(final String testName) {
    super(testName);
  }

  /**
     *  Returns true if the collections produced by
     *  {@link #makeObject()} and {@link #makeFullCollection()}
     *  support the <code>set operation.<p>
     *  Default implementation returns true.  Override if your collection
     *  class does not support set.
     */
  public boolean isSetSupported() {
    return true;
  }

  /**
     *  Verifies that the test queue implementation matches the confirmed queue
     *  implementation.
     */
  @Override public void verify() {
    super.verify();
    final Iterator<E> iterator1 = getCollection().iterator();
    for (final E e : getConfirmed()) {
      assertTrue(iterator1.hasNext());
      final Object o1 = iterator1.next();
      final Object o2 = e;
      assertEquals(o1, o2);
    }
  }

  /**
     * Returns an empty {@link ArrayList}.
     */
  @Override public Collection<E> makeConfirmedCollection() {
    final ArrayList<E> list = new ArrayList<>();
    return list;
  }

  /**
     * Returns a full {@link ArrayList}.
     */
  @Override public Collection<E> makeConfirmedFullCollection() {
    final ArrayList<E> list = new ArrayList<>();
    list.addAll(Arrays.asList(getFullElements()));
    return list;
  }

  /**
     * Returns {@link #makeObject()}.
     *
     * @return an empty queue to be used for testing
     */
  @Override public abstract Queue<E> makeObject();

  /**
     * {@inheritDoc}
     */
  @Override public Queue<E> makeFullCollection() {
    final Queue<E> queue = makeObject();
    queue.addAll(Arrays.asList(getFullElements()));
    return queue;
  }

  /**
     * Returns the {@link #collection} field cast to a {@link Queue}.
     *
     * @return the collection field as a Queue
     */
  @Override public Queue<E> getCollection() {
    return (Queue<E>) super.getCollection();
  }

  /**
     *  Tests {@link Queue#offer(Object)}.
     */
  public void testQueueOffer() {
    if (!isAddSupported()) {
      return;
    }
    final E[] elements = getFullElements();
    for (final E element : elements) {
      resetEmpty();
      final boolean r = getCollection().offer(element);
      getConfirmed().add(element);
      verify();
      assertTrue("Empty queue changed after add", r);
      assertEquals("Queue size is 1 after first add", 1, getCollection().size());
    }
    resetEmpty();
    int size = 0;
    for (final E element : elements) {
      final boolean r = getCollection().offer(element);
      getConfirmed().add(element);
      verify();
      if (r) {
        size++;
      }
      assertEquals("Queue size should grow after add", size, getCollection().size());
      assertTrue("Queue should contain added element", getCollection().contains(element));
    }
  }

  /**
     *  Tests {@link Queue#element()}.
     */
  public void testQueueElement() {
    resetEmpty();
    Exception exception = assertThrows(NoSuchElementException.class, () -> {
      getCollection().element();
    });
    if (null != exception.getMessage()) {
      assertTrue(exception.getMessage().contains("queue is empty"));
    }
    resetFull();
    assertTrue(getConfirmed().contains(getCollection().element()));
    if (!isRemoveSupported()) {
      return;
    }
    final int max = getFullElements().length;
    for (int i = 0; i < max; i++) {
      final E element = getCollection().element();
      if (!isNullSupported()) {
        assertNotNull(element);
      }
      assertTrue(getConfirmed().contains(element));
      getCollection().remove(element);
      getConfirmed().remove(element);
      verify();
    }
    exception = assertThrows(NoSuchElementException.class, () -> {
      getCollection().element();
    });
    if (null != exception.getMessage()) {
      assertTrue(exception.getMessage().contains("queue is empty"));
    }
  }

  /**
     *  Tests {@link Queue#peek()}.
     */
  public void testQueuePeek() {
    if (!isRemoveSupported()) {
      return;
    }
    resetEmpty();
    E element = getCollection().peek();
    assertNull(element);
    resetFull();
    final int max = getFullElements().length;
    for (int i = 0; i < max; i++) {
      element = getCollection().peek();
      if (!isNullSupported()) {
        assertNotNull(element);
      }
      assertTrue(getConfirmed().contains(element));
      getCollection().remove(element);
      getConfirmed().remove(element);
      verify();
    }
    element = getCollection().peek();
    assertNull(element);
  }

  /**
     *  Tests {@link Queue#remove()}.
     */
  public void testQueueRemove() {
    if (!isRemoveSupported()) {
      return;
    }
    resetEmpty();
    Exception exception = assertThrows(NoSuchElementException.class, () -> {
      getCollection().remove();
    });
    if (null != exception.getMessage()) {
      assertTrue(exception.getMessage().contains("queue is empty"));
    }
    resetFull();
    final int max = getFullElements().length;
    for (int i = 0; i < max; i++) {
      final E element = getCollection().remove();
      final boolean success = getConfirmed().remove(element);
      assertTrue("remove should return correct element", success);
      verify();
    }
    exception = assertThrows(NoSuchElementException.class, () -> {
      getCollection().element();
    });
    if (null != exception.getMessage()) {
      assertTrue(exception.getMessage().contains("queue is empty"));
    }
  }

  /**
     *  Tests {@link Queue#poll()}.
     */
  public void testQueuePoll() {
    if (!isRemoveSupported()) {
      return;
    }
    resetEmpty();
    E element = getCollection().poll();
    assertNull(element);
    resetFull();
    final int max = getFullElements().length;
    for (int i = 0; i < max; i++) {
      element = getCollection().poll();
      final boolean success = getConfirmed().remove(element);
      assertTrue("poll should return correct element", success);
      verify();
    }
    element = getCollection().poll();
    assertNull(element);
  }

  @SuppressWarnings(value = { "unchecked" }) public void testEmptyQueueSerialization() throws IOException, ClassNotFoundException {
    final Queue<E> queue = makeObject();
    if (!(queue instanceof Serializable && isTestSerialization())) {
      return;
    }
    final byte[] object = writeExternalFormToBytes((Serializable) queue);
    final Queue<E> queue2 = (Queue<E>) readExternalFormFromBytes(object);
    assertEquals("Both queues are empty", 0, queue.size());
    assertEquals("Both queues are empty", 0, queue2.size());
  }

  @SuppressWarnings(value = { "unchecked" }) public void testFullQueueSerialization() throws IOException, ClassNotFoundException {
    final Queue<E> queue = makeFullCollection();
    final int size = getFullElements().length;
    if (!(queue instanceof Serializable && isTestSerialization())) {
      return;
    }
    final byte[] object = writeExternalFormToBytes((Serializable) queue);
    final Queue<E> queue2 = (Queue<E>) readExternalFormFromBytes(object);
    assertEquals("Both queues are same size", size, queue.size());
    assertEquals("Both queues are same size", size, queue2.size());
  }

  /**
     * Compare the current serialized form of the Queue
     * against the canonical version in SCM.
     */
  @SuppressWarnings(value = { "unchecked" }) public void testEmptyQueueCompatibility() throws IOException, ClassNotFoundException {
    final Queue<E> queue = makeObject();
    if (queue instanceof Serializable && !skipSerializedCanonicalTests() && isTestSerialization()) {
      final Queue<E> queue2 = (Queue<E>) readExternalFormFromDisk(getCanonicalEmptyCollectionName(queue));
      assertEquals("Queue is empty", 0, queue2.size());
    }
  }

  /**
     * Compare the current serialized form of the Queue
     * against the canonical version in SCM.
     */
  @SuppressWarnings(value = { "unchecked" }) public void testFullQueueCompatibility() throws IOException, ClassNotFoundException {
    final Queue<E> queue = makeFullCollection();
    if (queue instanceof Serializable && !skipSerializedCanonicalTests() && isTestSerialization()) {
      final Queue<E> queue2 = (Queue<E>) readExternalFormFromDisk(getCanonicalFullCollectionName(queue));
      assertEquals("Queues are not the right size", queue.size(), queue2.size());
    }
  }
}