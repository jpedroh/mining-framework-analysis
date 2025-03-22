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
public interface GraphListener<V extends java.lang.Object, E extends java.lang.Object> extends VertexSetListener<V> {
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

  /**
     * Notifies that an edge weight has been updated.
     * 
     * @param e the edge event.
     */
  default void edgeWeightUpdated(GraphEdgeChangeEvent<V, E> e) {
  }
}