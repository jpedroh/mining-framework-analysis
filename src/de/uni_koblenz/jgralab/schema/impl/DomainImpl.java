package de.uni_koblenz.jgralab.schema.impl;
import de.uni_koblenz.jgralab.schema.Domain;
import de.uni_koblenz.jgralab.schema.Package;

public abstract class DomainImpl extends NamedElementImpl implements Domain {
  protected DomainImpl(String simpleName, Package pkg) {
    super(simpleName, pkg, pkg.getSchema());
    register();
  }

  @Override protected final void register() {
    ((SchemaImpl) getSchema()).addDomain(this);
    ((PackageImpl) parentPackage).addDomain(this);
    ((SchemaImpl) getSchema()).getDomainsDag().createNode(this);
  }

  @Override public String toString() {
    return "domain " + qualifiedName;
  }

  @Override public String getUniqueName() {
    return qualifiedName;
  }

  @Override public boolean isBoolean() {
    return false;
  }
}