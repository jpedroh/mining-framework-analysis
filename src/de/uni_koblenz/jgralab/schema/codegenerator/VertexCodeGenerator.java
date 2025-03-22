package de.uni_koblenz.jgralab.schema.codegenerator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.GraphElementClass;
import de.uni_koblenz.jgralab.schema.VertexClass;

/**
 * This class is used by the method Schema.commit() to generate the Java-classes
 * that implement the VertexClasses of a graph schema.
 * 
 * @author ist@uni-koblenz.de
 */
public class VertexCodeGenerator extends AttributedElementCodeGenerator<VertexClass, Vertex> {
  private RolenameCodeGenerator rolenameGenerator;

  public VertexCodeGenerator(VertexClass vertexClass, String schemaPackageName, CodeGeneratorConfiguration config) {
    super(vertexClass, schemaPackageName, config);
    rootBlock.setVariable("graphElementClass", "Vertex");
    rootBlock.setVariable("schemaElementClass", "VertexClass");
    rolenameGenerator = new RolenameCodeGenerator(aec);
    for (VertexClass superClass : vertexClass.getDirectSuperClasses().plus(vertexClass.getGraphClass().getDefaultVertexClass())) {
      interfaces.add(superClass.getQualifiedName());
    }
  }

  @Override protected String getSchemaTypeName() {
    return "VertexClass";
  }

  /**
	 * creates the header of the classfile, that is the part
	 * <code>public class VertexClassName extends Vertex {</code>
	 */
  @Override protected CodeBlock createHeader() {
    return super.createHeader();
  }

  /**
	 * creates the body of the class file, that are methods and attributes
	 */
  @Override protected CodeBlock createBody() {
    CodeList code = (CodeList) super.createBody();
    if (currentCycle.isStdOrDiskv2Impl()) {
      if (currentCycle.isStdImpl()) {
        addImports("#jgImplStdPackage#.#baseClassName#");
      } else {
        if (currentCycle.isDiskv2Impl()) {
          addImports("#jgImplDiskv2Package#.#baseClassName#");
        }
      }
      rootBlock.setVariable("baseClassName", "VertexImpl");
    }
    if (config.hasTypeSpecificMethodsSupport() && !currentCycle.isClassOnly()) {
      code.add(createNextVertexMethods());
      code.add(createFirstIncidenceMethods());
      code.add(rolenameGenerator.createRolenameMethods(currentCycle.isStdOrDiskv2Impl()));
      code.add(createIncidenceIteratorMethods());
    }
    return code;
  }

  /**
	 * creates the methods <code>getFirstEdgeName()</code>
	 * 
	 * @param createClass
	 *            if set to true, the method bodies will also be created
	 * @return the CodeBlock that contains the methods
	 */
  private CodeBlock createFirstIncidenceMethods() {
    CodeList code = new CodeList();
    VertexClass vc = aec;
    Set<EdgeClass> edgeClassSet = new HashSet<EdgeClass>();
    if (currentCycle.isStdOrDiskv2Impl()) {
      edgeClassSet.addAll(vc.getConnectedEdgeClasses());
    }
    if (currentCycle.isAbstract()) {
      edgeClassSet.addAll(vc.getOwnConnectedEdgeClasses());
      if (vc.getAllSuperClasses().size() == 1) {
        for (EdgeClass ec : vc.getConnectedEdgeClasses()) {
          VertexClass dvc = vc.getGraphClass().getDefaultVertexClass();
          if ((ec.getTo().getVertexClass() == dvc) || (ec.getFrom().getVertexClass() == dvc)) {
            edgeClassSet.add(ec);
          }
        }
      }
    }
    for (EdgeClass ec : edgeClassSet) {
      addImports("#jgPackage#.EdgeDirection");
      if (config.hasTypeSpecificMethodsSupport()) {
        code.addNoIndent(createFirstIncidenceMethod(ec, false));
        code.addNoIndent(createFirstIncidenceMethod(ec, true));
      }
    }
    return code;
  }

  /**
	 * creates the method <code>getFirstEdgeName()</code> for the given
	 * EdgeClass
	 * 
	 * @param createClass
	 *            if set to true, the method bodies will also be created
	 * @param withOrientation
	 *            toggles if the EdgeDirection-parameter will be created
	 * @return the CodeBlock that contains the method
	 */
  private CodeBlock createFirstIncidenceMethod(EdgeClass ec, boolean withOrientation) {
    CodeSnippet code = new CodeSnippet(true);
    code.setVariable("ecQualifiedName", absoluteName(ec));
    code.setVariable("ecCamelName", camelCase(ec.getUniqueName()));
    code.setVariable("formalParams", (withOrientation ? "EdgeDirection orientation" : ""));
    code.setVariable("actualParams", (withOrientation ? ", orientation" : ""));
    if (currentCycle.isAbstract()) {
      code.add("/**", " * @return the first edge of class #ecCamelName# at this vertex");
      if (withOrientation) {
        code.add(" * @param orientation the orientation of the edge");
      }
      code.add(" */", "public #ecQualifiedName# getFirst#ecCamelName#Incidence(#formalParams#);");
    }
    if (currentCycle.isStdOrDiskv2Impl()) {
      code.add("@Override", "public #ecQualifiedName# getFirst#ecCamelName#Incidence(#formalParams#) {", "\treturn (#ecQualifiedName#)getFirstIncidence(#ecQualifiedName#.EC#actualParams#);", "}");
    }
    return code;
  }

