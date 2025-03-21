package de.uni_koblenz.jgralab.codegenerator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.GraphElementClass;
import de.uni_koblenz.jgralab.schema.VertexClass;

/**
 * TODO add comment
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class GraphCodeGenerator extends AttributedElementCodeGenerator {
  public GraphCodeGenerator(GraphClass graphClass, String schemaPackageName, String schemaName, CodeGeneratorConfiguration config) {
    super(graphClass, schemaPackageName, config);
    rootBlock.setVariable("graphElementClass", "Graph");
    rootBlock.setVariable("schemaElementClass", "GraphClass");
    rootBlock.setVariable("schemaName", schemaName);
    rootBlock.setVariable("theGraph", "this");
  }

  @Override protected CodeBlock createHeader() {
    return super.createHeader();
  }

  @Override protected CodeBlock createBody() {
    CodeList code = (CodeList) super.createBody();
    if (currentCycle.isStdOrDbImplOrTransImpl()) {
      if (currentCycle.isStdImpl()) {
        addImports("#jgImplStdPackage#.#baseClassName#");
      }
      if (currentCycle.isTransImpl()) {
        addImports("#jgImplTransPackage#.#baseClassName#");
      }
      if (currentCycle.isDbImpl()) {
        addImports("de.uni_koblenz.jgralab.GraphException", "#jgImplDbPackage#.#baseClassName#", "#jgImplDbPackage#.GraphDatabase", "#jgImplDbPackage#.GraphDatabaseException");
      }
      rootBlock.setVariable("baseClassName", "GraphImpl");
      addImports("org.pcollections.POrderedSet");
      addImports("#jgPackage#.Vertex");
      addImports("#jgPackage#.greql2.evaluator.GreqlEvaluator");
      code.add(new CodeSnippet("\n\tprotected GreqlEvaluator greqlEvaluator;\n", "@Override", 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "public synchronized <T extends de.uni_koblenz.jgralab.Vertex> org.pcollections.POrderedSet<T> reachableVertices(de.uni_koblenz.jgralab.Vertex startVertex, String pathDescription, Class<T> vertexType) {"
=======
      "public synchronized <T extends Vertex> POrderedSet<T> reachableVertices(Vertex startVertex, String pathDescription, Class<T> vertexType) {"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "\tde.uni_koblenz.jgralab.greql2.evaluator.Query q = new de.uni_koblenz.jgralab.greql2.evaluator.Query(\"using v: v \" + pathDescription);"
=======
      "\tif (greqlEvaluator == null) {"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "\tjava.util.HashMap<String, Object> variables = new java.util.HashMap<String, Object>();"
=======
      "\t\tgreqlEvaluator = new GreqlEvaluator((String) null, this, null);"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "\tvariables.put(\"v\", startVertex);"
=======
      "\t}"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "\tde.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator eval = new de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator(q, this, variables, null);"
=======
      "\tgreqlEvaluator.setVariable(\"v\", startVertex);"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "\treturn eval.getResultSet();"
=======
      "\tgreqlEvaluator.setQuery(\"using v: v \" + pathDescription);"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , "\tgreqlEvaluator.startEvaluation();", "\treturn greqlEvaluator.getResultSet();", "}"));
    }
    code.add(createGraphElementClassMethods());
    code.add(createEdgeIteratorMethods());
    code.add(createVertexIteratorMethods());
    return code;
  }

  @Override protected CodeBlock createConstructor() {
    CodeSnippet code = new CodeSnippet(true);
    if (currentCycle.isTransImpl()) {
      code.setVariable("createSuffix", 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "WithTransactionSupport"
=======
      "TRANSACTION"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      );
    }
    if (currentCycle.isStdImpl()) {
      code.setVariable("createSuffix", 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      ""
=======
      "STANDARD"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      );
    }
    if (currentCycle.isDbImpl()) {
      code.setVariable("createSuffix", 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "WithDatabaseSupport"
=======
      "DATABASE"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      );
    }
    if (!currentCycle.isDbImpl()) {
      code.add(
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "/* Constructors and create methods with values for initial vertex and edge count */"
=======
      "/**"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "public #simpleClassName#Impl(int vMax, int eMax) {"
