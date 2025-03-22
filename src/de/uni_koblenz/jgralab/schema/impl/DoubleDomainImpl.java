package de.uni_koblenz.jgralab.schema.impl;
import java.io.IOException;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.codegenerator.CodeBlock;
import de.uni_koblenz.jgralab.codegenerator.CodeGenerator;
import de.uni_koblenz.jgralab.codegenerator.CodeSnippet;
import de.uni_koblenz.jgralab.schema.DoubleDomain;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.Schema;

public final class DoubleDomainImpl extends BasicDomainImpl implements DoubleDomain {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/DoubleDomainImpl.java/left.java
  protected
=======
>>>>>>> Unknown file: This is a bug in JDime.
   DoubleDomainImpl(Schema schema) {
    super(DOUBLEDOMAIN_NAME, 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/DoubleDomainImpl.java/left.java
    schema.getDefaultPackage()
=======
    (PackageImpl) schema.getDefaultPackage()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/DoubleDomainImpl.java/right.java
    );
  }

  @Override public String getJavaAttributeImplementationTypeName(String schemaRootPackagePrefix) {
    return "double";
  }

  @Override public String getJavaClassName(String schemaRootPackagePrefix) {
    return "java.lang.Double";
  }

  @Override public CodeBlock getReadMethod(String schemaPrefix, String variableName, String graphIoVariableName) {
    return new CodeSnippet(variableName + " = " + graphIoVariableName + ".matchDouble();");
  }

  @Override public String getTGTypeName(Package pkg) {
    return DOUBLEDOMAIN_NAME;
  }

  @Override public CodeBlock getWriteMethod(String schemaRootPackagePrefix, String variableName, String graphIoVariableName) {
    return new CodeSnippet(graphIoVariableName + ".writeDouble(" + variableName + ");");
  }

  @Override public CodeBlock getTransactionReadMethod(String schemaPrefix, String variableName, String graphIoVariableName) {
    return new CodeSnippet(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/DoubleDomainImpl.java/left.java
    this.getJavaAttributeImplementationTypeName(schemaPrefix) + " "
=======
    getJavaAttributeImplementationTypeName(schemaPrefix) + " "
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/DoubleDomainImpl.java/right.java
     + variableName + " = " + graphIoVariableName + ".matchDouble();");
  }

  @Override public CodeBlock getTransactionWriteMethod(String schemaRootPackagePrefix, String variableName, String graphIoVariableName) {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/DoubleDomainImpl.java/left.java
    this.getWriteMethod(schemaRootPackagePrefix, "get" + CodeGenerator.camelCase(variableName) + "()", graphIoVariableName)
=======
    getWriteMethod(schemaRootPackagePrefix, "get" + CodeGenerator.camelCase(variableName) + "()", graphIoVariableName)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/DoubleDomainImpl.java/right.java
    ;
  }

  @Override public String getTransactionJavaAttributeImplementationTypeName(String schemaRootPackagePrefix) {
    return "java.lang.Double";
  }

  @Override public String getTransactionJavaClassName(String schemaRootPackagePrefix) {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/DoubleDomainImpl.java/left.java
    this.getJavaClassName(schemaRootPackagePrefix)
=======
    getJavaClassName(schemaRootPackagePrefix)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/DoubleDomainImpl.java/right.java
    ;
  }

  @Override public String getVersionedClass(String schemaRootPackagePrefix) {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/DoubleDomainImpl.java/left.java
    "de.uni_koblenz.jgralab.impl.trans.VersionedReferenceImpl<" + this.getTransactionJavaClassName(schemaRootPackagePrefix)
=======
    "de.uni_koblenz.jgralab.impl.trans.VersionedReferenceImpl<" + getTransactionJavaClassName(schemaRootPackagePrefix)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/DoubleDomainImpl.java/right.java
     + ">";
  }

  @Override public String getInitialValue() {
    return "0.0";
  }

  @Override public boolean isPrimitive() {
    return true;
  }

  @Override public Object parseGenericAttribute(GraphIO io) throws GraphIOException {
    return io.matchDouble();
  }

  @Override public void serializeGenericAttribute(GraphIO io, Object data) throws IOException {
    io.writeDouble((Double) data);
  }

  @Override public boolean isConformGenericValue(Object value) {
    return Double.class.isInstance(value);
  }
}