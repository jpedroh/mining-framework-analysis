package de.uni_koblenz.jgralab.graphmarker;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.TraversalContext;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.algolib.functions.BooleanFunction;
import de.uni_koblenz.jgralab.algolib.functions.entries.BooleanFunctionEntry;

/**
 * This class serves as a special <code>BitSetGraphmarker</code>, although it
 * does not extend it. It is capable of marking both vertices and edges. This is
 * necessary for defining subgraphs. Internally all calls are delegated to an
 * instance of <code>BitSetVertexGraphMarker</code> and an instance of
 * <code>BitSetEdgeGraphMarker</code>.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class SubGraphMarker extends AbstractGraphMarker<GraphElement> implements BooleanFunction<GraphElement>, TraversalContext {
  private final BitSetEdgeMarker edgeGraphMarker;

  private final BitSetVertexMarker vertexGraphMarker;

  private long version;

  public SubGraphMarker(Graph graph) {
    super(graph);
    edgeGraphMarker = new BitSetEdgeMarker(graph);
    vertexGraphMarker = new BitSetVertexMarker(graph);
  }

  @Override public void clear() {
    if (isEmpty()) {
      return;
    }
    ++version;
    edgeGraphMarker.clear();
    vertexGraphMarker.clear();
  }

  public int getECount() {
    return edgeGraphMarker.size();
  }

  public int getVCount() {
    return vertexGraphMarker.size();
  }

  @Override public boolean isEmpty() {
    return edgeGraphMarker.isEmpty() && vertexGraphMarker.isEmpty();
  }

  @Override public boolean isMarked(GraphElement graphElement) {
    return graphElement instanceof Edge ? isMarked((Edge) graphElement) : isMarked((Vertex) graphElement);
  }

  public boolean isMarked(Vertex v) {
    return vertexGraphMarker.isMarked(v);
  }

  public boolean isMarked(Edge e) {
    return edgeGraphMarker.isMarked(e);
  }

  @Override public int size() {
    return edgeGraphMarker.size() + vertexGraphMarker.size();
  }

  @Override public boolean removeMark(GraphElement graphElement) {
    return graphElement instanceof Edge ? removeMark((Edge) graphElement) : removeMark((Vertex) graphElement);
  }

  /**
	 * Does the same as <code>removeMark</code> but without performing an
	 * <code>instanceof</code> check. It is recommended to use this method
	 * instead.
	 * 
	 * @param e
	 *            the edge to unmark
	 * @return false if the given edge has already been unmarked.
	 */
  public boolean removeMark(Edge e) {
    if (edgeGraphMarker.removeMark(e)) {
      ++version;
      return true;
    }
    return false;
  }

  /**
	 * Does the same as <code>unmark</code> but without performing an
	 * <code>instanceof</code> check. It is recommended to use this method
	 * instead. This method also removes the mark of all incident edges.
	 * 
	 * @param v
	 *            the vertex to unmark
	 * @return false if the given vertex has already been unmarked.
	 */
  public boolean removeMark(Vertex v) {
    if (vertexGraphMarker.removeMark(v)) {
      ++version;
      for (Edge e : v.incidences()) {
        edgeGraphMarker.removeMark(e);
      }
      return true;
    }
    return false;
  }

  /**
	 * Marks the given <code>graphElement</code>.
	 * 
	 * @param graphElement
	 *            the graph element to mark
	 * @return false if the given <code>graphElement</code> has already been
	 *         marked.
	 */
  public boolean mark(GraphElement graphElement) {
    return graphElement instanceof Edge ? mark((Edge) graphElement) : mark((Vertex) graphElement);
  }

  /**
	 * Does the same as <code>mark</code> but without performing an
	 * <code>instanceof</code> check. It is recommended to use this method
	 * instead. This method also marks the alpha and omega vertex of the given
	 * edge.
	 * 
	 * @param e
	 *            the edge to mark
	 * @return false if the given edge has already been marked.
	 */
  public boolean mark(Edge e) {
    if (edgeGraphMarker.mark(e)) {
      ++version;
      vertexGraphMarker.mark(e.getAlpha());
      vertexGraphMarker.mark(e.getOmega());
      return true;
    }
    return false;
  }

  /**
	 * Does the same as <code>mark</code> but without performing an
	 * <code>instanceof</code> check. It is recommended to use this method
	 * instead.
	 * 
	 * @param v
	 *            the vertex to mark
	 * @return false if the given vertex has already been marked.
	 */
  public boolean mark(Vertex v) {
    if (vertexGraphMarker.mark(v)) {
      version++;
      return true;
    }
    return false;
  }

  @Override public void edgeDeleted(Edge e) {
    edgeGraphMarker.edgeDeleted(e);
  }

  @Override public void vertexDeleted(Vertex v) {
    vertexGraphMarker.vertexDeleted(v);
  }

  @Override public void maxEdgeCountIncreased(int newValue) {
  }

  @Override public void maxVertexCountIncreased(int newValue) {
  }

  @Override public Iterable<GraphElement> getMarkedElements() {
    return new Iterable<GraphElement>() {
      @Override public Iterator<GraphElement> iterator() {
        return new ArrayGraphMarkerIterator<GraphElement>(version) {
          Iterator<Vertex> vertexIterator;

          Iterator<Edge> edgeIterator;

          {
            vertexIterator = vertexGraphMarker.getMarkedElements().iterator();
            edgeIterator = edgeGraphMarker.getMarkedElements().iterator();
          }

          @Override public boolean hasNext() {
            return vertexIterator.hasNext() || edgeIterator.hasNext();
          }

          @Override protected void moveIndex() {
          }

          @Override public GraphElement next() {
            if (version != SubGraphMarker.this.version) {
              throw new ConcurrentModificationException(MODIFIED_ERROR_MESSAGE);
            }
            if (vertexIterator.hasNext()) {
              return vertexIterator.next();
            }
            if (edgeIterator.hasNext()) {
              return edgeIterator.next();
            }
            throw new NoSuchElementException(NO_MORE_ELEMENTS_ERROR_MESSAGE);
          }
        };
      }
    };
  }

  @Override public boolean get(GraphElement parameter) {
    return isMarked(parameter);
  }

  @Override public boolean isDefined(GraphElement parameter) {
    return true;
  }

  @Override public void set(GraphElement parameter, boolean value) {
    if (value) {
      mark(parameter);
    } else {
      removeMark(parameter);
    }
  }

  @Override public Iterator<BooleanFunctionEntry<GraphElement>> iterator() {
    final Iterator<GraphElement> markedElements = getMarkedElements().iterator();
    return new Iterator<BooleanFunctionEntry<GraphElement>>() {
      @Override public boolean hasNext() {
        return markedElements.hasNext();
      }

      @Override public BooleanFunctionEntry<GraphElement> next() {
        GraphElement currentElement = markedElements.next();
        return new BooleanFunctionEntry<GraphElement>(currentElement, get(currentElement));
      }

      @Override public void remove() {
        markedElements.remove();
      }
    };
  }

  @Override public Iterable<GraphElement> getDomainElements() {
    return getMarkedElements();
  }

  @Override public boolean containsGraphElement(GraphElement e) {
    return isMarked(e);
  }

  @Override public boolean containsVertex(Vertex v) {
    return vertexGraphMarker.isMarked(v);
  }

  @Override public boolean containsEdge(Edge e) {
    return edgeGraphMarker.isMarked(e);
  }
}