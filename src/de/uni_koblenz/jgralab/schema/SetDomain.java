package de.uni_koblenz.jgralab.schema;

/**
 * represents a Set<...> domain, instances may exist multiple times per schema
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public interface SetDomain extends CollectionDomain {
  public static final String SETDOMAIN_NAME = "Set";

  public final static String SETDOMAIN_TYPE = "org.pcollections.PSet";

  public final static String EMPTY_SET = "de.uni_koblenz.jgralab.JGraLab.set()";
}