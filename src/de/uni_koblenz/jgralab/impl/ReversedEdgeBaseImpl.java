package de.uni_koblenz.jgralab.impl;
import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.AggregationKind;
import de.uni_koblenz.jgralab.schema.EdgeClass;

/**
 * TODO add comment
 * 
 * @author ist@uni-koblenz.de
 */
public abstract class ReversedEdgeBaseImpl extends IncidenceImpl implements InternalEdge {
  protected final EdgeBaseImpl normalEdge;

  /**
	 * @param normalEdge
	 * @param graph
	 */
  public ReversedEdgeBaseImpl(EdgeBaseImpl normalEdge, Graph graph) {
    super(graph);
    assert normalEdge != null;
    this.normalEdge = normalEdge;
  }

  @Override public Class<? extends AttributedElement> getM1Class() {
    return normalEdge.getM1Class();
  }

  @Override public int compareTo(AttributedElement a) {
    assert isValid();
    assert (a instanceof Edge);
    Edge e = (Edge) a;
    assert e.isValid();
    assert getGraph() == e.getGraph();
    if (e == getNormalEdge()) {
      return 1;
    } else {
      return Math.abs(getId()) - Math.abs(e.getId());
    }
  }

  @Override public void delete() {
    normalEdge.delete();
  }

  @Override public Vertex getAlpha() {
    return normalEdge.getIncidentVertex();
  }

  @Override public <T extends java.lang.Object> T getAttribute(String name) {
    return normalEdge.getAttribute(name);
  }

  @Override public <T extends java.lang.Object> void setAttribute(String name, T data) {
    normalEdge.setAttribute(name, data);
  }

  @Override public int getId() {
    return -normalEdge.getId();
  }

  @Override public Edge getNextEdge() {
    return normalEdge.getNextEdge();
  }

  @Override public InternalEdge getNextEdgeInESeq() {
    return normalEdge.getNextEdgeInESeq();
  }

  @Override public Edge getPrevEdge() {
    return normalEdge.getPrevEdge();
  }

  @Override public InternalEdge getPrevEdgeInESeq() {
    return normalEdge.getPrevEdgeInESeq();
  }

  @Override public Edge getNextEdge(EdgeClass anEdgeClass) {
    return normalEdge.getNextEdge(anEdgeClass);
  }

  @Override public Edge getNextEdge(Class<? extends Edge> anEdgeClass) {
    return normalEdge.getNextEdge(anEdgeClass);
  }

  @Override public Edge getNormalEdge() {
    return normalEdge;
  }

  @Override public Vertex getOmega() {
    assert isValid();
    return getIncidentVertex();
  }

  @Override public Edge getReversedEdge() {
    return normalEdge;
  }

  @Override public Vertex getThat() {
    return getAlpha();
  }

  @Override public String getThatRole() {
    return normalEdge.getThisRole();
  }

  @Override public Vertex getThis() {
    return getOmega();
  }

  @Override public String getThisRole() {
    return normalEdge.getThatRole();
  }

  @Override public void graphModified() {
    assert isValid();
    graph.graphModified();
  }

  @Override public boolean isAfterEdge(Edge e) {
    return normalEdge.isAfterEdge(e);
  }

  @Override public boolean isBeforeEdge(Edge e) {
    return normalEdge.isBeforeEdge(e);
  }

  @Override public boolean isNormal() {
    return false;
  }

  @Override public void putAfterEdge(Edge e) {
    normalEdge.putAfterEdge(e);
  }

  @Override public void putBeforeEdge(Edge e) {
    normalEdge.putBeforeEdge(e);
  }

  @Override public void setAlpha(Vertex alpha) {
    normalEdge.setAlpha(alpha);
  }

  @Override public void setOmega(Vertex omega) {
    normalEdge.setOmega(omega);
  }

  @Override public void setThat(Vertex v) {
    normalEdge.setAlpha(v);
  }

  @Override public void setThis(Vertex v) {
    normalEdge.setOmega(v);
  }

  @Override public String toString() {
    assert isValid();
    return "-e" + normalEdge.id + ": " + getAttributedElementClass().getQualifiedName();
  }

  @Override public boolean isValid() {
    return graph.eSeqContainsEdge(this);
  }

  @Override public void setId(int id) {
    normalEdge.setId(id);
  }

  @Override public AggregationKind getAggregationKind() {
    return normalEdge.getAggregationKind();
  }

  @Override public AggregationKind getAlphaAggregationKind() {
    return normalEdge.getAlphaAggregationKind();
  }

  @Override public AggregationKind getOmegaAggregationKind() {
    return normalEdge.getOmegaAggregationKind();
  }

  @Override public AggregationKind getThisAggregationKind() {
    return normalEdge.getOmegaAggregationKind();
  }

  @Override public AggregationKind getThatAggregationKind() {
    return normalEdge.getAlphaAggregationKind();
  }

  @Override public void setNextEdgeInGraph(Edge nextEdge) {
    normalEdge.setNextEdgeInGraph(nextEdge);
  }

  @Override public void setPrevEdgeInGraph(Edge prevEdge) {
    normalEdge.setPrevEdgeInGraph(prevEdge);
  }
}