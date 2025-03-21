package de.uni_koblenz.jgralab.schema;
import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.schema.exception.SchemaException;

/**
 * represents an attribute in schema layer, consists of a name and a domain
 *
 * @author ist@uni-koblenz.de
 *
 */
public interface Attribute {
  /**
	 * @return the textual representation of the attribute
	 */
  @Override public String toString();

  /**
	 * @return the domain of the attribute
	 */
  public Domain getDomain();

  /**
	 * @return the name of the attribute
	 */
  public String getName();

  /**
	 * @param newName
	 *            the new attribute name
	 */
  public void setName(String newName);

  /**
	 * Returns the default value of this Attribute as String conforming to the
	 * TG representation of the default value.
	 *
	 *
	 * @return the default value of this Attribute, or null, if no default value
	 *         was specified
	 */
  public String getDefaultValueAsString();

  /**
	 * Sets the default value of this Attribute as String conforming to the TG
	 * representation of the default value. The default value can be set only
	 * once.
	 *
	 * @param defaultValue
	 *            the default value of this Attribute in TG syntax
	 *
	 * @throws SchemaException
	 *             if a default value was already set.
	 */
  public void setDefaultValueAsString(String defaultValue) throws SchemaException;

  /**
	 * Set default value for attributed elements with transaction support.
	 *
	 * @param element
	 * @throws GraphIOException
	 */
  public void setDefaultTransactionValue(AttributedElement<?, ?> element) throws GraphIOException;

  /**
	 * Set default value for attributed elements without transaction support.
	 *
	 * @param el
	 * @throws GraphIOException
	 */
  public void setDefaultValue(AttributedElement<?, ?> el) throws GraphIOException;

  /**
	 * @return the owning AttributedElementClass
	 */
  public AttributedElementClass<?, ?> getAttributedElementClass();

  /**
	 * Returns a String suitable to sort Attributes of an AttributedElement.
	 *
	 * @return the sort key of this Attribute
	 */
  public String getSortKey();

  /**
	 * Deletes this attribute.
	 */
  public void delete();
}