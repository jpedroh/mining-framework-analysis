package de.uni_koblenz.jgralab.schema;
import java.util.Collection;

/**
 * Represents a RecordDomain, instances may exist multiple times per schema.
 * 
 * @author ist@uni-koblenz.de
 */
public interface RecordDomain extends CompositeDomain {
  public static class RecordComponent {
    private String name;

    private Domain domain;

    public RecordComponent(String name, Domain domain) {
      this.name = name;
      this.domain = domain;
    }

    @Override public String toString() {
      return getName() + ": " + getDomain();
    }

    public String getName() {
      return name;
    }

    public Domain getDomain() {
      return domain;
    }

    @Override public boolean equals(Object obj) {
      if (!(obj instanceof RecordComponent)) {
        return false;
      }
      RecordComponent other = (RecordComponent) obj;
      return name.equals(other.name) && domain.equals(other.domain);
    }

    @Override public int hashCode() {
      int x = 31;
      x = x * name.hashCode() + x;
      x = x * domain.hashCode() + x;
      return x;
    }
  }

  /**
	 * @return a map of all the record domain components
	 */
  public Collection<RecordComponent> getComponents();

  /**
	 * Adds a record domain component to the internal list
	 * 
	 * @param name
	 *            the unique name of the record domain component in the record
	 *            domain
	 * @param domain
	 *            the domain of the component
	 */
  public void addComponent(String name, Domain domain);

  /**
	 * @param name
	 *            a component name
	 * @return true if this RecordDomain has a coponent <code>name</code>
	 */
  public Boolean hasComponent(String name);

  /**
	 * Returns the standard-implementation-class (folder impl.std)
	 * 
	 * @return java representation of this attribute
	 */
  @Override public String getJavaAttributeImplementationTypeName(String schemaRootPackagePrefix);

  public Class<? extends Object> getSchemaClass();
}