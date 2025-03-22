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
public interface PathValidator<V extends java.lang.Object, E extends java.lang.Object> {
  /**
     * Checks if an edge can be added to a previous path element.
     * 
     * @param partialPath the path from source vertex up to the current vertex.
     * @param edge the new edge to be added to the path.
     * 
     * @return <code>true</code> if edge can be added, <code>false</code> otherwise.
     */
  public boolean isValidPath(
<<<<<<< /usr/src/app/output/jgrapht/jgrapht/b7c525bdcb82359ff37e9660cc48a8a13656e062/jgrapht-core/src/main/java/org/jgrapht/alg/PathValidator.java/left.java
  GraphPath<V, E> partialPath
=======
  GraphPath<V, E> prevPath
>>>>>>> /usr/src/app/output/jgrapht/jgrapht/b7c525bdcb82359ff37e9660cc48a8a13656e062/jgrapht-core/src/main/java/org/jgrapht/alg/PathValidator.java/right.java
  , E edge);
}