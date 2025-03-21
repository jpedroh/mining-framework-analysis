package de.uni_koblenz.jgralab.impl;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphException;
import de.uni_koblenz.jgralab.GraphFactory;
import de.uni_koblenz.jgralab.ImplementationType;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.db.GraphDatabase;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.VertexClass;
import de.uni_koblenz.jgralab.schema.exception.SchemaClassAccessException;
import de.uni_koblenz.jgralab.schema.exception.SchemaException;

/**
 * Default implementation for GraphFactory. Per default every create-method
 * creates an instance of exactly the specified class. To change this use
 * <code>setImplementationClass</code>-methods. Class is abstract because only
 * factories which are specific for their schema should be used.
 * 
 * @author ist@uni-koblenz.de
 */
public abstract class GraphFactoryImpl implements GraphFactory {
  protected Constructor<? extends Graph> graphConstructor;

  protected HashMap<EdgeClass, Constructor<? extends Edge>> edgeMap;

  protected HashMap<VertexClass, Constructor<? extends Vertex>> vertexMap;


<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/impl/GraphFactoryImpl.java/left.java
  protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> graphDatabaseMap;
=======
  protected Schema schema;
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/impl/GraphFactoryImpl.java/right.java



<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/impl/GraphFactoryImpl.java/left.java
  protected HashMap<Class<? extends Edge>, Constructor<? extends Edge>> edgeDatabaseMap;
=======
  protected ImplementationType implementationType;
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/impl/GraphFactoryImpl.java/right.java



<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/impl/GraphFactoryImpl.java/left.java
  protected HashMap<Class<? extends Vertex>, Constructor<? extends Vertex>> vertexDatabaseMap;
=======
  protected GraphDatabase graphDatabase;
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/impl/GraphFactoryImpl.java/right.java



<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/impl/GraphFactoryImpl.java/left.java
  protected HashMap<Class<? extends Graph>, Constructor<? extends Graph>> graphTransactionMap;
=======
  protected boolean graphCreated;
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/impl/GraphFactoryImpl.java/right.java


  protected HashMap<Class<? extends Edge>, Constructor<? extends Edge>> edgeTransactionMap;

  protected HashMap<Class<? extends Vertex>, Constructor<? extends Vertex>> vertexTransactionMap;

  /**
	 * Creates and initializes a new <code>GraphFactoryImpl</code>.
	 */
  protected GraphFactoryImpl(Schema s, ImplementationType i) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/impl/GraphFactoryImpl.java/left.java
    createMapsForStandardSupport();
=======
>>>>>>> Unknown file: This is a bug in JDime.


<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/impl/GraphFactoryImpl.java/left.java
    createMapsForDatabaseSupport()
=======
    schema = s
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/impl/GraphFactoryImpl.java/right.java
    ;

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/impl/GraphFactoryImpl.java/left.java
    createMapsForTransactionSupport()
=======
    implementationType = i
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/impl/GraphFactoryImpl.java/right.java
    ;
  }

  private void createMapsForStandardSupport() {
    graphMap = new HashMap<Class<? extends Graph>, Constructor<? extends Graph>>();
    edgeMap = new HashMap<Class<? extends Edge>, Constructor<? extends Edge>>();
    vertexMap = new HashMap<Class<? extends Vertex>, Constructor<? extends Vertex>>();
  }

  public void setGraphDatabase(GraphDatabase graphDatabase) {
    this.graphDatabase = graphDatabase;
  }

  private void createMapsForDatabaseSupport() {
    graphDatabaseMap = new HashMap<Class<? extends Graph>, Constructor<? extends Graph>>();
    edgeDatabaseMap = new HashMap<Class<? extends Edge>, Constructor<? extends Edge>>();
    vertexDatabaseMap = new HashMap<Class<? extends Vertex>, Constructor<? extends Vertex>>();
  }

  protected void createMaps() {
    edgeMap = new HashMap<EdgeClass, Constructor<? extends Edge>>();
    vertexMap = new HashMap<VertexClass, Constructor<? extends Vertex>>();
  }

  private void createMapsForTransactionSupport() {
    graphTransactionMap = new HashMap<Class<? extends Graph>, Constructor<? extends Graph>>();
    edgeTransactionMap = new HashMap<Class<? extends Edge>, Constructor<? extends Edge>>();
    vertexTransactionMap = new HashMap<Class<? extends Vertex>, Constructor<? extends Vertex>>();
  }

  @Override public ImplementationType getImplementationType() {
    return implementationType;
  }


