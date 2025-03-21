package de.uni_koblenz.jgralab.schema;
import java.io.IOException;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.codegenerator.CodeBlock;

/**
 * Base class of all JGraLab Domains.
 * 
 * @author ist@uni-koblenz.de
 */
public interface Domain extends NamedElement {
  /**
	 * example: int for Integer, List<Boolean> for a list with basedomain
	 * boolean
	 * 
	 * @return java representation of this attribute
	 */
  public String getJavaAttributeImplementationTypeName(String schemaRootPackagePrefix);

  /**
	 * example: Integer for integer
	 * 
	 * @return the non primitive representation of this attribute, only affects
	 *         int, boolean, double
	 */
  public String getJavaClassName(String schemaRootPackagePrefix);

  /**
	 * @return a code fragment to read a value of this domain from the GraphIO
	 *         object named graphIoVariablename into the variableName
	 */
  public CodeBlock getReadMethod(String schemaPrefix, String variableName, String graphIoVariableName);

  /**
	 * example: List<String>
	 * 
	 * @return the string how the domain is represented in the tg file
	 */
  public String getTGTypeName(Package pkg);

  /**
	 * @return a code fragment to write a value of this domain to the GraphIO
	 *         oject named graphIoVariablename into the variableName
	 */
  public CodeBlock getWriteMethod(String schemaRootPackagePrefix, String variableName, String graphIoVariableName);

  /**
	 * @return true if this domain is a composite domain
	 */
  public boolean isComposite();

  /**
	 * @return true if this domain is a primitive type
	 */
  public boolean isPrimitive();

  /**
	 * @return true if this domain is a primitive type
	 */
  public boolean isBoolean();

  /**
	 * example: Integer for integer List<Boolean> for a list with basedomain
	 * boolean
	 * 
	 * @return java representation of this attribute
	 */
  public String getTransactionJavaAttributeImplementationTypeName(String schemaRootPackagePrefix);

  /**
	 * example: Integer for integer
	 * 
	 * @return the non primitive representation of this attribute, only affects
	 *         int, boolean, double
	 */
  public String getTransactionJavaClassName(String schemaRootPackagePrefix);

  /**
	 * @return a code fragment to read a value of this domain from the GraphIO
	 *         object named graphIoVariablename into the variableName for
	 *         transaction support
	 */
  public CodeBlock getTransactionReadMethod(String schemaPrefix, String variableName, String graphIoVariableName);

  /**
	 * @return a code fragment to write a value of this domain to the GraphIO
	 *         object named graphIoVariablename into the variableName for
	 *         transaction support
	 */
  public CodeBlock getTransactionWriteMethod(String schemaRootPackagePrefix, String variableName, String graphIoVariableName);

  /**
	 * @param schemaRootPackagePrefix
	 * @return the name of the versioned class implementation for this domain.
	 */
  public String getVersionedClass(String schemaRootPackagePrefix);

  /**
	 * @return the initial value for this Domain
	 */
  public String getInitialValue();

  /**
	 * Parses a String representing an attribute value and returns an Object
	 * representing the attribute value. The created Object's type is determined
	 * by the attribute's Domain and the generic TGraph implementation's mapping
	 * of types to the domains. <br />
	 * <br />
	 * The type mapping is as follows:
	 * <table><tr><td><b>Domain</b></td><td><b>Java-type</b></td></tr>
	 * <tr><td>BooleanDomain</td><td>Boolean</td></tr>
	 * <tr><td>IntegerDomain</td><td>Integer</td></tr>
	 * <tr><td>LongDomain</td><td>Long</td></tr>
	 * <tr><td>DoubleDomain</td><td>Double</td></tr>
	 * <tr><td>StringDomain</td><td>String</td></tr>
	 * <tr><td>EnumDomain</td><td>String (possible values are determined by the EnumDomain)</td></tr>
	 * <tr><td>SetDomain</td><td>PSet</td></tr>
	 * <tr><td>ListDomain</td><td>PVector</td></tr>
	 * <tr><td>MapDomain</td><td>PMap</td></tr>
	 * <tr><td>RecordDomain</td><td>de.uni_koblenz.jgralab.impl.RecordImpl</td></tr></table>
	 * @param io
	 *            The {@link GraphIO} object serving as parser for the
	 *            attribute's value.
	 */
  public Object parseGenericAttribute(GraphIO io) throws GraphIOException;

  /**
	 * Serializes an attribute value in the generic implementation of this
	 * domain.
	 * 
	 * @param io
	 * @param data
	 * @throws IOException
	 */
  public void serializeGenericAttribute(GraphIO io, Object data) throws IOException;

  /**
	 * Checks, if an attribute value in the generic implementation conforms
	 * to this domain.
	 */
  public boolean isConformGenericValue(Object value);
}