=======
      " * DON\'T USE THE CONSTRUCTOR"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "\tthis(null, vMax, eMax);"
=======
      " * For instantiating a Graph, use the Schema and a GraphFactory"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "}"
=======
      "**/"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      ""
=======
      "public #simpleImplClassName#() {"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "public #simpleClassName#Impl(java.lang.String id, int vMax, int eMax) {"
=======
      "\tthis(null);"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , "\tsuper(id, #schemaName#.instance().#schemaVariableName#, vMax, eMax);", "\tinitializeAttributesWithDefaultValues();", "}", "", 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "public static #javaClassName# create(int vMax, int eMax) {"
=======
      "/**"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "\treturn (#javaClassName#) #schemaName#.instance().create#uniqueClassName##createSuffix#(null, vMax, eMax);"
=======
      " * DON\'T USE THE CONSTRUCTOR"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "}"
=======
      " * For instantiating a Graph, use the Schema and a GraphFactory"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      ""
=======
      "**/"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "public static #javaClassName# create(String id, int vMax, int eMax) {"
=======
      "public #simpleImplClassName#(int vMax, int eMax) {"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "\treturn (#javaClassName#) #schemaName#.instance().create#uniqueClassName##createSuffix#(id, vMax, eMax);"
=======
      "\tthis(null, vMax, eMax);"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , "}", "", 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "/* Constructors and create methods without values for initial vertex and edge count */"
=======
      "/**"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "public #simpleClassName#Impl() {"
=======
      " * DON\'T USE THE CONSTRUCTOR"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "\tthis(null);"
=======
      " * For instantiating a Graph, use the Schema and a GraphFactory"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "}"
=======
      "**/"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      ""
=======
      "public #simpleImplClassName#(java.lang.String id, int vMax, int eMax) {"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "public #simpleClassName#Impl(java.lang.String id) {"
=======
      "\tsuper(id, #javaClassName#.GC, vMax, eMax);"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , "\tsuper(id, #schemaName#.instance().#schemaVariableName#);", "\tinitializeAttributesWithDefaultValues();", "}", "", 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "public static #javaClassName# create() {"
=======
      "/**"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "\treturn (#javaClassName#) #schemaName#.instance().create#uniqueClassName##createSuffix#(null);"
=======
      " * DON\'T USE THE CONSTRUCTOR"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "}"
=======
      " * For instantiating a Graph, use the Schema and a GraphFactory"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      ""
=======
      "**/"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "public static #javaClassName# create(String id) {"
=======
      "public #simpleImplClassName#(java.lang.String id) {"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "\treturn (#javaClassName#) #schemaName#.instance().create#uniqueClassName##createSuffix#(id);"
