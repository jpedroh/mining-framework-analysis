/*
 * (C) Copyright 2003-2018, by Barak Naveh and Contributors.
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
package org.jgrapht.event;

/**
 * A listener that is notified when the graph changes.
 *
 * <p>
 * If only notifications on vertex set changes are required it is more efficient to use the
 * VertexSetListener.
 * </p>
 * 
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Barak Naveh
 * @see VertexSetListener
 * @since Jul 18, 2003
 */
public interface GraphListener<V, E>
    extends
    VertexSetListener<V>
{
    /**
     * Notifies that an edge has been added to the graph.
     *
     * @param e the edge event.
     */
    void edgeAdded(GraphEdgeChangeEvent<V, E> e);

    /**
     * Notifies that an edge has been removed from the graph.
     *
     * @param e the edge event.
     */
    void edgeRemoved(GraphEdgeChangeEvent<V, E> e);
<<<<<<< /usr/src/app/output/jgrapht/jgrapht/8f3dfa07cf62a9032f18a22f8022b3480dfbf1c7/jgrapht-core/src/main/java/org/jgrapht/event/GraphListener.java/left.java
    
    /**
     * Notifies that an edge weight has been updated.
     * 
     * @param e the edge event.
     */
    default void edgeWeightUpdated(GraphEdgeChangeEvent<V, E> e) {}

||||||| /usr/src/app/output/jgrapht/jgrapht/8f3dfa07cf62a9032f18a22f8022b3480dfbf1c7/jgrapht-core/src/main/java/org/jgrapht/event/GraphListener.java/base.java
=======

    /**
     * Notifies that an edge weight has been updated.
     * 
     * @param e the edge event.
     */
    default void edgeWeightUpdated(GraphEdgeChangeEvent<V, E> e)
    {
    }

>>>>>>> /usr/src/app/output/jgrapht/jgrapht/8f3dfa07cf62a9032f18a22f8022b3480dfbf1c7/jgrapht-core/src/main/java/org/jgrapht/event/GraphListener.java/right.java
}

// End GraphListener.java
