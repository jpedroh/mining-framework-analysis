package de.uni_koblenz.jgralab.schema.impl;
import java.io.IOException;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.codegenerator.CodeBlock;
import de.uni_koblenz.jgralab.codegenerator.CodeGenerator;
import de.uni_koblenz.jgralab.codegenerator.CodeSnippet;
import de.uni_koblenz.jgralab.schema.LongDomain;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.Schema;

public class LongDomainImpl extends BasicDomainImpl implements LongDomain {
  protected LongDomainImpl(Schema schema) {
    super(LONGDOMAIN_NAME, schema.getDefaultPackage());
  }

  @Override public String getJavaAttributeImplementationTypeName(String schemaRootPackagePrefix) {
    return "long";
  }

  @Override public String getJavaClassName(String schemaRootPackagePrefix) {
    return "java.lang.Long";
  }

  @Override public CodeBlock getReadMethod(String schemaPrefix, String variableName, String graphIoVariableName) {
    return new CodeSnippet(variableName + " = " + graphIoVariableName + ".matchLong();");
  }

  @Override public String getTGTypeName(Package pkg) {
    return LONGDOMAIN_NAME;
  }

  @Override public CodeBlock getWriteMethod(String schemaRootPackagePrefix, String variableName, String graphIoVariableName) {
    return new CodeSnippet(graphIoVariableName + ".writeLong(" + variableName + ");");
  }

  @Override public CodeBlock getTransactionReadMethod(String schemaPrefix, String variableName, String graphIoVariableName) {
    return new CodeSnippet(this.getJavaAttributeImplementationTypeName(schemaPrefix) + " " + variableName + " = " + graphIoVariableName + ".matchLong();");
  }

  @Override public CodeBlock getTransactionWriteMethod(String schemaRootPackagePrefix, String variableName, String graphIoVariableName) {
    return this.getWriteMethod(schemaRootPackagePrefix, "get" + CodeGenerator.camelCase(variableName) + "()", graphIoVariableName);
  }

  @Override public String getTransactionJavaAttributeImplementationTypeName(String schemaRootPackagePrefix) {
    return "java.lang.Long";
  }

  @Override public String getTransactionJavaClassName(String schemaRootPackagePrefix) {
    return this.getJavaClassName(schemaRootPackagePrefix);
  }

  @Override public String getVersionedClass(String schemaRootPackagePrefix) {
    return "de.uni_koblenz.jgralab.impl.trans.VersionedReferenceImpl<" + this.getTransactionJavaClassName(schemaRootPackagePrefix) + ">";
  }

  @Override public String getInitialValue() {
    return "0L";
  }

  @Override public boolean isPrimitive() {
    return true;
  }

  @Override public Object parseGenericAttribute(GraphIO io) throws GraphIOException {
    return io.matchLong();
  }

  @Override public void serializeGenericAttribute(GraphIO io, Object data) throws IOException {
    io.writeLong((Long) data);
  }

  @Override public boolean isConformGenericValue(Object value) {
    return Long.class.isInstance(value);
  }
}