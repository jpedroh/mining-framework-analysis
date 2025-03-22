package de.uni_koblenz.jgralab.schema.codegenerator;
import java.util.List;
import java.util.TreeSet;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.schema.Attribute;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.GraphElementClass;
import de.uni_koblenz.jgralab.schema.VertexClass;

/**
 * TODO add comment
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class ReversedEdgeCodeGenerator extends AttributedElementCodeGenerator<EdgeClass, Edge> {
  public ReversedEdgeCodeGenerator(EdgeClass edgeClass, String schemaPackageName, CodeGeneratorConfiguration config) {
    super(edgeClass, schemaPackageName, config);
    rootBlock.setVariable("graphElementClass", "ReversedEdge");
    rootBlock.setVariable("schemaElementClass", "EdgeClass");
    rootBlock.setVariable("isImplementationClassOnly", "true");
    rootBlock.setVariable("className", "Reversed" + edgeClass.getSimpleName());
    rootBlock.setVariable("simpleClassName", "Reversed" + edgeClass.getSimpleName());
    rootBlock.setVariable("simpleImplClassName", "Reversed" + edgeClass.getSimpleName() + "Impl");
    rootBlock.setVariable("normalQualifiedClassName", schemaRootPackageName + "." + edgeClass.getQualifiedName());
    for (EdgeClass superClass : edgeClass.getDirectSuperClasses().plus(edgeClass.getGraphClass().getDefaultEdgeClass())) {
      interfaces.add(superClass.getQualifiedName());
    }
  }

  @Override protected String getSchemaTypeName() {
    return "EdgeClass";
  }

  @Override protected CodeBlock createBody() {
    CodeList code = (CodeList) super.createBody();
    if (currentCycle.isStdOrDiskv2Impl()) {
      rootBlock.setVariable("baseClassName", "ReversedEdgeImpl");
      if (currentCycle.isStdImpl()) {
        addImports("#jgImplStdPackage#.#baseClassName#");
      } else {
        if (currentCycle.isDiskv2Impl()) {
          addImports("#jgImplDiskv2Package#.#baseClassName#");
        }
      }
      if (config.hasTypeSpecificMethodsSupport()) {
        code.add(createNextEdgeMethods());
        code.add(createNextIncidenceMethods());
      }
      code.add(createGetAlphaOmegaOverrides());
    }
    return code;
  }

  private CodeBlock createGetAlphaOmegaOverrides() {
    CodeSnippet b = new CodeSnippet();
    EdgeClass ec = aec;
    VertexClass from = ec.getFrom().getVertexClass();
    VertexClass to = ec.getTo().getVertexClass();
    b.setVariable("fromVertexClass", from.getSimpleName());
    b.setVariable("toVertexClass", to.getSimpleName());
    addImports(schemaRootPackageName + "." + from.getQualifiedName());
    addImports(schemaRootPackageName + "." + to.getQualifiedName());
    if (!currentCycle.isAbstract()) {
      b.add("public #fromVertexClass# getAlpha() {");
      b.add("\treturn (#fromVertexClass#) super.getAlpha();");
      b.add("}");
      b.add("public #toVertexClass# getOmega() {");
      b.add("\treturn (#toVertexClass#) super.getOmega();");
      b.add("}");
    }
    return b;
  }

  @Override protected CodeBlock createConstructor() {
    if (currentCycle.isStdImpl()) {
      addImports("#jgImplStdPackage#.EdgeImpl", "#jgPackage#.Graph");
    } else {
      if (currentCycle.isDiskv2Impl()) {
        addImports("#jgImplDiskv2Package#.EdgeImpl", "#jgPackage#.Graph");
      }
    }
    return new CodeSnippet(true, "#className#Impl(EdgeImpl e, Graph g) {", "\tsuper(e, g);", "}");
  }

  @Override protected CodeBlock createGetter(Attribute a) {
    CodeSnippet code = new CodeSnippet(true);
    code.setVariable("name", a.getName());
    code.setVariable("type", a.getDomain().getJavaAttributeImplementationTypeName(schemaRootPackageName));
    code.setVariable("isOrGet", a.getDomain().isBoolean() ? "is" : "get");
    if (currentCycle.isStdOrDiskv2Impl()) {
      code.add("public #type# #isOrGet#_#name#() {", "\treturn ((#normalQualifiedClassName#)normalEdge).#isOrGet#_#name#();", "}");
    }
    if (currentCycle.isAbstract()) {
      code.add("public #type# #isOrGet#_#name#();");
    }
    return code;
  }

  @Override protected CodeBlock createSetter(Attribute a) {
    CodeSnippet code = new CodeSnippet(true);
    code.setVariable("name", a.getName());
    code.setVariable("type", a.getDomain().getJavaAttributeImplementationTypeName(schemaRootPackageName));
    if (currentCycle.isStdOrDiskv2Impl()) {
      code.add("public void set_#name#(#type# _#name#) {", "\t((#normalQualifiedClassName#)normalEdge).set_#name#(_#name#);", "}");
    }
    if (currentCycle.isAbstract()) {
      code.add("public void set_#name#(#type# _#name#);");
    }
    return code;
  }

  @Override protected CodeBlock createGenericGetter(List<Attribute> attributes) {
    return null;
  }

  @Override protected CodeBlock createGenericSetter(List<Attribute> attributes) {
    return null;
  }

  private CodeBlock createNextEdgeMethods() {
    CodeList code = new CodeList();
    TreeSet<GraphElementClass<?, ?>> superClasses = new TreeSet<GraphElementClass<?, ?>>();
    superClasses.addAll(aec.getAllSuperClasses());
    superClasses.add(aec);
    for (GraphElementClass<?, ?> ec : superClasses) {
      EdgeClass ecl = (EdgeClass) ec;
      code.addNoIndent(createNextEdgeMethod(ecl));
    }
    return code;
  }

  private CodeBlock createNextEdgeMethod(EdgeClass ec) {
    CodeSnippet code = new CodeSnippet(true, "public #ecName# getNext#ecCamelName#InGraph(#formalParams#) {", "\treturn ((#ecName#)normalEdge).getNext#ecCamelName#InGraph(#actualParams#);", "}");
    code.setVariable("ecName", schemaRootPackageName + "." + ec.getQualifiedName());
    code.setVariable("ecCamelName", camelCase(ec.getUniqueName()));
    code.setVariable("formalParams", "");
    code.setVariable("actualParams", "");
    return code;
  }

  private CodeBlock createNextIncidenceMethods() {
    CodeList code = new CodeList();
    TreeSet<GraphElementClass<?, ?>> superClasses = new TreeSet<GraphElementClass<?, ?>>();
    superClasses.addAll(aec.getAllSuperClasses());
    superClasses.add(aec);
    for (GraphElementClass<?, ?> ec : superClasses) {
      addImports("#jgPackage#.EdgeDirection");
      EdgeClass ecl = (EdgeClass) ec;
      code.addNoIndent(createNextIncidenceMethod(ecl, false));
      code.addNoIndent(createNextIncidenceMethod(ecl, true));
    }
    return code;
  }

  private CodeBlock createNextIncidenceMethod(EdgeClass ec, boolean withOrientation) {
    CodeSnippet code = new CodeSnippet(true, "public #ecName# getNext#ecCamelName#Incidence(#formalParams#) {", "\treturn (#ecName#)getNextIncidence(#ecName#.EC#actualParams#);", "}");
    code.setVariable("ecName", schemaRootPackageName + "." + ec.getQualifiedName());
    code.setVariable("ecCamelName", camelCase(ec.getUniqueName()));
    code.setVariable("formalParams", (withOrientation ? "EdgeDirection orientation" : ""));
    code.setVariable("actualParams", (withOrientation ? ", orientation" : ""));
    return code;
  }

  @Override protected CodeBlock createStaticImplementationClassField() {
    return null;
  }

  @Override protected CodeBlock createFields(List<Attribute> attributes) {
    return null;
  }

  @Override protected CodeBlock createGetSchemaClassMethod() {
    return null;
  }

  @Override protected CodeBlock createReadAttributesFromStringMethod(List<Attribute> attributes) {
    CodeList code = new CodeList();
    addImports("#jgPackage#.GraphIO", "#jgPackage#.exception.GraphIOException");
    code.addNoIndent(new CodeSnippet(true, "public void readAttributeValueFromString(String attributeName, String value) throws GraphIOException {"));
    code.add(new CodeSnippet("throw new GraphIOException(\"Can not call readAttributeValuesFromString for reversed Edges.\");"));
    code.addNoIndent(new CodeSnippet("}"));
    return code;
  }

  @Override protected CodeBlock createWriteAttributeToStringMethod(List<Attribute> attributes) {
    CodeList code = new CodeList();
    addImports("#jgPackage#.GraphIO", "#jgPackage#.exception.GraphIOException");
    code.addNoIndent(new CodeSnippet(true, "public String writeAttributeValueToString(String _attributeName) throws IOException, GraphIOException {"));
    code.add(new CodeSnippet("throw new GraphIOException(\"Can not call writeAttributeValueToString for reversed Edges.\");"));
    code.addNoIndent(new CodeSnippet("}"));
    return code;
  }

  @Override protected CodeBlock createReadAttributesMethod(List<Attribute> attributes) {
    CodeList code = new CodeList();
    addImports("#jgPackage#.GraphIO", "#jgPackage#.exception.GraphIOException");
    code.addNoIndent(new CodeSnippet(true, "public void readAttributeValues(GraphIO io) throws GraphIOException {"));
    code.add(new CodeSnippet("throw new GraphIOException(\"Can not call readAttributeValues for reversed Edges.\");"));
    code.addNoIndent(new CodeSnippet("}"));
    return code;
  }

  @Override protected CodeBlock createWriteAttributesMethod(List<Attribute> attributes) {
    CodeList code = new CodeList();
    addImports("#jgPackage#.GraphIO", "#jgPackage#.exception.GraphIOException", "java.io.IOException");
    code.addNoIndent(new CodeSnippet(true, "public void writeAttributeValues(GraphIO io) throws GraphIOException, IOException {"));
    code.add(new CodeSnippet("throw new GraphIOException(\"Can not call writeAttributeValues for reversed Edges.\");"));
    code.addNoIndent(new CodeSnippet("}"));
    return code;
  }

  @Override protected CodeBlock createAttributedElementClassConstant() {
    return null;
  }

  @Override protected CodeBlock createGetAttributedElementClassMethod() {
    return new CodeSnippet(true, "@Override", "public final #jgSchemaPackage#.#schemaElementClass# getAttributedElementClass() {", "\treturn getNormalEdge().getAttributedElementClass();", "}");
  }
}