package de.uni_koblenz.jgralab.impl.std;

import java.io.IOException;
import java.util.HashMap;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphException;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.NoSuchAttributeException;
import de.uni_koblenz.jgralab.TemporaryEdge;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.GraphBaseImpl;
import de.uni_koblenz.jgralab.impl.InternalEdge;
import de.uni_koblenz.jgralab.impl.InternalGraph;
import de.uni_koblenz.jgralab.impl.InternalVertex;
import de.uni_koblenz.jgralab.impl.ReversedEdgeBaseImpl;
import de.uni_koblenz.jgralab.schema.AggregationKind;
import de.uni_koblenz.jgralab.schema.EdgeClass;

public class TemporaryEdgeImpl extends EdgeImpl implements TemporaryEdge{

	private HashMap<String,Object> attributes;
	
	protected TemporaryEdgeImpl(int anId, Graph graph, Vertex alpha,
			Vertex omega) {
		super(anId, graph, alpha, omega);
		this.attributes = new HashMap<String, Object>();
		((GraphBaseImpl)graph).addEdge(this, alpha, omega);
	}

	@Override
	public EdgeClass getAttributedElementClass() {
		return graph.getGraphClass().getTemporaryEdgeClass();
	}


	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAttribute(String name) throws NoSuchAttributeException {
		return (T) this.attributes.get(name);
	}

	@Override
	public <T> void setAttribute(String name, T data)
			throws NoSuchAttributeException {
		this.attributes.put(name, data);
	}

	@Override
	public void initializeAttributesWithDefaultValues() {
		// TODO Auto-generated method stub
		// do nothing - no attributes
	}

	@Override
	public boolean isInstanceOf(EdgeClass cls) {
		return cls.equals(graph.getGraphClass().getTemporaryEdgeClass());
	}

	@Override
	public Edge transformToRealGraphElement(EdgeClass edgeClass) {
		
		//test if valid
		if(!this.getAlpha().getAttributedElementClass().isValidFromFor(edgeClass)){
			throw new GraphException("Transformation of temporary edge "+this+ " failed. " 
					+ this.getAlpha() + " is not a valid source for "+ edgeClass);
		}
		if(!this.getOmega().getAttributedElementClass().isValidToFor(edgeClass)){
			throw new GraphException("Transformation of temporary edge "+this+ " failed. "
					+ this.getOmega() + " is not a valid target for "+ edgeClass);
		}
		
		// save properties
		Graph g = graph;
		int tempID = id;
		InternalEdge prevEdge = this.getPrevEdgeInESeq();
		InternalEdge nextEdge = this.getNextEdgeInESeq();
		
		InternalEdge prevIncidence = this.getPrevIncidenceInISeq();
		InternalEdge nextIncidence = this.getNextIncidenceInISeq();
		
		InternalEdge prevIncidenceReversed = ((InternalEdge)this.getReversedEdge()).getPrevIncidenceInISeq();
		InternalEdge nextIncidenceReversed = ((InternalEdge) this.getReversedEdge()).getNextIncidenceInISeq();
		
		// create new edge
		InternalEdge newEdge = g.createEdge(edgeClass, getAlpha(), getOmega());
		
		// attributes
		for(String attrName : this.attributes.keySet()){
			if(newEdge.getAttributedElementClass().containsAttribute(attrName)){
				newEdge.setAttribute(attrName, this.attributes.get(attrName));
			}
		}
				
		InternalEdge newLastEdge = newEdge.getPrevEdgeInESeq();
		InternalEdge newLastIncidence = newEdge.getPrevIncidenceInISeq();
		System.out.println("DEBUG: "+newLastIncidence);
		InternalEdge newLastIncidenceReversed = ((InternalEdge)newEdge.getReversedEdge()).getPrevIncidenceInISeq();
		System.out.println("DEBUG: "+newLastIncidenceReversed);

		this.delete();
		
		// eSeq
		if(nextEdge != null){
			nextEdge.setPrevEdgeInGraph(newEdge);
			newEdge.setNextEdgeInGraph(nextEdge);
			newEdge.setPrevEdgeInGraph(prevEdge);
			
			newLastEdge.setNextEdgeInGraph(null);
			
			if(prevEdge != null){
				prevEdge.setNextEdgeInGraph(newEdge);
			}else{// Temporary Edge is first Edge in graph
				((InternalGraph)g).setFirstEdgeInGraph(newEdge);
			}

			((InternalGraph)g).setLastEdgeInGraph(newLastEdge);
		}
		
		// iSeq edge
		correctISeq(prevIncidence, nextIncidence, newEdge, newLastIncidence);
		
		// iSeq reversed
		correctISeq(prevIncidenceReversed, nextIncidenceReversed, 
				(InternalEdge) newEdge.getReversedEdge(), newLastIncidenceReversed);
		
		
		
		newEdge.setId(tempID);
		return newEdge;
	}

	private void correctISeq(InternalEdge prevIncidence,
			InternalEdge nextIncidence, InternalEdge newEdge,
			InternalEdge newLastIncidence) {
		if(nextIncidence != null){
			nextIncidence.setPrevIncidenceInternal(newEdge);
			newEdge.setNextIncidenceInternal(nextIncidence);
			newEdge.setPrevIncidenceInternal(prevIncidence);
			
			newLastIncidence.setNextIncidenceInternal(null);
			
			if(prevIncidence != null){
				prevIncidence.setNextIncidenceInternal(newEdge);
			}else{// Temporary Edge is first incidence
				((InternalVertex)newEdge.getThis()).setFirstIncidence(newEdge);
			}
			
			((InternalVertex)newEdge.getThis()).setLastIncidence(newLastIncidence);
		}
	}

	@Override
	public void deleteAttribute(String name) {
		((TemporaryEdge)this.getNormalEdge()).deleteAttribute(name);
		
	}

	@Override
	protected ReversedEdgeBaseImpl createReversedEdge() {
		return new TemporaryReversedEdgeImpl(this, graph);
	}
	
	@Override
	public AggregationKind getAlphaAggregationKind() {
		return getAttributedElementClass().getFrom().getAggregationKind();
	}

	@Override
	public AggregationKind getOmegaAggregationKind() {
		return getAttributedElementClass().getTo().getAggregationKind();
	}

	
	//........................................................
	
	

	@Override
	public Class<? extends Edge> getSchemaClass() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void readAttributeValueFromString(String attributeName, String value)
			throws GraphIOException, NoSuchAttributeException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String writeAttributeValueToString(String attributeName)
			throws IOException, GraphIOException, NoSuchAttributeException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void writeAttributeValues(GraphIO io) throws IOException,
			GraphIOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void readAttributeValues(GraphIO io) throws GraphIOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public AggregationKind getAggregationKind() {
		AggregationKind fromAK = (getAttributedElementClass()).getFrom()
				.getAggregationKind();
		AggregationKind toAK = (getAttributedElementClass()).getTo()
				.getAggregationKind();
		return fromAK != AggregationKind.NONE ? fromAK
				: (toAK != AggregationKind.NONE ? toAK : AggregationKind.NONE);
	}
}
