package de.uni_koblenz.jgralab.schema.impl;
import de.uni_koblenz.jgralab.schema.Domain;

public abstract class DomainImpl extends NamedElementImpl implements Domain {
  protected DomainImpl(String simpleName, 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/DomainImpl.java/left.java
  Package
=======
  PackageImpl
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/DomainImpl.java/right.java
   pkg) {
    super(simpleName, pkg, 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/DomainImpl.java/left.java
    pkg.getSchema()
=======
    (SchemaImpl) pkg.getSchema()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/DomainImpl.java/right.java
    );
    schema.addDomain(this);

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/DomainImpl.java/left.java
    register();
=======
    parentPackage.addDomain(this);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/DomainImpl.java/right.java
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