<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/impl/GraphFactoryImpl.java/left.java
  @Override public Edge createEdge(Class<? extends Edge> edgeClass, int id, Graph g, Vertex alpha, Vertex omega) {
    try {
      if (!((InternalGraph) g).isLoading() && g.getECARuleManagerIfThere() != null) {
        g.getECARuleManagerIfThere().fireBeforeCreateEdgeEvents(edgeClass);
      }
      Edge e = edgeMap.get(edgeClass).newInstance(id, g, alpha, omega);
      return e;
    } catch (Exception ex) {
      if (ex.getCause() instanceof GraphException) {
        throw new GraphException(ex.getCause().getLocalizedMessage(), ex);
      }
      throw new SchemaClassAccessException("Cannot create edge of class " + edgeClass.getCanonicalName(), ex);
    }
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  @Override public Schema getSchema() {
    return schema;
  }


<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/impl/GraphFactoryImpl.java/left.java
  @Override public Graph createGraph(Class<? extends Graph> graphClass, String id, int vMax, int eMax) {
    try {
      Graph g = graphMap.get(graphClass).newInstance(id, vMax, eMax);
      return g;
    } catch (Exception ex) {
      throw new SchemaClassAccessException("Cannot create graph of class " + graphClass.getCanonicalName(), ex);
    }
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  @Override public Graph createGraph(Class<? extends Graph> graphClass, String id) {
    try {
      Graph g = graphMap.get(graphClass).newInstance(id, 1000, 1000);
      return g;
    } catch (Exception ex) {
      throw new SchemaClassAccessException("Cannot create graph of class " + graphClass.getCanonicalName(), ex);
    }
  }


<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/impl/GraphFactoryImpl.java/left.java
  @Override public Vertex createVertex(Class<? extends Vertex> vertexClass, int id, Graph g) {
    try {
      if (!((InternalGraph) g).isLoading() && g.getECARuleManagerIfThere() != null) {
        g.getECARuleManagerIfThere().fireBeforeCreateVertexEvents(vertexClass);
      }
      Vertex v = vertexMap.get(vertexClass).newInstance(id, g);
      return v;
    } catch (Exception ex) {
      if (ex.getCause() instanceof GraphException) {
        throw new GraphException(ex.getCause().getLocalizedMessage(), ex);
      }
      throw new SchemaClassAccessException("Cannot create vertex of class " + vertexClass.getCanonicalName(), ex);
    }
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  @Override public void setGraphImplementationClass(GraphClass gc, Class<? extends Graph> implementationClass) {
    if (graphCreated) {
      throw new IllegalStateException("Can\'t change implementation class after a graph was created.");
    }
    Class<? extends Graph> originalClass = gc.getSchemaClass();
    if (isSuperclassOrEqual(originalClass, implementationClass)) {
      try {

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/impl/GraphFactoryImpl.java/left.java
        Class<?>[] params = { String.class, int.class, int.class };
=======
        if (implementationType.equals(ImplementationType.DATABASE)) {
          Class<?>[] params = { String.class, int.class, int.class, GraphDatabase.class };
          graphConstructor = implementationClass.getConstructor(params);
        } else {
          Class<?>[] params = { String.class, int.class, int.class };
          graphConstructor = implementationClass.getConstructor(params);
        }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/impl/GraphFactoryImpl.java/right.java
      } catch (NoSuchMethodException ex) {
        throw new SchemaClassAccessException("Unable to locate constructor for graphclass " + implementationClass.getName(), ex);
      }
    } else {
      throw new SchemaException(implementationClass.getCanonicalName() + " does not implement " + originalClass.getCanonicalName());
    }
  }

  @Override public <G extends Graph> G createGraph(GraphClass gc, String id, int vMax, int eMax) {
    try {
      if (implementationType.equals(ImplementationType.DATABASE)) {
        @SuppressWarnings(value = { "unchecked" }) G dbGraph = (G) graphConstructor.newInstance(id, vMax, eMax, graphDatabase);
        dbGraph.setGraphFactory(this);
        graphCreated = true;
        return dbGraph;
      } else {
        @SuppressWarnings(value = { "unchecked" }) G graph = (G) graphConstructor.newInstance(id, vMax, eMax);
        graph.setGraphFactory(this);
        graphCreated = true;
        return graph;
      }
    } catch (Exception ex) {
      throw new SchemaClassAccessException("Cannot create graph of class " + graphConstructor.getDeclaringClass().getCanonicalName(), ex);
    }
  }

  @Override public <E extends Edge> E createEdge(EdgeClass ec, int id, Graph g, Vertex alpha, Vertex omega) {
    try {
      if (!((InternalGraph) g).isLoading() && (g.hasECARuleManager())) {
        g.getECARuleManager().fireBeforeCreateEdgeEvents(ec);
      }
      @SuppressWarnings(value = { "unchecked" }) E newInstance = (E) edgeMap.get(ec).newInstance(id, g, alpha, omega);
      return newInstance;
    } catch (Exception ex) {
      if (ex.getCause() instanceof GraphException) {
        throw new GraphException(ex.getCause().getLocalizedMessage(), ex);
      }
      throw new SchemaClassAccessException("Cannot create edge of class " + ec.getQualifiedName(), ex);
    }
  }

  @Override public <V extends Vertex> V createVertex(VertexClass vc, int id, Graph g) {
    try {
      if (!((InternalGraph) g).isLoading() && (g.hasECARuleManager())) {
        g.getECARuleManager().fireBeforeCreateVertexEvents(vc);
      }
      @SuppressWarnings(value = { "unchecked" }) V newInstance = (V) vertexMap.get(vc).newInstance(id, g);
      return newInstance;
    } catch (Exception ex) {
      if (ex.getCause() instanceof GraphException) {
        throw new GraphException(ex.getCause().getLocalizedMessage(), ex);
      }
      throw new SchemaClassAccessException("Cannot create vertex of class " + vc.getQualifiedName(), ex);
    }
  }

  @Override public void setVertexImplementationClass(VertexClass vc, Class<? extends Vertex> implementationClass) {
    if (graphCreated) {
      throw new IllegalStateException("Can\'t change implementation class after a graph was created.");
    }
    Class<? extends Vertex> originalClass = vc.getSchemaClass();
    if (isSuperclassOrEqual(originalClass, implementationClass)) {
      try {
        Class<?>[] params = { int.class, Graph.class };
        vertexMap.put(vc, implementationClass.getConstructor(params));
      } catch (NoSuchMethodException ex) {
        throw new SchemaClassAccessException("Unable to locate default constructor for vertexclass" + implementationClass, ex);
      }
    } else {
      throw new SchemaException(implementationClass.getCanonicalName() + " does not implement " + originalClass.getCanonicalName());
    }
  }

  @Override public void setEdgeImplementationClass(EdgeClass ec, Class<? extends Edge> implementationClass) {
    if (graphCreated) {
      throw new IllegalStateException("Can\'t change implementation class after a graph was created.");
    }
    Class<? extends Edge> originalClass = ec.getSchemaClass();
    if (isSuperclassOrEqual(originalClass, implementationClass)) {
      try {
        Class<?>[] params = { int.class, Graph.class, Vertex.class, Vertex.class };
        edgeMap.put(ec, implementationClass.getConstructor(params));
      } catch (NoSuchMethodException ex) {
        throw new SchemaClassAccessException("Unable to locate default constructor for edgeclass" + implementationClass, ex);
      }
    } else {
      throw new SchemaException(implementationClass.getCanonicalName() + " does not implement " + originalClass.getCanonicalName());
    }
  }

  @Override public Graph createGraphWithDatabaseSupport(Class<? extends Graph> graphClass, GraphDatabase graphDatabase, String id) {
    try {
      return graphDatabaseMap.get(graphClass).newInstance(id, 1000, 1000, graphDatabase);
    } catch (Exception exception) {
      throw new SchemaClassAccessException("Cannot create graph of class " + graphClass.getCanonicalName(), exception);
    }
  }

  @Override public Graph createGraphWithDatabaseSupport(Class<? extends Graph> graphClass, GraphDatabase graphDatabase, String id, int vMax, int eMax) {
    try {
      return graphDatabaseMap.get(graphClass).newInstance(id, vMax, eMax, graphDatabase);
    } catch (Exception exception) {
      throw new SchemaClassAccessException("Cannot create graph of class " + graphClass.getCanonicalName(), exception);
    }
  }

  @Override public Edge createEdgeWithDatabaseSupport(Class<? extends Edge> edgeClass, int id, Graph graph, Vertex alpha, Vertex omega) {
    try {
      return edgeDatabaseMap.get(edgeClass).newInstance(id, graph, alpha, omega);
    } catch (Exception exception) {
      if (exception.getCause() instanceof GraphException) {
        throw new GraphException(exception.getCause().getLocalizedMessage());
      } else {
        throw new SchemaClassAccessException("Cannot create edge of class " + edgeClass.getCanonicalName(), exception);
      }
    }
  }

  @Override public Vertex createVertexWithDatabaseSupport(Class<? extends Vertex> vertexClass, int id, Graph graph) {
    try {
      Constructor<? extends Vertex> constructor = vertexDatabaseMap.get(vertexClass);
      return constructor.newInstance(id, graph);
    } catch (Exception exception) {
      if (exception.getCause() instanceof GraphException) {
        throw new GraphException(exception.getCause().getLocalizedMessage());
      } else {
        throw new SchemaClassAccessException("Cannot create vertex of class " + vertexClass.getCanonicalName(), exception);
      }
    }
  }

  @Override public void setGraphDatabaseImplementationClass(Class<? extends Graph> originalClass, Class<? extends Graph> implementationClass) {
    if (isSuperclassOrEqual(originalClass, implementationClass)) {
      try {
        Class<?>[] params = { String.class, int.class, int.class, GraphDatabase.class };
        graphDatabaseMap.put(originalClass, implementationClass.getConstructor(params));
      } catch (NoSuchMethodException exception) {
        throw new SchemaClassAccessException("Unable to locate default constructor for graphclass " + implementationClass.getName(), exception);
      }
    }
  }

  @Override public void setVertexDatabaseImplementationClass(Class<? extends Vertex> originalClass, Class<? extends Vertex> implementationClass) {
    if (isSuperclassOrEqual(originalClass, implementationClass)) {
      try {
        Class<?>[] params = { int.class, Graph.class };
        vertexDatabaseMap.put(originalClass, implementationClass.getConstructor(params));
      } catch (NoSuchMethodException exception) {
        throw new SchemaClassAccessException("Unable to locate default constructor for vertex class" + implementationClass, exception);
      }
    }
  }

  @Override public void setEdgeDatabaseImplementationClass(Class<? extends Edge> originalClass, Class<? extends Edge> implementationClass) {
    if (isSuperclassOrEqual(originalClass, implementationClass)) {
      try {
        Class<?>[] params = { int.class, Graph.class, Vertex.class, Vertex.class };
        edgeDatabaseMap.put(originalClass, implementationClass.getConstructor(params));
      } catch (NoSuchMethodException exception) {
        throw new SchemaClassAccessException("Unable to locate default constructor for edge class" + implementationClass, exception);
      }
    }
  }

  @Override public Edge createEdgeWithTransactionSupport(Class<? extends Edge> edgeClass, int id, Graph g, Vertex alpha, Vertex omega) {
    try {
      Edge e = edgeTransactionMap.get(edgeClass).newInstance(id, g, alpha, omega);
      e.initializeAttributesWithDefaultValues();
      return e;
    } catch (Exception ex) {
      if (ex.getCause() instanceof GraphException) {
        throw new GraphException(ex.getCause().getLocalizedMessage(), ex);
      }
      throw new SchemaClassAccessException("Cannot create edge of class " + edgeClass.getCanonicalName(), ex);
    }
  }

  @Override public Graph createGraphWithTransactionSupport(Class<? extends Graph> graphClass, String id, int vMax, int eMax) {
    try {
      Graph g = graphTransactionMap.get(graphClass).newInstance(id, vMax, eMax);
      return g;
    } catch (Exception ex) {
      if (ex.getCause() instanceof GraphException) {
        throw new GraphException(ex.getCause().getLocalizedMessage(), ex);
      }
      throw new SchemaClassAccessException("Cannot create graph of class " + graphClass.getCanonicalName(), ex);
    }
  }

  @Override public Graph createGraphWithTransactionSupport(Class<? extends Graph> graphClass, String id) {
    try {
      Graph g = graphTransactionMap.get(graphClass).newInstance(id, 1000, 1000);
      return g;
    } catch (Exception ex) {
      if (ex.getCause() instanceof GraphException) {
        throw new GraphException(ex.getCause().getLocalizedMessage(), ex);
      }
      throw new SchemaClassAccessException("Cannot create graph of class " + graphClass.getCanonicalName(), ex);
    }
  }

  @Override public Vertex createVertexWithTransactionSupport(Class<? extends Vertex> vertexClass, int id, Graph g) {
    try {
      Vertex v = vertexTransactionMap.get(vertexClass).newInstance(id, g);
      return v;
    } catch (Exception ex) {
      if (ex.getCause() instanceof GraphException) {
        throw new GraphException(ex.getCause().getLocalizedMessage(), ex);
      }
      throw new SchemaClassAccessException("Cannot create vertex of class " + vertexClass.getCanonicalName(), ex);
    }
  }

  public void setGraphTransactionImplementationClass(Class<? extends Graph> originalClass, Class<? extends Graph> implementationClass) {
    if (isSuperclassOrEqual(originalClass, implementationClass)) {
      try {
        Class<?>[] params = { String.class, int.class, int.class };
        graphTransactionMap.put(originalClass, implementationClass.getConstructor(params));
      } catch (NoSuchMethodException ex) {
        throw new SchemaClassAccessException("Unable to locate transaction constructor for graphclass " + implementationClass.getName(), ex);
      }
    }
  }

  @Override public void setVertexTransactionImplementationClass(Class<? extends Vertex> originalClass, Class<? extends Vertex> implementationClass) {
    if (isSuperclassOrEqual(originalClass, implementationClass)) {
      try {
        Class<?>[] params = { int.class, Graph.class };
        vertexTransactionMap.put(originalClass, implementationClass.getConstructor(params));
      } catch (NoSuchMethodException ex) {
        throw new SchemaClassAccessException("Unable to locate transaction constructor for vertexclass" + implementationClass, ex);
      }
    }
  }

  @Override public void setEdgeTransactionImplementationClass(Class<? extends Edge> originalClass, Class<? extends Edge> implementationClass) {
    if (isSuperclassOrEqual(originalClass, implementationClass)) {
      try {
        Class<?>[] params = { int.class, Graph.class, Vertex.class, Vertex.class };
        edgeTransactionMap.put(originalClass, implementationClass.getConstructor(params));
      } catch (NoSuchMethodException ex) {
        throw new SchemaClassAccessException("Unable to locate transaction constructor for edgeclass" + implementationClass, ex);
      }
    }
  }

  /**
	 * tests if a is a superclass of b or the same class than b
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
  protected boolean isSuperclassOrEqual(Class<?> a, Class<?> b) {
    if (a == b) {
      return true;
    }
    if (implementsInterface(b, a)) {
      return true;
    }
    while (b.getSuperclass() != null) {
      if (b.getSuperclass() == a) {
        return true;
      }
      if (implementsInterface(b, a)) {
        return true;
      }
      b = b.getSuperclass();
    }
    return false;
  }

  /**
	 * tests if class a implements the interface b
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
  protected boolean implementsInterface(Class<?> a, Class<?> b) {
    Class<?>[] list = a.getInterfaces();
    for (Class<?> c : list) {
      if (c == b) {
        return true;
      }
    }
    return false;
  }
}