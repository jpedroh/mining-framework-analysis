package org.apache.commons.rdf;
import java.util.Optional;

public interface Quad {
  Resource getSubject();

  /**
	 * The predicate {@link IRI} of this quad.
	 * 
	 * @return The predicate {@link IRI} of this quad.
	 * @see <a href="http://www.w3.org/TR/rdf11-concepts/#dfn-predicate">RDF-1.1
	 *      Triple predicate</a>
	 */
  IRI getPredicate();

  RDFTerm getObject();

  Optional<BlankNodeOrIRI> getGraph();

  /**
     * Convert to a Triple
     */
  Triple asTriple();
}