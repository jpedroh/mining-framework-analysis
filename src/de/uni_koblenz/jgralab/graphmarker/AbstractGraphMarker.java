package de.uni_koblenz.jgralab.graphmarker;
import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphStructureChangedAdapterWithAutoRemove;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.InternalGraph;

public abstract class AbstractGraphMarker<T extends AttributedElement> extends GraphStructureChangedAdapterWithAutoRemove {
  protected final InternalGraph graph;

  protected AbstractGraphMarker(Graph graph) {
    this.graph = (InternalGraph) graph;
    graph.addGraphStructureChangedListener(this);
  }

  /**
	 * Checks if the given <code>graphElement</code> is marked.
	 * 
	 * @param graphElement
	 *            the graph element to check.
	 * @return true if the given <code>graphElement</code> is marked.
	 */
  public abstract boolean isMarked(T graphElement);

  /**
	 * Unmarks the given <code>graphElement</code>.
	 * 
	 * @param graphElement
	 *            the graph element to unmark.
	 * @return false if the given <code>graphElement</code> has already been
	 *         unmarked.
	 */
  public abstract boolean removeMark(T graphElement);

  /**
	 * Returns the number of marked graph elements.
	 * 
	 * @return the number of marked graph elements.
	 */
  public abstract int size();

  /**
	 * Checks if this graph marker is empty.
	 * 
	 * @return true if this graph marker is empty.
	 */
  public abstract boolean isEmpty();

  /**
	 * Unmarks all marked graph elements.
	 */
  public abstract void clear();

  public Graph getGraph() {
    return graph;
  }

  public abstract Iterable<T> getMarkedElements();

  @Override public abstract void edgeDeleted(Edge e);

  @Override public abstract void maxEdgeCountIncreased(int newValue);

  @Override public abstract void maxVertexCountIncreased(int newValue);

  @Override public abstract void vertexDeleted(Vertex v);
}