package de.uni_koblenz.jgralab.schema;
import org.pcollections.PVector;

/**
 * Represents an enumeration domain, instances may exist multiple times per
 * schema.
 *
 * @author ist@uni-koblenz.de
 */
public interface EnumDomain extends Domain {
  /**
	 * @return all the enum constants of this enum domain
	 */
  public PVector<String> getConsts();

  /**
	 * add an enum constant
	 *
	 * @param constName
	 */
  void addConst(String constName);

  public Class<? extends Object> getSchemaClass();
}