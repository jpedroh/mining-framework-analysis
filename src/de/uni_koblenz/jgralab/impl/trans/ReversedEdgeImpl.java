/*
 * JGraLab - The Java graph laboratory
 * (c) 2006-2010 Institute for Software Technology
 *               University of Koblenz-Landau, Germany
 * 
 *               ist@uni-koblenz.de
 * 
 * Please report bugs to http://serres.uni-koblenz.de/bugzilla
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.uni_koblenz.jgralab.impl.trans;

import java.util.Map;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphException;
import de.uni_koblenz.jgralab.impl.EdgeBaseImpl;
import de.uni_koblenz.jgralab.impl.IncidenceImpl;
import de.uni_koblenz.jgralab.impl.VertexBaseImpl;
import de.uni_koblenz.jgralab.trans.ListPosition;
import de.uni_koblenz.jgralab.trans.TransactionState;
import de.uni_koblenz.jgralab.trans.VersionedIncidence;

/**
 * Implementation of <code>ReversedEdgeImpl</code> with versioning.
 * 
 * Incident vertex and next and previous incidence in Iseq(incident vertex) are
 * versioned.
 * 
 * @author Jose Monte(monte@uni-koblenz.de)
 */
public abstract class ReversedEdgeImpl extends
		de.uni_koblenz.jgralab.impl.ReversedEdgeBaseImpl implements
		VersionedIncidence {
	// attributes inherited from <code>IncidenceImpl</code>
	protected VersionedReferenceImpl<VertexBaseImpl> incidentVertex;
	protected VersionedReferenceImpl<IncidenceImpl> nextIncidence;
	protected VersionedReferenceImpl<IncidenceImpl> prevIncidence;

	/**
	 * Initialization of versioned attributes is avoided here, to not have
	 * persistent and temporary values for new instances within the transaction
	 * this instance is created in.
	 * 
	 * @param normalEdge
	 * @param graph
	 */
	protected ReversedEdgeImpl(EdgeBaseImpl normalEdge, Graph graph) {
		super(normalEdge, graph);
	}
	
	@Override
	public Edge getNextEdge() {
		return getNextIncidence();
	}
	
	@Override
	public Edge getPrevEdge() {
		return getPrevIncidence();
	}

	// --- getter ---//

	@Override
	protected VertexBaseImpl getIncidentVertex() {
		if (incidentVertex == null) {
			return null;
		}
		// Note: if this instance isn't valid within current transaction, then
		// null is returned
		return incidentVertex.getValidValue(graph.getCurrentTransaction());
	}

	@Override
	protected IncidenceImpl getNextIncidence() {
		if (nextIncidence == null) {
			return null;
		}
		// Note: if this instance isn't valid within current transaction, then
		// null is returned
		return nextIncidence.getValidValue(graph.getCurrentTransaction());
	}

	@Override
	protected IncidenceImpl getPrevIncidence() {
		if (prevIncidence == null) {
			return null;
		}
		// Note: if this instance isn't valid within current transaction, then
		// null is returned
		return prevIncidence.getValidValue(graph.getCurrentTransaction());
	}

	// --- setter --- //
	@Override
	protected void setIncidentVertex(VertexBaseImpl v) {
		if (graph.isLoading()) {
			incidentVertex = new VersionedReferenceImpl<VertexBaseImpl>(normalEdge,
					v);
		} else {
			// initialize here
			if (incidentVertex == null) {
				incidentVertex = new VersionedReferenceImpl<VertexBaseImpl>(
						normalEdge);
			}
			incidentVertex.setValidValue(v, graph.getCurrentTransaction());
		}
	}

	@Override
	protected void setNextIncidence(IncidenceImpl nextIncidence) {
		if (graph.isLoading()) {
			this.nextIncidence = new VersionedReferenceImpl<IncidenceImpl>(
					normalEdge, nextIncidence);
		} else {
			TransactionImpl transaction = (TransactionImpl) graph
					.getCurrentTransaction();
			if (transaction == null) {
				throw new GraphException("Current transaction is null.");
			}
			boolean explicitChange = false;
			// check if next incidence has been changed explicitly or not - only
			// relevant in writing-phase
			if (transaction.getState() == TransactionState.WRITING) {
				if (transaction.changedIncidences != null) {
					VertexBaseImpl currentIncidentVertex = this.incidentVertex
							.getTemporaryValue(transaction);
					Map<IncidenceImpl, Map<ListPosition, Boolean>> incidenceList = transaction.changedIncidences
							.get(currentIncidentVertex);
					explicitChange = false;
					if (incidenceList != null) {
						explicitChange = incidenceList.containsKey(this)
								&& (incidenceList.get(this).keySet()
										.contains(ListPosition.NEXT));
					}
				}
			}
			// initialize here
			if (this.nextIncidence == null) {
				this.nextIncidence = new VersionedReferenceImpl<IncidenceImpl>(
						normalEdge);
			}
			this.nextIncidence.setValidValue(nextIncidence, transaction,
					explicitChange);
		}
	}

	@Override
	protected void setPrevIncidence(IncidenceImpl prevIncidence) {
		if (graph.isLoading()) {
			this.prevIncidence = new VersionedReferenceImpl<IncidenceImpl>(
					normalEdge, prevIncidence);
		} else {
			TransactionImpl transaction = (TransactionImpl) graph
					.getCurrentTransaction();
			if (transaction == null) {
				throw new GraphException("Current transaction is null.");
			}
			boolean explicitChange = false;
			// check if previous incidence has been changed explicitly or not -
			// only relevant in writing-phase
			if (transaction.getState() == TransactionState.WRITING) {
				if (transaction.changedIncidences != null) {
					VertexBaseImpl currentIncidentVertex = this.incidentVertex
							.getTemporaryValue(transaction);
					Map<IncidenceImpl, Map<ListPosition, Boolean>> incidenceList = transaction.changedIncidences
							.get(currentIncidentVertex);
					explicitChange = false;
					if (incidenceList != null) {
						explicitChange = incidenceList.containsKey(this)
								&& (incidenceList.get(this).keySet()
										.contains(ListPosition.PREV));
					}
				}
			}
			// initialize here
			if (this.prevIncidence == null) {
				this.prevIncidence = new VersionedReferenceImpl<IncidenceImpl>(
						normalEdge);
			}
			this.prevIncidence.setValidValue(prevIncidence, transaction,
					explicitChange);
		}
	}

	@Override
	public VersionedReferenceImpl<IncidenceImpl> getVersionedNextIncidence() {
		return this.nextIncidence;
	}

	@Override
	public VersionedReferenceImpl<IncidenceImpl> getVersionedPrevIncidence() {
		return this.prevIncidence;
	}
	
	@Override
	public String toString() {
		return "-e" + normalEdge.getId() + ": "
				+ getAttributedElementClass().getQualifiedName();
	}
}