=======
      "\tsuper(id, #javaClassName#.GC);"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , "\tinitializeAttributesWithDefaultValues();", "}");
    } else {
      code.add(
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "/* Constructors and create methods for database support */"
=======
      "/**"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      ""
=======
      " * DON\'T USE THE CONSTRUCTOR"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "public #simpleClassName#Impl(java.lang.String id, GraphDatabase graphDatabase) {"
=======
      " * For instantiating a Graph, use a GraphFactory"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "\tsuper(id, #schemaName#.instance().#schemaVariableName#, graphDatabase);"
=======
      "**/"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , "public #simpleImplClassName#(java.lang.String id, GraphDatabase graphDatabase) {", "\tsuper(id, #javaClassName#.GC, graphDatabase);", "\tinitializeAttributesWithDefaultValues();", "}", "", 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "public #simpleClassName#Impl(java.lang.String id, int vMax, int eMax, GraphDatabase graphDatabase) {"
=======
      "/**"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "\tsuper(id, vMax, eMax, #schemaName#.instance().#schemaVariableName#, graphDatabase);"
=======
      " * DON\'T USE THE CONSTRUCTOR"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , " * For instantiating a Graph, use a GraphFactory", "**/", "public #simpleImplClassName#(java.lang.String id, int vMax, int eMax, GraphDatabase graphDatabase) {", "\tsuper(id, vMax, eMax, #javaClassName#.GC, graphDatabase);", "\tinitializeAttributesWithDefaultValues();", "}", "", "public static #javaClassName# create(String id, GraphDatabase graphDatabase) {", "\ttry{", "\t\treturn (#javaClassName#) #schemaName#.instance().create#uniqueClassName##createSuffix#(id, graphDatabase);", "\t}", "\tcatch(GraphDatabaseException exception){", "\t\tthrow new GraphException(\"Could not create graph.\", exception);", "\t}", "}");
    }
    return code;
  }

  private CodeBlock createGraphElementClassMethods() {
    CodeList code = new CodeList();
    GraphClass gc = (GraphClass) aec;
    TreeSet<GraphElementClass<?, ?>> sortedClasses = new TreeSet<GraphElementClass<?, ?>>();
    sortedClasses.addAll(gc.getGraphElementClasses());
    for (GraphElementClass<?, ?> gec : sortedClasses) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      if (!gec.isInternal()) {
        CodeList gecCode = new CodeList();
        code.addNoIndent(gecCode);
        gecCode.addNoIndent(new CodeSnippet(true, "// ------------------------ Code for #ecQualifiedName# ------------------------"));
        gecCode.setVariable("ecSimpleName", gec.getSimpleName());
        gecCode.setVariable("ecUniqueName", gec.getUniqueName());
        gecCode.setVariable("ecQualifiedName", gec.getQualifiedName());
        gecCode.setVariable("ecSchemaVariableName", gec.getVariableName());
        gecCode.setVariable("ecJavaClassName", schemaRootPackageName + "." + gec.getQualifiedName());
        gecCode.setVariable("ecType", (gec instanceof VertexClass ? "Vertex" : "Edge"));
        gecCode.setVariable("ecTypeInComment", (gec instanceof VertexClass ? "vertex" : "edge"));
        gecCode.setVariable("ecCamelName", camelCase(gec.getUniqueName()));
        gecCode.setVariable("ecImplName", (gec.isAbstract() ? "**ERROR**" : camelCase(gec.getQualifiedName()) + "Impl"));
        gecCode.addNoIndent(createGetFirstMethods(gec));
        gecCode.addNoIndent(createFactoryMethods(gec));
      }
=======
      CodeList gecCode = new CodeList();
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java

      code.addNoIndent(gecCode);
      gecCode.addNoIndent(new CodeSnippet(true, "// ------------------------ Code for #ecQualifiedName# ------------------------"));
      gecCode.setVariable("ecSimpleName", gec.getSimpleName());
      gecCode.setVariable("ecUniqueName", gec.getUniqueName());
      gecCode.setVariable("ecQualifiedName", gec.getQualifiedName());
      gecCode.setVariable("ecSchemaVariableName", gec.getVariableName());
      gecCode.setVariable("ecJavaClassName", schemaRootPackageName + "." + gec.getQualifiedName());
      gecCode.setVariable("ecType", (gec instanceof VertexClass ? "Vertex" : "Edge"));
      gecCode.setVariable("ecTypeInComment", (gec instanceof VertexClass ? "vertex" : "edge"));
      gecCode.setVariable("ecTypeAecConstant", (gec instanceof VertexClass ? "VC" : "EC"));
      gecCode.setVariable("ecCamelName", camelCase(gec.getUniqueName()));
      gecCode.setVariable("ecImplName", (gec.isAbstract() ? "**ERROR**" : camelCase(gec.getQualifiedName()) + "Impl"));
      gecCode.addNoIndent(createGetFirstMethods(gec));
      gecCode.addNoIndent(createFactoryMethods(gec));
    }
    return code;
  }

  private CodeBlock createGetFirstMethods(GraphElementClass<?, ?> gec) {
    CodeList code = new CodeList();
    if (config.hasTypeSpecificMethodsSupport()) {
      code.addNoIndent(createGetFirstMethod(gec));
    }
    return code;
  }

  private CodeBlock createGetFirstMethod(GraphElementClass<?, ?> gec) {
    CodeSnippet code = new CodeSnippet(true);
    if (currentCycle.isAbstract()) {
      code.add("/**", " * @return the first #ecSimpleName# #ecTypeInComment# in this graph");
      code.add(" */", 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "public #ecJavaClassName# getFirst#ecCamelName#(#formalParams#);"
=======
      "public #ecJavaClassName# getFirst#ecCamelName#();"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      );
    }
    if (currentCycle.isStdOrDbImplOrTransImpl()) {
      code.add(
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "public #ecJavaClassName# getFirst#ecCamelName#(#formalParams#) {"
=======
      "public #ecJavaClassName# getFirst#ecCamelName#() {"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "\treturn (#ecJavaClassName#)getFirst#ecType#(#schemaName#.instance().#ecSchemaVariableName##actualParams#);"
=======
      "\treturn (#ecJavaClassName#)getFirst#ecType#(#ecJavaClassName#.#ecTypeAecConstant#);"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , "}");
    }

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
    code.setVariable("formalParams", "");
=======
>>>>>>> Unknown file: This is a bug in JDime.


<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
    code.setVariable("actualParams", "");
=======
>>>>>>> Unknown file: This is a bug in JDime.

    return code;
  }

  private CodeBlock createFactoryMethods(GraphElementClass<?, ?> gec) {
    if (gec.isAbstract()) {
      return null;
    }
    CodeList code = new CodeList();
    code.addNoIndent(createFactoryMethod(gec, false));
    if (currentCycle.isStdOrDbImplOrTransImpl()) {
      code.addNoIndent(createFactoryMethod(gec, true));
    }
    return code;
  }

  private CodeBlock createFactoryMethod(GraphElementClass<?, ?> gec, boolean withId) {
    CodeSnippet code = new CodeSnippet(true);
    if (currentCycle.isStdImpl()) {
      code.setVariable("cycleSupportSuffix", "");
    } else {
      if (currentCycle.isTransImpl()) {
        code.setVariable("cycleSupportSuffix", "WithTransactionSupport");
      } else {
        if (currentCycle.isDbImpl()) {
          code.setVariable("cycleSupportSuffix", "WithDatabaseSupport");
        }
      }
    }
    if (currentCycle.isAbstract()) {
      code.add("/**", " * Creates a new #ecUniqueName# #ecTypeInComment# in this graph.", " *");
      if (withId) {
        code.add(" * @param id the <code>id</code> of the #ecTypeInComment#");
      }
      if (gec instanceof EdgeClass) {
        code.add(" * @param alpha the start vertex of the edge", " * @param omega the target vertex of the edge");
      }
      code.add("*/", "public #ecJavaClassName# create#ecCamelName#(#formalParams#);");
    }
    if (currentCycle.isStdOrDbImplOrTransImpl()) {
      code.add("public #ecJavaClassName# create#ecCamelName#(#formalParams#) {", 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/left.java
      "\t#ecJavaClassName# new#ecType# = (#ecJavaClassName#) graphFactory.create#ecType##cycleSupportSuffix#(#ecJavaClassName#.class, #newActualParams#, this#additionalParams#);"
=======
      "\treturn graphFactory.<#ecJavaClassName#> create#ecType#(#ecJavaClassName#.#ecTypeAecConstant#, #newActualParams#, this#additionalParams#);"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphCodeGenerator.java/right.java
      , "\treturn new#ecType#;", "}");
      code.setVariable("additionalParams", "");
    }
    if (gec instanceof EdgeClass) {
      EdgeClass ec = (EdgeClass) gec;
      String fromClass = ec.getFrom().getVertexClass().getQualifiedName();
      String toClass = ec.getTo().getVertexClass().getQualifiedName();
      if (fromClass.equals("Vertex")) {
        code.setVariable("fromClass", "#jgPackage#.Vertex");
      } else {
        code.setVariable("fromClass", "#schemaPackage#." + fromClass);
      }
      if (toClass.equals("Vertex")) {
        code.setVariable("toClass", "#jgPackage#.Vertex");
      } else {
        code.setVariable("toClass", "#schemaPackage#." + toClass);
      }
      code.setVariable("formalParams", (withId ? "int id, " : "") + "#fromClass# alpha, #toClass# omega");
      code.setVariable("addActualParams", ", alpha, omega");
      code.setVariable("additionalParams", ", alpha, omega");
    } else {
      code.setVariable("formalParams", (withId ? "int id" : ""));
      code.setVariable("addActualParams", "");
    }
    code.setVariable("newActualParams", (withId ? "id" : "0"));
    return code;
  }

  private CodeBlock createEdgeIteratorMethods() {
    GraphClass gc = (GraphClass) aec;
    CodeList code = new CodeList();
    if (!config.hasTypeSpecificMethodsSupport()) {
      return code;
    }
    Set<EdgeClass> edgeClassSet = new HashSet<EdgeClass>();
    edgeClassSet.addAll(gc.getEdgeClasses());
    for (EdgeClass edge : edgeClassSet) {
      if (edge.isInternal()) {
        continue;
      }
      if (currentCycle.isStdOrDbImplOrTransImpl()) {
        addImports("#jgImplPackage#.EdgeIterable");
      }
      CodeSnippet s = new CodeSnippet(true);
      code.addNoIndent(s);
      s.setVariable("edgeUniqueName", camelCase(edge.getUniqueName()));
      s.setVariable("edgeQualifiedName", edge.getQualifiedName());
      s.setVariable("edgeJavaClassName", schemaRootPackageName + "." + edge.getQualifiedName());
      if (currentCycle.isAbstract()) {
        s.add("/**");
        s.add(" * @return an Iterable for all edges of this graph that are of type #edgeQualifiedName# or subtypes.");
        s.add(" */");
        s.add("public Iterable<#edgeJavaClassName#> get#edgeUniqueName#Edges();");
      }
      if (currentCycle.isStdOrDbImplOrTransImpl()) {
        s.add("public Iterable<#edgeJavaClassName#> get#edgeUniqueName#Edges() {");
        s.add("\treturn new EdgeIterable<#edgeJavaClassName#>(this, #edgeJavaClassName#.class);");
        s.add("}");
      }
      s.add("");
    }
    return code;
  }

  private CodeBlock createVertexIteratorMethods() {
    GraphClass gc = (GraphClass) aec;
    CodeList code = new CodeList();
    if (!config.hasTypeSpecificMethodsSupport()) {
      return code;
    }
    Set<VertexClass> vertexClassSet = new HashSet<VertexClass>();
    vertexClassSet.addAll(gc.getVertexClasses());
    for (VertexClass vertex : vertexClassSet) {
      if (vertex.isInternal()) {
        continue;
      }
      if (currentCycle.isStdOrDbImplOrTransImpl()) {
        addImports("#jgImplPackage#.VertexIterable");
      }
      CodeSnippet s = new CodeSnippet(true);
      code.addNoIndent(s);
      s.setVariable("vertexQualifiedName", vertex.getQualifiedName());
      s.setVariable("vertexJavaClassName", "#schemaPackage#." + vertex.getQualifiedName());
      s.setVariable("vertexCamelName", camelCase(vertex.getUniqueName()));
      if (currentCycle.isAbstract()) {
        s.add("/**");
        s.add(" * @return an Iterable for all vertices of this graph that are of type #vertexQualifiedName# or subtypes.");
        s.add(" */");
        s.add("public Iterable<#vertexJavaClassName#> get#vertexCamelName#Vertices();");
      }
      if (currentCycle.isStdOrDbImplOrTransImpl()) {
        s.add("public Iterable<#vertexJavaClassName#> get#vertexCamelName#Vertices() {");
        s.add("\treturn new VertexIterable<#vertexJavaClassName#>(this, #vertexJavaClassName#.class);");
        s.add("}");
      }
      s.add("");
    }
    return code;
  }

  @Override protected void addCheckValidityCode(CodeSnippet code) {
  }

  @Override protected CodeBlock createAttributedElementClassConstant() {
    return new CodeSnippet(true, "public static final #jgSchemaPackage#.#schemaElementClass# GC" + " = #schemaPackageName#.#schemaName#.instance().#schemaVariableName#;");
  }

  @Override protected CodeBlock createGetAttributedElementClassMethod() {
    return new CodeSnippet(true, "@Override", "public final #jgSchemaPackage#.#schemaElementClass# getAttributedElementClass() {", "\treturn #javaClassName#.GC;", "}");
  }
}