package de.uni_koblenz.jgralab.schema.impl;
import java.io.IOException;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.codegenerator.CodeBlock;
import de.uni_koblenz.jgralab.codegenerator.CodeGenerator;
import de.uni_koblenz.jgralab.codegenerator.CodeSnippet;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.StringDomain;

public class StringDomainImpl extends BasicDomainImpl implements StringDomain {
  protected StringDomainImpl(Schema schema) {
    super(STRINGDOMAIN_NAME, schema.getDefaultPackage());
  }

  @Override public String getJavaAttributeImplementationTypeName(String schemaRootPackagePrefix) {
    return "java.lang.String";
  }

  @Override public String getJavaClassName(String schemaRootPackagePrefix) {
    return this.getJavaAttributeImplementationTypeName(schemaRootPackagePrefix);
  }

  @Override public CodeBlock getReadMethod(String schemaPrefix, String variableName, String graphIoVariableName) {
    return new CodeSnippet(variableName + " = " + graphIoVariableName + ".matchUtfString();");
  }

  @Override public String getTGTypeName(Package pkg) {
    return STRINGDOMAIN_NAME;
  }



  @Override public CodeBlock getWriteMethod(String schemaRootPackagePrefix, String variableName, String graphIoVariableName) {
    return new CodeSnippet(graphIoVariableName + ".writeUtfString(" + variableName + ");");
  }

  @Override public CodeBlock getTransactionReadMethod(String schemaPrefix, String variableName, String graphIoVariableName) {
    return new CodeSnippet(this.getJavaAttributeImplementationTypeName(schemaPrefix) + " " + variableName + " = " + graphIoVariableName + ".matchUtfString();");
  }

  @Override public CodeBlock getTransactionWriteMethod(String schemaRootPackagePrefix, String variableName, String graphIoVariableName) {
    return this.getWriteMethod(schemaRootPackagePrefix, "get" + CodeGenerator.camelCase(variableName) + "()", graphIoVariableName);
  }

  @Override public String getTransactionJavaAttributeImplementationTypeName(String schemaRootPackagePrefix) {
    return this.getJavaAttributeImplementationTypeName(schemaRootPackagePrefix);
  }

  @Override public String getTransactionJavaClassName(String schemaRootPackagePrefix) {
    return "java.lang.String";
  }

  @Override public String getVersionedClass(String schemaRootPackagePrefix) {
    return "de.uni_koblenz.jgralab.impl.trans.VersionedReferenceImpl<" + this.getTransactionJavaAttributeImplementationTypeName(schemaRootPackagePrefix) + ">";
  }

  @Override public String getInitialValue() {
    return "null";
  }

  @Override public boolean isPrimitive() {
    return false;
  }

  @Override public Object parseGenericAttribute(GraphIO io) throws GraphIOException {
    String result = io.matchUtfString();
    return result;
  }

  @Override public void serializeGenericAttribute(GraphIO io, Object data) throws IOException {
    io.writeUtfString((String) data);
  }

  @Override public boolean isConformGenericValue(Object value) {
    return value == null || String.class.isInstance(value);
  }
}