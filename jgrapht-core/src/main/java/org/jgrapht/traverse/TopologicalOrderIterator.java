/*
 * (C) Copyright 2004-2017, by Marden Neubert and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package org.jgrapht.traverse;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.util.*;

/**
 * A topological ordering iterator for a directed acyclic graph.
 * 
 * <p>
 * A topological order is a permutation <tt>p</tt> of the vertices of a graph such that an edge
 * <tt>(i,j)</tt> implies that <tt>i</tt> appears before <tt>j</tt> in <tt>p</tt>. For more
 * information see <a href="https://en.wikipedia.org/wiki/Topological_sorting">wikipedia</a> or
 * <a href="http://mathworld.wolfram.com/TopologicalSort.html">wolfram</a>.
 *
 * <p>
 * The iterator crosses components but does not track them, it only tracks visited vertices. The
 * iterator will detect (at some point) if the graph is not a directed acyclic graph and throw a
 * {@link IllegalArgumentException}.
 * 
 * <p>
 * For this iterator to work correctly the graph must not be modified during iteration. Currently
 * there are no means to ensure that, nor to fail-fast. The results of such modifications are
 * undefined.
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Marden Neubert
 * @author Dimitrios Michail
 * @since December 2004
 */
public class TopologicalOrderIterator<V, E>
    extends AbstractGraphIterator<V, E>
{
    private static final String GRAPH_IS_NOT_A_DAG = "Graph is not a DAG";

    private Queue<V> queue;
    private Map<V, ModifiableInteger> inDegreeMap;
    private int remainingVertices;
    private V cur;

    /**
     * Construct a topological order iterator.
     * 
     * <p>
     * Traversal will start at one of the graph's <i>sources</i>. See the definition of source at
     * <a href="http://mathworld.wolfram.com/Source.html">
     * http://mathworld.wolfram.com/Source.html</a>. In case of partial order, tie-breaking is
     * arbitrary.
     *
     * @param graph the directed graph to be iterated
     */
<<<<<<< /usr/src/app/output/jgrapht/jgrapht/d7d129c0f6880a29fc0e593f1f1c10226ef22182/jgrapht-core/src/main/java/org/jgrapht/traverse/TopologicalOrderIterator.java/left.java
    public TopologicalOrderIterator(DirectedGraph<V, E> graph)
||||||| /usr/src/app/output/jgrapht/jgrapht/d7d129c0f6880a29fc0e593f1f1c10226ef22182/jgrapht-core/src/main/java/org/jgrapht/traverse/TopologicalOrderIterator.java/base.java
    public TopologicalOrderIterator(DirectedGraph<V, E> dg)
=======
    public TopologicalOrderIterator(Graph<V, E> dg)
>>>>>>> /usr/src/app/output/jgrapht/jgrapht/d7d129c0f6880a29fc0e593f1f1c10226ef22182/jgrapht-core/src/main/java/org/jgrapht/traverse/TopologicalOrderIterator.java/right.java
    {
        this(graph, (Comparator<V>) null);
    }

    /**
     * Construct a topological order iterator.
     * 
     * Creates a new topological order iterator over the directed graph specified, with a
     * user-supplied queue implementation to allow customized control over tie-breaking in case of
     * partial order. Traversal will start at one of the graph's <i>sources</i>. See the definition
     * of source at <a href="http://mathworld.wolfram.com/Source.html">
     * http://mathworld.wolfram.com/Source.html</a>.
     *
     * @param graph the directed graph to be iterated.
     * @param queue queue to use for tie-break in case of partial order (e.g. a PriorityQueue can be
     *        used to break ties according to vertex priority); must be initially empty
     * @deprecated in favor of {@link #TopologicalOrderIterator(DirectedGraph, Comparator)}
     */
<<<<<<< /usr/src/app/output/jgrapht/jgrapht/d7d129c0f6880a29fc0e593f1f1c10226ef22182/jgrapht-core/src/main/java/org/jgrapht/traverse/TopologicalOrderIterator.java/left.java
    @Deprecated
    public TopologicalOrderIterator(DirectedGraph<V, E> graph, Queue<V> queue)
||||||| /usr/src/app/output/jgrapht/jgrapht/d7d129c0f6880a29fc0e593f1f1c10226ef22182/jgrapht-core/src/main/java/org/jgrapht/traverse/TopologicalOrderIterator.java/base.java
    public TopologicalOrderIterator(DirectedGraph<V, E> dg, Queue<V> queue)
=======
    public TopologicalOrderIterator(Graph<V, E> dg, Queue<V> queue)
>>>>>>> /usr/src/app/output/jgrapht/jgrapht/d7d129c0f6880a29fc0e593f1f1c10226ef22182/jgrapht-core/src/main/java/org/jgrapht/traverse/TopologicalOrderIterator.java/right.java
    {
        super(graph);

<<<<<<< /usr/src/app/output/jgrapht/jgrapht/d7d129c0f6880a29fc0e593f1f1c10226ef22182/jgrapht-core/src/main/java/org/jgrapht/traverse/TopologicalOrderIterator.java/left.java
        this.queue = Objects.requireNonNull(queue, "Queue must not be null");
        if (!queue.isEmpty()) {
            throw new IllegalArgumentException("Queue must be empty");
||||||| /usr/src/app/output/jgrapht/jgrapht/d7d129c0f6880a29fc0e593f1f1c10226ef22182/jgrapht-core/src/main/java/org/jgrapht/traverse/TopologicalOrderIterator.java/base.java
    // NOTE: This is a hack to deal with the fact that CrossComponentIterator
    // needs to know the start vertex in its constructor
    private TopologicalOrderIterator(
        DirectedGraph<V, E> dg, Queue<V> queue, Map<V, ModifiableInteger> inDegreeMap)
    {
        this(dg, initialize(dg, queue, inDegreeMap));
        this.queue = queue;
        this.inDegreeMap = inDegreeMap;

        // empty queue for non-empty graph would indicate presence of
        // cycles (no roots found)
        assert dg.vertexSet().isEmpty() || !queue.isEmpty();
    }

    // NOTE: This is intentionally private, because starting the sort "in the
    // middle" doesn't make sense.
    private TopologicalOrderIterator(DirectedGraph<V, E> dg, V start)
    {
        super(dg, start);
    }

    /**
     * @see CrossComponentIterator#isConnectedComponentExhausted()
     */
    @Override
    protected boolean isConnectedComponentExhausted()
    {
        // FIXME jvs 25-Apr-2005: This isn't correct for a graph with more than
        // one component. We will actually exhaust a connected component
        // before the queue is empty, because initialize adds roots from all
        // components to the queue.
        return queue.isEmpty();
    }

    /**
     * @see CrossComponentIterator#encounterVertex(Object, Object)
     */
    @Override
    protected void encounterVertex(V vertex, E edge)
    {
        putSeenData(vertex, null);
        decrementInDegree(vertex);
    }

    /**
     * @see CrossComponentIterator#encounterVertexAgain(Object, Object)
     */
    @Override
    protected void encounterVertexAgain(V vertex, E edge)
    {
        decrementInDegree(vertex);
    }

    /**
     * @see CrossComponentIterator#provideNextVertex()
     */
    @Override
    protected V provideNextVertex()
    {
        return queue.remove();
    }

    /**
     * Decrements the in-degree of a vertex.
     *
     * @param vertex the vertex whose in-degree will be decremented.
     */
    private void decrementInDegree(V vertex)
    {
        ModifiableInteger inDegree = inDegreeMap.get(vertex);

        if (inDegree.value > 0) {
            inDegree.value--;

            if (inDegree.value == 0) {
                queue.offer(vertex);
            }
=======
    // NOTE: This is a hack to deal with the fact that CrossComponentIterator
    // needs to know the start vertex in its constructor
    private TopologicalOrderIterator(
        Graph<V, E> dg, Queue<V> queue, Map<V, ModifiableInteger> inDegreeMap)
    {
        this(dg, initialize(dg, queue, inDegreeMap));
        this.queue = queue;
        this.inDegreeMap = inDegreeMap;

        // empty queue for non-empty graph would indicate presence of
        // cycles (no roots found)
        assert dg.vertexSet().isEmpty() || !queue.isEmpty();
    }

    // NOTE: This is intentionally private, because starting the sort "in the
    // middle" doesn't make sense.
    private TopologicalOrderIterator(Graph<V, E> dg, V start)
    {
        super(dg, start);
    }

    /**
     * @see CrossComponentIterator#isConnectedComponentExhausted()
     */
    @Override
    protected boolean isConnectedComponentExhausted()
    {
        // FIXME jvs 25-Apr-2005: This isn't correct for a graph with more than
        // one component. We will actually exhaust a connected component
        // before the queue is empty, because initialize adds roots from all
        // components to the queue.
        return queue.isEmpty();
    }

    /**
     * @see CrossComponentIterator#encounterVertex(Object, Object)
     */
    @Override
    protected void encounterVertex(V vertex, E edge)
    {
        putSeenData(vertex, null);
        decrementInDegree(vertex);
    }

    /**
     * @see CrossComponentIterator#encounterVertexAgain(Object, Object)
     */
    @Override
    protected void encounterVertexAgain(V vertex, E edge)
    {
        decrementInDegree(vertex);
    }

    /**
     * @see CrossComponentIterator#provideNextVertex()
     */
    @Override
    protected V provideNextVertex()
    {
        return queue.remove();
    }

    /**
     * Decrements the in-degree of a vertex.
     *
     * @param vertex the vertex whose in-degree will be decremented.
     */
    private void decrementInDegree(V vertex)
    {
        ModifiableInteger inDegree = inDegreeMap.get(vertex);

        if (inDegree.value > 0) {
            inDegree.value--;

            if (inDegree.value == 0) {
                queue.offer(vertex);
            }
>>>>>>> /usr/src/app/output/jgrapht/jgrapht/d7d129c0f6880a29fc0e593f1f1c10226ef22182/jgrapht-core/src/main/java/org/jgrapht/traverse/TopologicalOrderIterator.java/right.java
        }

<<<<<<< /usr/src/app/output/jgrapht/jgrapht/d7d129c0f6880a29fc0e593f1f1c10226ef22182/jgrapht-core/src/main/java/org/jgrapht/traverse/TopologicalOrderIterator.java/left.java
        // count in-degrees
        this.inDegreeMap = new HashMap<>();
        for (V v : graph.vertexSet()) {
            int d = 0;
            for (E e : specifics.incomingEdgesOf(v)) {
                V u = Graphs.getOppositeVertex(graph, e, v);
                if (v.equals(u)) {
                    throw new IllegalArgumentException(GRAPH_IS_NOT_A_DAG);
                }
                d++;
            }
            inDegreeMap.put(v, new ModifiableInteger(d));
            if (d == 0) {
                queue.offer(v);
||||||| /usr/src/app/output/jgrapht/jgrapht/d7d129c0f6880a29fc0e593f1f1c10226ef22182/jgrapht-core/src/main/java/org/jgrapht/traverse/TopologicalOrderIterator.java/base.java
    /**
     * Initializes the internal traversal object structure. Sets up the internal queue with the
     * directed graph vertices and creates the control structure for the in-degrees.
     *
     * @param dg the directed graph to be iterated.
     * @param queue initializer for queue
     * @param inDegreeMap initializer for inDegreeMap
     *
     * @return start vertex
     */
    private static <V, E> V initialize(
        DirectedGraph<V, E> dg, Queue<V> queue, Map<V, ModifiableInteger> inDegreeMap)
    {
        for (V vertex : dg.vertexSet()) {
            int inDegree = dg.inDegreeOf(vertex);
            inDegreeMap.put(vertex, new ModifiableInteger(inDegree));

            if (inDegree == 0) {
                queue.offer(vertex);
=======
    /**
     * Initializes the internal traversal object structure. Sets up the internal queue with the
     * directed graph vertices and creates the control structure for the in-degrees.
     *
     * @param dg the directed graph to be iterated.
     * @param queue initializer for queue
     * @param inDegreeMap initializer for inDegreeMap
     *
     * @return start vertex
     */
    private static <V,
        E> V initialize(Graph<V, E> dg, Queue<V> queue, Map<V, ModifiableInteger> inDegreeMap)
    {
        GraphTests.requireDirected(dg);

        for (V vertex : dg.vertexSet()) {
            int inDegree = dg.inDegreeOf(vertex);
            inDegreeMap.put(vertex, new ModifiableInteger(inDegree));

            if (inDegree == 0) {
                queue.offer(vertex);
>>>>>>> /usr/src/app/output/jgrapht/jgrapht/d7d129c0f6880a29fc0e593f1f1c10226ef22182/jgrapht-core/src/main/java/org/jgrapht/traverse/TopologicalOrderIterator.java/right.java
            }
        }

        // record vertices count
        this.remainingVertices = graph.vertexSet().size();
    }

    /**
     * Construct a topological order iterator.
     * 
     * <p>
     * Traversal will start at one of the graph's <i>sources</i>. See the definition of source at
     * <a href="http://mathworld.wolfram.com/Source.html">
     * http://mathworld.wolfram.com/Source.html</a>. In case of partial order, a comparator is used
     * to break ties.
     *
     * @param graph the directed graph to be iterated
     * @param comparator comparator in order to break ties in case of partial order
     */
    public TopologicalOrderIterator(DirectedGraph<V, E> graph, Comparator<V> comparator)
    {
        super(graph);

        // create queue
        if (comparator == null) {
            this.queue = new LinkedList<>();
        } else {
            this.queue = new PriorityQueue<>(comparator);
        }

        // count in-degrees
        this.inDegreeMap = new HashMap<>();
        for (V v : graph.vertexSet()) {
            int d = 0;
            for (E e : specifics.incomingEdgesOf(v)) {
                V u = Graphs.getOppositeVertex(graph, e, v);
                if (v.equals(u)) {
                    throw new IllegalArgumentException(GRAPH_IS_NOT_A_DAG);
                }
                d++;
            }
            inDegreeMap.put(v, new ModifiableInteger(d));
            if (d == 0) {
                queue.offer(v);
            }
        }

        // record vertices count
        this.remainingVertices = graph.vertexSet().size();
    }

    /**
     * {@inheritDoc}
     * 
     * Always returns true since the iterator does not care about components.
     */
    @Override
    public boolean isCrossComponentTraversal()
    {
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * Trying to disable the cross components nature of this iterator will result into throwing a
     * {@link IllegalArgumentException}.
     */
    @Override
    public void setCrossComponentTraversal(boolean crossComponentTraversal)
    {
        if (!crossComponentTraversal) {
            throw new IllegalArgumentException("Iterator is always cross-component");
        }
    }

    @Override
    public boolean hasNext()
    {
        if (cur != null) {
            return true;
        }
        cur = advance();
        if (cur != null && nListeners != 0) {
            fireVertexTraversed(createVertexTraversalEvent(cur));
        }
        return cur != null;
    }

    @Override
    public V next()
    {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        V result = cur;
        cur = null;
        if (nListeners != 0) {
            fireVertexFinished(createVertexTraversalEvent(result));
        }
        return result;
    }

    private V advance()
    {
        V result = queue.poll();

        if (result != null) {
            for (E e : specifics.edgesOf(result)) {
                V other = Graphs.getOppositeVertex(graph, e, result);

                ModifiableInteger inDegree = inDegreeMap.get(other);
                if (inDegree.value > 0) {
                    inDegree.value--;

                    if (inDegree.value == 0) {
                        queue.offer(other);
                    }
                }
            }

            --remainingVertices;
        } else {
            /*
             * Still expecting some vertices, but no vertex has zero degree.
             */
            if (remainingVertices > 0) {
                throw new IllegalArgumentException(GRAPH_IS_NOT_A_DAG);
            }
        }

        return result;
    }

}

// End TopologicalOrderIterator.java
