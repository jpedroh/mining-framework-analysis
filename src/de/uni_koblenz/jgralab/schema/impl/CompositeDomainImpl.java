package de.uni_koblenz.jgralab.schema.impl;
import de.uni_koblenz.jgralab.schema.CompositeDomain;
import de.uni_koblenz.jgralab.schema.Package;

public abstract class CompositeDomainImpl extends DomainImpl implements CompositeDomain {
  protected CompositeDomainImpl(String simpleName, 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/CompositeDomainImpl.java/left.java
  Package
=======
  PackageImpl
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/CompositeDomainImpl.java/right.java
   pkg) {
    super(simpleName, pkg);
  }


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/CompositeDomainImpl.java/left.java
  @Override public Set<CompositeDomain> getAllComponentCompositeDomains() {
    Domain d;
    HashSet<CompositeDomain> componentCompositeDomains = new HashSet<CompositeDomain>();
    Set<Domain> componentDomains = getAllComponentDomains();
    for (Iterator<Domain> cdit = componentDomains.iterator(); cdit.hasNext(); ) {
      d = cdit.next();
      if (d instanceof CompositeDomain) {
        componentCompositeDomains.add((CompositeDomain) d);
      } else {
        cdit.remove();
      }
    }
    return componentCompositeDomains;
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  @Override public boolean isComposite() {
    return true;
  }

  @Override public boolean isPrimitive() {
    return false;
  }
}