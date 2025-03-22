package de.uni_koblenz.jgralab.codegenerator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.VertexClass;

/**
 * This class is used by the method Schema.commit() to generate the Java-classes
 * that implement the VertexClasses of a graph schema.
 *
 * @author ist@uni-koblenz.de
 */
public class VertexCodeGenerator extends AttributedElementCodeGenerator {
  private RolenameCodeGenerator rolenameGenerator;

  public VertexCodeGenerator(VertexClass vertexClass, String schemaPackageName, CodeGeneratorConfiguration config) {
    super(vertexClass, schemaPackageName, config);
    rootBlock.setVariable("graphElementClass", "Vertex");
    rootBlock.setVariable("schemaElementClass", "VertexClass");
    rolenameGenerator = new RolenameCodeGenerator((VertexClass) aec);
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
    if (currentCycle.isStdOrDbImplOrTransImpl()) {
      if (currentCycle.isStdImpl()) {
        addImports("#jgImplStdPackage#.#baseClassName#");
      } else {
        if (currentCycle.isTransImpl()) {
          addImports("#jgImplTransPackage#.#baseClassName#");
        } else {
          if (currentCycle.isDbImpl()) {
            addImports("#jgImplDbPackage#.#baseClassName#");
          }
        }
      }
      rootBlock.setVariable("baseClassName", "VertexImpl");
    }
    if (config.hasTypeSpecificMethodsSupport() && !currentCycle.isClassOnly()) {
      code.add(createNextVertexMethods());
      code.add(createFirstIncidenceMethods());
      code.add(rolenameGenerator.createRolenameMethods(currentCycle.isStdOrDbImplOrTransImpl()));
      code.add(createIncidenceIteratorMethods());
    }
    if (currentCycle.isStdOrDbImplOrTransImpl()) {
      code.add(createGetEdgeForRolenameMethod());
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
    VertexClass vc = (VertexClass) aec;
    Set<EdgeClass> edgeClassSet = new HashSet<EdgeClass>();
    if (currentCycle.isStdOrDbImplOrTransImpl()) {
      edgeClassSet.addAll(vc.getConnectedEdgeClasses());
    }
    if (currentCycle.isAbstract()) {
      edgeClassSet.addAll(vc.getOwnConnectedEdgeClasses());
      if (vc.getAllSuperClasses().size() == 1) {
        for (EdgeClass ec : vc.getConnectedEdgeClasses()) {
          VertexClass dvc = vc.getGraphClass().getSchema().getDefaultVertexClass();
          if ((ec.getTo().getVertexClass() == dvc) || (ec.getFrom().getVertexClass() == dvc)) {
            edgeClassSet.add(ec);
          }
        }
      }
    }
    for (EdgeClass ec : edgeClassSet) {
      if (ec.isInternal()) {
        continue;
      }
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
    if (currentCycle.isStdOrDbImplOrTransImpl()) {
      code.add("@Override", "public #ecQualifiedName# getFirst#ecCamelName#Incidence(#formalParams#) {", "\treturn (#ecQualifiedName#)getFirstIncidence(#ecQualifiedName#.class#actualParams#);", "}");
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
    TreeSet<AttributedElementClass<?, ?>> superClasses = new TreeSet<AttributedElementClass<?, ?>>();
    superClasses.addAll(aec.getAllSuperClasses());
    superClasses.add(aec);
    if (config.hasTypeSpecificMethodsSupport()) {
      for (AttributedElementClass<?, ?> ec : superClasses) {
        if (ec.isInternal()) {
          continue;
        }
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
    if (currentCycle.isStdOrDbImplOrTransImpl()) {
      code.add("@Override", "public #vcQualifiedName# getNext#vcCamelName#(#formalParams#) {", "\treturn (#vcQualifiedName#)getNextVertex(#vcQualifiedName#.class#actualParams#);", "}");
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
    VertexClass vc = (VertexClass) aec;
    CodeList code = new CodeList();
    Set<EdgeClass> edgeClassSet = new HashSet<EdgeClass>();
    if (currentCycle.isStdOrDbImplOrTransImpl()) {
      edgeClassSet.addAll(vc.getConnectedEdgeClasses());
    }
    if (currentCycle.isAbstract()) {
      edgeClassSet.addAll(vc.getOwnConnectedEdgeClasses());
      if (vc.getAllSuperClasses().size() == 1) {
        for (EdgeClass ec : vc.getConnectedEdgeClasses()) {
          VertexClass dvc = vc.getGraphClass().getSchema().getDefaultVertexClass();
          if ((ec.getTo().getVertexClass() == dvc) || (ec.getFrom().getVertexClass() == dvc)) {
            edgeClassSet.add(ec);
          }
        }
      }
    }
    for (EdgeClass ec : edgeClassSet) {
      if (ec.isInternal()) {
        continue;
      }
      if (currentCycle.isStdOrDbImplOrTransImpl()) {
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
      if (currentCycle.isStdOrDbImplOrTransImpl()) {
        s.add("@Override");
        s.add("public Iterable<#edgeClassQualifiedName#> get#edgeClassUniqueName#Incidences() {");
        s.add("\treturn new IncidenceIterable<#edgeClassQualifiedName#>(this, #edgeClassQualifiedName#.class);");
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
      if (currentCycle.isStdOrDbImplOrTransImpl()) {
        s.add("@Override");
        s.add("public Iterable<#edgeClassQualifiedName#> get#edgeClassUniqueName#Incidences(EdgeDirection direction) {");
        s.add("\treturn new IncidenceIterable<#edgeClassQualifiedName#>(this, #edgeClassQualifiedName#.class, direction);");
        s.add("}");
      }
    }
    return code;
  }

  private CodeBlock createGetEdgeForRolenameMethod() {
    CodeList list = new CodeList();
    addImports("de.uni_koblenz.jgralab.schema.impl.DirectedSchemaEdgeClass");
    CodeSnippet code = new CodeSnippet(true);
    code.add("private static java.util.Map<String, DirectedSchemaEdgeClass> roleMap;");
    list.addNoIndent(code);
    code = new CodeSnippet(true);
    code.add("static {");
    code.add("roleMap = new java.util.HashMap<String, DirectedSchemaEdgeClass>();");
    list.addNoIndent(code);
    VertexClass vc = (VertexClass) aec;
    for (EdgeClass ec : vc.getValidFromEdgeClasses()) {
      if (!ec.getTo().getRolename().isEmpty()) {
        code = new CodeSnippet(true);
        code.setVariable("rolename", ec.getTo().getRolename());
        code.setVariable("edgeclass", ec.getSchema().getQualifiedName() + ".instance()." + ec.getVariableName());
        code.setVariable("dir", "de.uni_koblenz.jgralab.EdgeDirection.OUT");
        code.add("roleMap.put(\"#rolename#\", new DirectedSchemaEdgeClass(#edgeclass#, #dir#));");
        list.addNoIndent(code);
      }
    }
    for (EdgeClass ec : vc.getValidToEdgeClasses()) {
      if (!ec.getFrom().getRolename().isEmpty()) {
        code = new CodeSnippet(true);
        code.setVariable("rolename", ec.getFrom().getRolename());
        code.setVariable("edgeclass", ec.getSchema().getQualifiedName() + ".instance()." + ec.getVariableName());
        code.setVariable("dir", "de.uni_koblenz.jgralab.EdgeDirection.IN");
        code.add("roleMap.put(\"#rolename#\", new DirectedSchemaEdgeClass(#edgeclass#, #dir#));");
        list.addNoIndent(code);
      }
    }
    code = new CodeSnippet(true);
    code.add("}");
    list.addNoIndent(code);
    code = new CodeSnippet(true);
    code.add("public DirectedSchemaEdgeClass getEdgeForRolename(String rolename) {", "\treturn roleMap.get(rolename);", "}");
    list.addNoIndent(code);
    return list;
  }
}