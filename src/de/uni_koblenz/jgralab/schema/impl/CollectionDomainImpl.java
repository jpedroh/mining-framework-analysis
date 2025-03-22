package de.uni_koblenz.jgralab.schema.impl;
import de.uni_koblenz.jgralab.schema.CollectionDomain;
import de.uni_koblenz.jgralab.schema.Domain;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.exception.SchemaException;
import de.uni_koblenz.jgralab.schema.exception.WrongSchemaException;

public abstract class CollectionDomainImpl extends CompositeDomainImpl implements CollectionDomain {
  /**
	 * The base domain, every element of an instance of a collection must be of
	 * that domain
	 */
  protected Domain baseDomain;

  protected CollectionDomainImpl(String simpleName, 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/CollectionDomainImpl.java/left.java
  Package
=======
  PackageImpl
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/CollectionDomainImpl.java/right.java
   pkg, Domain baseDomain) {
    super(simpleName, pkg);
    if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/CollectionDomainImpl.java/left.java
    pkg.getSchema().getDomain(baseDomain.getQualifiedName()) != baseDomain
=======
    schema != ((DomainImpl) baseDomain).schema
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/CollectionDomainImpl.java/right.java
    ) {
      throw new 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/CollectionDomainImpl.java/left.java
      WrongSchemaException
=======
      SchemaException
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/CollectionDomainImpl.java/right.java
      (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/CollectionDomainImpl.java/left.java
      baseDomain.getQualifiedName() + " must be a domain of the schema " + pkg.getSchema().getQualifiedName()
=======
      "Base domain " + baseDomain.getQualifiedName() + " belongs to a different schema."
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/CollectionDomainImpl.java/right.java
      );
    }
    this.baseDomain = baseDomain;

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/CollectionDomainImpl.java/left.java
    ((SchemaImpl) pkg.getSchema()).getDomainsDag().createEdge(baseDomain, this)
=======
    schema.addDomainDependency(this, baseDomain)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/CollectionDomainImpl.java/right.java
    ;
  }

  @Override public Domain getBaseDomain() {
    return baseDomain;
  }
}