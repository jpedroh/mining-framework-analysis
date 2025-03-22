package de.uni_koblenz.jgralab.schema.impl;
import de.uni_koblenz.jgralab.schema.BasicDomain;

public abstract class BasicDomainImpl extends DomainImpl implements BasicDomain {
  protected BasicDomainImpl(String simpleName, 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/BasicDomainImpl.java/left.java
  Package
=======
  PackageImpl
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/BasicDomainImpl.java/right.java
   pkg) {
    super(simpleName, pkg);
  }

  @Override public boolean isComposite() {
    return false;
  }

  public static final boolean isBasicDomain(String domainName) {
    return BASIC_DOMAINS.contains(domainName);
  }
}