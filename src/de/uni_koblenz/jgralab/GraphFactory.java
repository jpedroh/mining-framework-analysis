package de.uni_koblenz.jgralab;
import de.uni_koblenz.jgralab.impl.db.GraphDatabase;
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
	 * creates a Graph-object for the specified class. The returned object may
	 * be an instance of a subclass of the specified graphClass.
	 */
  public Graph createGraph(Class<? extends Graph> graphClass, String id);

  public Schema getSchema();

  /**
	 */
  public <G extends Graph> G createGraph(GraphClass gc, String id, int vMax, int eMax);


<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/GraphFactory.java/left.java
  /**
	 * creates a Edge-object for the specified class. The returned object may be
	 * an instance of a subclass of the specified edgeClass.
	 */
  public Edge createEdge(Class<? extends Edge> edgeClass, int id, Graph g, Vertex alpha, Vertex omega);
=======
>>>>>>> Unknown file: This is a bug in JDime.


  /**
	 * Creates a Vertex for the specified class.
	 */
  public <V extends Vertex> V createVertex(VertexClass vc, int id, Graph g);

  /**
	 * Creates an Edge for the specified class.
	 */
  public <E extends Edge> E createEdge(EdgeClass ec, int id, Graph g, Vertex alpha, Vertex omega);

  public void setGraphImplementationClass(
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/GraphFactory.java/left.java
  Class<? extends Graph> graphSchemaClass
=======
  GraphClass gc
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/GraphFactory.java/right.java
  , Class<? extends Graph> graphImplementationClass);

  public void setVertexImplementationClass(
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/GraphFactory.java/left.java
  Class<? extends Vertex> vertexSchemaClass
=======
  VertexClass vc
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/GraphFactory.java/right.java
  , Class<? extends Vertex> vertexImplementationClass);

  public void setEdgeImplementationClass(
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/GraphFactory.java/left.java
  Class<? extends Edge> edgeSchemaClass
=======
  EdgeClass ec
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/GraphFactory.java/right.java
  , Class<? extends Edge> edgeImplementationClass);

  /**
	 * Creates a graph with database support.
	 * 
	 * @param graphClass
	 *            The graph class.
	 * @param graphDatabase
	 *            Database graph should be contained in.
	 * @param id
	 *            Id of graph.
	 */
  public Graph createGraphWithDatabaseSupport(Class<? extends Graph> graphClass, GraphDatabase graphDatabase, String id);

  /**
	 * Creates a graph with database support.
	 * 
	 * @param graphClass
	 *            The graph class.
	 * @param graphDatabase
	 *            Database graph should be contained in.
	 * @param id
	 *            Id of graph.
	 * @param vMax
	 *            Maximum initial count of vertices that can be held in graph.
	 * @param eMax
	 *            Maximum initial count of edges that can be held in graph.
	 */
  public Graph createGraphWithDatabaseSupport(Class<? extends Graph> graphClass, GraphDatabase graphDatabase, String id, int vMax, int eMax);

  /**
	 * Creates a vertex instance of a specified class with database support.
	 * Returned object may be an instance of a subclass of specified vertex
	 * class.
	 * 
	 * @param vertexClass
	 *            Class of vertex to instance.
	 * @param id
	 *            Identifier of vertex.
	 * @param graph
	 *            Graph which should contain created vertex.
	 */
  public Vertex createVertexWithDatabaseSupport(Class<? extends Vertex> vertexClass, int id, Graph graph);

  /**
	 * Creates an edge instance of specified class with database support.
	 * Returned object may be an instance of a subclass of specified edge class.
	 * 
	 * @param edgeClass
	 *            Class of edge to instance.
	 * @param id
	 *            Identifier of edge.
	 * @param graph
	 *            Graph which should contain created edge.
	 * @param alpha
	 *            Start vertex of edge.
	 * @param omega
	 *            End vertex of edge.
	 */
  public Edge createEdgeWithDatabaseSupport(Class<? extends Edge> edgeClass, int id, Graph graph, Vertex alpha, Vertex omega);

  /**
	 * Assigns an implementation class with database support for a
	 * <code>Graph</code>.
	 * 
	 * @param graphSchemaClass
	 * @param graphImplementationClass
	 */
  public void setGraphDatabaseImplementationClass(Class<? extends Graph> graphSchemaClass, Class<? extends Graph> graphImplementationClass);

  /**
	 * Assigns an implementation class with database support for a
	 * <code>Vertex</code>.
	 * 
	 * @param vertexSchemaClass
	 * @param vertexImplementationClass
	 */
  public void setVertexDatabaseImplementationClass(Class<? extends Vertex> vertexSchemaClass, Class<? extends Vertex> vertexImplementationClass);

  /**
	 * Assigns an implementation class with database support for an
	 * <code>Edge</code>.
	 * 
	 * @param edgeSchemaClass
	 * @param edgeImplementationClass
	 */
  public void setEdgeDatabaseImplementationClass(Class<? extends Edge> edgeSchemaClass, Class<? extends Edge> edgeImplementationClass);

  /**
	 * creates a Graph-object for the specified class with transaction support.
	 * The returned object may be an instance of a subclass of the specified
	 * graphClass.
	 */
  public Graph createGraphWithTransactionSupport(Class<? extends Graph> graphClass, String id, int vMax, int eMax);

  /**
	 * creates a Graph-object for the specified class with transaction support.
	 * The returned object may be an instance of a subclass of the specified
	 * graphClass.
	 */
  public Graph createGraphWithTransactionSupport(Class<? extends Graph> graphClass, String id);

  /**
	 * creates a Vertex-object for the specified class with transaction support.
	 * The returned object may be an instance of a subclass of the specified
	 * vertexClass.
	 */
  public Vertex createVertexWithTransactionSupport(Class<? extends Vertex> vertexClass, int id, Graph g);

  /**
	 * creates a Edge-object for the specified class with transaction support.
	 * The returned object may be an instance of a subclass of the specified
	 * edgeClass.
	 */
  public Edge createEdgeWithTransactionSupport(Class<? extends Edge> edgeClass, int id, Graph g, Vertex alpha, Vertex omega);

  /**
	 * Assigns an implementation class with transaction support for a
	 * <code>Graph</code>.
	 * 
	 * @param graphSchemaClass
	 * @param grapgImplementationClass
	 */
  public void setGraphTransactionImplementationClass(Class<? extends Graph> graphSchemaClass, Class<? extends Graph> grapgImplementationClass);

  /**
	 * Assigns an implementation class with transaction support for a
	 * <code>Vertex</code>.
	 * 
	 * @param vertexSchemaClass
	 * @param vertexImplementationClass
	 */
  public void setVertexTransactionImplementationClass(Class<? extends Vertex> vertexSchemaClass, Class<? extends Vertex> vertexImplementationClass);

  /**
	 * Assigns an implementation class with transaction support for an
	 * <code>Edge</code>.
	 * 
	 * @param edgeSchemaClass
	 * @param edgeImplementationClass
	 */
  public void setEdgeTransactionImplementationClass(Class<? extends Edge> edgeSchemaClass, Class<? extends Edge> edgeImplementationClass);
}