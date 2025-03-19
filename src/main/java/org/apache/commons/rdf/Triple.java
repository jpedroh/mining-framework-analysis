package org.apache.commons.rdf;

public interface Triple {
  Resource getSubject();

  /**
	 * The predicate {@link IRI} of this triple.
	 * 
	 * @return The predicate {@link IRI} of this triple.
	 * @see <a href="http://www.w3.org/TR/rdf11-concepts/#dfn-predicate">RDF-1.1
	 *      Triple predicate</a>
	 */
  IRI getPredicate();

  RDFTerm getObject();
}