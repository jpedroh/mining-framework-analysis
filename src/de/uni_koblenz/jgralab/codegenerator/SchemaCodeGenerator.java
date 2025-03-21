package de.uni_koblenz.jgralab.codegenerator;
import java.util.List;
import java.util.Stack;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.schema.AggregationKind;
import de.uni_koblenz.jgralab.schema.Attribute;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;
import de.uni_koblenz.jgralab.schema.CompositeDomain;
import de.uni_koblenz.jgralab.schema.Constraint;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.EnumDomain;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.ListDomain;
import de.uni_koblenz.jgralab.schema.MapDomain;
import de.uni_koblenz.jgralab.schema.NamedElement;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.RecordDomain;
import de.uni_koblenz.jgralab.schema.RecordDomain.RecordComponent;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.SetDomain;
import de.uni_koblenz.jgralab.schema.VertexClass;

/**
 * TODO add comment
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class SchemaCodeGenerator extends CodeGenerator {
  private final Schema schema;

  /**
	 * Creates a new SchemaCodeGenerator which creates code for the given schema
	 * 
	 * @param schema
	 *            the schema to create the code for
	 * @param schemaPackageName
	 *            the package the schema is located in
	 * @param config
	 *            a CodeGenaratorConfiguration specifying the required variants
	 */
  public SchemaCodeGenerator(Schema schema, String schemaPackageName, CodeGeneratorConfiguration config) {
    super(schemaPackageName, "", config);
    this.schema = schema;
    rootBlock.setVariable("simpleClassName", schema.getName());
    rootBlock.setVariable("baseClassName", 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/SchemaCodeGenerator.java/left.java
    schema.getName()
=======
    "SchemaImpl"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/SchemaCodeGenerator.java/right.java
    );
    rootBlock.setVariable("isClassOnly", "true");
    rootBlock.setVariable("gcName", schema.getGraphClass().getQualifiedName());
    rootBlock.setVariable("gcCamelName", camelCase(schema.getGraphClass().getQualifiedName()));
    rootBlock.setVariable("gcImplName", schema.getGraphClass().getQualifiedName() + "Impl");
  }

  @Override protected CodeBlock createHeader() {
    addImports("#jgSchemaImplPackage#.#baseClassName#");
    addImports("#jgSchemaPackage#.VertexClass");
    addImports("java.lang.ref.WeakReference");
    CodeSnippet code = new CodeSnippet(true, "/**", " * The schema #simpleClassName# is implemented following the singleton pattern.", " * To get the instance, use the static method <code>instance()</code>.", " */", "public class #simpleClassName# extends #baseClassName# {");
    return code;
  }

  @Override protected CodeBlock createBody() {
    CodeList code = new CodeList();
    if (currentCycle.isClassOnly()) {
      code.add(createVariables());
      code.add(createConstructor());
      code.add(createGetDefaultGraphFactoryMethod());
      code.add(
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/SchemaCodeGenerator.java/left.java
      createGraphFactoryMethod()
=======
      createGraphFactoryMethods()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/SchemaCodeGenerator.java/right.java
      );
    }
    return code;
  }

  @Override protected CodeBlock createFooter() {
    CodeList footer = new CodeList();
    footer.add(new CodeSnippet("", "@Override", "public boolean equals(Object o) {", "\treturn super.equals(o);", "}"));
    footer.add(new CodeSnippet("", "@Override", "public int hashCode() {", "\treturn super.hashCode();", "}"));
    footer.addNoIndent(super.createFooter());
    return footer;
  }

  private CodeBlock createGraphFactoryMethod() {
    addImports("#jgPackage#.Graph", "#jgPackage#.ProgressFunction", "#jgPackage#.GraphIO", "#jgImplDbPackage#.GraphDatabaseException", "#jgImplDbPackage#.GraphDatabase", "#jgPackage#.GraphIOException");
    if (config.hasDatabaseSupport()) {
      addImports("#jgPackage#.GraphException");
    }
    CodeSnippet code = new CodeSnippet(true, "/**", " * Creates a new #gcName# graph with initial vertex and edge counts <code>vMax</code>, <code>eMax</code>.", " *", " * @param vMax initial vertex count", " * @param eMax initial edge count", "*/", "public #gcName# create#gcCamelName#(int vMax, int eMax) {", ((config.hasStandardSupport()) ? "\treturn (#gcCamelName#) graphFactory.createGraph(#gcCamelName#.class, null, vMax, eMax);" : "\tthrow new UnsupportedOperationException(\"No Standard support compiled.\");"), "}", "", "/**", " * Creates a new #gcName# graph with the ID <code>id</code> initial vertex and edge counts <code>vMax</code>, <code>eMax</code>.", " *", " * @param id the id name of the new graph", " * @param vMax initial vertex count", " * @param eMax initial edge count", " */", "public #gcName# create#gcCamelName#(String id, int vMax, int eMax) {", ((config.hasStandardSupport()) ? "\treturn (#gcCamelName#) graphFactory.createGraph(#gcCamelName#.class, id, vMax, eMax);" : "\tthrow new UnsupportedOperationException(\"No Standard support compiled.\");"), "}", "", "/**", " * Creates a new #gcName# graph.", "*/", "public #gcName# create#gcCamelName#() {", ((config.hasStandardSupport()) ? "\treturn (#gcCamelName#) graphFactory.createGraph(#gcCamelName#.class, null);" : "\tthrow new UnsupportedOperationException(\"No Standard support compiled.\");"), "}", "", "/**", " * Creates a new #gcName# graph with the ID <code>id</code>.", " *", " * @param id the id name of the new graph", " */", "public #gcName# create#gcCamelName#(String id) {", ((config.hasStandardSupport()) ? "\treturn (#gcCamelName#) graphFactory.createGraph(#gcCamelName#.class, id);" : "\tthrow new UnsupportedOperationException(\"No Standard support compiled.\");"), "}", "", "/**", " * Creates a new #gcName# graph in a database with given <code>id</code>.", " *", " * @param id Identifier of new graph", " * @param graphDatabase Database which should contain graph", " */", "public #gcName# create#gcCamelName#WithDatabaseSupport(String id, GraphDatabase graphDatabase) throws GraphDatabaseException{", ((config.hasDatabaseSupport()) ? "\tGraph graph = graphFactory.createGraphWithDatabaseSupport(#gcCamelName#.class, graphDatabase, id );\n\t\tif(!graphDatabase.containsGraph(id)){\n\t\t\tgraphDatabase.insert((#jgImplDbPackage#.GraphImpl)graph);\n\t\t\treturn (#gcCamelName#)graph;\n\t\t}\n\t\telse\n\t\t\tthrow new GraphException(\"Graph with identifier \" + id + \" already exists in database.\");" : "\tthrow new UnsupportedOperationException(\"No database support compiled.\");"), "}", "/**", " * Creates a new #gcName# graph in a database with given <code>id</code>.", " *", " * @param id Identifier of new graph", " * @param vMax Maximum initial count of vertices that can be held in graph.", " * @param eMax Maximum initial count of edges that can be held in graph.", " * @param graphDatabase Database which should contain graph", " */", "public #gcName# create#gcCamelName#WithDatabaseSupport(String id, int vMax, int eMax, GraphDatabase graphDatabase) throws GraphDatabaseException{", ((config.hasDatabaseSupport()) ? "\tGraph graph = graphFactory.createGraphWithDatabaseSupport(#gcCamelName#.class, graphDatabase, id, vMax, eMax );\n\t\tif(!graphDatabase.containsGraph(id)){\n\t\t\tgraphDatabase.insert((#jgImplDbPackage#.GraphImpl)graph);\n\t\t\treturn (#gcCamelName#)graph;\n\t\t}\n\t\telse\n\t\t\tthrow new GraphException(\"Graph with identifier \" + id + \" already exists in database.\");" : "\tthrow new UnsupportedOperationException(\"No database support compiled.\");"), "}", "/**", " * Creates a new #gcName# graph with transaction support with initial vertex and edge counts <code>vMax</code>, <code>eMax</code>.", " *", " * @param vMax initial vertex count", " * @param eMax initial edge count", "*/", "public #gcName# create#gcCamelName#WithTransactionSupport(int vMax, int eMax) {", ((config.hasTransactionSupport()) ? "\treturn (#gcCamelName#) graphFactory.createGraphWithTransactionSupport(#gcCamelName#.class, null, vMax, eMax);" : "\tthrow new UnsupportedOperationException(\"No Transaction support compiled.\");"), "}", "", "/**", " * Creates a new #gcName# graph with transaction support with the ID <code>id</code> initial vertex and edge counts <code>vMax</code>, <code>eMax</code>.", " *", " * @param id the id name of the new graph", " * @param vMax initial vertex count", " * @param eMax initial edge count", " */", "public #gcName# create#gcCamelName#WithTransactionSupport(String id, int vMax, int eMax) {", ((config.hasTransactionSupport()) ? "\treturn (#gcCamelName#) graphFactory.createGraphWithTransactionSupport(#gcCamelName#.class, id, vMax, eMax);" : "\tthrow new UnsupportedOperationException(\"No Transaction support compiled.\");"), "}", "", "/**", " * Creates a new #gcName# graph.", "*/", "public #gcName# create#gcCamelName#WithTransactionSupport() {", ((config.hasTransactionSupport()) ? "\treturn (#gcCamelName#) graphFactory.createGraphWithTransactionSupport(#gcCamelName#.class, null);" : "\tthrow new UnsupportedOperationException(\"No Transaction support compiled.\");"), "}", "", "/**", " * Creates a new #gcName# graph with the ID <code>id</code>.", " *", " * @param id the id name of the new graph", " */", "public #gcName# create#gcCamelName#WithTransactionSupport(String id) {", ((config.hasTransactionSupport()) ? "\treturn (#gcCamelName#) graphFactory.createGraphWithTransactionSupport(#gcCamelName#.class, id);" : "\tthrow new UnsupportedOperationException(\"No Transaction support compiled.\");"), "}", "", "/**", " * Loads a #gcName# graph from the file <code>filename</code>.", " *", " * @param filename the name of the file", " * @return the loaded #gcName#", " * @throws GraphIOException if the graph cannot be loaded", " */", "public #gcName# load#gcCamelName#(String filename) throws GraphIOException {", ((config.hasStandardSupport()) ? "\treturn load#gcCamelName#(filename, null);" : "\tthrow new UnsupportedOperationException(\"No Standard support compiled.\");"), "}", "", "/**", " * Loads a #gcName# graph from the file <code>filename</code>.", " *", " * @param filename the name of the file", " * @param pf a progress function to monitor graph loading", " * @return the loaded #gcName#", " * @throws GraphIOException if the graph cannot be loaded", " */", "public #gcName# load#gcCamelName#(String filename, ProgressFunction pf) throws GraphIOException {", ((config.hasStandardSupport()) ? "\tGraph graph = GraphIO.loadGraphFromFileWithStandardSupport(filename, this, pf);\n" + "\tif (!(graph instanceof #gcName#)) {\n" + "\t\tthrow new GraphIOException(\"Graph in file \'\" + filename + \"\' is not an instance of GraphClass #gcName#\");\n" + "\t}" + "\treturn (#gcName#) graph;\n" : "\tthrow new UnsupportedOperationException(\"No Standard support compiled.\");"), "}", "", "/**", " * Loads a #gcName# graph with transaction support from the file <code>filename</code>.", " *", " * @param filename the name of the file", " * @return the loaded #gcName#", " * @throws GraphIOException if the graph cannot be loaded", " */", "public #gcName# load#gcCamelName#WithTransactionSupport(String filename) throws GraphIOException {", ((config.hasTransactionSupport()) ? "\treturn load#gcCamelName#WithTransactionSupport(filename, null);" : "\tthrow new UnsupportedOperationException(\"No Transaction support compiled.\");"), "}", "", "/**", " * Loads a #gcName# graph with transaction support from the file <code>filename</code>.", " *", " * @param filename the name of the file", " * @param pf a progress function to monitor graph loading", " * @return the loaded #gcName#", " * @throws GraphIOException if the graph cannot be loaded", " */", "public #gcName# load#gcCamelName#WithTransactionSupport(String filename, ProgressFunction pf) throws GraphIOException {", ((config.hasTransactionSupport()) ? "\tGraph graph = GraphIO.loadGraphFromFileWithTransactionSupport(filename, pf);\n" + "\tif (!(graph instanceof #gcName#)) {\n" + "\t\tthrow new GraphIOException(\"Graph in file \'\" + filename + \"\' is not an instance of GraphClass #gcName#\");\n" + "\t}" + "\treturn (#gcName#) graph;" : "\tthrow new UnsupportedOperationException(\"No Transaction support compiled.\");"), "}");
    code.setVariable("gcName", schema.getGraphClass().getQualifiedName());
    code.setVariable("gcCamelName", camelCase(schema.getGraphClass().getQualifiedName()));
    code.setVariable("gcImplName", schema.getGraphClass().getQualifiedName() + "Impl");
    return code;
  }

  private CodeBlock createGetDefaultGraphFactoryMethod() {
    CodeList code = new CodeList();
    code.addNoIndent(new CodeSnippet(true, "@Override", "public #jgPackage#.GraphFactory createDefaultGraphFactory(#jgPackage#.ImplementationType implementationType) {"));
    code.add(new CodeSnippet("switch(implementationType) {"));
    code.add(new CodeSnippet("\tcase GENERIC:", "\t\treturn new #jgImplPackage#.generic.GenericGraphFactoryImpl(this);"));
    if (config.hasStandardSupport()) {
      code.add(new CodeSnippet("\tcase STANDARD:", "\t\treturn new #schemaImplStdPackage#.#gcCamelName#FactoryImpl();"));
    }
    if (config.hasTransactionSupport()) {
      code.add(new CodeSnippet("\tcase TRANSACTION:", "\t\treturn new #schemaImplTransPackage#.#gcCamelName#FactoryImpl();"));
    }
    if (config.hasDatabaseSupport()) {
      code.add(new CodeSnippet("\tcase DATABASE:", "\t\treturn new #schemaImplDbPackage#.#gcCamelName#FactoryImpl();"));
    }
    code.add(new CodeSnippet("}", "throw new UnsupportedOperationException(\"No \" + implementationType + \" support compiled.\");"));
    code.addNoIndent(new CodeSnippet("}"));
    return code;
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  private CodeBlock createGraphFactoryMethods() {
    addImports("#jgPackage#.GraphIO", "#jgImplDbPackage#.GraphDatabaseException", "#jgImplDbPackage#.GraphDatabase", "#jgPackage#.GraphIOException");
    if (config.hasDatabaseSupport()) {
      addImports("#jgPackage#.GraphException");
    }
    CodeList code = new CodeList();
    code.setVariable("gcVariableName", schema.getGraphClass().getVariableName());
    code.addNoIndent(new CodeSnippet(true, "/**", " * Creates a new #gcName# graph.", "*/", "public #gcName# create#gcCamelName#(#jgPackage#.ImplementationType implType) {", "\treturn create#gcCamelName#(implType, null, 100, 100);", "}"));
    code.addNoIndent(new CodeSnippet(true, "/**", " * Creates a new #gcName# graph with initial vertex and edge counts <code>vMax</code>, <code>eMax</code>.", " *", " * @param vMax initial vertex count", " * @param eMax initial edge count", "*/", "public #gcName# create#gcCamelName#(#jgPackage#.ImplementationType implType, String id, int vMax, int eMax) {", "\t#jgPackage#.GraphFactory factory = createDefaultGraphFactory(implType);", "\treturn factory.createGraph(#gcVariableName#, id, vMax, eMax);", "}"));
    code.addNoIndent(new CodeSnippet(true, "/**", " * Creates a new #gcName# graph.", "*/", "public #gcName# create#gcCamelName#(#jgPackage#.GraphFactory factory) {", "\treturn factory.createGraph(#gcVariableName#, null, 100, 100);", "}"));
    code.addNoIndent(new CodeSnippet(true, "/**", " * Creates a new #gcName# graph.", "*/", "public #gcName# create#gcCamelName#(#jgPackage#.GraphFactory factory, String id, int vMax, int eMax) {", "\treturn factory.createGraph(#gcVariableName#, id, vMax, eMax);", "}"));
    code.addNoIndent(new CodeSnippet(true, "/**", " * Creates a new #gcName# graph in a database with given <code>id</code>.", " *", " * @param id Identifier of new graph", " * @param graphDatabase Database which should contain graph", " */", "public #gcName# create#gcCamelName#(String id, GraphDatabase graphDatabase) throws GraphDatabaseException{", "\treturn create#gcCamelName#(id, 100, 100, graphDatabase);", "}"));
    code.addNoIndent(new CodeSnippet(true, "/**", " * Creates a new #gcName# graph in a database with given <code>id</code>.", " *", " * @param id Identifier of new graph", " * @param vMax Maximum initial count of vertices that can be held in graph.", " * @param eMax Maximum initial count of edges that can be held in graph.", " * @param graphDatabase Database which should contain graph", " */", "public #gcName# create#gcCamelName#(String id, int vMax, int eMax, GraphDatabase graphDatabase) throws GraphDatabaseException{"));
    if (config.hasDatabaseSupport()) {
      code.add(new CodeSnippet("#jgImplPackage#.GraphFactoryImpl graphFactory = (#jgImplPackage#.GraphFactoryImpl) createDefaultGraphFactory(#jgPackage#.ImplementationType.DATABASE);", "graphFactory.setGraphDatabase(graphDatabase);", "#gcCamelName# graph = graphFactory.createGraph(#gcVariableName#, id, vMax, eMax);", "if (!graphDatabase.containsGraph(id)) {", "\tgraphDatabase.insert((#jgImplDbPackage#.GraphImpl)graph);", "\treturn graph;", "} else {", "\tthrow new GraphException(\"Graph with identifier \" + id + \" already exists in database.\");", "}"));
    } else {
      code.add(new CodeSnippet("throw new UnsupportedOperationException(\"No DATABASE support compiled.\");"));
    }
    code.addNoIndent(new CodeSnippet("}"));
    if (config.hasStandardSupport()) {
      code.addNoIndent(new CodeSnippet(true, "public #gcName# load#gcCamelName#(String filename) throws GraphIOException {", "\t#jgPackage#.GraphFactory factory = createDefaultGraphFactory(#jgPackage#.ImplementationType.STANDARD);", "\treturn load#gcCamelName#(filename, factory, null);", "}"));
      code.addNoIndent(new CodeSnippet(true, "public #gcName# load#gcCamelName#(String filename, #jgPackage#.ProgressFunction pf) throws GraphIOException {", "\t#jgPackage#.GraphFactory factory = createDefaultGraphFactory(#jgPackage#.ImplementationType.STANDARD);", "\treturn load#gcCamelName#(filename, factory, pf);", "}"));
    }
    code.addNoIndent(new CodeSnippet(true, "public #gcName# load#gcCamelName#(String filename, #jgPackage#.ImplementationType implType) throws GraphIOException {", "\t#jgPackage#.GraphFactory factory = createDefaultGraphFactory(implType);", "\treturn load#gcCamelName#(filename, factory, null);", "}"));
    code.addNoIndent(new CodeSnippet(true, "", "public #gcName# load#gcCamelName#(String filename, #jgPackage#.ImplementationType implType, #jgPackage#.ProgressFunction pf) throws GraphIOException {", "\t#jgPackage#.GraphFactory factory = createDefaultGraphFactory(implType);", "\treturn load#gcCamelName#(filename, factory, pf);", "}"));
    code.addNoIndent(new CodeSnippet(true, "public #gcName# load#gcCamelName#(String filename, #jgPackage#.GraphFactory factory) throws GraphIOException {", "\treturn GraphIO.loadGraphFromFile(filename, factory, null);", "}"));
    code.addNoIndent(new CodeSnippet(true, "public #gcName# load#gcCamelName#(String filename, #jgPackage#.GraphFactory factory, #jgPackage#.ProgressFunction pf) throws GraphIOException {", "\treturn GraphIO.loadGraphFromFile(filename, factory, pf);", "}"));
    return code;
  }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/SchemaCodeGenerator.java/right.java


  private CodeBlock createConstructor() {
    CodeList code = new CodeList();
    code.addNoIndent(new CodeSnippet(true, "/**", " * the weak reference to the singleton instance", " */", "static WeakReference<#simpleClassName#> theInstance = new WeakReference<#simpleClassName#>(null);", "", "/**", " * @return the singleton instance of #simpleClassName#", " */", "public static #simpleClassName# instance() {", "\t#simpleClassName# s = theInstance.get();", "\tif (s != null) {", "\t\treturn s;", "\t}", "\tsynchronized (#simpleClassName#.class) {", "\t\ts = theInstance.get();", "\t\tif (s != null) {", "\t\t\treturn s;", "\t\t}", "\t\ts = new #simpleClassName#();", "\t\ttheInstance = new WeakReference<#simpleClassName#>(s);", "\t}", "\treturn s;", "}", "", "/**", " * Creates a #simpleClassName# and builds its schema classes.", " * This constructor is private. Use the <code>instance()</code> method", " * to acess the schema.", " */", "private #simpleClassName#() {", "\tsuper(\"#simpleClassName#\", \"#schemaPackage#\");"));
    code.add(createEnumDomains());
    code.add(createCompositeDomains());
    code.add(createGraphClass());
    code.add(createPackageComments());

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/SchemaCodeGenerator.java/left.java
    addImports("#schemaPackage#.#simpleClassName#Factory");
=======
>>>>>>> Unknown file: This is a bug in JDime.

    code.addNoIndent(new CodeSnippet(true, "\tfinish();", "}"));
    return code;
  }

  private CodeBlock createPackageComments() {
    CodeList code = new CodeList();
    Package pkg = schema.getDefaultPackage();
    Stack<Package> s = new Stack<Package>();
    s.push(pkg);
    boolean hasComment = false;
    while (!s.isEmpty()) {
      pkg = s.pop();
      for (Package sub : pkg.getSubPackages().values()) {
        s.push(sub);
      }
      List<String> comments = pkg.getComments();
      if (comments.isEmpty()) {
        continue;
      }
      if (!hasComment) {
        code.addNoIndent(new CodeSnippet(true, "{"));
        hasComment = true;
      }
      for (String comment : comments) {
        code.add(new CodeSnippet("getPackage(\"" + pkg.getQualifiedName() + "\").addComment(\"" + stringQuote(comment) + "\");"));
      }
    }
    if (hasComment) {
      code.addNoIndent(new CodeSnippet(false, "}"));
    }
    return code;
  }

  private CodeBlock createGraphClass() {
    GraphClass gc = schema.getGraphClass();
    CodeList code = new CodeList();
    addImports("#jgSchemaPackage#.GraphClass");
    code.setVariable("gcVariable", "gc");
    code.setVariable("aecVariable", "gc");
    code.setVariable("schemaVariable", gc.getVariableName());
    code.setVariable("gcAbstract", gc.isAbstract() ? "true" : "false");
    code.addNoIndent(new CodeSnippet(true, "{", "\tGraphClass #gcVariable# = #schemaVariable# = createGraphClass(\"#gcName#\");", "\t#gcVariable#.setAbstract(#gcAbstract#);"));
    for (GraphClass superClass : gc.getDirectSuperClasses()) {
      if (superClass.isInternal()) {
        continue;
      }
      CodeSnippet s = new CodeSnippet("#gcVariable#.addSuperClass(getGraphClass(\"#superClassName#\"));");
      s.setVariable("superClassName", superClass.getQualifiedName());
      code.add(s);
    }
    code.add(createAttributes(gc));
    code.add(createConstraints(gc));
    code.add(createComments("gc", gc));
    code.add(createVertexClasses(gc));
    code.add(createEdgeClasses(gc));
    code.addNoIndent(new CodeSnippet(false, "}"));
    return code;
  }

  private CodeBlock createComments(String variableName, NamedElement ne) {
    CodeList code = new CodeList();
    code.setVariable("namedElement", variableName);
    for (String comment : ne.getComments()) {
      code.addNoIndent(new CodeSnippet("#namedElement#.addComment(" + GraphIO.toUtfString(comment) + ");"));
    }
    return code;
  }

  private CodeBlock createVariables() {
    CodeList code = new CodeList();
    code.addNoIndent(new CodeSnippet("public final GraphClass " + schema.getGraphClass().getVariableName() + ";"));
    for (VertexClass vc : 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/SchemaCodeGenerator.java/left.java
    schema.getVertexClassesInTopologicalOrder()
=======
    schema.getGraphClass().getVertexClasses()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/SchemaCodeGenerator.java/right.java
    ) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/SchemaCodeGenerator.java/left.java
      if (!vc.isInternal()) {
        code.addNoIndent(new CodeSnippet("public final VertexClass " + vc.getVariableName() + ";"));
      }
=======
>>>>>>> Unknown file: This is a bug in JDime.
    }
    for (EdgeClass ec : schema.getGraphClass().getEdgeClasses()) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/SchemaCodeGenerator.java/left.java
      if (!ec.isInternal()) {
        code.addNoIndent(new CodeSnippet("public final EdgeClass " + ec.getVariableName() + ";"));
      }
=======
      code.addNoIndent(new CodeSnippet("public final EdgeClass " + ec.getVariableName() + ";"));
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/SchemaCodeGenerator.java/right.java
    }
    return code;
  }

  private CodeBlock createEdgeClasses(GraphClass gc) {
    CodeList code = new CodeList();
    for (EdgeClass ec : schema.getGraphClass().getEdgeClasses()) {
      if (
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/SchemaCodeGenerator.java/left.java
      !ec.isInternal() && (ec.getGraphClass() == gc)
=======
      (ec.getGraphClass() == gc)
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/SchemaCodeGenerator.java/right.java
      ) {
        code.addNoIndent(createEdgeClass(ec));
      }
    }
    return code;
  }

  private CodeBlock createEdgeClass(EdgeClass ec) {
    CodeList code = new CodeList();
    addImports("#jgSchemaPackage#.EdgeClass");
    code.setVariable("ecType", "EdgeClass");
    code.setVariable("ecName", ec.getQualifiedName());
    code.setVariable("schemaVariable", ec.getVariableName());
    code.setVariable("aecVariable", "ec");
    code.setVariable("ecAbstract", ec.isAbstract() ? "true" : "false");
    code.setVariable("fromClass", ec.getFrom().getVertexClass().getVariableName());
    code.setVariable("fromRole", ec.getFrom().getRolename());
    code.setVariable("toClass", ec.getTo().getVertexClass().getVariableName());
    code.setVariable("toRole", ec.getTo().getRolename());
    code.setVariable("toAggregation", AggregationKind.class.getCanonicalName() + "." + ec.getTo().getAggregationKind().toString());
    code.setVariable("fromAggregation", AggregationKind.class.getCanonicalName() + "." + ec.getFrom().getAggregationKind().toString());
    code.setVariable("fromPart", "#fromClass#, " + ec.getFrom().getMin() + ", " + ec.getFrom().getMax() + ", \"#fromRole#\"" + ", #fromAggregation#");
    code.setVariable("toPart", "#toClass#, " + ec.getTo().getMin() + ", " + ec.getTo().getMax() + ", \"#toRole#\"" + ", #toAggregation#");
    code.addNoIndent(new CodeSnippet(true, "{", 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/SchemaCodeGenerator.java/left.java
    "\t#ecType# #ecVariable# = #schemaVariable# = #gcVariable#.create#ecType#(\"#ecName#\","
=======
    "\t#ecType# #aecVariable# = #schemaVariable# = #gcVariable#.create#ecType#(\"#ecName#\","
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/SchemaCodeGenerator.java/right.java
    , "\t\t#fromPart#,", "\t\t#toPart#);", "\t#aecVariable#.setAbstract(#ecAbstract#);"));
    for (
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/SchemaCodeGenerator.java/left.java
    AttributedElementClass
=======
    EdgeClass
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/SchemaCodeGenerator.java/right.java
     superClass : ec.getDirectSuperClasses()) {
      if (superClass.isInternal()) {
        continue;
      }
      CodeSnippet s = new CodeSnippet(
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/SchemaCodeGenerator.java/left.java
      "#ecVariable#.addSuperClass(#superClassName#);"
=======
      "#aecVariable#.addSuperClass(#superClassName#);"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/SchemaCodeGenerator.java/right.java
      );
      s.setVariable("superClassName", superClass.getVariableName());
      code.add(s);
    }
    for (String redefinedFromRole : ec.getFrom().getRedefinedRoles()) {
      CodeSnippet s = new CodeSnippet(
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/SchemaCodeGenerator.java/left.java
      "#ecVariable#.getFrom().addRedefinedRole(\"#redefinedFromRole#\");"
=======
      "#aecVariable#.getFrom().addRedefinedRole(\"#redefinedFromRole#\");"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/SchemaCodeGenerator.java/right.java
      );
      s.setVariable("redefinedFromRole", redefinedFromRole);
      code.add(s);
    }
    for (String redefinedToRole : ec.getTo().getRedefinedRoles()) {
      CodeSnippet s = new CodeSnippet(
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/SchemaCodeGenerator.java/left.java
      "#ecVariable#.getTo().addRedefinedRole(\"#redefinedToRole#\");"
=======
      "#aecVariable#.getTo().addRedefinedRole(\"#redefinedToRole#\");"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/SchemaCodeGenerator.java/right.java
      );
      s.setVariable("redefinedToRole", redefinedToRole);
      code.add(s);
    }
    code.add(createAttributes(ec));
    code.add(createConstraints(ec));
    code.add(createComments("ec", ec));
    code.addNoIndent(new CodeSnippet("}"));
    return code;
  }

  private CodeBlock createVertexClasses(GraphClass gc) {
    CodeList code = new CodeList();
    for (VertexClass vc : schema.getVertexClasses()) {
      if (vc.isInternal()) {
        CodeSnippet s = new CodeSnippet();
        s.setVariable("schemaVariable", vc.getVariableName());
        s.add("@SuppressWarnings(\"unused\")");
        s.add("VertexClass #schemaVariable# = getDefaultVertexClass();");
        code.addNoIndent(s);
      } else {
        if (vc.getGraphClass() == gc) {
          code.addNoIndent(createVertexClass(vc));
        }
      }
    }
    return code;
  }

  private CodeBlock createVertexClass(VertexClass vc) {
    CodeList code = new CodeList();
    code.setVariable("vcName", vc.getQualifiedName());
    code.setVariable("aecVariable", "vc");
    code.setVariable("schemaVariable", vc.getVariableName());
    code.setVariable("vcAbstract", vc.isAbstract() ? "true" : "false");
    code.addNoIndent(new CodeSnippet(true, "{", 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/SchemaCodeGenerator.java/left.java
    "\tVertexClass #vcVariable# = #schemaVariable# = #gcVariable#.createVertexClass(\"#vcName#\");"
=======
    "\tVertexClass #aecVariable# = #schemaVariable# = #gcVariable#.createVertexClass(\"#vcName#\");"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/SchemaCodeGenerator.java/right.java
    , "\t#aecVariable#.setAbstract(#vcAbstract#);"));
    for (VertexClass superClass : vc.getDirectSuperClasses()) {
      if (superClass.isInternal()) {
        continue;
      }
      CodeSnippet s = new CodeSnippet("#aecVariable#.addSuperClass(#superClassName#);");
      s.setVariable("superClassName", superClass.getVariableName());
      code.add(s);
    }
    code.add(createAttributes(vc));
    code.add(createConstraints(vc));
    code.add(createComments("vc", vc));
    code.addNoIndent(new CodeSnippet("}"));
    return code;
  }

  private CodeBlock createAttributes(AttributedElementClass<?, ?> aec) {
    CodeList code = new CodeList();
    for (Attribute attr : aec.getOwnAttributeList()) {
      CodeSnippet s = new CodeSnippet(false, "#aecVariable#.addAttribute(createAttribute(\"#attrName#\", getDomain(\"#domainName#\"), getAttributedElementClass(\"#aecName#\"), #defaultValue#));");
      s.setVariable("attrName", attr.getName());
      s.setVariable("domainName", attr.getDomain().getQualifiedName());
      s.setVariable("aecName", aec.getQualifiedName());
      if (attr.getDefaultValueAsString() == null) {
        s.setVariable("defaultValue", "null");
      } else {
        String defaultValue = attr.getDefaultValueAsString().replaceAll("([\\\"])", "\\\\$1");
        defaultValue = defaultValue.replaceAll("#", "\\u0023");
        s.setVariable("defaultValue", "\"" + defaultValue + "\"");
      }
      code.addNoIndent(s);
    }
    return code;
  }

  private CodeBlock createConstraints(
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/SchemaCodeGenerator.java/left.java
  AttributedElementClass aec
=======
  AttributedElementClass<?, ?> aec
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/SchemaCodeGenerator.java/right.java
  ) {
    CodeList code = new CodeList();
    for (Constraint constraint : aec.getConstraints()) {
      addImports("#jgSchemaImplPackage#.ConstraintImpl");
      CodeSnippet constraintSnippet = new CodeSnippet(false);
      constraintSnippet.add("#aecVariable#.addConstraint(" + "new ConstraintImpl(#message#, #predicate#, #offendingElements#));");
      constraintSnippet.setVariable("message", "\"" + stringQuote(constraint.getMessage()) + "\"");
      constraintSnippet.setVariable("predicate", "\"" + stringQuote(constraint.getPredicate()) + "\"");
      if (constraint.getOffendingElementsQuery() != null) {
        constraintSnippet.setVariable("offendingElements", "\"" + stringQuote(constraint.getOffendingElementsQuery()) + "\"");
      } else {
        constraintSnippet.setVariable("offendingElements", "null");
      }
      code.addNoIndent(constraintSnippet);
    }
    return code;
  }

  private CodeBlock createEnumDomains() {
    CodeList code = new CodeList();
    for (EnumDomain dom : schema.getEnumDomains()) {
      CodeSnippet s = new CodeSnippet(true);
      s.setVariable("domName", dom.getQualifiedName());
      code.addNoIndent(s);
      addImports("#jgSchemaPackage#.EnumDomain");
      s.add("{", "\tEnumDomain dom = createEnumDomain(\"#domName#\");");
      for (String c : dom.getConsts()) {
        s.add("\tdom.addConst(\"" + c + "\");");
      }
      code.add(createComments("dom", dom));
      code.addNoIndent(new CodeSnippet("}"));
    }
    return code;
  }

  private CodeBlock createCompositeDomains() {
    CodeList code = new CodeList();
    for (CompositeDomain dom : schema.getCompositeDomains()) {
      CodeSnippet s = new CodeSnippet(true);
      s.setVariable("domName", dom.getQualifiedName());
      code.addNoIndent(s);
      if (dom instanceof ListDomain) {
        s.setVariable("componentDomainName", ((ListDomain) dom).getBaseDomain().getQualifiedName());
        s.add("createListDomain(getDomain(\"#componentDomainName#\"));");
      } else {
        if (dom instanceof SetDomain) {
          s.setVariable("componentDomainName", ((SetDomain) dom).getBaseDomain().getQualifiedName());
          s.add("createSetDomain(getDomain(\"#componentDomainName#\"));");
        } else {
          if (dom instanceof MapDomain) {
            MapDomain mapDom = (MapDomain) dom;
            s.setVariable("keyDomainName", mapDom.getKeyDomain().getQualifiedName());
            s.setVariable("valueDomainName", mapDom.getValueDomain().getQualifiedName());
            s.add("createMapDomain(getDomain(\"#keyDomainName#\"), getDomain(\"#valueDomainName#\"));");
          } else {
            if (dom instanceof RecordDomain) {
              addImports("#jgSchemaPackage#.RecordDomain");
              s.add("{", "\tRecordDomain dom = createRecordDomain(\"#domName#\");");
              RecordDomain rd = (RecordDomain) dom;
              for (RecordComponent c : rd.getComponents()) {
                s.add("\tdom.addComponent(\"" + c.getName() + "\", getDomain(\"" + c.getDomain().getQualifiedName() + "\"));");
              }
              code.add(createComments("dom", rd));
              code.addNoIndent(new CodeSnippet("}"));
            } else {
              throw new RuntimeException("FIXME!");
            }
          }
        }
      }
    }
    return code;
  }
}