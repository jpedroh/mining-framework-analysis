package de.uni_koblenz.jgralab.schema.impl;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.pcollections.PVector;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.codegenerator.CodeBlock;
import de.uni_koblenz.jgralab.codegenerator.CodeGenerator;
import de.uni_koblenz.jgralab.codegenerator.CodeList;
import de.uni_koblenz.jgralab.codegenerator.CodeSnippet;
import de.uni_koblenz.jgralab.schema.Domain;
import de.uni_koblenz.jgralab.schema.ListDomain;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.Schema;

public final class ListDomainImpl extends CollectionDomainImpl implements ListDomain {
  /**
	 * @param aList
	 *            the list which needs to be converted to a set
	 * @return the list elements in a set (loses order and duplicates)
	 */
  public static Set<Object> toSet(List<Object> aList) {
    return new HashSet<Object>(aList);
  }


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/left.java
  protected
=======
>>>>>>> Unknown file: This is a bug in JDime.
   ListDomainImpl(Schema schema, Domain aBaseDomain) {
    super(LISTDOMAIN_NAME + "<" + aBaseDomain.getTGTypeName(schema.getDefaultPackage()) + ">", 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/left.java
    schema.getDefaultPackage()
=======
    (PackageImpl) schema.getDefaultPackage()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/right.java
    , aBaseDomain);
  }


<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/left.java
  @Override public Set<Domain> getAllComponentDomains() {
    HashSet<Domain> componentDomainSet = new HashSet<Domain>(1);
    componentDomainSet.add(this.baseDomain);
    return componentDomainSet;
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  @Override public String getJavaAttributeImplementationTypeName(String schemaRootPackagePrefix) {
    return LISTDOMAIN_TYPE + "<" + this.baseDomain.getJavaClassName(schemaRootPackagePrefix) + ">";
  }

  @Override public String getTransactionJavaAttributeImplementationTypeName(String schemaRootPackagePrefix) {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/left.java
    this.getJavaAttributeImplementationTypeName(schemaRootPackagePrefix)
=======
    getJavaAttributeImplementationTypeName(schemaRootPackagePrefix)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/right.java
    ;
  }

  @Override public String getJavaClassName(String schemaRootPackagePrefix) {
    return this.getJavaAttributeImplementationTypeName(schemaRootPackagePrefix);
  }

  @Override public String getTransactionJavaClassName(String schemaRootPackagePrefix) {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/left.java
    this.getJavaAttributeImplementationTypeName(schemaRootPackagePrefix)
=======
    getJavaAttributeImplementationTypeName(schemaRootPackagePrefix)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/right.java
    ;
  }

  @Override public String getVersionedClass(String schemaRootPackagePrefix) {
    return 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/left.java
    "de.uni_koblenz.jgralab.impl.trans.VersionedReferenceImpl<" + this.getTransactionJavaAttributeImplementationTypeName(schemaRootPackagePrefix)
=======
    "de.uni_koblenz.jgralab.impl.trans.VersionedReferenceImpl<" + getTransactionJavaAttributeImplementationTypeName(schemaRootPackagePrefix)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/right.java
     + ">";
  }

  @Override public CodeBlock getReadMethod(String schemaPrefix, String variableName, String graphIoVariableName) {
    CodeList code = new CodeList();
    code.setVariable("init", "");

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/left.java
    this.internalGetReadMethod(code, schemaPrefix, variableName, graphIoVariableName)
=======
    internalGetReadMethod(code, schemaPrefix, variableName, graphIoVariableName)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/right.java
    ;
    return code;
  }

  @Override public String getTGTypeName(Package pkg) {
    return LISTDOMAIN_NAME + "<" + this.baseDomain.getTGTypeName(pkg) + ">";
  }

  @Override public CodeBlock getWriteMethod(String schemaRootPackagePrefix, String variableName, String graphIoVariableName) {
    CodeList code = new CodeList();
    code.setVariable("name", variableName);

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/left.java
    this.internalGetWriteMethod(code, schemaRootPackagePrefix, variableName, graphIoVariableName)
=======
    internalGetWriteMethod(code, schemaRootPackagePrefix, variableName, graphIoVariableName)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/right.java
    ;
    return code;
  }

  @Override public String toString() {
    return "domain " + LISTDOMAIN_NAME + "<" + this.baseDomain.toString() + ">";
  }

  private void internalGetReadMethod(CodeList code, String schemaPrefix, String variableName, String graphIoVariableName) {
    code.setVariable("name", variableName);
    code.setVariable("empty", ListDomain.EMPTY_LIST);
    code.setVariable("basedom", 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/left.java
    this.getBaseDomain().getJavaClassName(schemaPrefix)
=======
    getBaseDomain().getJavaClassName(schemaPrefix)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/right.java
    );
    code.setVariable("basetype", 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/left.java
    this.getBaseDomain().getJavaAttributeImplementationTypeName(schemaPrefix)
=======
    getBaseDomain().getJavaAttributeImplementationTypeName(schemaPrefix)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/right.java
    );
    code.setVariable("io", graphIoVariableName);
    code.addNoIndent(new CodeSnippet("#init#"));
    code.addNoIndent(new CodeSnippet("if (#io#.isNextToken(\"[\")) {"));
    code.add(new CodeSnippet(LISTDOMAIN_TYPE + "<#basedom#> $#name# = #empty#;", "#io#.match(\"[\");", "while (!#io#.isNextToken(\"]\")) {"));
    if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/left.java
    this.getBaseDomain().isComposite()
=======
    getBaseDomain().isComposite()
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/right.java
    ) {
      code.add(new CodeSnippet("\t#basetype# $#name#Element = null;"));
    } else {
      code.add(new CodeSnippet("\t#basetype# $#name#Element;"));
    }
    code.add(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/left.java
    this.getBaseDomain().getReadMethod(schemaPrefix, "$" + variableName + "Element", graphIoVariableName)
=======
    getBaseDomain().getReadMethod(schemaPrefix, "$" + variableName + "Element", graphIoVariableName)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/right.java
    , 1);
    code.add(new CodeSnippet("\t$#name# = $#name#.plus($#name#Element);", "}", "#io#.match(\"]\");", "#name# = $#name#;"));
    code.addNoIndent(new CodeSnippet("} else if (#io#.isNextToken(GraphIO.NULL_LITERAL)) {"));
    code.add(new CodeSnippet("#io#.match(); ", "#name# = null;"));
    code.addNoIndent(new CodeSnippet("} else {", "\t#name# = null;", "}"));
  }

