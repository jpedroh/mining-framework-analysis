package de.uni_koblenz.jgralab.impl;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.TraversalContext;
import de.uni_koblenz.jgralab.schema.AggregationKind;
import de.uni_koblenz.jgralab.schema.EdgeClass;

/**
 * Common base class for EdgeImpl and ReversedEdgeImpl. Implements incidence
 * list and related operations.
 * 
 * @author ist@uni-koblenz.de
 */
public abstract class IncidenceImpl extends GraphElementImpl implements Edge, InternalEdge {
  protected IncidenceImpl(Graph graph) {
    super(graph);
  }


<<<<<<< /usr/src/app/output/jgralab/jgralab/ba28dc3943bf31e37f3beb96450dac330ebebd47/src/de/uni_koblenz/jgralab/impl/IncidenceImpl.java/left.java
  @Override public InternalEdge getNextIncidence() {
    InternalEdge nextIncidence = getNextIncidenceInISeq();
    TraversalContext tc = graph.getTraversalContext();
    if (!(tc == null || nextIncidence == null || tc.containsEdge(nextIncidence))) {
      while (!(nextIncidence == null || tc.containsEdge(nextIncidence))) {
        nextIncidence = nextIncidence.getNextIncidenceInISeq();
      }
    }
    return nextIncidence;
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  @Override public InternalEdge getPrevIncidence() {
    InternalEdge prevIncidence = getPrevIncidenceInISeq();
    TraversalContext tc = graph.getTraversalContext();
    if (!(tc == null || prevIncidence == null || tc.containsEdge(prevIncidence))) {
      while (!(prevIncidence == null || tc.containsEdge(prevIncidence))) {
        prevIncidence = prevIncidence.getPrevIncidenceInISeq();
      }
    }
    return prevIncidence;
  }

  @Override public Edge getNextIncidence() {
    assert isValid();
    return;
  }

  @Override public Edge getNextIncidence(EdgeDirection orientation) {
    assert isValid();
    Edge i = getNextIncidence();
    switch (orientation) {
      case IN:
      while ((i != null) && i.isNormal()) {
        i = i.getNextIncidence();
      }
      return i;
      case OUT:
      while ((i != null) && !i.isNormal()) {
        i = i.getNextIncidence();
      }
      return i;
      case INOUT:
      return i;
      default:
      throw new RuntimeException("FIXME!");
    }
  }

  @Override public Edge getNextIncidence(boolean thisIncidence, AggregationKind... kinds) {
    assert isValid();
    Edge i = getNextIncidence();
    if (kinds.length == 0) {
      return i;
    }
    while (i != null) {
      for (AggregationKind element : kinds) {
        if ((thisIncidence ? i.getThisAggregationKind() : i.getThatAggregationKind()) == element) {
          return i;
        }
      }
      i = i.getNextIncidence();
    }
    return null;
  }

  @Override public Edge getNextIncidence(Class<? extends Edge> anEdgeClass) {
    assert anEdgeClass != null;
    assert isValid();
    return getNextIncidence(anEdgeClass, EdgeDirection.INOUT);
  }

  @Override public Edge getNextIncidence(Class<? extends Edge> anEdgeClass, EdgeDirection orientation) {
    assert anEdgeClass != null;
    assert isValid();
    Edge currentEdge = getNextIncidence(orientation);
    while (currentEdge != null) {
      if (anEdgeClass.isInstance(currentEdge)) {
        return currentEdge;
      }
      currentEdge = currentEdge.getNextIncidence(orientation);
    }
    return null;
  }

  @Override public Edge getNextIncidence(EdgeClass anEdgeClass) {
    assert anEdgeClass != null;
    assert isValid();
    return getNextIncidence(anEdgeClass.getM1Class(), EdgeDirection.INOUT);
  }

  @Override public Edge getNextIncidence(EdgeClass anEdgeClass, EdgeDirection orientation) {
    assert anEdgeClass != null;
    assert isValid();
    return getNextIncidence(anEdgeClass.getM1Class(), orientation);
  }

  @Override public boolean isBeforeIncidence(Edge e) {
    assert e != null;
    assert isValid();
    assert e.isValid();
    assert getGraph() == e.getGraph();
    assert getThis() == e.getThis();
    if (e == this) {
      return false;
    }
    IncidenceImpl i = (IncidenceImpl) getNextIncidenceInISeq();
    while ((i != null) && (i != e)) {
      i = (IncidenceImpl) i.getNextIncidenceInISeq();
    }
    return i != null;
  }

  @Override public boolean isAfterIncidence(Edge e) {
    assert e != null;
    assert isValid();
    assert e.isValid();
    assert getGraph() == e.getGraph();
    assert getThis() == e.getThis();
    if (e == this) {
      return false;
    }
    IncidenceImpl i = (IncidenceImpl) getPrevIncidenceInISeq();
    while ((i != null) && (i != e)) {
      i = (IncidenceImpl) i.getPrevIncidenceInISeq();
    }
    return i != null;
  }

  @Override public void putIncidenceBefore(Edge e) {
    assert e != null;
    assert isValid();
    assert e.isValid();
    assert getGraph() == e.getGraph();
    assert getThis() == e.getThis();
    VertexBaseImpl v = (VertexBaseImpl) getThis();
    assert v.isValid();
    assert e != this;
    if (this != e) {
      v.putIncidenceBefore((IncidenceImpl) e, this);
    }
  }

  @Override public void putIncidenceAfter(Edge e) {
    assert e != null;
    assert isValid();
    assert e.isValid();
    assert getGraph() == e.getGraph();
    assert getThis() == e.getThis() : "this-vertices don\'t match: " + getThis() + " != " + e.getThis();
    VertexBaseImpl v = (VertexBaseImpl) getThis();
    assert v.isValid();
    assert e != this;
    if (this != e) {
      v.putIncidenceAfter((IncidenceImpl) e, this);
    }
  }
}