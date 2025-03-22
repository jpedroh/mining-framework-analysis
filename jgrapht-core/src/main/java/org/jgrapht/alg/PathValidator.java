/*
 * (C) Copyright 2016-2016, by Assaf Mizrachi and Contributors.
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
package org.jgrapht.alg;

import org.jgrapht.*;

/**
 * May be used to provide external path validations in addition to the basic validations done by
 * {@link KShortestPaths} - that the path is from source to target and that it does not contain
 * loops.
 * 
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 * 
 * @author Assaf Mizrachi
 * @since July, 21, 2016
 *
 */
public interface PathValidator<V, E>
{

    /**
     * Checks if an edge can be added to a previous path element.
     * 
<<<<<<< /usr/src/app/output/jgrapht/jgrapht/b7c525bdcb82359ff37e9660cc48a8a13656e062/jgrapht-core/src/main/java/org/jgrapht/alg/PathValidator.java/left.java
     * @param partialPath the path from source vertex up to the current vertex.
     * @param edge the new edge to be added to the path.
||||||| /usr/src/app/output/jgrapht/jgrapht/b7c525bdcb82359ff37e9660cc48a8a13656e062/jgrapht-core/src/main/java/org/jgrapht/alg/PathValidator.java/base.java
     * @param prevPathElement the previous path element
     * @param edge the edge to be added to the path.
=======
     * @param prevPath the path from source vertex up to the new edge.
     * @param edge the edge to be added to the path.
>>>>>>> /usr/src/app/output/jgrapht/jgrapht/b7c525bdcb82359ff37e9660cc48a8a13656e062/jgrapht-core/src/main/java/org/jgrapht/alg/PathValidator.java/right.java
     * 
     * @return <code>true</code> if edge can be added, <code>false</code> otherwise.
     */
<<<<<<< /usr/src/app/output/jgrapht/jgrapht/b7c525bdcb82359ff37e9660cc48a8a13656e062/jgrapht-core/src/main/java/org/jgrapht/alg/PathValidator.java/left.java
    public boolean isValidPath(GraphPath<V, E> partialPath, E edge);
||||||| /usr/src/app/output/jgrapht/jgrapht/b7c525bdcb82359ff37e9660cc48a8a13656e062/jgrapht-core/src/main/java/org/jgrapht/alg/PathValidator.java/base.java
    public boolean isValidPath(GraphPath<V, E> prevPathElement, E edge);
=======
    public boolean isValidPath(GraphPath<V, E> prevPath, E edge);
>>>>>>> /usr/src/app/output/jgrapht/jgrapht/b7c525bdcb82359ff37e9660cc48a8a13656e062/jgrapht-core/src/main/java/org/jgrapht/alg/PathValidator.java/right.java
}
