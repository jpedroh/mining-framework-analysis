package de.uni_koblenz.jgralab.codegenerator;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.VertexClass;

/**
 * This class generates the code of the GraphElement Factory.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class GraphFactoryGenerator extends CodeGenerator {
  private final Schema schema;

  public GraphFactoryGenerator(Schema schema, String schemaPackageName, CodeGeneratorConfiguration config) {
    super(schemaPackageName, "", config);
    this.schema = schema;
    rootBlock.setVariable("schemaName", schema.getQualifiedName());
    rootBlock.setVariable("simpleClassName", 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java
    schema.getName()
=======
    schema.getGraphClass().getSimpleName()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java
     + "Factory");
    rootBlock.setVariable("simpleClassName", 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java
    schema.getName()
=======
    schema.getGraphClass().getSimpleName()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java
     + "FactoryImpl");
    rootBlock.setVariable("isClassOnly", "false");
  }

  @Override protected CodeBlock createHeader() {
    CodeSnippet code = new CodeSnippet(true);

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java
    code.setVariable("className", schema.getName() + "Factory");
=======
    if (currentCycle.isAbstract()) {
      addImports("#jgPackage#.GraphFactory");
      code.add("public interface #simpleClassName# extends GraphFactory {");
    } else {
      addImports("#schemaPackage#.#simpleClassName#");
      addImports("#jgImplPackage#.GraphFactoryImpl");
      addImports("#jgPackage#.ImplementationType");
      code.add("public class #simpleImplClassName# extends GraphFactoryImpl implements #simpleClassName# {");
    }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java

    return code;
  }

  @Override protected CodeBlock createBody() {
    CodeList code = new CodeList();
    if (currentCycle.
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java
    isClassOnly()
=======
    isStdOrDbImplOrTransImpl()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java
    ) {
      code.add(createConstructor());
      code.add(createFillTableMethod());
    }
    return code;
  }

  protected CodeBlock createConstructor() {
    CodeList code = new CodeList();

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java
    code.setVariable("className", schema.getName() + "Factory");
=======
    if (currentCycle.isStdImpl()) {
      code.setVariable("implTypeInfix", "STANDARD");
    }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java

    if (currentCycle.isTransImpl()) {
      code.setVariable("implTypeInfix", "TRANSACTION");
    }
    if (currentCycle.isDbImpl()) {
      code.setVariable("implTypeInfix", "DATABASE");
    }
    CodeSnippet s = new CodeSnippet(true);
    s.add("public #simpleImplClassName#() {", "\tsuper(#schemaName#.instance(), ImplementationType.#implTypeInfix#);", "\tcreateMaps();");
    code.addNoIndent(s);
    code.add(createFillTableMethod());
    code.addNoIndent(new CodeSnippet("}"));
    return code;
  }

  protected CodeBlock createFillTableMethod() {
    if (currentCycle.isAbstract()) {
      return null;
    }
    CodeList code = new CodeList();
    GraphClass graphClass = schema.getGraphClass();
    code.
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java
    add(createFillTableForGraph(graphClass))
=======
    addNoIndent(createFillTableForGraph(graphClass))
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java
    ;
    for (VertexClass vertexClass : graphClass.getVertexClasses()) {
      code.
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java
      add(createFillTableForVertex(vertexClass))
=======
      addNoIndent(createFillTableForVertex(vertexClass))
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java
      ;
    }
    for (EdgeClass edgeClass : graphClass.getEdgeClasses()) {
      code.
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java
      add(createFillTableForEdge(edgeClass))
=======
      addNoIndent(createFillTableForEdge(edgeClass))
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java
      ;
    }
    return code;
  }

  protected CodeBlock createFillTableForGraph(GraphClass graphClass) {
    if (graphClass.isAbstract()) {
      return null;
    }
    CodeSnippet code = new CodeSnippet(false);
    code.setVariable("graphName", graphClass.getQualifiedName() + ".GC");
    code.setVariable("graphImplName", 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java
    schemaRootPackageName + ".impl.std."
=======
    "#schemaImplStdPackage#." + graphClass.getQualifiedName()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java
     + "Impl");
    code.setVariable("graphTransactionImplName", 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java
    schemaRootPackageName + ".impl.trans." + graphClass.getQualifiedName()
=======
    "#schemaImplTransPackage#." + graphClass.getQualifiedName() + "Impl"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java
    );
    code.setVariable("graphDatabaseImplName", 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java
    schemaRootPackageName + ".impl.db." + graphClass.getQualifiedName()
=======
    "#schemaImplDbPackage#." + graphClass.getQualifiedName() + "Impl"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java
    );
    if (!graphClass.isAbstract()) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java
      if (config.hasStandardSupport()) {
        code.add("setGraphImplementationClass(#graphName#.class, #graphImplName#Impl.class);");
      }
=======
      if (currentCycle.isStdImpl() && config.hasStandardSupport()) {
        code.add("setGraphImplementationClass(#schemaPackage#.#graphName#, #graphImplName#.class);");
      }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java
      if (config.hasTransactionSupport()) {
        code.add("setGraphTransactionImplementationClass(#graphName#.class, #graphTransactionImplName#Impl.class);");
      }
=======
      if (currentCycle.isTransImpl() && config.hasTransactionSupport()) {
        code.add("setGraphImplementationClass(#schemaPackage#.#graphName#, #graphTransactionImplName#.class);");
      }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java
      if (config.hasDatabaseSupport()) {
        code.add("setGraphDatabaseImplementationClass(#graphName#.class, #graphDatabaseImplName#Impl.class);");
      }
=======
      if (currentCycle.isDbImpl() && config.hasDatabaseSupport()) {
        code.add("setGraphImplementationClass(#schemaPackage#.#graphName#, #graphDatabaseImplName#.class);");
      }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java
    }
    return code;
  }

  protected CodeBlock createFillTableForVertex(VertexClass vertexClass) {
    if (vertexClass.isAbstract()) {
      return null;
    }
    CodeSnippet code = new CodeSnippet(false);
    code.setVariable("vertexName", vertexClass.getQualifiedName() + ".VC");
    code.setVariable("vertexImplName", 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java
    schemaRootPackageName + ".impl.std."
=======
    "#schemaImplStdPackage#." + vertexClass.getQualifiedName()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java
     + "Impl");
    code.setVariable("vertexTransactionImplName", 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java
    schemaRootPackageName + ".impl.trans." + vertexClass.getQualifiedName()
=======
    "#schemaImplTransPackage#." + vertexClass.getQualifiedName() + "Impl"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java
    );
    code.setVariable("vertexDatabaseImplName", 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java
    schemaRootPackageName + ".impl.db." + vertexClass.getQualifiedName()
=======
    "#schemaImplDbPackage#." + vertexClass.getQualifiedName() + "Impl"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java
    );
    if (!vertexClass.isAbstract()) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java
      if (config.hasStandardSupport()) {
        code.add("setVertexImplementationClass(#vertexName#.class, #vertexImplName#Impl.class);");
      }
=======
      if (currentCycle.isStdImpl() && config.hasStandardSupport()) {
        code.add("setVertexImplementationClass(#schemaPackage#.#vertexName#, #vertexImplName#.class);");
      }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java
      if (config.hasTransactionSupport()) {
        code.add("setVertexTransactionImplementationClass(#vertexName#.class, #vertexTransactionImplName#Impl.class);");
      }
=======
      if (currentCycle.isTransImpl() && config.hasTransactionSupport()) {
        code.add("setVertexImplementationClass(#schemaPackage#.#vertexName#, #vertexTransactionImplName#.class);");
      }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java
      if (config.hasDatabaseSupport()) {
        code.add("setVertexDatabaseImplementationClass(#vertexName#.class, #vertexDatabaseImplName#Impl.class);");
      }
=======
      if (currentCycle.isDbImpl() && config.hasDatabaseSupport()) {
        code.add("setVertexImplementationClass(#schemaPackage#.#vertexName#, #vertexDatabaseImplName#.class);");
      }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java
    }
    return code;
  }

  protected CodeBlock createFillTableForEdge(EdgeClass edgeClass) {
    CodeSnippet code = new CodeSnippet(false);
    code.setVariable("edgeName", edgeClass.getQualifiedName() + ".EC");
    code.setVariable("edgeImplName", 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java
    schemaRootPackageName + ".impl.std."
=======
    "#schemaImplStdPackage#." + edgeClass.getQualifiedName()
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java
     + "Impl");
    code.setVariable("edgeTransactionImplName", 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java
    schemaRootPackageName + ".impl.trans." + edgeClass.getQualifiedName()
=======
    "#schemaImplTransPackage#." + edgeClass.getQualifiedName() + "Impl"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java
    );
    code.setVariable("edgeDatabaseImplName", 
<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java
    schemaRootPackageName + ".impl.db." + edgeClass.getQualifiedName()
=======
    "#schemaImplDbPackage#." + edgeClass.getQualifiedName() + "Impl"
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java
    );
    if (!edgeClass.isAbstract()) {

<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java
      if (config.hasStandardSupport()) {
        code.add("setEdgeImplementationClass(#edgeName#.class, #edgeImplName#Impl.class);");
      }
=======
      if (currentCycle.isStdImpl() && config.hasStandardSupport()) {
        code.add("setEdgeImplementationClass(#schemaPackage#.#edgeName#, #edgeImplName#.class);");
      }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java
      if (config.hasTransactionSupport()) {
        code.add("setEdgeTransactionImplementationClass(#edgeName#.class, #edgeTransactionImplName#Impl.class);");
      }
=======
      if (currentCycle.isTransImpl() && config.hasTransactionSupport()) {
        code.add("setEdgeImplementationClass(#schemaPackage#.#edgeName#, #edgeTransactionImplName#.class);");
      }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java


<<<<<<< /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/left.java
      if (config.hasDatabaseSupport()) {
        code.add("setEdgeDatabaseImplementationClass(#edgeName#.class, #edgeDatabaseImplName#Impl.class);");
      }
=======
      if (currentCycle.isDbImpl() && config.hasDatabaseSupport()) {
        code.add("setEdgeImplementationClass(#schemaPackage#.#edgeName#, #edgeDatabaseImplName#.class);");
      }
>>>>>>> /usr/src/app/output/jgralab/jgralab/1502a3525248376410ba747530bf6ef93a0656f2/src/de/uni_koblenz/jgralab/codegenerator/GraphFactoryGenerator.java/right.java
    }
    return code;
  }
}