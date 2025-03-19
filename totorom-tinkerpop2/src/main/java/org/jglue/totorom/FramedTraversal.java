package org.jglue.totorom;
import java.util.Iterator;
import java.util.List;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Predicate;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.Tokens;
import com.tinkerpop.pipes.PipeFunction;


<<<<<<< /usr/src/app/output/syncleus/ferma/b273f174b303afbba124b948e959de1f0b4ea1d4/totorom-tinkerpop2/src/main/java/org/jglue/totorom/FramedTraversal.java/left.java
public interface FramedTraversal<S extends java.lang.Object, E extends java.lang.Object> {
  public FramedVertexTraversal<S, E> V();

  public FramedEdgeTraversal<S, E> E();

  /**
	 * Traversal over a of vertices in the graph.
	 * 
	 * @param ids
	 *            The ids of the vertices.
	 * @return The traversal.
	 */
  public FramedVertexTraversal<S, E> v(Object... ids);

  /**
	 * Traversal over a list of edges in the graph.
	 * 
	 * @param ids
	 *            The ids of the edges.
	 * @return The traversal.
	 */
  public FramedEdgeTraversal<S, E> e(Object... ids);

  /**
	 * Add an identity step.
	 * 
	 * @return The traversal.
	 */
  public FramedTraversal<S, E> identity();

  public long count();
}
=======
/**
 * @author Bryn Cooke (http://jglue.org)
 * 
 * @param <S>
 * @param <E>
 */
public class FramedTraversal<S extends java.lang.Object, E extends java.lang.Object> extends GremlinPipeline<S, E> {
  private FramedGraph graph;

  protected FramedTraversal(FramedGraph graph, Graph delegate) {
    super(delegate, true);
    this.graph = graph;
  }

  protected FramedTraversal(FramedGraph graph, Iterator<S> starts) {
    super(starts, true);
    this.graph = graph;
  }

  protected FramedTraversal(FramedGraph graph, S starts) {
    super(starts, true);
    this.graph = graph;
  }

  @Override public FramedTraversal<S, Vertex> V() {
    return (FramedTraversal<S, Vertex>) super.V();
  }

  @Override public FramedTraversal<S, Edge> E() {
    return (FramedTraversal<S, Edge>) super.E();
  }

  @Override public FramedTraversal<S, E> _() {
    return (FramedTraversal<S, E>) super._();
  }

  /**
	 * Traversal over a of vertices in the graph.
	 * 
	 * @param ids
	 *            The ids of the vertices.
	 * @return The traversal.
	 */
  public FramedTraversal<Vertex, Vertex> v(final Object... ids) {
    return graph.v(ids);
  }

  /**
	 * Traversal over a list of edges in the graph.
	 * 
	 * @param ids
	 *            The ids of the edges.
	 * @return The traversal.
	 */
  public FramedTraversal<Edge, Edge> e(final Object... ids) {
    return graph.e(ids);
  }

  @Override public FramedTraversal<S, ? extends Element> has(final String key) {
    return (FramedTraversal<S, ? extends Element>) super.has(key);
  }

  @Override public FramedTraversal<S, ? extends Element> has(final String key, final Object value) {
    return (FramedTraversal<S, ? extends Element>) super.has(key, value);
  }

  @Override public FramedTraversal<S, ? extends Element> has(final String key, final Tokens.T compareToken, final Object value) {
    return (FramedTraversal<S, ? extends Element>) super.has(key, compareToken, value);
  }

  @Override public FramedTraversal<S, ? extends Element> has(final String key, final Predicate predicate, final Object value) {
    return (FramedTraversal<S, ? extends Element>) super.has(key, predicate, value);
  }

  @Override public FramedTraversal<S, ? extends Element> hasNot(final String key) {
    return (FramedTraversal<S, ? extends Element>) super.hasNot(key);
  }

  @Override public FramedTraversal<S, ? extends Element> hasNot(final String key, final Object value) {
    return (FramedTraversal<S, ? extends Element>) super.hasNot(key, value);
  }

  @SuppressWarnings(value = { "rawtypes" }) public FramedTraversal<S, ? extends Element> interval(final String key, final Comparable startValue, final Comparable endValue) {
    return (FramedTraversal<S, ? extends Element>) super.interval(key, startValue, endValue);
  }

  /**
	 * Add an identity step.
	 * 
	 * @return The traversal.
	 */
  public FramedTraversal<S, E> identity() {
    return (FramedTraversal<S, E>) super._();
  }

