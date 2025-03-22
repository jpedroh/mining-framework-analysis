package de.uni_koblenz.jgralab.schema.impl;
import java.io.IOException;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.codegenerator.CodeBlock;
import de.uni_koblenz.jgralab.codegenerator.CodeGenerator;
import de.uni_koblenz.jgralab.codegenerator.CodeSnippet;
import de.uni_koblenz.jgralab.schema.BooleanDomain;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.Schema;

public class BooleanDomainImpl extends BasicDomainImpl implements BooleanDomain {
  protected BooleanDomainImpl(Schema schema) {
    super(BOOLEANDOMAIN_NAME, schema.getDefaultPackage());
  }

  @Override public String getJavaAttributeImplementationTypeName(String schemaRootPackagePrefix) {
    return "boolean";
  }

  @Override public String getJavaClassName(String schemaRootPackagePrefix) {
    return "java.lang.Boolean";
  }

  @Override public CodeBlock getReadMethod(String schemaPrefix, String variableName, String graphIoVariableName) {
    return new CodeSnippet(variableName + " = " + graphIoVariableName + ".matchBoolean();");
  }

  @Override public String getTGTypeName(Package pkg) {
    return BOOLEANDOMAIN_NAME;
  }

  @Override public CodeBlock getWriteMethod(String schemaRootPackagePrefix, String variableName, String graphIoVariableName) {
    return new CodeSnippet(graphIoVariableName + ".writeBoolean(" + variableName + ");");
  }

  @Override public CodeBlock getTransactionReadMethod(String schemaPrefix, String variableName, String graphIoVariableName) {
    return new CodeSnippet(this.getJavaAttributeImplementationTypeName(schemaPrefix) + " " + variableName + " = " + graphIoVariableName + ".matchBoolean();");
  }

  @Override public CodeBlock getTransactionWriteMethod(String schemaRootPackagePrefix, String variableName, String graphIoVariableName) {
    return this.getWriteMethod(schemaRootPackagePrefix, "is" + CodeGenerator.camelCase(variableName) + "()", graphIoVariableName);
  }

  @Override public String getTransactionJavaAttributeImplementationTypeName(String schemaRootPackagePrefix) {
    return "java.lang.Boolean";
  }

  @Override public String getTransactionJavaClassName(String schemaRootPackagePrefix) {
    return this.getJavaClassName(schemaRootPackagePrefix);
  }

  @Override public String getVersionedClass(String schemaRootPackagePrefix) {
    return "de.uni_koblenz.jgralab.impl.trans.VersionedReferenceImpl<" + this.getTransactionJavaClassName(schemaRootPackagePrefix) + ">";
  }

  @Override public String getInitialValue() {
    return "false";
  }

  @Override public boolean isPrimitive() {
    return true;
  }

  @Override public boolean isBoolean() {
    return true;
  }

  @Override public Object parseGenericAttribute(GraphIO io) throws GraphIOException {
    return io.matchBoolean();
  }

  @Override public void serializeGenericAttribute(GraphIO io, Object data) throws IOException {
    io.writeBoolean((Boolean) data);
  }

  @Override public boolean isConformGenericValue(Object value) {
    return Boolean.class.isInstance(value);
  }
}