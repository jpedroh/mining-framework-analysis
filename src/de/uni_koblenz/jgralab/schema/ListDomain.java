package de.uni_koblenz.jgralab.schema;

/**
 * Represents a List<...> domain, instances may exist multiple times per schema.
 * 
 * @author ist@uni-koblenz.de
 */
public interface ListDomain extends CollectionDomain {
  public final static String LISTDOMAIN_NAME = "List";

  public final static String LISTDOMAIN_TYPE = "org.pcollections.PVector";

  public final static String EMPTY_LIST = "de.uni_koblenz.jgralab.JGraLab.vector()";
}