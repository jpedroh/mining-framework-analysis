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

  /**
	 * @return the {@link Schema} this GraphFactory is bound to
	 */
  public Schema getSchema();

  /**
	 * Creates a {@link Graph} for the specified {@link GraphClass}
	 * 
	 * @param gc
	 * 			the {@GraphClass} of the new created {@link Graph}
	 * @param id
	 * 			the {@code String} representing the ID of the new {@link Graph}
	 * @param vMax
	 * 			the maximal number of vertices in the new {@link Graph} 
	 * 			(not forced, number of vertices can grow above)
	 * @param eMax
	 * 			the maximal number of edges in the new {@link Graph} 
	 * 			(not forced, number of edges can grow above)
 	 */
  public <G extends Graph> G createGraph(GraphClass gc, String id, int vMax, int eMax);

  /**
	 * Creates a {@link Vertex} for the specified {@link VertexClass}.
	 * 
	 * @param vc
	 * 			the {@link VertexClass} of the new created {@link Vertex}
	 * @param id
	 * 			the {@code int} value representing the ID of the new {@link Vertex}
	 * @param g
	 * 			the {@link Graph} that contains the new {@link Vertex}
	 */
  public <V extends Vertex> V createVertex(VertexClass vc, int id, Graph g);

  public <V extends Vertex> V restoreVertex(VertexClass vc, int id, Graph g);

  /**
	 * Creates an {@link Edge} for the specified {@link EdgeClass}.
	 * 
	 * @param ec
	 * 			the {@link EdgeClass} of the new created {@link Edge}
	 * @param id 
	 * 			the {@link int} value representing the ID of the new {@link Edge}
	 * @param g
	 * 			the {@link Graph} that contains the new {@link Edge}
	 * @param alpha
	 * 			the start {@link Vertex} of the new {@link Edge}
	 * @param omega
	 * 			the omega {@link Vertex} of the new {@link Edge}
	 */
  public <E extends Edge> E createEdge(EdgeClass ec, int id, Graph g, Vertex alpha, Vertex omega);

  public <E extends Edge> E restoreEdge(EdgeClass ec, int id, Graph g, Vertex alpha, Vertex omega);

  /**
	 * Sets the implementation class of the specified {@link GraphClass}
	 * 
	 * @param gc
	 * 			the {@link GraphClass} to set the implementation class for
	 * @param graphImplementationClass
	 * 			the implementation class to set
	 */
  public void setGraphImplementationClass(GraphClass gc, Class<? extends Graph> graphImplementationClass);

  /**
	 * Sets the implementation class of the specified {@link VertexClass}
	 * 
	 * @param vc
	 * 			the {@link VertexClass} to set the implementation class for
	 * @param vertexImplementationClass
	 * 			the implementation class to set
	 */
  public void setVertexImplementationClass(VertexClass vc, Class<? extends Vertex> vertexImplementationClass);

  /**
	 * Sets the implementation class of the specified {@link EdgeClass}
	 * 
	 * @param vc
	 * 			the {@link EdgeClass} to set the implementation class for
	 * @param vertexImplementationClass
	 * 			the implementation class to set
	 */
  public void setEdgeImplementationClass(EdgeClass ec, Class<? extends Edge> edgeImplementationClass);

  public Class<? extends Vertex> getVertexImplementationClass(VertexClass vc);

  public Class<? extends Edge> getEdgeImplementationClass(EdgeClass ec);
}