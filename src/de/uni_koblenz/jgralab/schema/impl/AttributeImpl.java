package de.uni_koblenz.jgralab.schema.impl;
import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.schema.Attribute;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;
import de.uni_koblenz.jgralab.schema.Domain;
import de.uni_koblenz.jgralab.schema.exception.SchemaException;

/**
 * TODO add comment
 *
 * @author ist@uni-koblenz.de
 */
public class AttributeImpl implements Attribute, Comparable<Attribute> {
  /**
	 * the name of the attribute
	 */
  private final String name;

  /**
	 * the domain of the attribute
	 */
  private final Domain domain;

  /**
	 * the owning AttributedElementClass of the atribute
	 */
  private final AttributedElementClass<?, ?> aec;

  /**
	 * defines a total order of all attributes
	 */
  private final String sortKey;

  private String defaultValueAsString;

  private Object defaultValue;

  private Object defaultTransactionValue;

  private boolean defaultTransactionValueComputed;

  private boolean defaultValueComputed;

  /**
	 * builds a new attribute
	 *
	 * @param name
	 *            the name of the attribute
	 * @param domain
	 *            the domain of the attribute
	 * @param aec
	 *            the {@link AttributedElementClass} owning the
	 *            {@link Attribute}
	 * @param defaultValue
	 *            a String in TG value format denoting the default value of this
	 *            Attribute, or null if no default value shall be specified.
	 */
  public AttributeImpl(String name, Domain domain, AttributedElementClass<?, ?> aec, String defaultValue) {
    this.name = name;
    this.domain = domain;
    this.aec = aec;
    sortKey = name + ":" + domain.getQualifiedName();
    setDefaultValueAsString(defaultValue);
  }

  @Override public String toString() {
    return "Attribute " + sortKey;
  }

  @Override public Domain getDomain() {
    return domain;
  }

  @Override public String getName() {
    return name;
  }

  @Override public AttributedElementClass<?, ?> getAttributedElementClass() {
    return aec;
  }

  @Override public boolean equals(Object o) {
    return this == o;
  }

  @Override public int hashCode() {
    return sortKey.hashCode() + aec.hashCode();
  }

  @Override public int compareTo(Attribute o) {
    int i = sortKey.compareTo(o.getSortKey());
    if (i != 0) {
      return i;
    }
    return aec.compareTo(o.getAttributedElementClass());
  }

  @Override public String getSortKey() {
    return sortKey;
  }

  @Override public String getDefaultValueAsString() {
    return defaultValueAsString;
  }

  @Override public void setDefaultValueAsString(String defaultValue) throws SchemaException {
    if (defaultValueAsString != null) {
      throw new SchemaException("Cannot assign a new default value to Attribute " + name + " of " + aec.getQualifiedName() + ".");
    }
    defaultValueAsString = defaultValue;
  }

  @Override public void setDefaultTransactionValue(AttributedElement<?, ?> element) throws GraphIOException {
    if (defaultValueAsString != null) {
      if (defaultTransactionValueComputed) {
        element.setAttribute(name, defaultTransactionValue);
      } else {
        if (defaultValueAsString != null) {
          element.readAttributeValueFromString(name, defaultValueAsString);
        }
        defaultTransactionValue = element.getAttribute(name);
        defaultTransactionValueComputed = true;
      }
    }
  }

  @Override public void setDefaultValue(AttributedElement<?, ?> element) throws GraphIOException {
    if (defaultValueComputed) {
      element.setAttribute(name, defaultValue);
    } else {
      if (defaultValueAsString != null) {
        element.readAttributeValueFromString(name, defaultValueAsString);
      }
      defaultValue = element.getAttribute(name);
      defaultValueComputed = true;
    }
  }
}