  /**
	 * Creates <code>getNextVertexClassName()</code> methods
	 * 
	 * @param createClass
	 *            if set to true, also the method bodies will be created
	 * @return the CodeBlock that contains the methods
	 */
  private CodeBlock createNextVertexMethods() {
    CodeList code = new CodeList();
    TreeSet<GraphElementClass<?, ?>> superClasses = new TreeSet<GraphElementClass<?, ?>>();
    superClasses.addAll(aec.getAllSuperClasses());
    superClasses.add(aec);
    if (config.hasTypeSpecificMethodsSupport()) {
      for (GraphElementClass<?, ?> ec : superClasses) {
        VertexClass vc = (VertexClass) ec;
        code.addNoIndent(createNextVertexMethod(vc));
      }
    }
    return code;
  }

  /**
	 * Creates <code>getNextVertexClassName()</code> method for given
	 * VertexClass
	 * 
	 * @param createClass
	 *            if set to true, the method bodies will also be created
	 * @return the CodeBlock that contains the method
	 */
  private CodeBlock createNextVertexMethod(VertexClass vc) {
    CodeSnippet code = new CodeSnippet(true);
    code.setVariable("vcQualifiedName", absoluteName(vc));
    code.setVariable("vcCamelName", camelCase(vc.getUniqueName()));
    code.setVariable("formalParams", "");
    code.setVariable("actualParams", "");
    if (currentCycle.isAbstract()) {
      code.add("/**", " * @return the next #vcQualifiedName# vertex in the global vertex sequence");
      code.add(" */", "public #vcQualifiedName# getNext#vcCamelName#(#formalParams#);");
    }
    if (currentCycle.isStdOrDiskv2Impl()) {
      code.add("@Override", "public #vcQualifiedName# getNext#vcCamelName#(#formalParams#) {", "\treturn (#vcQualifiedName#)getNextVertex(#vcQualifiedName#.VC#actualParams#);", "}");
    }
    return code;
  }

  /**
	 * Creates <code>getEdgeNameIncidences</code> methods.
	 * 
	 * @param createClass
	 *            if set to true, also the method bodies will be created
	 * @return the CodeBlock that contains the code for the
	 *         getEdgeNameIncidences-methods
	 */
  private CodeBlock createIncidenceIteratorMethods() {
    VertexClass vc = aec;
    CodeList code = new CodeList();
    Set<EdgeClass> edgeClassSet = new HashSet<EdgeClass>();
    if (currentCycle.isStdOrDiskv2Impl()) {
      edgeClassSet.addAll(vc.getConnectedEdgeClasses());
    }
    if (currentCycle.isAbstract()) {
      edgeClassSet.addAll(vc.getOwnConnectedEdgeClasses());
      if (vc.getAllSuperClasses().size() == 1) {
        for (EdgeClass ec : vc.getConnectedEdgeClasses()) {
          VertexClass dvc = vc.getGraphClass().getDefaultVertexClass();
          if ((ec.getTo().getVertexClass() == dvc) || (ec.getFrom().getVertexClass() == dvc)) {
            edgeClassSet.add(ec);
          }
        }
      }
    }
    for (EdgeClass ec : edgeClassSet) {
      if (currentCycle.isStdOrDiskv2Impl()) {
        addImports("#jgImplPackage#.IncidenceIterable");
      }
      CodeSnippet s = new CodeSnippet(true);
      code.addNoIndent(s);
      String targetClassName = schemaRootPackageName + "." + ec.getQualifiedName();
      s.setVariable("edgeClassSimpleName", ec.getSimpleName());
      s.setVariable("edgeClassQualifiedName", targetClassName);
      s.setVariable("edgeClassUniqueName", ec.getUniqueName());
      if (currentCycle.isAbstract()) {
        s.add("/**");
        s.add(" * Returns an Iterable for all incidence edges of this vertex that are of type #edgeClassSimpleName# or subtypes.");
        s.add(" */");
        s.add("public Iterable<#edgeClassQualifiedName#> get#edgeClassUniqueName#Incidences();");
      }
      if (currentCycle.isStdOrDiskv2Impl()) {
        s.add("@Override");
        s.add("public Iterable<#edgeClassQualifiedName#> get#edgeClassUniqueName#Incidences() {");
        s.add("\treturn new IncidenceIterable<#edgeClassQualifiedName#>(this, #edgeClassQualifiedName#.EC);");
        s.add("}");
      }
      s.add("");
      if (currentCycle.isAbstract()) {
        s.add("/**");
        s.add(" * Returns an Iterable for all incidence edges of this vertex that are of type #edgeClassSimpleName#.");
        s.add(" * @param direction EdgeDirection.IN or EdgeDirection.OUT, only edges of this direction will be included in the Iterable");
        s.add(" */");
        s.add("public Iterable<#edgeClassQualifiedName#> get#edgeClassUniqueName#Incidences(EdgeDirection direction);");
      }
      if (currentCycle.isStdOrDiskv2Impl()) {
        s.add("@Override");
        s.add("public Iterable<#edgeClassQualifiedName#> get#edgeClassUniqueName#Incidences(EdgeDirection direction) {");
        s.add("\treturn new IncidenceIterable<#edgeClassQualifiedName#>(this, #edgeClassQualifiedName#.EC, direction);");
        s.add("}");
      }
    }
    return code;
  }

  @Override protected CodeBlock createAttributedElementClassConstant() {
    CodeSnippet s = new CodeSnippet(true, "public static final #jgSchemaPackage#.#schemaElementClass# VC" + " = #schemaPackageName#.#schemaName#.instance().getGraphClass().getVertexClass(\"#vcQualifiedName#\");");
    s.setVariable("vcQualifiedName", aec.getQualifiedName());
    return s;
  }

  @Override protected CodeBlock createGetAttributedElementClassMethod() {
    return new CodeSnippet(true, "@Override", "public final #jgSchemaPackage#.#schemaElementClass# getAttributedElementClass() {", "\treturn #javaClassName#.VC;", "}");
  }
}