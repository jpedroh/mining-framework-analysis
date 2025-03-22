package de.uni_koblenz.jgralab.schema;

/**
 * Base class of List and Set domains.
 * 
 * @author ist@uni-koblenz.de
 */
public interface CollectionDomain extends CompositeDomain {
  /**
	 * @return the base domain of the collection
	 */
  public Domain getBaseDomain();
}