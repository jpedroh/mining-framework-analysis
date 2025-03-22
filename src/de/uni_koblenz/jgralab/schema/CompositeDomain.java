package de.uni_koblenz.jgralab.schema;

/**
 * Base class of the composite domains List, Set, Record and Map.
 * 
 * @author ist@uni-koblenz.de
 */
public interface CompositeDomain extends Domain {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/CompositeDomain.java/left.java
  /**
	 * Returns the component composite domains of the composite domain. If the
	 * component composite domains contain other composite domains, the latter
	 * are not included in the returned Set.
	 * 
	 * @return the Set of the composite domain's component composite domains
	 */
  public Set<CompositeDomain> getAllComponentCompositeDomains();
=======
>>>>>>> Unknown file: This is a bug in JDime.



<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/CompositeDomain.java/left.java
  /**
	 * Returns the component domains of the composite domain. If component
	 * composite domains contain other composite domains, the latter are not
	 * included in the returned Set.
	 * 
	 * @return the Set of the composite domain's component domains
	 */
  public Set<Domain> getAllComponentDomains();
=======
>>>>>>> Unknown file: This is a bug in JDime.
}