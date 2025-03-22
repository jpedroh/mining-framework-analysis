package de.uni_koblenz.jgralab;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.VertexClass;

/**
 * Creates instances of graphs, edges and vertices. By changing factory it is
 * possible to extend Graph, Vertex, and Edge classes used in a graph.
 * 
 * @author ist@uni-koblenz.de
 */
public interface GraphFactory {
  /**
	 * @return the {@link ImplementationType} of this GraphFactory
	 */
  public ImplementationType getImplementationType();

  public Schema getSchema();

  /**
	 */
  public <G extends Graph> G createGraph(GraphClass gc, String id, int vMax, int eMax);

  /**
	 * Creates a Vertex for the specified class.
	 */
  public <V extends Vertex> V createVertex(VertexClass vc, int id, Graph g);

  /**
	 * Creates an Edge for the specified class.
	 */
  public <E extends Edge> E createEdge(EdgeClass ec, int id, Graph g, Vertex alpha, Vertex omega);

  public void setGraphImplementationClass(GraphClass gc, Class<? extends Graph> graphImplementationClass);

  public void setVertexImplementationClass(VertexClass vc, Class<? extends Vertex> vertexImplementationClass);

  public void setEdgeImplementationClass(EdgeClass ec, Class<? extends Edge> edgeImplementationClass);
}