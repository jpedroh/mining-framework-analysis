package de.uni_koblenz.jgralab.impl;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.VertexClass;

/**
 * This class provides an Iterable to iterate over vertices in a graph. One may
 * use this class to use the advanced for-loop of Java 5. Instances of this
 * class should never, and this means <b>never</b> created manually but only
 * using the methods <code>vertices(params)</code> of th graph. Every special
 * graphclass contains generated methods similar to
 * <code>vertices(params)</code> for every VertexClass that is part of the
 * GraphClass.
 * 
 * @author ist@uni-koblenz.de
 * 
 * @param <V>
 *            The type of the vertices to iterate over. To mention it again,
 *            <b>don't</b> create instances of this class directly.
 */
public class VertexIterable<V extends Vertex> implements Iterable<V> {
  class VertexIterator implements Iterator<V> {
    /**
		 * the vertex that hasNext() retrieved and that a call of next() will
		 * return
		 */
    protected V current = null;

    /**
		 * the graph this iterator works on
		 */
    protected InternalGraph graph = null;

    protected Class<? extends Vertex> vc;

    protected VertexClass schemaVc;

    /**
		 * the version of the vertex list of the graph at the beginning of the
		 * iteration. This information is used to check if the vertex list has
		 * changed, the failfast-iterator will then throw an exception the next
		 * time "next()" is called
		 */
    protected long vertexListVersion;

    /**
		 * creates a new VertexIterator for the given graph
		 * 
		 * @param g
		 *            the graph to work on
		 */
    @SuppressWarnings(value = { "unchecked" }) VertexIterator(InternalGraph g, Class<? extends Vertex> vc) {
      graph = g;
      this.vc = vc;
      vertexListVersion = g.getVertexListVersion();
      current = (V) (vc == null ? graph.getFirstVertex() : graph.getFirstVertex(vc));
    }

    /**
		 * Creates a new Vertex iterator for the given <code>Graph</code>, that iterates over
		 * vertices of a given <code>VertexClass</code>
		 * @param g The <code>Graph</code>.
		 * @param vc They <code>VertexClass</code> determining which type of vertex should be
		 * iterated over.
		 */
    @SuppressWarnings(value = { "unchecked" }) VertexIterator(InternalGraph g, VertexClass vc) {
      graph = g;
      schemaVc = vc;
      vertexListVersion = g.getVertexListVersion();
      current = (V) (vc == null ? graph.getFirstVertex() : graph.getFirstVertex(vc));
    }

    /**
		 * @return the next vertex in the graph which mathes the conditions of
		 *         this iterator
		 */
    @SuppressWarnings(value = { "unchecked" }) public V next() {
      if (graph.isVertexListModified(vertexListVersion)) {
        throw new ConcurrentModificationException("The vertex list of the graph has been modified - the iterator is not longer valid");
      }
      if (current == null) {
        throw new NoSuchElementException();
      }
      V result = current;
      current = (V) (vc == null && schemaVc == null ? current.getNextVertex() : schemaVc == null ? current.getNextVertex(vc) : current.getNextVertex(schemaVc));
      return result;
    }

    /**
		 * @return true iff there is at least one next vertex to retrieve
		 */
    public boolean hasNext() {
      return current != null;
    }

    /**
		 * Using the VertexIterator, it is <b>not</b> possible to remove
		 * vertices from a graph neither the iterator will recognize such a
		 * removal.
		 * 
		 * @throw UnsupportedOperationException every time the method is called
		 */
    public void remove() {
      throw new UnsupportedOperationException("It is not allowed to remove vertices during iteration.");
    }
  }

  private VertexIterator iter;

  public VertexIterable(Graph g) {
    this(g, (Class<? extends Vertex>) null);
  }

  public VertexIterable(Graph g, Class<? extends Vertex> vc) {
    assert g != null;
    iter = new VertexIterator((InternalGraph) g, vc);
  }

  public VertexIterable(Graph g, VertexClass vc) {
    assert g != null;
    iter = new VertexIterator((InternalGraph) g, vc);
  }

  public Iterator<V> iterator() {
    return iter;
  }
}