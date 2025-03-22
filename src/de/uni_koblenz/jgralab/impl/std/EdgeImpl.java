package de.uni_koblenz.jgralab.impl.std;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.InternalEdge;
import de.uni_koblenz.jgralab.impl.InternalVertex;

/**
 * The implementation of an <code>Edge</code> accessing attributes without
 * versioning.
 *
 * @author Jose Monte(monte@uni-koblenz.de)
 */
public abstract class EdgeImpl extends de.uni_koblenz.jgralab.impl.EdgeBaseImpl {
  private InternalEdge nextEdge;

  private InternalEdge prevEdge;

  private InternalVertex incidentVertex;

  private InternalEdge nextIncidence;

  private InternalEdge prevIncidence;

  @Override public InternalEdge getNextEdgeInESeq() {
    assert isValid();
    return nextEdge;
  }

  @Override public InternalEdge getPrevEdgeInESeq() {
    assert isValid();
    return prevEdge;
  }

  @Override public InternalVertex getIncidentVertex() {
    return incidentVertex;
  }

  @Override public InternalEdge getNextIncidenceInISeq() {
    return nextIncidence;
  }

  @Override public InternalEdge getPrevIncidenceInISeq() {
    return prevIncidence;
  }

  @Override public void setNextEdgeInGraph(Edge nextEdge) {
    this.nextEdge = (InternalEdge) nextEdge;
  }

  @Override public void setPrevEdgeInGraph(Edge prevEdge) {
    this.prevEdge = (InternalEdge) prevEdge;
  }

  @Override public void setIncidentVertex(Vertex v) {
    incidentVertex = (InternalVertex) v;
  }

  @Override public void setNextIncidenceInternal(InternalEdge nextIncidence) {
    this.nextIncidence = nextIncidence;
  }

  @Override public void setPrevIncidenceInternal(InternalEdge prevIncidence) {
    this.prevIncidence = prevIncidence;
  }

  protected EdgeImpl(int anId, Graph graph, Vertex alpha, Vertex omega) {
    super(anId, graph, alpha, omega);

<<<<<<< Unknown file: This is a bug in JDime.
=======
    addToGraph(graph, alpha, omega);
>>>>>>> /usr/src/app/output/jgralab/jgralab/6b4faa42c0de2bdb921de9423bda798251cc7903/src/de/uni_koblenz/jgralab/impl/std/EdgeImpl.java/right.java
  }

  protected void addToGraph(Graph graph, Vertex alpha, Vertex omega) {
    ((GraphImpl) graph).addEdge(this, alpha, omega);
  }

  @Override public void setId(int id) {
    assert id >= 0;
    this.id = id;
  }
}