  @Override public FramedTraversal<S, Vertex> out(final int branchFactor, final String... labels) {
    return (FramedTraversal<S, Vertex>) super.out(branchFactor, labels);
  }

  @Override public FramedTraversal<S, Vertex> out(final String... labels) {
    return (FramedTraversal<S, Vertex>) super.out(labels);
  }

  @Override public FramedTraversal<S, Vertex> in(final int branchFactor, final String... labels) {
    return (FramedTraversal<S, Vertex>) super.in(branchFactor, labels);
  }

  @Override public FramedTraversal<S, Vertex> in(final String... labels) {
    return (FramedTraversal<S, Vertex>) super.in(labels);
  }

  @Override public FramedTraversal<S, Vertex> both(final int branchFactor, final String... labels) {
    return (FramedTraversal<S, Vertex>) super.both(branchFactor, labels);
  }

  @Override public FramedTraversal<S, Vertex> both(final String... labels) {
    return (FramedTraversal<S, Vertex>) super.both(labels);
  }

  @Override public FramedTraversal<S, Edge> outE(final int branchFactor, final String... labels) {
    return (FramedTraversal<S, Edge>) super.outE(branchFactor, labels);
  }

  @Override public FramedTraversal<S, Edge> outE(final String... labels) {
    return (FramedTraversal<S, Edge>) super.outE(labels);
  }

  @Override public FramedTraversal<S, Edge> inE(final int branchFactor, final String... labels) {
    return (FramedTraversal<S, Edge>) super.inE(branchFactor, labels);
  }

  @Override public FramedTraversal<S, Edge> inE(final String... labels) {
    return (FramedTraversal<S, Edge>) super.inE(labels);
  }

  @Override public FramedTraversal<S, Edge> bothE(final int branchFactor, final String... labels) {
    return (FramedTraversal<S, Edge>) super.bothE(branchFactor, labels);
  }

  @Override public FramedTraversal<S, Edge> bothE(final String... labels) {
    return (FramedTraversal<S, Edge>) super.bothE(labels);
  }

  @Override public FramedTraversal<S, Vertex> inV() {
    return (FramedTraversal<S, Vertex>) super.inV();
  }

  @Override public FramedTraversal<S, Vertex> outV() {
    return (FramedTraversal<S, Vertex>) super.outV();
  }

  @Override public FramedTraversal<S, Vertex> bothV() {
    return (FramedTraversal<S, Vertex>) super.bothV();
  }

  @Override public FramedTraversal filter(PipeFunction<E, Boolean> pipeFunction) {
    return new FramedTraversal<>(graph, super.filter(pipeFunction).getStarts());
  }

  /**
	 * Get the next object emitted from the pipeline. If no such object exists,
	 * then a NoSuchElementException is thrown.
	 * 
	 * @param kind
	 *            The type of frame for the element.
	 * @return the next emitted object
	 */
  public <T extends FramedElement<?>> T next(Class<T> kind) {
    return graph.frameElement((Element) super.next(), kind);
  }

  /**
	 * Return the next X objects in the traversal as a list.
	 * 
	 * @param number
	 *            the number of objects to return
	 * @param kind
	 *            the type of frame to for each element.
	 * @return a list of X objects (if X objects occur)
	 */
  public <T extends FramedElement<?>> List<T> next(int amount, final Class<T> kind) {
    return Lists.transform(super.next(amount), new Function<E, T>() {
      @Override public T apply(E e) {
        return graph.frameElement((Element) e, kind);
      }
    });
  }

  /**
	 * Return an iterator of framed elements.
	 * 
	 * @param kind
	 *            The kind of framed elements to return.
	 * @return An iterator of framed elements.
	 */
  @SuppressWarnings(value = { "unchecked" }) public <T extends FramedElement<?>> Iterator<T> frame(Class<T> kind) {
    return graph.frame((Iterator<Element>) this, kind);
  }

  /**
	 * Return a list of all the objects in the pipeline.
	 * 
	 * @param kind
	 *            The kind of framed elements to return.
	 * @return a list of all the objects
	 */
  public <T extends FramedElement<?>> List<T> toList(final Class<T> kind) {
    return Lists.transform(super.toList(), new Function<E, T>() {
      @Override public T apply(E e) {
        return graph.frameElement((Element) e, kind);
      }
    });
  }
}
>>>>>>> /usr/src/app/output/syncleus/ferma/b273f174b303afbba124b948e959de1f0b4ea1d4/totorom-tinkerpop2/src/main/java/org/jglue/totorom/FramedTraversal.java/right.java