  private void internalGetWriteMethod(CodeList code, String schemaRootPackagePrefix, String variableName, String graphIoVariableName) {
    code.setVariable("basedom", 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/left.java
    this.getBaseDomain().getJavaClassName(schemaRootPackagePrefix)
=======
    getBaseDomain().getJavaClassName(schemaRootPackagePrefix)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/right.java
    );
    code.setVariable("basetype", 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/left.java
    this.getBaseDomain().getJavaAttributeImplementationTypeName(schemaRootPackagePrefix)
=======
    getBaseDomain().getJavaAttributeImplementationTypeName(schemaRootPackagePrefix)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/right.java
    );
    code.setVariable("io", graphIoVariableName);
    String element = variableName + "Element";
    element = element.replace('(', '_');
    element = element.replace(')', '_');
    code.setVariable("element", element);
    code.addNoIndent(new CodeSnippet("if (#name# != null) {"));
    code.add(new CodeSnippet("#io#.writeSpace();", "#io#.write(\"[\");", "#io#.noSpace();", "for (#basetype# #element# : #name#) {"));
    code.add(
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/left.java
    this.getBaseDomain().getWriteMethod(schemaRootPackagePrefix, code.getVariable("element"), graphIoVariableName)
=======
    getBaseDomain().getWriteMethod(schemaRootPackagePrefix, code.getVariable("element"), graphIoVariableName)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/right.java
    , 1);
    code.add(new CodeSnippet("}", "#io#.write(\"]\");", "#io#.space();"));
    code.addNoIndent(new CodeSnippet("} else {"));
    code.add(new CodeSnippet(graphIoVariableName + ".writeIdentifier(GraphIO.NULL_LITERAL);"));
    code.addNoIndent(new CodeSnippet("}"));
  }

  @Override public CodeBlock getTransactionReadMethod(String schemaPrefix, String variableName, String graphIoVariableName) {
    CodeList code = new CodeList();
    code.setVariable("init", LISTDOMAIN_TYPE + "<#basedom#> #name# = null;");

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/left.java
    this.internalGetReadMethod(code, schemaPrefix, variableName, graphIoVariableName);
=======
    internalGetReadMethod(code, schemaPrefix, variableName, graphIoVariableName);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/right.java

    return code;
  }

  @Override public CodeBlock getTransactionWriteMethod(String schemaRootPackagePrefix, String variableName, String graphIoVariableName) {
    CodeList code = new CodeList();
    code.setVariable("name", "get" + CodeGenerator.camelCase(variableName) + "()");

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/left.java
    this.internalGetWriteMethod(code, schemaRootPackagePrefix, variableName, graphIoVariableName);
=======
    internalGetWriteMethod(code, schemaRootPackagePrefix, variableName, graphIoVariableName);
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/right.java

    return code;
  }

  @Override public String getInitialValue() {
    return "null";
  }

  @Override public Object parseGenericAttribute(GraphIO io) throws GraphIOException {
    if (io.isNextToken("[")) {
      PVector<Object> result = JGraLab.vector();
      io.match("[");
      while (!io.isNextToken("]")) {
        Object listElement = null;
        listElement = 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/left.java
        this.getBaseDomain().parseGenericAttribute(io)
=======
        getBaseDomain().parseGenericAttribute(io)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/right.java
        ;
        result = result.plus(listElement);
      }
      io.match("]");
      return result;
    } else {
      if (io.isNextToken(GraphIO.NULL_LITERAL)) {
        io.match();
        return null;
      } else {
        return null;
      }
    }
  }

  @SuppressWarnings(value = { "unchecked" }) @Override public void serializeGenericAttribute(GraphIO io, Object data) throws IOException {
    if (data != null) {
      io.writeSpace();
      io.write("[");
      io.noSpace();
      for (Object value : (PVector<Object>) data) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/left.java
        this.getBaseDomain().serializeGenericAttribute(io, value)
=======
        getBaseDomain().serializeGenericAttribute(io, value)
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/right.java
        ;
      }
      io.write("]");
      io.space();
    } else {
      io.writeIdentifier(GraphIO.NULL_LITERAL);
    }
  }

  @Override public boolean isConformGenericValue(Object value) {
    boolean result = true;
    if (value == null) {
      return result;
    }
    result &= (value instanceof PVector);
    if (!result) {
      return false;
    }
    Iterator<?> iterator = ((PVector<?>) value).iterator();
    while (iterator.hasNext() && result) {
      result &= 
<<<<<<< /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/left.java
      this.getBaseDomain().isConformGenericValue(iterator.next())
=======
      getBaseDomain().isConformGenericValue(iterator.next())
>>>>>>> /usr/src/app/output/jgralab/jgralab/8cef5d06ba740f3eaeac87876172c02a85a5819f/src/de/uni_koblenz/jgralab/schema/impl/ListDomainImpl.java/right.java
      ;
    }
    assert (!iterator.hasNext());
    return result;
  }
}